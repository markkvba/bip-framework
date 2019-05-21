package gov.va.bip.framework.swagger.autoconfigure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import gov.va.bip.framework.swagger.autoconfigure.ApiInfoProperties.Contact;
import gov.va.bip.framework.swagger.autoconfigure.ApiInfoProperties.License;

public class ApiInfoPropertiesTest {

	@Test
	public void testSetters() {
		Contact contact =new Contact();
		contact.setEmail("test@va.gov");
		contact.setName("va.gov");
		contact.setUrl("https://www.va.gov/");
		
		License license = new License();
		license.setName("Apache 2.0");
		license.setUrl("https://www.apache.org/licenses/LICENSE-2.0");
		
		ApiInfoProperties apiInfoProperties = new ApiInfoProperties();
		apiInfoProperties.setDescription("New Description");
		apiInfoProperties.setTitle("New Title");
		apiInfoProperties.setVersion("New Version");
		apiInfoProperties.setContact(contact);
		apiInfoProperties.setLicense(license);
		apiInfoProperties.setTermsOfService("https://developer.va.gov/terms-of-service");
		
		assertEquals("New Description", apiInfoProperties.getDescription());
		assertEquals("New Title", apiInfoProperties.getTitle());
		assertEquals("New Version", apiInfoProperties.getVersion());
		assertEquals("va.gov", apiInfoProperties.getContact().getName());
		assertEquals("https://www.va.gov/", apiInfoProperties.getContact().getUrl());
		assertEquals("test@va.gov", apiInfoProperties.getContact().getEmail());
		assertEquals("Apache 2.0", apiInfoProperties.getLicense().getName());
		assertEquals("https://www.apache.org/licenses/LICENSE-2.0", apiInfoProperties.getLicense().getUrl());
		assertEquals("https://developer.va.gov/terms-of-service", apiInfoProperties.getTermsOfService());
	}
}
