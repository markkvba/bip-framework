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
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
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
import gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsImpl;
import gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsMBean;
import gov.va.bip.framework.cache.autoconfigure.server.BipEmbeddedRedisServer;
import gov.va.bip.framework.cache.interceptor.BipCacheInterceptor;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.validation.Defense;

/**
 * Cache auto configuration:
 * <ul>
 * <li> Configure and start Redis embedded server if necessary (as declared under spring profiles in application yaml).
 * See {@link BipRedisProps}.
 * <li> Configure the Redis "Standalone" module with the host and port. See {@link BipRedisProps}.
 * // * <li> Configure the Jedis Client, including SSL and Connection Pooling. See {@link BipRedisClientProps}.
 * // * <li> Configure Cache TTLs and expirations. See {@link BipRedisCacheProps}.
 * </ul>
 */
@Configuration
@EnableConfigurationProperties({BipRedisCacheProperties.class, BipRedisRefreshProperties.class})
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableCaching
@ConditionalOnProperty(name = BipCacheAutoConfiguration.CONDITIONAL_SPRING_REDIS,
		havingValue = BipCacheAutoConfiguration.CACHE_SERVER_TYPE)
@EnableMBeanExport(defaultDomain = BipCacheAutoConfiguration.JMX_DOMAIN, registration = RegistrationPolicy.FAIL_ON_EXISTING)
public class BipCacheAutoConfiguration extends CachingConfigurerSupport {
	/** Class logger */
	static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipCacheAutoConfiguration.class);

	/** Domain under which JMX beans are exposed */
	public static final String JMX_DOMAIN = "gov.va.bip";
	/** ConditionalOnProperty property name */
	public static final String CONDITIONAL_SPRING_REDIS = "spring.cache.type";
	/** The cache server type */
	public static final String CACHE_SERVER_TYPE = "redis";

	/** Cache properties derived from application properties file */
	@Autowired
	private BipRedisCacheProperties propertiesCache;

	/** Embedded Redis bean to make sure embedded redis is started before redis cache is created. */
	@SuppressWarnings("unused")
	@Autowired(required = false)
	private BipEmbeddedRedisServer referenceServerRedisEmbedded;
	
	@Autowired
    private BipRedisRefreshProperties redisRefreshProps;
	
	@Autowired
    private Environment environment;
	
	@Autowired
	private JedisConnectionFactory jedisConnectionFactory;

	/**
	 * Post construction validations.
	 */
	@PostConstruct
	public void postConstruct() {
		LOGGER.info("postConstruct invoked here");
		Defense.notNull(propertiesCache, BipRedisCacheProperties.class.getSimpleName() + " cannot be null.");
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
	
	//Create a new Bean definition for JedisConnectionFactory
	@Bean
	@RefreshScope
	public JedisConnectionFactory redisConnectionFactory() {
		LOGGER.info("redisConnectionFactory invoked here");
		LOGGER.info("RedisProperties: " + redisRefreshProps.toString());
		LOGGER.info("RedisProperties Host: " + redisRefreshProps.getHost());
		LOGGER.info("environment.getProperty(spring.redis.host)" + environment.getProperty("spring.redis.host"));
		LOGGER.info("propertiesCache.getDefaultExpires()" + propertiesCache.getDefaultExpires());
	    //Create the Builder for JedisClientConfiguration
	    JedisClientConfiguration.JedisClientConfigurationBuilder builder = JedisClientConfiguration
	            .builder();

	    if(redisRefreshProps.isSsl()) builder.useSsl();

	    //Final JedisClientConfiguration
	    JedisClientConfiguration clientConfig = builder.usePooling().build();

	    //TODO: Later: Add configurations for connection pool sizing.

	    //Create RedisStandAloneConfiguration
	    RedisStandaloneConfiguration redisConfig =
	            new RedisStandaloneConfiguration(redisRefreshProps.getHost(), redisRefreshProps.getPort());

	    //Create JedisConnectionFactory
	    JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisConfig, clientConfig);
	    return jedisConnectionFactory;
	}

	/**
	 * Create the default cache configuration, with TTL set as declared by {@code reference:cache:defaultExpires} in the
	 * <i>[project]/src/main/resources/[app].yml</i>. Used by {@link RedisCacheManager}.
	 *
	 * @return RedisCacheConfiguration
	 */
	@Bean
	public RedisCacheConfiguration redisCacheConfiguration() {
		LOGGER.info("redisCacheConfiguration invoked here");
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
		LOGGER.info("redisCacheConfigurations invoked here");
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
	@RefreshScope
	public CacheManager cacheManager(final RedisConnectionFactory redisConnectionFactory) {
		LOGGER.info("cacheManager invoked here");
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
		LOGGER.info("cacheInterceptor invoked here");
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
		LOGGER.info("keyGenerator invoked here");
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
	
	/**
	 * On application event.
	 *
	 * @param event the event
	 */
	@EventListener
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        LOGGER.info("Reconfiguring caches after refresh event");
        LOGGER.info("event.getName() {}", event.getName());
        LOGGER.info("event.getSource() {}", event.getSource());
        
        jedisConnectionFactory.destroy();
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
