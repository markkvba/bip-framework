package gov.va.bip.framework.cache.autoconfigure.jmx;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class BipCacheOpsImplTest {

	@Mock
	private RedisCacheManager cacheManager;

	BipCacheOpsImpl bipCacheOpsImpl;

	@Before
	public void setup() {
		bipCacheOpsImpl = new BipCacheOpsImpl();
		assertNotNull(bipCacheOpsImpl);
	}

	@Test
	public void testClearAllCaches() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field cm = bipCacheOpsImpl.getClass().getDeclaredField("cacheManager");
		cm.setAccessible(true);
		cm.set(bipCacheOpsImpl, cacheManager);

		bipCacheOpsImpl.clearAllCaches();
	}

	@Test
	public void testClearAllCachesNoCache()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field cm = bipCacheOpsImpl.getClass().getDeclaredField("cacheManager");
		cm.setAccessible(true);
		cm.set(bipCacheOpsImpl, null);

		bipCacheOpsImpl.clearAllCaches();
	}
}
