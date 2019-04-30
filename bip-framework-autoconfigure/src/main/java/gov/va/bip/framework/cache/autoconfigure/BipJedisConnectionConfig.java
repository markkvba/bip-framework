package gov.va.bip.framework.cache.autoconfigure;

import java.time.Duration;

import javax.annotation.PostConstruct;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.validation.Defense;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Imported by {@link BipCacheAutoConfiguration} so it can participate in the autoconfiguration bootstrap.
 * <p>
 * Configures:
 * <ul>
 * <li> RedisStandaloneConfiguration - the redis "Standalone" module (host, port, db index, password)
 * <li> JedisClientConfiguration (timeouts, connection pool, SSL)
 * </ul>
 *
 * @author aburkholder
 */
@Configuration
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableConfigurationProperties({ RedisProperties.class })
public class BipJedisConnectionConfig {
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipJedisConnectionConfig.class);

	/** Build properties to provide a unique "clientName" for the JedisClientConfiguration */
	@Autowired
	BuildProperties buildProperties;

	/** Properties from the application YAML */
	@Autowired
	private RedisProperties redisProperties;

	/** ConnectionFactory from spring context, so it can be destroyed on refresh events */
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;

	/**
	 * Ensure autowiring succeeded.
	 */
	@PostConstruct
	public void postConstruct() {
		Defense.notNull(buildProperties);
		Defense.notNull(redisProperties);
		Defense.notNull(jedisConnectionFactory);
	}

	/**
	 * On the RefreshScope refresh event, destroy the JedisConnectionFactory to force it
	 * to rebuild from {@link #redisConnectionFactory()} with the current setting from the application YAML.
	 *
	 * @param event the refresh event
	 */
	@EventListener
	public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
		LOGGER.debug("Reconfiguring caches after refresh event: event.getName() {}",
				event.getName() + "; event.getSource() {}",
				event.getSource());
		jedisConnectionFactory.destroy();
		LOGGER.debug("redisConnectionFactory destroyed");
	}

	/**
	 * Replaces the default RedisConnectionFactory in the spring context.
	 * This bean configures from the application YAML, and will rebuild the
	 * Redis Connection after {@link #onApplicationEvent(RefreshScopeRefreshedEvent)}
	 * destroys the previous factory.
	 *
	 * @return a connection factory with current property values
	 */
	@Bean
	@RefreshScope
	public JedisConnectionFactory redisConnectionFactory() {
		return new JedisConnectionFactory(getRedisStandaloneConfiguration(), getJedisClientConfiguration());
	}

	/**
	 * Get a Jedis Client config with properties as currently declared in the application YAML.
	 *
	 * @return configuration object for the Jedis Client
	 */
	private JedisClientConfiguration getJedisClientConfiguration() {
		JedisClientConfigurationBuilder builder = JedisClientConfiguration
				.builder()
				.clientName(buildProperties.getName() + "_" + buildProperties.getVersion())
				.connectTimeout((redisProperties.getTimeout() == null
						? Duration.ofMillis(Protocol.DEFAULT_TIMEOUT)
						: redisProperties.getTimeout()))
				.readTimeout((redisProperties.getTimeout() == null
						? Duration.ofMillis(Protocol.DEFAULT_TIMEOUT)
						: redisProperties.getTimeout()));

		if (redisProperties.getJedis() != null && redisProperties.getJedis().getPool() != null) {
			builder.usePooling()
					.poolConfig(jedisPoolConfig(redisProperties.getJedis().getPool()));
		}
		if (redisProperties.isSsl()) {
			builder.useSsl()
					.hostnameVerifier(NoopHostnameVerifier.INSTANCE);
			/* also could set: .sslParameters(..) and sslSocketFactory(..) */
		}
		return builder.build();
	}

	/**
	 * Get a Jedis pool config with properties as currently declared in the application YAML.
	 * <p>
	 * This method is stolen from {@code org.springframework.boot.autoconfigure.data.redis.JedisConnectionConfiguration}
	 * with the assumption that this is the correct way to configure the values
	 * required by Jedis.
	 *
	 * @param pool the RedisProperties.getJedis().getPool()
	 * @return configuration object for the Jedis pool
	 */
	private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal((pool.getMaxActive() <= 0
				? GenericObjectPoolConfig.DEFAULT_MAX_TOTAL
				: pool.getMaxActive()));
		config.setMaxIdle((pool.getMaxIdle() <= 0
				? GenericObjectPoolConfig.DEFAULT_MAX_IDLE
				: pool.getMaxIdle()));
		config.setMinIdle((pool.getMinIdle() <= 0
				? GenericObjectPoolConfig.DEFAULT_MIN_IDLE
				: pool.getMinIdle()));
		config.setMaxWaitMillis((pool.getMaxWait() == null
				? GenericObjectPoolConfig.DEFAULT_MAX_WAIT_MILLIS
				: pool.getMaxWait().toMillis()));
		return config;
	}

	/**
	 * Get a Jedis connection configuration object with properties as currently declared in the application YAML.
	 *
	 * @return the standalone configuration
	 */
	private RedisStandaloneConfiguration getRedisStandaloneConfiguration() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
		redisStandaloneConfiguration.setHostName(redisProperties.getHost());
		redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
		redisStandaloneConfiguration.setPort(redisProperties.getPort());
		return redisStandaloneConfiguration;
	}
}
