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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.CollectionUtils;

import gov.va.bip.framework.cache.autoconfigure.BipRedisCacheProperties.RedisExpires;
import gov.va.bip.framework.cache.interceptor.BipCacheInterceptor;
import gov.va.bip.framework.log.BipBanner;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.validation.Defense;

@Configuration
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableConfigurationProperties({ BipRedisCacheProperties.class })
public class BipCachesConfig extends CachingConfigurerSupport {
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipCachesConfig.class);

	/** Cache properties derived from application YAML */
	@Autowired // TODO
	private BipRedisCacheProperties bipRedisCacheProperties;

	/**
	 * Post construction validations.
	 */
	@PostConstruct
	public void postConstruct() {
		Defense.notNull(bipRedisCacheProperties, BipRedisCacheProperties.class.getSimpleName() + " cannot be null.");
	}

	/**
	 * Create the default cache configuration, with TTL set as declared by {@code reference:cache:defaultExpires} in the
	 * <i>[project]/src/main/resources/[app].yml</i>. Used by {@link RedisCacheManager}.
	 *
	 * @return RedisCacheConfiguration
	 */
	@Bean
	public RedisCacheConfiguration redisCacheConfiguration() {
		LOGGER.debug("redisCacheConfiguration invoked here");
		return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(bipRedisCacheProperties.getDefaultExpires()));
	}

	/**
	 * Produce the Map of {@link RedisCacheConfiguration} objects derived from the list declared by
	 * {@code reference:cache:expires:*}
	 * in the <i>[app].yml</i>. Used by {@link RedisCacheManager}.
	 *
	 * @return Map&lt;String, org.springframework.data.redis.cache.RedisCacheConfiguration&gt;
	 */
	@Bean
	public Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> redisCacheConfigurations() {
		LOGGER.debug("redisCacheConfigurations invoked here");
		Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> cacheConfigs = new HashMap<>();

		if (!CollectionUtils.isEmpty(bipRedisCacheProperties.getExpires())) {
			// key = name, value - TTL
			final Map<String, Long> resultExpires = bipRedisCacheProperties.getExpires().stream().filter(o -> o.getName() != null)
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
	 * Create the cacheManager bean, configured by the redisCacheConfiguration bean.
	 *
	 * @param redisConnectionFactory
	 * @return CacheManager
	 */
	@Bean
	@RefreshScope
	public CacheManager cacheManager(final RedisConnectionFactory redisConnectionFactory) {
		LOGGER.debug("cacheManager invoked here");
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
		LOGGER.debug("cacheInterceptor invoked here");
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
		public void handleCachePutError(final RuntimeException exception, final Cache cache, final Object key,
				final Object value) {
			LOGGER.error(BipBanner.newBanner("Unable to put into cache " + cache.getName(), Level.ERROR), exception.getMessage());
		}

		@Override
		public void handleCacheEvictError(final RuntimeException exception, final Cache cache, final Object key) {
			LOGGER.error(BipBanner.newBanner("Unable to evict from cache " + cache.getName(), Level.ERROR),
					exception.getMessage());
		}

		@Override
		public void handleCacheClearError(final RuntimeException exception, final Cache cache) {
			LOGGER.error(BipBanner.newBanner("Unable to clean cache " + cache.getName(), Level.ERROR), exception.getMessage());
		}
	}
}
