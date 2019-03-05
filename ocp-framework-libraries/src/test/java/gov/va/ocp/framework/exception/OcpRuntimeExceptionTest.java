package gov.va.ocp.framework.exception;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.va.ocp.framework.exception.OcpRuntimeException;

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

	// TODO for some reason, System.getProperty(SERVER_NAME_PROPERTY)
	// in OcpRuntimeException comes back as null.
	// Not sure why it does that now, as in Ascent it always came back as "Test Server"
	@Ignore
	@Test
	public void instantiateBaseReferenceExceptions() throws Exception {
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException();

		Assert.assertEquals("Test Server", ocpRuntimeException.getServerName());
	}

	@Test
	public void getMessageTestServerName() throws Exception {
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException();

		Assert.assertEquals(null, ocpRuntimeException.getMessage());

	}

	@Test
	public void getMessageTestServerNameNull() throws Exception {
		// setup
		// do crazy reflection to make server name null
		Field field = OcpRuntimeException.class.getDeclaredField("SERVER_NAME");
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.isAccessible();
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.isAccessible();
		field.setAccessible(true);
		field.set(null, null);

		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException();

		Assert.assertNull(ocpRuntimeException.getMessage());

		// Reset server name to Test Server
		field.set(null, "Test Server");
	}

	@Test
	public void getMessageTestCategoryNull() throws Exception {
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException();
		Assert.assertEquals(null, ocpRuntimeException.getMessage());

	}

	@Test
	public void getSuperCauseTest() throws Exception {
		Throwable cause = new Throwable("test");
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException(cause);
		Assert.assertEquals("java.lang.Throwable: test", ocpRuntimeException.getMessage());

	}

	@Test
	public void getMessageCauseAndMessageTest() throws Exception {
		Throwable cause = new Throwable("test");
		OcpRuntimeException ocpRuntimeException = new OcpRuntimeException("Test Message", cause);
		Assert.assertEquals("Test Message", ocpRuntimeException.getMessage());

	}
}
