package gov.va.ocp.framework.cache.autoconfigure;

import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * An auditing extension to the {@link RedisCacheManager}.
 *
 * @author aburkholder
 */
public class OcpRedisCacheManager extends RedisCacheManager {

	/* ------- Recreate the constructors from RedisCacheManager ------- */

	/**
	 * Creates new {@link RedisCacheManager} using given {@link RedisCacheWriter} and default
	 * {@link RedisCacheConfiguration}.
	 *
	 * @param cacheWriter - must not be {@literal null}.
	 * @param defaultCacheConfiguration - must not be {@literal null}. Maybe just use
	 *            {@link RedisCacheConfiguration#defaultCacheConfig()}.
	 */
	public OcpRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
		super(cacheWriter, defaultCacheConfiguration);
	}

	/**
	 * Creates new {@link RedisCacheManager} using given {@link RedisCacheWriter} and default
	 * {@link RedisCacheConfiguration}.
	 *
	 * @param cacheWriter - must not be {@literal null}.
	 * @param defaultCacheConfiguration - must not be {@literal null}. Maybe just use
	 *            {@link RedisCacheConfiguration#defaultCacheConfig()}.
	 * @param initialCacheNames - optional set of known cache names that will be created with given
	 *            {@literal defaultCacheConfiguration}.
	 */
	public OcpRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
			String... initialCacheNames) {
		super(cacheWriter, defaultCacheConfiguration, initialCacheNames);
	}

	/**
	 * Creates new {@link RedisCacheManager} using given {@link RedisCacheWriter} and default
	 * {@link RedisCacheConfiguration}.
	 *
	 * @param cacheWriter - must not be {@literal null}.
	 * @param defaultCacheConfiguration - must not be {@literal null}. Maybe just use
	 *            {@link RedisCacheConfiguration#defaultCacheConfig()}.
	 * @param initialCacheConfigurations - Map of known cache names along with the configuration to use for those caches. Must not be
	 *            {@literal null}.
	 */
	public OcpRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
			Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
		super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations);
	}

	/**
	 * Creates new {@link RedisCacheManager} using given {@link RedisCacheWriter} and default
	 * {@link RedisCacheConfiguration}.
	 *
	 * @param cacheWriter - must not be {@literal null}.
	 * @param defaultCacheConfiguration - must not be {@literal null}. Maybe just use
	 *            {@link RedisCacheConfiguration#defaultCacheConfig()}.
	 * @param allowInFlightCacheCreation - if set to {@literal true} no new caches can be acquire at runtime but limited to the given
	 *            list of initial cache names.
	 * @param initialCacheNames - optional set of known cache names that will be created with given
	 *            {@literal defaultCacheConfiguration}.
	 */
	public OcpRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
			boolean allowInFlightCacheCreation, String... initialCacheNames) {
		super(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation, initialCacheNames);
	}

	/**
	 * Creates new {@link RedisCacheManager} using given {@link RedisCacheWriter} and default
	 * {@link RedisCacheConfiguration}.
	 *
	 * @param cacheWriter - must not be {@literal null}.
	 * @param defaultCacheConfiguration - must not be {@literal null}. Maybe just use
	 *            {@link RedisCacheConfiguration#defaultCacheConfig()}.
	 * @param initialCacheConfigurations - Map of known cache names along with the configuration to use for those caches. Must not be
	 *            {@literal null}.
	 * @param allowInFlightCacheCreation - if set to {@literal false} this cache manager is limited to the initial cache configurations
	 *            and will not create new caches at runtime.
	 */
	public OcpRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
			Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
		super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
	}

	/* ------- Override methods to enable auditing ------- */

	@Override
	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		return cache;
	}
}
