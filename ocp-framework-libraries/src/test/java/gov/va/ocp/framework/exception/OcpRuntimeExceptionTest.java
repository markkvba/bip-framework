package gov.va.ocp.framework.exception;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OcpRuntimeExceptionTest {

	private static final String SERVER_NAME_PROPERTY = "server.name";

	@Before
	public void setUp() {
		System.setProperty(SERVER_NAME_PROPERTY, "Test Server");
	}

	@BeforeClass
	public static void setUpClass() {
		System.setProperty(SERVER_NAME_PROPERTY, "Test Server");
	}

	@Test
	public void getMessageTestServerNameNull() throws Exception {
		OcpRuntimeException ocpRuntimeException =
				new OcpRuntimeException(null, null, null, null);

		Assert.assertNull(ocpRuntimeException.getMessage());
	}

	@Test
	public void getMessageTestCategoryNull() throws Exception {
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException(null, null, null, null);
		Assert.assertEquals(null, ocpRuntimeException.getMessage());
	}

	@Test
	public void getMessageCauseAndMessageTest() throws Exception {
		Throwable cause = new Throwable("test");
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException(null, "Test Message", null, null, cause);
		Assert.assertEquals("Test Message", ocpRuntimeException.getMessage());
	}
}
