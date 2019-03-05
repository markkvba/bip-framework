package gov.va.ocp.framework.exception;

import org.junit.Assert;
import org.junit.Test;

import gov.va.ocp.framework.exception.OcpFeignRuntimeException;

public class OcpFeignRuntimeExceptionTest {

	private static final String TEST_KEY = "test.key";
	private static final String TEST_TEXT = "text for test.key";
	private static final String TEST_HTTP_STATUS_STRING = "400";
	private static final String TEST_SEVERITY = "ERROR";

	@Test
	public void instantiateOcpFeignRuntimeExceptionTest() throws Exception {
		OcpFeignRuntimeException ocpFeignRuntimeException =
				new OcpFeignRuntimeException(TEST_KEY, TEST_TEXT, TEST_HTTP_STATUS_STRING, TEST_SEVERITY);

		Assert.assertTrue(ocpFeignRuntimeException.getKey().equals(TEST_KEY));
		Assert.assertTrue(ocpFeignRuntimeException.getText().equals(TEST_TEXT));
		Assert.assertTrue(ocpFeignRuntimeException.getStatus().equals(TEST_HTTP_STATUS_STRING));
		Assert.assertTrue(ocpFeignRuntimeException.getSeverity().equals(TEST_SEVERITY));
	}

}
