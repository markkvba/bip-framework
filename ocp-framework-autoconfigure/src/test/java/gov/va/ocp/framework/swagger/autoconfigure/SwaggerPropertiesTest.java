package gov.va.ocp.framework.swagger.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SwaggerPropertiesTest {

	@Test
	public void testSetters() {
		SwaggerProperties swaggerProperties = new SwaggerProperties();
		swaggerProperties.setEnabled(false);
		swaggerProperties.setDescription("New Description");
		swaggerProperties.setSecurePaths("New Secure Paths");
		swaggerProperties.setGroupName("New GroupName");
		swaggerProperties.setTitle("New Title");
		swaggerProperties.setVersion("New Version");
		assertFalse(swaggerProperties.isEnabled());
		assertEquals("New Description", swaggerProperties.getDescription());
		assertEquals("New Secure Paths", swaggerProperties.getSecurePaths());
		assertEquals("New GroupName", swaggerProperties.getGroupName());
		assertEquals("New Title", swaggerProperties.getTitle());
		assertEquals("New Version", swaggerProperties.getVersion());
	}

	@Test
	public void testGetters() {
		SwaggerProperties swaggerProperties = new SwaggerProperties();
		assertTrue(swaggerProperties.isEnabled());
		assertEquals("[Api description via 'ocp.swagger.description']", swaggerProperties.getDescription());
		assertEquals("[Api secure paths via ocp.swagger.securePaths]", swaggerProperties.getSecurePaths());
		assertEquals("[Api Group Name via ocp.swagger.groupName]", swaggerProperties.getGroupName());
		assertEquals("[Api title via 'ocp.swagger.title']", swaggerProperties.getTitle());
		assertEquals("[Api version via 'ocp.swagger.version']", swaggerProperties.getVersion());

	}

}