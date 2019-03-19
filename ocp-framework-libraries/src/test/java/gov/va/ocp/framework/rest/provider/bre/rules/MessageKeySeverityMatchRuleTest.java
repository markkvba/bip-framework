package gov.va.ocp.framework.rest.provider.bre.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.rest.provider.Message;

public class MessageKeySeverityMatchRuleTest {

	MessageKeySeverityMatchRule messageKeySeverityMatchRule;
	Set<Message> messagesToEval = new HashSet<Message>();

	@Before
	public void setUp() throws Exception {
		Message errMessage = new Message(MessageSeverity.ERROR.name(), "ErrorKey", "Error Text", null);
		Message fatalMessage = new Message(MessageSeverity.FATAL.name(), "FatalKey", "Fatal Error Text", null);
		Message warnMessage = new Message(MessageSeverity.WARN.name(), "WarnKey", "Warn Text", null);
		Message infoMessage = new Message(MessageSeverity.INFO.name(), "InfoKey", "Info Text", null);
		messagesToEval.add(errMessage);
		messagesToEval.add(fatalMessage);
		messagesToEval.add(warnMessage);
		messagesToEval.add(infoMessage);
	}

	@After
	public void tearDown() throws Exception {
		messagesToEval.clear();
	}

	@Test
	public void testEval() {
		Message messageToMatch = new Message("ERROR", "ErrorKey", "Error Text", HttpStatus.UNAUTHORIZED.value());
		messageKeySeverityMatchRule = new MessageKeySeverityMatchRule(messageToMatch, HttpStatus.UNAUTHORIZED);
		assertEquals(HttpStatus.UNAUTHORIZED, messageKeySeverityMatchRule.eval(messagesToEval));
	}

	@Test
	public void testToString() {
		Message messageToMatch = new Message("ERROR", "ErrorKey", "Error Text", HttpStatus.UNAUTHORIZED.value());
		messageKeySeverityMatchRule = new MessageKeySeverityMatchRule(messageToMatch, HttpStatus.UNAUTHORIZED);
		assertNotNull(messageKeySeverityMatchRule.toString());
	}

	@Test
	public void testEvalNull() {
		Message messageToMatch = new Message("ERROR", "klahsdjh", "Error kuahdkj", null);
		messageKeySeverityMatchRule = new MessageKeySeverityMatchRule(messageToMatch, HttpStatus.UNAUTHORIZED);
		assertEquals(null, messageKeySeverityMatchRule.eval(messagesToEval));
	}

}
