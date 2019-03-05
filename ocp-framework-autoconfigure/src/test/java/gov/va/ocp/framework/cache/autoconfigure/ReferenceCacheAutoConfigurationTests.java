package gov.va.ocp.framework.cache.autoconfigure;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.ocp.framework.cache.autoconfigure.OcpCacheAutoConfiguration;

/**
 * Created by vgadda on 8/11/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ReferenceCacheAutoConfigurationTests {

	private AnnotationConfigApplicationContext context;

	@Mock
	CacheManager cacheManager;

	@Mock
	Cache mockCache;

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testReferenceCacheConfiguration() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("spring.cache.type=redis").applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);
		CacheManager cacheManager = context.getBean(CacheManager.class);
		assertNotNull(cacheManager);
	}

	@Test
	public void testReferenceCacheConfigurationKeyGenerator() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("spring.cache.type=redis").applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);
		KeyGenerator keyGenerator = context.getBean(KeyGenerator.class);
		String key = (String) keyGenerator.generate(new Object(), myMethod(), new Object());
		assertNotNull(key);
	}

	@Test
	public void testCacheGetError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("spring.cache.type=redis").applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);

		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);
		ocpCacheAutoConfiguration.errorHandler().handleCacheGetError(new RuntimeException("Test Message"), mockCache, "TestKey");
	}

	@Test
	public void testCachePutError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("spring.cache.type=redis").applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);

		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);
		ocpCacheAutoConfiguration.errorHandler().handleCachePutError(new RuntimeException("Test Message"), mockCache, "TestKey",
				"TestValue");
	}

	@Test
	public void testCacheEvictError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("spring.cache.type=redis").applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);

		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);
		ocpCacheAutoConfiguration.errorHandler().handleCacheEvictError(new RuntimeException("Test Message"), mockCache, "TestKey");
	}

	@Test
	public void testCacheClearError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		TestPropertyValues.of("spring.cache.type=redis").applyTo(context);
		context.register(RedisAutoConfiguration.class, OcpCacheAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);

		OcpCacheAutoConfiguration ocpCacheAutoConfiguration = context.getBean(OcpCacheAutoConfiguration.class);
		ocpCacheAutoConfiguration.errorHandler().handleCacheClearError(new RuntimeException("Test Message"), mockCache);
	}

	public Method myMethod() throws NoSuchMethodException {
		return getClass().getDeclaredMethod("someMethod");
	}

	public void someMethod() {
		// do nothing
	}
}
