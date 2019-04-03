package gov.va.ocp.framework.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.ocp.framework.config.MessageKeysConfig;
import gov.va.ocp.framework.messages.MessageKey;
import gov.va.ocp.framework.messages.MessageKeys;
import gov.va.ocp.framework.messages.MessageSeverity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MessageKeysConfig.class })
public class OcpServiceExceptionTest {

	private static final String TEST_KEY_MESSAGE = "NO_KEY";
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmptyConstructor() {
		OcpServiceException ocpServiceException = new OcpServiceException(TEST_KEY, null, null, null, null);
		assertTrue(ocpServiceException.getMessage().equals(TEST_KEY_MESSAGE));
		assertNull(ocpServiceException.getSeverity());
		assertNull(ocpServiceException.getStatus());
		assertNull(ocpServiceException.getCause());
	}

	@Test
	public void testPopulatedConstructor() {
		OcpServiceException ocpServiceException =
				new OcpServiceException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
						new Throwable("test"));
		assertNotNull(ocpServiceException.getKey());
		assertNotNull(ocpServiceException.getMessage());
		assertNotNull(ocpServiceException.getSeverity());
		assertNotNull(ocpServiceException.getStatus());
		assertNotNull(ocpServiceException.getCause());
	}

	@Test
	public void testPopulatedNoCauseConstructor() {
		OcpServiceException ocpServiceException =
				new OcpServiceException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
		assertNotNull(ocpServiceException.getKey());
		assertNotNull(ocpServiceException.getMessage());
		assertNotNull(ocpServiceException.getSeverity());
		assertNotNull(ocpServiceException.getStatus());
		assertNull(ocpServiceException.getCause());
	}

}
