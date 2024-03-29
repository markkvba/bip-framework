package gov.va.bip.framework.cache.autoconfigure.jmx;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import redis.clients.jedis.JedisPoolConfig;

/**
 * A JMX MBean implementation for operations on the current spring cache context.
 *
 * @author aburkholder
 */
@Component
@ManagedResource(objectName = BipCacheOpsImpl.OBJECT_NAME,
description = "A JMX MBean implementation for operations on the current spring cache context.")
public class BipCacheOpsImpl implements BipCacheOpsMBean {
	/** Class logger */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipCacheOpsImpl.class);

	/** Prefix for log statements */
	private static final String PREFIX = ":::: ";
	/** String replacement for null */
	private static final String NULL = "null";

	/** Domain part of object name for JMX bean */
	private static final String OBJECT_NAME_DOMAIN = "gov.va.bip.cache";
	/** Properties part of object name for JMX bean */
	private static final String OBJECT_NAME_PROPERTIES = "type=Support,name=cacheOps";
	/** The object name for JMX bean */
	static final String OBJECT_NAME = OBJECT_NAME_DOMAIN + ":" + OBJECT_NAME_PROPERTIES;

	/** The configured spring cache manager */
	@Autowired
	private RedisCacheManager cacheManager;

	/** The configured spring cache manager */
	@Autowired
	private JedisConnectionFactory redisConnectionFactory;

	/** Build properties to get app name */
	@Autowired
	BuildProperties buildProperties;

	/** The default cache configuration properties bean */
	@Autowired
	private RedisCacheConfiguration redisCacheConfiguration;

	/** The configuration beans for each individual cache */
	@Autowired
	private Map<String, RedisCacheConfiguration> redisCacheConfigurations;

	/**
	 * Instantiate this class.
	 */
	public BipCacheOpsImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.bip.framework.cache.autoconfigure.BipCacheOpsMBean#clearAllCaches()
	 */
	@ManagedOperation(description = "Clear all cache entries known to the spring cache manager.")
	@Override
	public void clearAllCaches() {
		if (cacheManager != null) {
			cacheManager.getCacheNames().parallelStream()
			.forEach(name -> cacheManager.getCache(name).clear());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsMBean#viewCacheConfigs()
	 */
	@ManagedOperation(
			description = "INFO logging of default and individual cache configurations from the current spring source (e.g. application yaml).")
	@Override
	public void logCacheConfigProperties() {
		LOGGER.info(PREFIX + "Cache Configs in '" + (buildProperties == null ? NULL : buildProperties.getName()) + "'");
		LOGGER.info(PREFIX + "Config for Default: [TTL=" + (redisCacheConfiguration.getTtl() == null
				? NULL
						: redisCacheConfiguration.getTtl().toMillis())
				+ ";UsePrefix=" + redisCacheConfiguration.usePrefix()
				+ ";AllowCacheNullValues=" + redisCacheConfiguration.getAllowCacheNullValues()
				+ ";ConversionService=" + (redisCacheConfiguration.getConversionService() == null
				? NULL
						: redisCacheConfiguration.getConversionService().getClass().getName()));
		logRedisCacheConfig();
		if ((cacheManager == null) || (cacheManager.getCacheNames() == null)) {
			LOGGER.info(PREFIX + "Cache names in CacheManager: null");
		} else if (cacheManager.getCacheNames().isEmpty()) {
			LOGGER.info(PREFIX + "Cache names in CacheManager: empty");
		} else {
			LOGGER.info(PREFIX + "Cache names in CacheManager: ["
					+ String.join(", ", cacheManager.getCacheNames()) + "]");
		}
	}

	private void logRedisCacheConfig() {
		if (redisCacheConfigurations == null) {
			LOGGER.info(PREFIX + "Config for !null cunfigurations list!");
		} else if (redisCacheConfigurations.isEmpty()) {
			LOGGER.info(PREFIX + "Config for !empty configurations list!");
		} else {
			for (Map.Entry<String, RedisCacheConfiguration> entry : redisCacheConfigurations.entrySet()) {
				RedisCacheConfiguration config = entry.getValue();
				String msg = PREFIX + "Config for [key=" + entry.getKey() + "[KeyPrefix=" + config.getKeyPrefixFor(entry.getKey())
				+ ";TTL=" + (config.getTtl() == null ? NULL : config.getTtl().toMillis())
				+ ";UsePrefix=" + config.usePrefix()
				+ ";AllowCacheNullValues=" + config.getAllowCacheNullValues()
				+ ";ConversionService=" + (config.getConversionService() == null
				? NULL
						: config.getConversionService().getClass().getName())
				+ "]]; ";
				LOGGER.info(msg);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsMBean#logCurrentJedisConnectionFactoryFields()
	 */
	@Override
	@ManagedOperation(
			description = "INFO logging of the JedisConnectionFactory field values from the current bean in the application context.")
	public void logCurrentJedisConnectionFactoryFields() {

		LOGGER.info(PREFIX + "JedisClientConfiguration = " + redisConnectionFactory);
		if (redisConnectionFactory == null) {
			return;
		}

		try {
			LOGGER.info(PREFIX + "    clientName = " + redisConnectionFactory.getClientName());
			LOGGER.info(PREFIX + "    Database = " + redisConnectionFactory.getDatabase());
			LOGGER.info(PREFIX + "    hostName = " + redisConnectionFactory.getHostName());
			LOGGER.info(PREFIX + "    password = *****");
			LOGGER.info(PREFIX + "    port = " + redisConnectionFactory.getPort());
			LOGGER.info(PREFIX + "    timeout = " + redisConnectionFactory.getTimeout());
			LOGGER.info(PREFIX + "    usePool = " + redisConnectionFactory.getUsePool());
			LOGGER.info(PREFIX + "    useSsl = " + redisConnectionFactory.isUseSsl());
			LOGGER.info(PREFIX + "    clusterAware = " + redisConnectionFactory.isRedisClusterAware());
			LOGGER.info(PREFIX + "    sentinelAware = " + redisConnectionFactory.isRedisSentinelAware());

			RedisStandaloneConfiguration rsc = redisConnectionFactory.getStandaloneConfiguration();
			LOGGER.info(PREFIX + "GenericObjectPoolConfig = " + rsc);
			if (rsc != null) {
				LOGGER.info(PREFIX + "    database = " + rsc.getDatabase());
				LOGGER.info(PREFIX + "    hostName = " + rsc.getHostName());
				LOGGER.info(PREFIX + "    port = " + rsc.getPort());
				LOGGER.info(PREFIX + "    password = *****");
			}

			JedisClientConfiguration jcc = redisConnectionFactory.getClientConfiguration();
			LOGGER.info(PREFIX + "JedisClientConfiguration = " + jcc);
			if (jcc != null) {
				LOGGER.info(PREFIX + "    clientName = " + jcc.getClientName());
				LOGGER.info(PREFIX + "    connectTimeout = " + jcc.getConnectTimeout().toMillis());
				LOGGER.info(PREFIX + "    hostnameVerifier = " + jcc.getHostnameVerifier());
				LOGGER.info(PREFIX + "    poolConfig = " + (jcc.getPoolConfig().isPresent() ? jcc.getPoolConfig().getClass() : NULL));
				LOGGER.info(PREFIX + "    readTimeout = " + jcc.getReadTimeout().toMillis());
			}

			@SuppressWarnings("unchecked")
			GenericObjectPoolConfig<JedisPoolConfig> gopc = redisConnectionFactory.getPoolConfig();
			LOGGER.info(PREFIX + "GenericObjectPoolConfig = " + (gopc == null ? NULL : gopc.getClass()));
			if (gopc != null) {
				LOGGER.info(PREFIX + "    evictionPolicyClassName = " + gopc.getEvictionPolicyClassName());
				LOGGER.info(PREFIX + "    evictorShutdownTimeoutMillis = " + gopc.getEvictorShutdownTimeoutMillis());
				LOGGER.info(PREFIX + "    maxIdle = " + gopc.getMaxIdle());
				LOGGER.info(PREFIX + "    maxTotal = " + gopc.getMaxTotal());
				LOGGER.info(PREFIX + "    maxWaitMillis = " + gopc.getMaxWaitMillis());
				LOGGER.info(PREFIX + "    minEvictableIdleTimeMillis = " + gopc.getMinEvictableIdleTimeMillis());
				LOGGER.info(PREFIX + "    minIdle = " + gopc.getMinIdle());
				LOGGER.info(PREFIX + "    numTestsPerEvictionRun = " + gopc.getNumTestsPerEvictionRun());
				LOGGER.info(PREFIX + "    softMinEvictableIdleTimeMillis = " + gopc.getSoftMinEvictableIdleTimeMillis());
				LOGGER.info(PREFIX + "    timeBetweenEvictionRunsMillis = " + gopc.getTimeBetweenEvictionRunsMillis());
				LOGGER.info(PREFIX + "    blockWhenExhausted = " + gopc.getBlockWhenExhausted());
				LOGGER.info(PREFIX + "    fairness = " + gopc.getFairness());
				LOGGER.info(PREFIX + "    lifo = " + gopc.getLifo());
				LOGGER.info(PREFIX + "    testOnBorrow = " + gopc.getTestOnBorrow());
				LOGGER.info(PREFIX + "    testOnCreate = " + gopc.getTestOnCreate());
				LOGGER.info(PREFIX + "    testOnReturn = " + gopc.getTestOnReturn());
				LOGGER.info(PREFIX + "    testWhileIdle = " + gopc.getTestWhileIdle());
			}
		} catch (Exception e) {
			LOGGER.error("While logging JedisConnectionFactory field values", e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsMBean#logCurrentCacheManagerFields()
	 */
	@ManagedOperation(description = "INFO logs of the CacheManager field values from the current bean in the application context.")
	@Override
	public void logCurrentCacheManagerFields() {
		LOGGER.info(PREFIX + "RedisCacheManager = " + cacheManager);
		if (cacheManager == null) {
			return;
		}

		try {
			LOGGER.info(PREFIX + "RedisCacheConfiguration (immutable) = " + redisCacheConfiguration);
			if (redisCacheConfiguration != null) {
				LOGGER.info(PREFIX + "    allowCacheNullValues = " + redisCacheConfiguration.getAllowCacheNullValues());
				LOGGER.info(PREFIX + "    keySerializationPair = " + (redisCacheConfiguration.getKeySerializationPair() == null
						? NULL
								: redisCacheConfiguration.getKeySerializationPair().getClass()));
				LOGGER.info(PREFIX + "    valueSerializationPair = "
						+ (redisCacheConfiguration.getValueSerializationPair() == null
						? NULL
								: redisCacheConfiguration.getValueSerializationPair().getClass()));
				LOGGER.info(PREFIX + "    conversionService = " + (redisCacheConfiguration.getConversionService() == null
						? NULL
								: redisCacheConfiguration.getConversionService().getClass()));
				LOGGER.info(PREFIX + "    usePrefix = " + redisCacheConfiguration.usePrefix());
			}

			Collection<String> cacheNames = cacheManager.getCacheNames();
			LOGGER.info(PREFIX + "Caches = " + (cacheNames == null ? "null" : cacheNames.getClass()));
			logInfoForAllCacheNames(cacheNames);
		} catch (Exception e) {
			LOGGER.error("While logging CacheManager field values", e);
		}
	}

	private void logInfoForAllCacheNames(final Collection<String> cacheNames) {
		if (cacheNames != null) {
			for (String name : cacheNames) {
				LOGGER.info(PREFIX + "    cacheName = " + name);
				RedisCache cache = (cacheManager.isTransactionAware()
						? (RedisCache) ((TransactionAwareCacheDecorator) cacheManager.getCache(name)).getTargetCache()
								: (RedisCache) cacheManager.getCache(name));
				RedisCacheConfiguration config = cache.getCacheConfiguration();
				LOGGER.info(PREFIX + "        allowCacheNullValues = " + config.getAllowCacheNullValues());
				LOGGER.info(PREFIX + "        ttl = " + config.getTtl().toMillis());
				LOGGER.info(PREFIX + "        keySerializationPair = " + (config.getKeySerializationPair() == null
						? NULL
								: config.getKeySerializationPair().getClass()));
				LOGGER.info(PREFIX + "        valueSerializationPair = " + (config.getValueSerializationPair() == null
						? NULL
								: config.getValueSerializationPair().getClass()));
				LOGGER.info(PREFIX + "        conversionService = " + (config.getConversionService() == null
						? NULL
								: config.getConversionService().getClass()));
				LOGGER.info(PREFIX + "        usePrefix = " + config.usePrefix());
			}
		}
	}
}
