package gov.va.bip.framework.cache.server;

import java.util.ArrayList;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.bip.framework.cache.autoconfigure.BipRedisClientProperties;
import gov.va.bip.framework.cache.autoconfigure.BipRedisClientProperties.RedisConfig;
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
	public BipRedisClientProperties bipRedisClientProperties() {
		BipRedisClientProperties bipRedisClientProperties = new BipRedisClientProperties();
		bipRedisClientProperties.setRedisProps(new RedisProps());
		bipRedisClientProperties.getRedisProps().setHost("localhost");
		bipRedisClientProperties.setExpires(new ArrayList<>());
		bipRedisClientProperties.setDefaultExpires(500L);
		return bipRedisClientProperties;
	}
}