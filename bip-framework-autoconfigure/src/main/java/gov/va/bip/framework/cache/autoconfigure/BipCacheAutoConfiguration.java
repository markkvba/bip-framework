package gov.va.bip.framework.cache.autoconfigure;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

import gov.va.bip.framework.audit.AuditLogSerializer;
import gov.va.bip.framework.audit.BaseAsyncAudit;
import gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsImpl;
import gov.va.bip.framework.cache.autoconfigure.jmx.BipCacheOpsMBean;
import gov.va.bip.framework.cache.autoconfigure.server.BipEmbeddedRedisServer;
import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;

/**
 * Cache auto configuration:
 * <ul>
 * <li> Configure and start Redis embedded server if necessary (as declared under spring profiles in application yaml).
 * <li> Configure the Redis "Standalone" module with the host and port.
 * <li> Configure the Jedis Client, including SSL and Connection Pooling.
 * <li> Configure Cache TTLs and expirations.
 * </ul>
 */
@Configuration
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableCaching
/* @Import to participate in the autoconfigure bootstrap process */
@Import({ BipCachesConfig.class, BipJedisConnectionConfig.class })
@ConditionalOnProperty(name = BipCacheAutoConfiguration.CONDITIONAL_SPRING_REDIS,
		havingValue = BipCacheAutoConfiguration.CACHE_SERVER_TYPE)
@EnableMBeanExport(defaultDomain = BipCacheAutoConfiguration.JMX_DOMAIN, registration = RegistrationPolicy.FAIL_ON_EXISTING)
public class BipCacheAutoConfiguration {
	/** Class logger */
	static final BipLogger LOGGER = BipLoggerFactory.getLogger(BipCacheAutoConfiguration.class);

	/** Domain under which JMX beans are exposed */
	public static final String JMX_DOMAIN = "gov.va.bip";
	/** ConditionalOnProperty property name */
	public static final String CONDITIONAL_SPRING_REDIS = "spring.cache.type";
	/** The cache server type */
	public static final String CACHE_SERVER_TYPE = "redis";

	/** Embedded Redis bean to make sure embedded redis is started before redis cache is created. */
	@SuppressWarnings("unused")
	@Autowired(required = false)
	private BipEmbeddedRedisServer referenceServerRedisEmbedded;

	/**
	 * Unwrap the actual object from a JDK or CGLib proxy object.
	 *
	 * @param proxy the proxy object
	 * @return the object wrapped by the proxy
	 */
	@SuppressWarnings({ "unchecked" }) // TODO
	static <T> T getTargetObject(Object proxy) {
		while ((AopUtils.isJdkDynamicProxy(proxy))) {
			try {
				return (T) getTargetObject(((org.springframework.aop.framework.Advised) proxy).getTargetSource().getTarget());
			} catch (Exception e) {
				// do nothing
				LOGGER.error("Could not cast proxy to Advised.", e);
			}
		}
		return (T) proxy; // expected to be cglib proxy then, which is simply a specialized class
	}

	/**
	 * JMX MBean that exposes cache management operations.
	 *
	 * @return BipCacheOpsMBean - the management bean
	 */
	@Bean
	public BipCacheOpsMBean bipCacheOpsMBean() {
		return new BipCacheOpsImpl();
	}

	/**
	 * Audit log serializer.
	 *
	 * @return the audit log serializer
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuditLogSerializer auditLogSerializer() {
		return new AuditLogSerializer();
	}

	/**
	 * Base async audit.
	 *
	 * @return the base async audit
	 */
	@Bean
	@ConditionalOnMissingBean
	public BaseAsyncAudit baseAsyncAudit() {
		return new BaseAsyncAudit();
	}
}
