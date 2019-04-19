package gov.va.bip.framework.cache.autoconfigure;

import java.time.Duration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.pool2.impl.EvictionPolicy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * Properties to configure the Redis Client.
 * <p>
 * For Redis "Standalone" and EmbeddedRedisServer configuration see {@link BipRedisProperties}.<br/>
 * For Cache configuration, see {@link BipRedisCacheProperties}.
 * <p>
 * <p>
 * The Application YAML (e.g. <tt>bip-<i>your-app-name</i>.yml</tt>) <b>must</b>
 * declare the {@code bip.framework:redis:client:clientName} property.<br/>
 * Other properties can be overridden by adding them to the {@code bip.framework:redis}
 * section:
 * <p>
 * <table border="1px">
 * <tr><th colspan="3">Properties under: {@code bip.framework:redis:client}</th></tr>
 * <tr><th>Property Name</th><th>Default Value</th><th>Type</th></tr>
 * <tr><td>clientName</td><td>???</td><td>String</td></tr>
 * <tr><td>connectTimeout</td><td>2000</td><td>Duration.ofSeconds</td></tr>
 * <tr><td>readTimeout</td><td>2000</td><td>Duration.ofSeconds</td></tr>
 * <tr><td>usePooling</td><td>true</td><td>boolean</td></tr>
 * <tr><td>poolConfig</td><td>see topic below</td><td>JedisPoolProps</td></tr>
 * <tr><td>useSsl</td><td>true</td><td>boolean</td></tr>
 * <tr><td>sslProps</td><td>see topic below</td><td>SslProps</td></tr>
 * </table>
 * <p>
 * SSL properties default to the values described below, and cannot be
 * changed. When {@code useSsl} (described above) is set to {@code true} (the default value)
 * the default behavior is used as outlined in the table below. Options available
 * to the service application are to turn off SSL (set {@code useSsl} to {@code false},
 * or to override the {@link SSLSocketFactory}.
 * <table border="1">
 * <tr><th colspan="3">No YAML properties available</th></tr>
 * <tr><th>Name</th><th>Default Value</th><th>Type</th></tr>
 * <tr><td>sslSocketFactory</td><td>SSLSocketFactory.getDefault()</td><td>SSLSocketFactory</td></tr>
 * <tr><td>sslParameters</td><td>SSLContext.getDefaultSSLParameters()</td><td>SSLParameters</td></tr>
 * <tr><td>hostnameVerifier</td><td>NoopHostnameVerifier</td><td>HostnameVerifier</td></tr>
 * </table>
 * <p>
 * Redis connection pooling provides extensive properties for fine-tuning the behavior
 * of the pooled connections. Descriptions for these properties can be seen by following the
 * "see also" links for each property in the JavaDocs of {@link JedisPoolConfig}.<br/>
 * The Application YAML can override property values by
 * adding them to the {@code bip.framework:redis:client:poolConfig} section:
 * <table border="1">
 * <tr><th colspan="3">Properties under: {@code bip.framework:redis:client:poolConfig}</th></tr>
 * <tr><th>Property Name</th><th>Default Value</th><th>Type</th></tr>
 * <tr><td>maxTotal</td><td>8</td><td>int</td></tr>
 * <tr><td>maxIdle</td><td>8</td><td>int</td></tr>
 * <tr><td>maxWaitMillis</td><td>-1</td><td>long</td></tr>
 * <tr><td>minEvictableIdleTimeMillis</td><td>1800000</td><td>long</td></tr>
 * <tr><td>minIdle</td><td>0</td><td>int</td></tr>
 * <tr><td>blockWhenExhausted</td><td>true</td><td>boolean</td></tr>
 * <!-- tr><td>evictionPolicyClassName</td><td>org.apache.commons.pool2.impl.DefaultEvictionPolicy</td><td>String</td></tr -->
 * <!-- tr><td>evictionPolicy</td><td>DefaultEvictionPolicy</td><td>EvictionPolicy</td></tr -->
 * <tr><td>evictorShutdownTimeoutMillis</td><td>10000</td><td>long</td></tr>
 * <tr><td>fairness</td><td>false</td><td>boolean</td></tr>
 * <tr><td>jmxEnabled</td><td>true</td><td>boolean</td></tr>
 * <tr><td>jmxNameBase</td><td>gov.va.bip.redis</td><td>String</td></tr>
 * <tr><td>jmxNamePrefix</td><td>type=Support,name=redisOps</td><td>String</td></tr>
 * <tr><td>lifo</td><td>true</td><td>boolean</td></tr>
 * <tr><td>numTestsPerEvictionRun</td><td>3</td><td>int</td></tr>
 * <tr><td>softMinEvictableIdleTimeMillis</td><td>-1</td><td>long</td></tr>
 * <tr><td>testOnBorrow</td><td>false</td><td>boolean</td></tr>
 * <tr><td>testOnCreate</td><td>false</td><td>boolean</td></tr>
 * <tr><td>testOnReturn</td><td>false</td><td>boolean</td></tr>
 * <tr><td>testWhileIdle</td><td>false</td><td>boolean</td></tr>
 * </table>
 *
 * @see org.springframework.data.redis.connection.RedisStandaloneConfiguration
 * @see org.springframework.data.redis.connection.jedis.DefaultJedisClientConfiguration
 * @see redis.clients.jedis.JedisPoolConfig
 * @see org.apache.commons.pool2.impl.GenericObjectPoolConfig
 * @see org.apache.commons.pool2.impl.BaseObjectPoolConfig
 */
@ConfigurationProperties(prefix = "bip.framework.redis")
@Configuration
public class BipRedisClientProperties extends BipRedisProperties {
	static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipRedisClientProperties.class);

	/** Domain part of object name for JMX bean */
	private static final String OBJECT_NAME_DOMAIN = "gov.va.bip.redis";
	/** Properties part of object name for JMX bean */
	private static final String OBJECT_NAME_PROPERTIES = "type=Support,name=redisOps";

	// TODO is there some way to get the app name from the *static* inner class?
//	@Autowired
//	private BuildProperties buildProperties;

	private JedisClientProps jedisClientProps = new JedisClientProps();

	/**
	 * JedisClientProps for {@link JedisClientConfiguration} in {@link BipCacheAutoConfiguration}.
	 *
	 * @return the jedisClientProps
	 */
	public JedisClientProps getJedisClientProps() {
		return jedisClientProps;
	}

	/**
	 * JedisClientProps for {@link JedisClientConfiguration} in {@link BipCacheAutoConfiguration}.
	 *
	 * @param jedisClientProps the jedisClientProps to set
	 */
	public void setJedisClientProps(JedisClientProps jedisClientProps) {
		this.jedisClientProps = jedisClientProps;
	}

	/**
	 * Inner class for JedisClientConfiguration properties.
	 *
	 * @author aburkholder
	 */
	public static class JedisClientProps {
		String clientName; // TODO cannot access Autowired from static class: buildProperties.getArtifact();
		Duration connectTimeout = Duration.ofSeconds(Protocol.DEFAULT_TIMEOUT);
		Duration readTimeout = Duration.ofSeconds(Protocol.DEFAULT_TIMEOUT);
		boolean usePooling = true;
		private JedisPoolProps poolConfig = new JedisPoolProps();
		boolean useSsl = true;
		private SslProps sslProps = new SslProps();

		/**
		 * The Jedis connection pool configuration properties.
		 *
		 * @return the poolConfig
		 */
		public JedisPoolProps getPoolConfig() {
			return poolConfig;
		}

		/**
		 * The Jedis connection pool configuration properties.
		 *
		 * @param poolConfig the poolConfig to set
		 */
		public void setPoolConfig(JedisPoolProps poolConfig) {
			this.poolConfig = poolConfig;
		}

		/**
		 * The SSL configuration properties.
		 *
		 * @return the sslProps
		 */
		public SslProps getSslProps() {
			return sslProps;
		}

		/**
		 * The SSL configuration properties.
		 *
		 * @param sslProps the sslProps to set
		 */
		public void setSslProps(SslProps sslProps) {
			this.sslProps = sslProps;
		}

	}

	/**
	 * Inner class for JedisPoolConfiguration properties.
	 *
	 * @author aburkholder
	 */
	public static class JedisPoolProps {
		int maxTotal = JedisPoolConfig.DEFAULT_MAX_TOTAL;
		int maxIdle = JedisPoolConfig.DEFAULT_MAX_IDLE;
		long maxWaitMillis = JedisPoolConfig.DEFAULT_MAX_WAIT_MILLIS;
		long minEvictableIdleTimeMillis = JedisPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
		int minIdle = JedisPoolConfig.DEFAULT_MIN_IDLE;
		boolean blockWhenExhausted = JedisPoolConfig.DEFAULT_BLOCK_WHEN_EXHAUSTED;
		String evictionPolicyClassName = JedisPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME;
		EvictionPolicy<?> evictionPolicy;// TODO
		long evictorShutdownTimeoutMillis = JedisPoolConfig.DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS;
		boolean fairness = JedisPoolConfig.DEFAULT_FAIRNESS;
		boolean jmxEnabled = JedisPoolConfig.DEFAULT_JMX_ENABLE;
		String jmxNameBase = OBJECT_NAME_DOMAIN;
		String jmxNamePrefix = OBJECT_NAME_PROPERTIES;
		boolean lifo = JedisPoolConfig.DEFAULT_LIFO;
		int numTestsPerEvictionRun = JedisPoolConfig.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
		long softMinEvictableIdleTimeMillis = JedisPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
		boolean testOnBorrow = JedisPoolConfig.DEFAULT_TEST_ON_BORROW;
		boolean testOnCreate = JedisPoolConfig.DEFAULT_TEST_ON_CREATE;
		boolean testOnReturn = JedisPoolConfig.DEFAULT_TEST_ON_RETURN;
		boolean testWhileIdle = JedisPoolConfig.DEFAULT_TEST_WHILE_IDLE;
		long timeBetweenEvictionRunsMillis = JedisPoolConfig.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
	}

	public static class SslProps {
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		// SSLParameters sslParameters; //TODO I think it will use SSLContext.getDefaultSSLParameters()
	}
}
