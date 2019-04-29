package gov.va.bip.framework.cache.autoconfigure;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

/**
 * Properties used to configure the Redis Reloadable properties.
 *
 * Properties used to configure the Redis "Standalone" module, and the EmbeddedRedisServer.
 * <p>
 * For Redis "Standalone" and EmbeddedRedisServer configuration see {@link BipRedisProperties}.<br/>
 * For Redis Client configuration, see {@link BipRedisClientProperties}.<br/>
 * <p>
 * The Application YAML (e.g. <tt>bip-<i>your-app-name</i>.yml</tt>) can
 * override property values by adding them to the {@code bip.framework:redis:cache}
 * section:
 * <p>
 * <table border="1px">
 * <tr><th colspan="3">Properties under: {@code bip.framework:redis:cache}</th></tr>
 * <tr><th>Property Name</th><th>Default Value</th><th>Type</th></tr>
 * <tr><td>defaultExpires</td><td>86400</td><td>Long</td></tr>
 * <tr><td>expires</td><td>null</td><td>List&lt;RedisExpires&gt;</td></tr>
 * </table>
 * <p>
 * The {@link RedisExpires} list is populated from list entries in the application yaml
 * under {@code bip.framework:redis:cache:expires}.
 *
 */
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
@RefreshScope
public class BipRedisRefreshProperties {

	static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipRedisRefreshProperties.class);

	/**
	 * Redis server host.
	 */
	private String host;

	/**
	 * Redis server port.
	 */
	private int port;
	
	/**
	 * Whether to enable SSL support.
	 */
	private boolean ssl;
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the ssl
	 */
	public boolean isSsl() {
		return ssl;
	}

	/**
	 * @param ssl the ssl to set
	 */
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
}
