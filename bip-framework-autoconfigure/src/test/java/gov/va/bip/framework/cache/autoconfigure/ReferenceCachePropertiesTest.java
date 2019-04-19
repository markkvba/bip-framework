package gov.va.bip.framework.cache.autoconfigure;

import org.junit.Test;

import gov.va.bip.framework.cache.autoconfigure.BipRedisClientProperties;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ReferenceCachePropertiesTest {

	@Test
	public void testGetters() {
		BipRedisClientProperties bipRedisClientProperties = new BipRedisClientProperties();
		assertNull(bipRedisClientProperties.getExpires());
		assertEquals(new Long(86400L), bipRedisClientProperties.getDefaultExpires());
	}

	@Test
	public void testSetters() {
		BipRedisClientProperties bipRedisClientProperties = new BipRedisClientProperties();
		List<BipRedisClientProperties.RedisExpires> listRedisExpires = new ArrayList<>();
		BipRedisClientProperties.RedisExpires redisExpires = new BipRedisClientProperties.RedisExpires();
		redisExpires.setName("methodcachename_projectname_projectversion");
		redisExpires.setTtl(86400L);
		listRedisExpires.add(0, redisExpires);
		bipRedisClientProperties.setExpires(listRedisExpires);
		bipRedisClientProperties.setDefaultExpires(500L);
		assertTrue(!bipRedisClientProperties.getExpires().isEmpty());
		assertTrue(Long.valueOf(86400L).equals(bipRedisClientProperties.getExpires().get(0).getTtl()));
		assertEquals(new Long(500L), bipRedisClientProperties.getDefaultExpires());
	}
}