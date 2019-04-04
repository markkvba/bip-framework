package gov.va.ocp.framework.exception;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.va.ocp.framework.messages.MessageKey;
import gov.va.ocp.framework.messages.MessageKeys;
import gov.va.ocp.framework.messages.MessageSeverity;

@RunWith(SpringJUnit4ClassRunner.class)
public class OcpExceptionTest {
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	@Test
	public void initializeOcpExceptionTest() {
		assertNotNull(new OcpException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST));
		assertNotNull(new OcpException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, new Exception("wrapped message")));
	}

	@Test
	public void getterTest() {
		OcpException ocpException =
				new OcpException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST,
						new Exception("wrapped message"));
		assertTrue(ocpException.getKey().equals(TEST_KEY.getKey()));
		assertTrue(ocpException.getMessage().equals(TEST_KEY.getKey()));
		assertTrue(ocpException.getSeverity().equals(MessageSeverity.ERROR));
		assertTrue(ocpException.getStatus().equals(HttpStatus.BAD_REQUEST));
	}

}
