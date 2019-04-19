package gov.va.bip.framework.cache.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

/**
 * Properties used to configure the Redis "Standalone" module, and the EmbeddedRedisServer.
 * <p>
 * For Redis Client configuration, see {@link BipRedisClientProperties}.<br/>
 * For Redis Cache configuration, see {@link BipRedisCacheProperties}.
 * <p>
 * The Application YAML (e.g. <tt>bip-<i>your-app-name</i>.yml</tt>) can
 * override property values by adding them to the {@code bip.framework:redis}
 * section:
 * <p>
 * <table border="1px">
 * <tr><th colspan="3">Properties under: {@code bip.framework:redis}</th></tr>
 * <tr><th>Property Name</th><th>Default Value</th><th>Type</th></tr>
 * <tr><td>host</td><td>localhost</td><td>String</td></tr>
 * <tr><td>port</td><td>6379</td><td>Integer</td></tr>
 * </table>
 *
 */
@ConfigurationProperties(prefix = "bip.framework.redis")
@Configuration
public class BipRedisProperties {

	static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipRedisProperties.class);

	/** Inner class for redis embedded server config properties */
	private RedisProps redisProps = new RedisProps();

	/**
	 * Set the {@link RedisProps} configuration object
	 *
	 * @param redisProps
	 */
	public void setRedisProps(final RedisProps redisProps) {
		this.redisProps = redisProps;
	}

	/**
	 * Get the {@link RedisProps} configuration object
	 *
	 * @return RedisProps
	 */
	public RedisProps getRedisProps() {
		return this.redisProps;
	}

	/**
	 * Inner class for a redis host and the port it runs on.
	 */
	public static class RedisProps {

		/** Redis Host */
		private String host = "localhost";

		/** Redis port */
		private Integer port = 6379;

		public void setHost(final String host) {
			this.host = host;
		}

		public String getHost() {
			return host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(final Integer port) {
			this.port = port;
		}

	}

}
