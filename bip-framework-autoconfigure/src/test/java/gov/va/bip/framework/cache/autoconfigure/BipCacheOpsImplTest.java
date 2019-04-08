package gov.va.bip.framework.cache.autoconfigure;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class BipCacheOpsImplTest {

	@Mock
	private CacheManager cacheManager;

	@InjectMocks
	BipCacheOpsImpl bipCacheOpsImpl;

	@Before
	public void setup() {
		bipCacheOpsImpl = new BipCacheOpsImpl();
		assertNotNull(bipCacheOpsImpl);
	}

	@Test
	public void testClearAllCaches() {
		bipCacheOpsImpl.clearAllCaches();
	}

	@Test
	public void testClearAllCachesMNoCache() {
		doReturn(null).when(cacheManager);
	}
}
