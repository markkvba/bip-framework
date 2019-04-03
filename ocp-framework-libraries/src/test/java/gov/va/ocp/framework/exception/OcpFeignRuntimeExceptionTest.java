package gov.va.ocp.framework.exception;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import gov.va.ocp.framework.messages.MessageKey;
import gov.va.ocp.framework.messages.MessageKeys;
import gov.va.ocp.framework.messages.MessageSeverity;

@RunWith(SpringJUnit4ClassRunner.class)
public class OcpFeignRuntimeExceptionTest {

	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;
	private static final String TEST_TEXT = "NO_KEY";
	private static final HttpStatus TEST_HTTP_STATUS = HttpStatus.BAD_REQUEST;
	private static final MessageSeverity TEST_SEVERITY = MessageSeverity.ERROR;

	@Test
	public void instantiateOcpFeignRuntimeExceptionTest() throws Exception {
		OcpFeignRuntimeException ocpFeignRuntimeException =
				new OcpFeignRuntimeException(TEST_KEY, TEST_SEVERITY, TEST_HTTP_STATUS);

		Assert.assertTrue(ocpFeignRuntimeException.getKey().equals(TEST_KEY.getKey()));
		Assert.assertTrue(ocpFeignRuntimeException.getMessage().equals(TEST_TEXT));
		Assert.assertTrue(ocpFeignRuntimeException.getStatus().equals(TEST_HTTP_STATUS));
		Assert.assertTrue(ocpFeignRuntimeException.getSeverity().equals(TEST_SEVERITY));
	}

	@Test
	public void instantiateUsingOtherConstructorOcpFeignRuntimeExceptionTest() throws Exception {
		OcpFeignRuntimeException ocpFeignRuntimeException =
				new OcpFeignRuntimeException(TEST_KEY, TEST_SEVERITY,
						TEST_HTTP_STATUS, new Exception("test wrapped error"));

		Assert.assertTrue(ocpFeignRuntimeException.getKey().equals(TEST_KEY.getKey()));
		Assert.assertTrue(ocpFeignRuntimeException.getMessage().equals(TEST_TEXT));
		Assert.assertTrue(ocpFeignRuntimeException.getStatus().equals(TEST_HTTP_STATUS));
		Assert.assertTrue(ocpFeignRuntimeException.getSeverity().equals(TEST_SEVERITY));
	}

}
