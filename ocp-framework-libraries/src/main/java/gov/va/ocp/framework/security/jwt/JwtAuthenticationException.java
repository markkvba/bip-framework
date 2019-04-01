package gov.va.ocp.framework.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import gov.va.ocp.framework.exception.OcpExceptionExtender;
import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * Created by vgadda on 5/4/17.
 */
public class JwtAuthenticationException extends AuthenticationException implements OcpExceptionExtender {

	private String key;
	private HttpStatus status;
	private MessageSeverity severity;

	public JwtAuthenticationException(final String key, String msg, final MessageSeverity severity, final HttpStatus status,
			Throwable t) {
		super(msg, t);
		this.key = key;
		this.status = status;
		this.severity = severity;
	}

	public JwtAuthenticationException(String msg) {
		super(msg);
	}

	public JwtAuthenticationException(String msg, Throwable t) {
		super(msg, t);
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public MessageSeverity getSeverity() {
		return severity;
	}

	@Override
	public String getServerName() {
		return System.getProperty("server.name");
	}
}
