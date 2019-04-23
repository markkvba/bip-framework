package gov.va.bip.framework.cache.autoconfigure.properties;

import java.time.Duration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.pool2.impl.EvictionPolicy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;

import gov.va.bip.framework.cache.autoconfigure.BipCacheAutoConfiguration;
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
 * declare the {@code bip.framework:redis:client:clientname} property.<br/>
 * Other properties can be overridden by adding them to the {@code bip.framework:redis}
 * section:
 * <p>
 * <table border="1px">
 * <tr><th colspan="3">Properties under: {@code bip.framework:redis:client}</th></tr>
 * <tr><th>Property Name</th><th>Default Value</th><th>Type</th></tr>
 * <tr><td>clientname</td><td>null - MUST BE SET</td><td>String</td></tr>
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
@ConfigurationProperties(prefix = "bip.framework.redis.client", ignoreInvalidFields = false, ignoreUnknownFields = false)
@Configuration("bipRedisClientProperties")
public class BipRedisClientProperties {
	static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipRedisClientProperties.class);

	/** Domain part of object name for JMX bean */
	private static final String OBJECT_NAME_DOMAIN = "gov.va.bip.redis";
	/** Properties part of object name for JMX bean */
	private static final String OBJECT_NAME_PROPERTIES = "type=Support,name=redisOps";

	// TODO is there some way to get the app name from the *static* inner class?
//	@Autowired
//	private BuildProperties buildProperties;

	private JedisClientProps client = new JedisClientProps();
	String clientname; // TODO cannot access Autowired from static class: buildProperties.getArtifact();

	/**
	 * The unique client name for the Standalone redis client module.
	 *
	 * @return client name
	 */
	public String getClientname() {
		return clientname;
	}

	/**
	 * The unique client name for the Standalone redis client module.
	 *
	 * @param client name
	 */
	public void setClientname(String clientname) {
		this.clientname = clientname;
	}

	/**
	 * JedisClientProps for {@link JedisClientConfiguration} in {@link BipCacheAutoConfiguration}.
	 *
	 * @return the client
	 */
	public JedisClientProps getJedisClientProps() {
		return client;
	}

	/**
	 * JedisClientProps for {@link JedisClientConfiguration} in {@link BipCacheAutoConfiguration}.
	 *
	 * @param client the client props
	 */
	public void setJedisClientProps(JedisClientProps client) {
		this.client = client;
	}

	/**
	 * Inner class for JedisClientConfiguration properties.
	 *
	 * @author aburkholder
	 */
	public static class JedisClientProps {
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

		/**
		 * @return the connectTimeout
		 */
		public Duration getConnectTimeout() {
			return connectTimeout;
		}

		/**
		 * @param connectTimeout the connectTimeout to set
		 */
		public void setConnectTimeout(Duration connectTimeout) {
			this.connectTimeout = connectTimeout;
		}

		/**
		 * @return the readTimeout
		 */
		public Duration getReadTimeout() {
			return readTimeout;
		}

		/**
		 * @param readTimeout the readTimeout to set
		 */
		public void setReadTimeout(Duration readTimeout) {
			this.readTimeout = readTimeout;
		}

		/**
		 * @return the usePooling
		 */
		public boolean isUsePooling() {
			return usePooling;
		}

		/**
		 * @param usePooling the usePooling to set
		 */
		public void setUsePooling(boolean usePooling) {
			this.usePooling = usePooling;
		}

		/**
		 * @return the useSsl
		 */
		public boolean isUseSsl() {
			return useSsl;
		}

		/**
		 * @param useSsl the useSsl to set
		 */
		public void setUseSsl(boolean useSsl) {
			this.useSsl = useSsl;
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

		/**
		 * @return the maxTotal
		 */
		public int getMaxTotal() {
			return maxTotal;
		}

		/**
		 * @param maxTotal the maxTotal to set
		 */
		public void setMaxTotal(int maxTotal) {
			this.maxTotal = maxTotal;
		}

		/**
		 * @return the maxIdle
		 */
		public int getMaxIdle() {
			return maxIdle;
		}

		/**
		 * @param maxIdle the maxIdle to set
		 */
		public void setMaxIdle(int maxIdle) {
			this.maxIdle = maxIdle;
		}

		/**
		 * @return the maxWaitMillis
		 */
		public long getMaxWaitMillis() {
			return maxWaitMillis;
		}

		/**
		 * @param maxWaitMillis the maxWaitMillis to set
		 */
		public void setMaxWaitMillis(long maxWaitMillis) {
			this.maxWaitMillis = maxWaitMillis;
		}

		/**
		 * @return the minEvictableIdleTimeMillis
		 */
		public long getMinEvictableIdleTimeMillis() {
			return minEvictableIdleTimeMillis;
		}

		/**
		 * @param minEvictableIdleTimeMillis the minEvictableIdleTimeMillis to set
		 */
		public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
			this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
		}

		/**
		 * @return the minIdle
		 */
		public int getMinIdle() {
			return minIdle;
		}

		/**
		 * @param minIdle the minIdle to set
		 */
		public void setMinIdle(int minIdle) {
			this.minIdle = minIdle;
		}

		/**
		 * @return the blockWhenExhausted
		 */
		public boolean isBlockWhenExhausted() {
			return blockWhenExhausted;
		}

		/**
		 * @param blockWhenExhausted the blockWhenExhausted to set
		 */
		public void setBlockWhenExhausted(boolean blockWhenExhausted) {
			this.blockWhenExhausted = blockWhenExhausted;
		}

		/**
		 * @return the evictionPolicyClassName
		 */
		public String getEvictionPolicyClassName() {
			return evictionPolicyClassName;
		}

		/**
		 * @param evictionPolicyClassName the evictionPolicyClassName to set
		 */
		public void setEvictionPolicyClassName(String evictionPolicyClassName) {
			this.evictionPolicyClassName = evictionPolicyClassName;
		}

		/**
		 * @return the evictionPolicy
		 */
		public EvictionPolicy<?> getEvictionPolicy() {
			return evictionPolicy;
		}

		/**
		 * @param evictionPolicy the evictionPolicy to set
		 */
		public void setEvictionPolicy(EvictionPolicy<?> evictionPolicy) {
			this.evictionPolicy = evictionPolicy;
		}

		/**
		 * @return the evictorShutdownTimeoutMillis
		 */
		public long getEvictorShutdownTimeoutMillis() {
			return evictorShutdownTimeoutMillis;
		}

		/**
		 * @param evictorShutdownTimeoutMillis the evictorShutdownTimeoutMillis to set
		 */
		public void setEvictorShutdownTimeoutMillis(long evictorShutdownTimeoutMillis) {
			this.evictorShutdownTimeoutMillis = evictorShutdownTimeoutMillis;
		}

		/**
		 * @return the fairness
		 */
		public boolean isFairness() {
			return fairness;
		}

		/**
		 * @param fairness the fairness to set
		 */
		public void setFairness(boolean fairness) {
			this.fairness = fairness;
		}

		/**
		 * @return the jmxEnabled
		 */
		public boolean isJmxEnabled() {
			return jmxEnabled;
		}

		/**
		 * @param jmxEnabled the jmxEnabled to set
		 */
		public void setJmxEnabled(boolean jmxEnabled) {
			this.jmxEnabled = jmxEnabled;
		}

		/**
		 * @return the jmxNameBase
		 */
		public String getJmxNameBase() {
			return jmxNameBase;
		}

		/**
		 * @param jmxNameBase the jmxNameBase to set
		 */
		public void setJmxNameBase(String jmxNameBase) {
			this.jmxNameBase = jmxNameBase;
		}

		/**
		 * @return the jmxNamePrefix
		 */
		public String getJmxNamePrefix() {
			return jmxNamePrefix;
		}

		/**
		 * @param jmxNamePrefix the jmxNamePrefix to set
		 */
		public void setJmxNamePrefix(String jmxNamePrefix) {
			this.jmxNamePrefix = jmxNamePrefix;
		}

		/**
		 * @return the lifo
		 */
		public boolean isLifo() {
			return lifo;
		}

		/**
		 * @param lifo the lifo to set
		 */
		public void setLifo(boolean lifo) {
			this.lifo = lifo;
		}

		/**
		 * @return the numTestsPerEvictionRun
		 */
		public int getNumTestsPerEvictionRun() {
			return numTestsPerEvictionRun;
		}

		/**
		 * @param numTestsPerEvictionRun the numTestsPerEvictionRun to set
		 */
		public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
			this.numTestsPerEvictionRun = numTestsPerEvictionRun;
		}

		/**
		 * @return the softMinEvictableIdleTimeMillis
		 */
		public long getSoftMinEvictableIdleTimeMillis() {
			return softMinEvictableIdleTimeMillis;
		}

		/**
		 * @param softMinEvictableIdleTimeMillis the softMinEvictableIdleTimeMillis to set
		 */
		public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
			this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
		}

		/**
		 * @return the testOnBorrow
		 */
		public boolean isTestOnBorrow() {
			return testOnBorrow;
		}

		/**
		 * @param testOnBorrow the testOnBorrow to set
		 */
		public void setTestOnBorrow(boolean testOnBorrow) {
			this.testOnBorrow = testOnBorrow;
		}

		/**
		 * @return the testOnCreate
		 */
		public boolean isTestOnCreate() {
			return testOnCreate;
		}

		/**
		 * @param testOnCreate the testOnCreate to set
		 */
		public void setTestOnCreate(boolean testOnCreate) {
			this.testOnCreate = testOnCreate;
		}

		/**
		 * @return the testOnReturn
		 */
		public boolean isTestOnReturn() {
			return testOnReturn;
		}

		/**
		 * @param testOnReturn the testOnReturn to set
		 */
		public void setTestOnReturn(boolean testOnReturn) {
			this.testOnReturn = testOnReturn;
		}

		/**
		 * @return the testWhileIdle
		 */
		public boolean isTestWhileIdle() {
			return testWhileIdle;
		}

		/**
		 * @param testWhileIdle the testWhileIdle to set
		 */
		public void setTestWhileIdle(boolean testWhileIdle) {
			this.testWhileIdle = testWhileIdle;
		}

		/**
		 * @return the timeBetweenEvictionRunsMillis
		 */
		public long getTimeBetweenEvictionRunsMillis() {
			return timeBetweenEvictionRunsMillis;
		}

		/**
		 * @param timeBetweenEvictionRunsMillis the timeBetweenEvictionRunsMillis to set
		 */
		public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
			this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
		}
	}

	public static class SslProps {
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLParameters sslParameters; // TODO I think it will use SSLContext.getDefaultSSLParameters()

		/**
		 * @return the hostnameVerifier
		 */
		public HostnameVerifier getHostnameVerifier() {
			return hostnameVerifier;
		}

		/**
		 * @param hostnameVerifier the hostnameVerifier to set
		 */
		public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
			this.hostnameVerifier = hostnameVerifier;
		}

		/**
		 * @return the sslSocketFactory
		 */
		public SSLSocketFactory getSslSocketFactory() {
			return sslSocketFactory;
		}

		/**
		 * @param sslSocketFactory the sslSocketFactory to set
		 */
		public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
			this.sslSocketFactory = sslSocketFactory;
		}

		/**
		 * @return the sslParameters
		 */
		public SSLParameters getSslParameters() {
			return sslParameters;
		}

		/**
		 * @param sslParameters the sslParameters to set
		 */
		public void setSslParameters(SSLParameters sslParameters) {
			this.sslParameters = sslParameters;
		}
	}
}
