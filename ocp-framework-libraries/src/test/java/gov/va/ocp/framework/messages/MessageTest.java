package gov.va.ocp.framework.messages;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.Message;
import gov.va.ocp.framework.messages.MessageSeverity;

public class MessageTest {

	@Test
	public void testEmptyConstructor() throws Exception {
		Message message = new Message();
		assertNull(message.getKey());
		assertNull(message.getSeverity());
		assertNull(message.getText());
	}

	@Test
	public void testSeverityKeyConstructor() throws Exception {
		Message message = new Message(MessageSeverity.ERROR, "UnitTestKey", "Message text", HttpStatus.BAD_REQUEST);
		assertEquals(MessageSeverity.ERROR, message.getSeverity());
		assertEquals("UnitTestKey", message.getKey());
	}

	@Test
	public void testSeverityKeyTextConstructor() throws Exception {
		Message message = new Message(MessageSeverity.WARN, "UnitTestKey", "TextMsg", null);
		assertEquals(MessageSeverity.WARN, message.getSeverity());
		assertEquals("UnitTestKey", message.getKey());
		assertEquals("TextMsg", message.getText());
	}

	@Test
	public void testParamsConstructor() throws Exception {
		Message message = new Message(MessageSeverity.WARN, "UnitTestKey", "TextMsg", null,
				1, new String[] { "0" }, new String[] { "1" });
		assertEquals(new Integer(1), message.getParamCount());
		assertArrayEquals(new String[] { "0" }, message.getParamNames());
		assertArrayEquals(new String[] { "1" }, message.getParamValues());

		message.setParamCount(2);
		message.setParamNames(new String[] { "0" });
		message.setParamValues(new String[] { "1" });
	}

	@Test
	public void testParamsOnlyConstructor() throws Exception {
		Message message = new Message(1, new String[] { "0" }, new String[] { "1" });
		assertEquals(new Integer(1), message.getParamCount());
		assertArrayEquals(new String[] { "0" }, message.getParamNames());
		assertArrayEquals(new String[] { "1" }, message.getParamValues());
		assertNull(message.getHttpStatus());
	}

	@Test
	public void testSetters() throws Exception {
		Message message = new Message(MessageSeverity.WARN, "UnitTestKey", "TextMsg", null);
		assertEquals(MessageSeverity.WARN, message.getSeverity());
		assertEquals("UnitTestKey", message.getKey());
		assertEquals("TextMsg", message.getText());
		message.setKey("UpdatedKey");
		message.setSeverity(MessageSeverity.FATAL);
		message.setText("UpdatedText");
		assertEquals(MessageSeverity.FATAL, message.getSeverity());
		assertEquals("UpdatedKey", message.getKey());
		assertEquals("UpdatedText", message.getText());
	}

	@Test
	public void testEquals() throws Exception {
		Message message1 = new Message(MessageSeverity.INFO, "UnitTestKey", "TextMsg", null);
		Message message2 = new Message(MessageSeverity.INFO, "UnitTestKey", "Not included in equals determination", null);
		assertTrue(message1.equals(message2));
	}

	@Test
	public void testSetStatus() throws Exception {
		Message message1 = new Message(MessageSeverity.INFO, "UnitTestKey", "TextMsg", null);
		message1.setHttpStatus(HttpStatus.BAD_REQUEST);
		assertTrue(message1.getHttpStatus() == HttpStatus.BAD_REQUEST);
		assertNotNull(message1.getStatus());
	}

	@Test
	public void testMessageSeverityValueOf() throws Exception {

		assertEquals(MessageSeverity.WARN, MessageSeverity.fromValue("WARN"));
		assertEquals("WARN", MessageSeverity.WARN.value());
	}

}