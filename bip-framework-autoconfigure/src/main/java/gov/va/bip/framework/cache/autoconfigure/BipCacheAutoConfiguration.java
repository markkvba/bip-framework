package gov.va.bip.framework.cache.autoconfigure;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.util.CollectionUtils;

import gov.va.bip.framework.audit.AuditLogSerializer;
import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.cache.autoconfigure.BipRedisCacheProperties.RedisExpires;
import gov.va.bip.framework.cache.autoconfigure.BipRedisClientProperties.JedisPoolProps;
import gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsImpl;
import gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsMBean;
import gov.va.bip.framework.cache.autoconfigure.server.BipEmbeddedRedisServer;
import gov.va.bip.framework.cache.interceptor.BipCacheInterceptor;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.validation.Defense;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Cache auto configuration:
 * <ul>
 * <li> Configure and start Redis embedded server if necessary (as declared under spring profiles in application yaml).
 * See {@link BipRedisProps}.
 * <li> Configure the Redis "Standalone" module with the host and port. See {@link BipRedisProps}.
 * <li> Configure the Jedis Client, including SSL and Connection Pooling. See {@link BipRedisClientProps}.
 * <li> Configure Cache TTLs and expirations. See {@link BipRedisCacheProps}.
 * </ul>
 */
@Configuration
@Import({ BipRedisProperties.class, BipRedisClientProperties.class, BipRedisCacheProperties.class })
//@EnableConfigurationProperties({ BipRedisProperties.class, BipRedisClientProperties.class, BipRedisCacheProperties.class })
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
@EnableMBeanExport(defaultDomain = "gov.va.bip", registration = RegistrationPolicy.FAIL_ON_EXISTING)
public class BipCacheAutoConfiguration extends CachingConfigurerSupport {

	static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipCacheAutoConfiguration.class);

	/** Cache properties derived from application BipRedisProperties file */
	@Autowired
//	@Qualifier("bipRedisProperties")
	private BipRedisProperties properties;

	/** Cache propertiesClient derived from application BipRedisClientProperties file */
	@Autowired
//	@Qualifier("bipRedisClientProperties")
	private BipRedisClientProperties propertiesClient;

	/** Cache properties derived from application properties file */
	@Autowired
//	@Qualifier("bipRedisCacheProperties")
	private BipRedisCacheProperties propertiesCache;

	/** Embedded Redis bean to make sure embedded redis is started before redis cache is created. */
	@SuppressWarnings("unused")
	@Autowired(required = false)
	private BipEmbeddedRedisServer referenceServerRedisEmbedded;

	/**
	 * Post construction validations.
	 */
	@PostConstruct
	public void postConstruct() {
		Defense.notNull(propertiesCache, BipRedisCacheProperties.class.getSimpleName() + " cannot be null.");
		Defense.notNull(propertiesClient, BipRedisClientProperties.class.getSimpleName() + " cannot be null.");
		Defense.notNull(propertiesClient.getJedisClientProps().getClientname(),
				BipRedisClientProperties.class.getSimpleName() + ".clientName cannot be null.");
		Defense.notNull(referenceServerRedisEmbedded, BipEmbeddedRedisServer.class.getSimpleName() + " cannot be null.");
	}

	/**
	 * JMX MBean that exposes cache management operations.
	 *
	 * @return BipCacheOpsMBean - the management bean
	 */
	@Bean
	public BipCacheOpsMBean bipCacheOpsMBean() {
		return new BipCacheOpsImpl();
	}

	/**
	 * Audit log serializer.
	 *
	 * @return the audit log serializer
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuditLogSerializer auditLogSerializer() {
		return new AuditLogSerializer();
	}

	/**
	 * Base async audit.
	 *
	 * @return the base async audit
	 */
	@Bean
	@ConditionalOnMissingBean
	public BaseAsyncAudit baseAsyncAudit() {
		return new BaseAsyncAudit();
	}

	/**
	 * JedisConnectionFactory configuration for Redis Client, Jedis Pool, and SSL
	 *
	 * @return RedisConnectionFactory
	 */
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {

//		Defense.notNull(properties.getRedisProps(),
//				propertiesClient.getClass().getSimpleName() + ".RedisConfig cannot be null.");

		/* ======== Redis Standalone Config ======== */

		Defense.hasText(properties.getHost(),
				propertiesClient.getClass().getSimpleName() + ".host must have a value.");
		Defense.notNull(properties.getPort(),
				propertiesClient.getClass().getSimpleName() + ".port must have a value.");
		Defense.isTrue(properties.getPort() > 0,
				propertiesClient.getClass().getSimpleName() + ".port must be a valid port number.");

		RedisStandaloneConfiguration redisStandaloneConfig =
				new RedisStandaloneConfiguration(properties.getHost(),
						Integer.valueOf(properties.getPort()));

		/* ======== Jedis Client Config ======== */

		JedisPoolProps jedisPoolProps = propertiesClient.getJedisClientProps().getPoolConfig();

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(jedisPoolProps.getMaxTotal());
		poolConfig.setMaxIdle(jedisPoolProps.getMaxIdle()); // max
		poolConfig.setMaxWaitMillis(jedisPoolProps.getMaxWaitMillis());
		poolConfig.setMinEvictableIdleTimeMillis(jedisPoolProps.getMinEvictableIdleTimeMillis());
		poolConfig.setMinIdle(jedisPoolProps.getMinIdle());
		poolConfig.setBlockWhenExhausted(jedisPoolProps.isBlockWhenExhausted());
		poolConfig.setEvictionPolicy(jedisPoolProps.getEvictionPolicy());
		poolConfig.setEvictionPolicyClassName(jedisPoolProps.getEvictionPolicyClassName());
		poolConfig.setEvictorShutdownTimeoutMillis(jedisPoolProps.getEvictorShutdownTimeoutMillis());
		poolConfig.setFairness(jedisPoolProps.isFairness());
		poolConfig.setJmxEnabled(jedisPoolProps.isJmxEnabled());
		poolConfig.setJmxNameBase(jedisPoolProps.getJmxNameBase());
		poolConfig.setJmxNamePrefix(jedisPoolProps.getJmxNamePrefix());
		poolConfig.setLifo(jedisPoolProps.isLifo());
		poolConfig.setNumTestsPerEvictionRun(jedisPoolProps.getNumTestsPerEvictionRun());
		poolConfig.setSoftMinEvictableIdleTimeMillis(jedisPoolProps.getSoftMinEvictableIdleTimeMillis());
		poolConfig.setTestOnBorrow(jedisPoolProps.isTestOnBorrow());
		poolConfig.setTestOnCreate(jedisPoolProps.isTestOnCreate());
		poolConfig.setTestOnReturn(jedisPoolProps.isTestOnReturn());
		poolConfig.setTestWhileIdle(jedisPoolProps.isTestWhileIdle());

		JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfigBuilder = JedisClientConfiguration.builder()
				// .clientName(clientName) // TODO should this be the same as the first part of the cache key?
				.connectTimeout(propertiesClient.getJedisClientProps().getConnectTimeout())
				.readTimeout(propertiesClient.getJedisClientProps().getReadTimeout());

		if (propertiesClient.getJedisClientProps().isUsePooling()) {
			JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
			jedisPoolConfig.setBlockWhenExhausted(jedisPoolProps.isBlockWhenExhausted());
			jedisPoolConfig.setEvictorShutdownTimeoutMillis(jedisPoolProps.getEvictorShutdownTimeoutMillis());
			jedisPoolConfig.setFairness(jedisPoolProps.isFairness());
			jedisPoolConfig.setJmxEnabled(jedisPoolProps.isJmxEnabled());
			jedisPoolConfig.setJmxNameBase(jedisPoolProps.getJmxNameBase());
			jedisPoolConfig.setJmxNamePrefix(jedisPoolProps.getJmxNamePrefix());
			jedisPoolConfig.setLifo(jedisPoolProps.isLifo());
			jedisPoolConfig.setMaxIdle(jedisPoolProps.getMaxIdle());
			jedisPoolConfig.setMaxTotal(jedisPoolProps.getMaxTotal());
			jedisPoolConfig.setMaxWaitMillis(jedisPoolProps.getMaxWaitMillis());
			jedisPoolConfig.setMinEvictableIdleTimeMillis(jedisPoolProps.getMinEvictableIdleTimeMillis());
			jedisPoolConfig.setMinIdle(jedisPoolProps.getMinIdle());
			jedisPoolConfig.setNumTestsPerEvictionRun(jedisPoolProps.getNumTestsPerEvictionRun());
			jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(jedisPoolProps.getSoftMinEvictableIdleTimeMillis());
			jedisPoolConfig.setTestOnBorrow(jedisPoolProps.isTestOnBorrow());
			jedisPoolConfig.setTestOnCreate(jedisPoolProps.isTestOnCreate());
			jedisPoolConfig.setTestOnReturn(jedisPoolProps.isTestOnReturn());
			jedisPoolConfig.setTestWhileIdle(jedisPoolProps.isTestWhileIdle());
			jedisPoolConfig.setTimeBetweenEvictionRunsMillis(jedisPoolProps.getTimeBetweenEvictionRunsMillis());

			jedisClientConfigBuilder.usePooling().poolConfig(jedisPoolConfig);
		}

		if (propertiesClient.getJedisClientProps().isUseSsl()) {
			jedisClientConfigBuilder
					.useSsl()
			// .hostnameVerifier(hostnameVerifier) // TODO don't think this is needed
//					.sslParameters(new SSLParameters(new String[] { "cipherSuites" }))
//					.sslSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault())
			;
		}

		JedisClientConfiguration jedisClientConfig = jedisClientConfigBuilder.build();

		/* ======== Jedis Connection Factory ======== */

		JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisStandaloneConfig, jedisClientConfig);

		return connectionFactory;
	}

	/**
	 * Create the default cache configuration, with TTL set as declared by {@code reference:cache:defaultExpires} in the
	 * <i>[project]/src/main/resources/[app].yml</i>. Used by {@link RedisCacheManager}.
	 *
	 * @return RedisCacheConfiguration
	 */
	@Bean
	public RedisCacheConfiguration redisCacheConfiguration() {
		return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(propertiesCache.getDefaultExpires()));
	}

	/**
	 * Produce the Map of {@link RedisCacheConfiguration} objects derived from the list declared by {@code reference:cache:expires:*}
	 * in the <i>[app].yml</i>. Used by {@link RedisCacheManager}.
	 *
	 * @return Map&lt;String, org.springframework.data.redis.cache.RedisCacheConfiguration&gt;
	 */
	@Bean
	public Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfigurations() {
		Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> cacheConfigs = new HashMap<>();

		if (!CollectionUtils.isEmpty(propertiesCache.getExpires())) {
			// key = name, value - TTL
			final Map<String, Long> resultExpires = propertiesCache.getExpires().stream().filter(o -> o.getName() != null)
					.filter(o -> o.getTtl() != null).collect(Collectors.toMap(RedisExpires::getName, RedisExpires::getTtl));
			for (Entry<String, Long> entry : resultExpires.entrySet()) {
				org.springframework.data.redis.cache.RedisCacheConfiguration rcc =
						org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
								.entryTtl(Duration.ofSeconds(entry.getValue()));
				cacheConfigs.put(entry.getKey(), rcc);
			}
		}
		return cacheConfigs;
	}

	/**
	 * Create the cacheManager bean, configured by the redisCacheConfiguration beans.
	 *
	 * @param redisConnectionFactory
	 * @return CacheManager
	 */
	@Bean
	// @Override
	public CacheManager cacheManager(final RedisConnectionFactory redisConnectionFactory) {
		return RedisCacheManager
				.builder(redisConnectionFactory)
				.cacheDefaults(this.redisCacheConfiguration())
				.withInitialCacheConfigurations(this.redisCacheConfigurations())
				.transactionAware()
				.build();
	}

	/**
	 * Interface to get cache operation attribute sources. Required by {@link #cacheInterceptor()}.
	 *
	 * @return CacheOperationSource - the cache operation attribute source
	 */
	@Bean
	public CacheOperationSource cacheOperationSource() {
		return new AnnotationCacheOperationSource();
	}

	/**
	 * Custom {@link BipCacheInterceptor} to audit {@code cache.get(Object, Object)} operations.
	 *
	 * @return CacheInterceptor - the interceptor
	 */
	@Bean
	public CacheInterceptor cacheInterceptor() {
		CacheInterceptor interceptor = new BipCacheInterceptor();
		interceptor.setCacheOperationSources(cacheOperationSource());
		return interceptor;
	}

	/**
	 * Reference cache keys follow a specific naming convention, as enforced by this bean.
	 * <p>
	 * {@inheritDoc}
	 */
	@Bean
	@Override
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() { // NOSONAR lambda expressions do not accept optional params
			@Override
			public Object generate(final Object o, final Method method, final Object... objects) {
				LOGGER.debug("Generating cacheKey");
				final StringBuilder sb = new StringBuilder();
				sb.append(o.getClass().getName()).append(ClassUtils.PACKAGE_SEPARATOR_CHAR);
				sb.append(method.getName());
				for (final Object obj : objects) {
					sb.append(ClassUtils.PACKAGE_SEPARATOR_CHAR).append(obj.toString());
				}
				LOGGER.debug("Generated cacheKey: {}", sb.toString());
				return sb.toString();
			}
		};
	}

	@Bean
	@Override
	public CacheErrorHandler errorHandler() {
		return new RedisCacheErrorHandler();
	}

	/**
	 * The {@link CacheErrorHandler} strategy for Redis implementations.
	 */
	public static class RedisCacheErrorHandler implements CacheErrorHandler {

		@Override
		public void handleCacheGetError(final RuntimeException exception, final Cache cache, final Object key) {
			LOGGER.error(BipBanner.newBanner("Unable to get from cache " + cache.getName(), Level.ERROR), exception.getMessage());
		}

		@Override
		public void handleCachePutError(final RuntimeException exception, final Cache cache, final Object key, final Object value) {
			LOGGER.error(BipBanner.newBanner("Unable to put into cache " + cache.getName(), Level.ERROR), exception.getMessage());
		}

		@Override
		public void handleCacheEvictError(final RuntimeException exception, final Cache cache, final Object key) {
			LOGGER.error(BipBanner.newBanner("Unable to evict from cache " + cache.getName(), Level.ERROR), exception.getMessage());
		}

		@Override
		public void handleCacheClearError(final RuntimeException exception, final Cache cache) {
			LOGGER.error(BipBanner.newBanner("Unable to clean cache " + cache.getName(), Level.ERROR), exception.getMessage());
		}
	}

}
