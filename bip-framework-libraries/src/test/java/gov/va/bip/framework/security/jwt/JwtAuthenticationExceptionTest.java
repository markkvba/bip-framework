package gov.va.bip.framework.security.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.bip.framework.messages.MessageKey;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.security.jwt.JwtAuthenticationException;

public class JwtAuthenticationExceptionTest {

	private static final String[] PARAMS = new String[] { "param1", "param2" };
	private static final MessageKey TEST_KEY = MessageKeys.NO_KEY;

	public JwtAuthenticationExceptionTest() {
	}

	@Test
	public void testSomeMethod() {
		new JwtAuthenticationException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
	}

	@Test
	public void testSomeMethod1() {
		new JwtAuthenticationException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, new Throwable());
	}

	@Test
	public final void testGettersAndSetters() {
		JwtAuthenticationException exception =
				new JwtAuthenticationException(TEST_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, new Throwable(), PARAMS);

		assertTrue(exception.getKey().equals(TEST_KEY.getKey()));
		assertTrue(exception.getMessageKey().equals(TEST_KEY));
		assertTrue(exception.getStatus().equals(HttpStatus.BAD_REQUEST));
		assertTrue(exception.getSeverity().equals(MessageSeverity.ERROR));

		assertEquals(exception.getParams().length, 2);
		assertTrue(exception.getParams()[0].equals(PARAMS[0]));
		assertTrue(exception.getParams()[1].equals(PARAMS[1]));


	}

}