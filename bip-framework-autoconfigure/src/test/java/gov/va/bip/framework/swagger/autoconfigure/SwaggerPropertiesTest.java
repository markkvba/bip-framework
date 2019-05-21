package gov.va.bip.framework.swagger.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.va.bip.framework.swagger.autoconfigure.SwaggerProperties;

public class SwaggerPropertiesTest {

	@Test
	public void testSetters() {
		SwaggerProperties swaggerProperties = new SwaggerProperties();
		swaggerProperties.setEnabled(false);
		swaggerProperties.setSecurePaths("New Secure Paths");
		swaggerProperties.setGroupName("New GroupName");
		assertFalse(swaggerProperties.isEnabled());
		assertEquals("New Secure Paths", swaggerProperties.getSecurePaths());
		assertEquals("New GroupName", swaggerProperties.getGroupName());
	}

	@Test
	public void testGetters() {
		SwaggerProperties swaggerProperties = new SwaggerProperties();
		assertTrue(swaggerProperties.isEnabled());
		assertEquals("[Api secure paths via bip.framework.swagger.securePaths]", swaggerProperties.getSecurePaths());
		assertEquals("", swaggerProperties.getGroupName());
	}

}
