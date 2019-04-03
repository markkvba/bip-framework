package gov.va.ocp.framework.security.jwt;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageKey;
import gov.va.ocp.framework.messages.MessageKeys;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.security.jwt.JwtAuthenticationException;

public class JwtAuthenticationExceptionTest {

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

}
