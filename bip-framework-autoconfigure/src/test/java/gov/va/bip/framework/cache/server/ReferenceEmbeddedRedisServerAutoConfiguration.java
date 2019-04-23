package gov.va.bip.framework.cache.server;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.bip.framework.cache.autoconfigure.properties.BipRedisCacheProperties;
import gov.va.bip.framework.cache.autoconfigure.properties.BipRedisClientProperties;
import gov.va.bip.framework.cache.autoconfigure.properties.BipRedisProperties;
import gov.va.bip.framework.cache.autoconfigure.server.BipEmbeddedRedisServer;

/**
 *
 * @author rthota
 *
 */
@Configuration
public class ReferenceEmbeddedRedisServerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public BipEmbeddedRedisServer bipEmbeddedRedisServer() {
		return new BipEmbeddedRedisServer();
	}

	@Bean
	@ConditionalOnMissingBean
	public BipRedisProperties bipRedisProperties() {
		return new BipRedisProperties();
	}

	@Bean
	@ConditionalOnMissingBean
	public BipRedisClientProperties bipRedisClientProperties() {
		BipRedisClientProperties bipRedisClientProperties = new BipRedisClientProperties();
		bipRedisClientProperties.setClientname("test-client-name");
		return bipRedisClientProperties;
	}

	@Bean
	@ConditionalOnMissingBean
	public BipRedisCacheProperties bipRedisCacheProperties() {
		return new BipRedisCacheProperties();
	}
}