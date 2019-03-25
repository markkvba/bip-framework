package gov.va.ocp.framework.cache.autoconfigure;

import java.util.concurrent.Callable;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.Auditable;

/**
 * Add Auditability to {@link RedisCache} gets and puts.
 * 
 * @author aburkholder
 */
public class OcpRedisCache extends RedisCache {

	/**
	 * Create new {@link RedisCache}.
	 *
	 * @param name must not be {@literal null}.
	 * @param cacheWriter must not be {@literal null}.
	 * @param cacheConfig must not be {@literal null}.
	 */
	public OcpRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
		super(name, cacheWriter, cacheConfig);
	}

	/**
	 * Audit all data being pulled out of the cache as a response to a previoiusly logged request.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	@Auditable(event = AuditEvents.CACHED_SERVICE_RESPONSE, activity = "")
	public synchronized <T> T get(Object key, Callable<T> valueLoader) {
		return super.get(key, valueLoader);
	}
}
