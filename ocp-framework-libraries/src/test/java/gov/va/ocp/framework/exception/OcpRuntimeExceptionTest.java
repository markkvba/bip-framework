package gov.va.ocp.framework.exception;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import gov.va.ocp.framework.messages.MessageKey;
import gov.va.ocp.framework.messages.MessageKeys;

public class OcpRuntimeExceptionTest {

	private static final String TEST_KEY_MESSAGE = "NO_KEY";
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;
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
				new OcpRuntimeException(TEST_KEY, null, null, (Object[]) null);

		Assert.assertTrue(ocpRuntimeException.getServerName().equals(System.getProperty("server.name")));
	}

	@Test
	public void getMessageTestCategoryNull() throws Exception {
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException(TEST_KEY, null, null, (Object[]) null);
		Assert.assertTrue(ocpRuntimeException.getMessage().equals(TEST_KEY_MESSAGE));
	}

	@Test
	public void getMessageCauseAndMessageTest() throws Exception {
		Throwable cause = new Throwable("test");
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException(TEST_KEY, null, null, cause);
		Assert.assertTrue(ocpRuntimeException.getMessage().equals(TEST_KEY_MESSAGE));
	}
}
