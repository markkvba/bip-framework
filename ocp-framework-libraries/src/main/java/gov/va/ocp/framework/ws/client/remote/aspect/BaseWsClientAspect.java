package gov.va.ocp.framework.ws.client.remote.aspect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.AuditLogSerializer;
import gov.va.ocp.framework.audit.RequestAuditData;
import gov.va.ocp.framework.audit.ResponseAuditData;
import gov.va.ocp.framework.exception.OcpExceptionExtender;
import gov.va.ocp.framework.exception.OcpPartnerRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.validation.Defense;
import gov.va.ocp.framework.ws.client.remote.RemoteServiceCall;

/**
 * This is the base class for REST provider aspects.
 * It provides the Point Cuts to be used by extending classes that declare types of @Aspect advice.
 *
 * @author jshrader
 */
public class BaseWsClientAspect {

	/** The Constant LOGGER. */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(BaseWsClientAspect.class);

	/** How many bytes of an uploaded file will be read for inclusion in the audit record */
	private static final int NUMBER_OF_BYTES = 1024;

	/** Asynchronous logger for audit logging */
	@Autowired
	AuditLogSerializer asyncLogging;

	/**
	 * Protected constructor.
	 */
	protected BaseWsClientAspect() {
		super();
	}

	/**
	 * Defenses against system related problems.
	 */
	@PostConstruct
	void postConstruct() {
		Defense.notNull(asyncLogging, "AuditLogSerializer asyncLogging cannot be null.");
	}

	/**
	 * This point cut selects any code that...
	 * <ol>
	 * <li>Implements the {@link RemoteServiceCall} interface
	 * <li>Executes any method declared on that interface
	 * </ol>
	 */
	@Pointcut("execution(* gov.va.ocp.framework.ws.client.remote.RemoteServiceCall+.*(..))")
	protected static final void remoteServiceCall() {
		// Do nothing.
	}

	/**
	 * This point cut selects code (e.g. methods) that ...
	 * <ol>
	 * <li>are annotated with gov.va.ocp.framework.audit.Auditable
	 * </ol>
	 */
	@Pointcut("@annotation(gov.va.ocp.framework.audit.Auditable)")
	protected static final void auditableAnnotation() {
		// Do nothing.
	}

	/**
	 * Convenience method to get the default audit event data from a method.
	 *
	 * @param method the method
	 * @return the auditable instance
	 */
	public static AuditEventData getDefaultAuditableInstance(final Method method) {
		if (method != null) {
			return new AuditEventData(AuditEvents.PARTNER_REQUEST_RESPONSE, method.getName(), method.getDeclaringClass().getName());
		} else {
			return new AuditEventData(AuditEvents.PARTNER_REQUEST_RESPONSE, "", "");
		}
	}

	/**
	 * Write audit for request.
	 *
	 * @param request the request
	 * @param auditEventData the auditable annotation
	 */
	protected void writeRequestInfoAudit(final List<Object> request, final AuditEventData auditEventData) {

		LOGGER.debug("Request {}", ReflectionToStringBuilder.toString(request));

		final RequestAuditData requestAuditData = new RequestAuditData();

		// set request on audit data
		if (request != null) {
			requestAuditData.setRequest(request);
		}

		LOGGER.debug("RequestAuditData: {}", requestAuditData.toString());

		if (asyncLogging != null) {
			asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, requestAuditData, RequestAuditData.class,
					MessageSeverity.INFO, null);
		}
	}

	/**
	 * Read the first 1024 bytes and convert that into a string.
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected static String convertBytesToString(final InputStream in) throws IOException {
		int offset = 0;
		int bytesRead = 0;
		final byte[] data = new byte[NUMBER_OF_BYTES];
		while ((bytesRead = in.read(data, offset, data.length - offset)) != -1) {
			offset += bytesRead;
			if (offset >= data.length) {
				break;
			}
		}
		return new String(data, 0, offset, "UTF-8");
	}

	/**
	 * Write audit for response.
	 *
	 * @param response the response
	 * @param auditEventData the auditable annotation
	 */
	protected void writeResponseAudit(final Object response, final AuditEventData auditEventData,
			final MessageSeverity messageSeverity, final Throwable t) {

		final ResponseAuditData responseAuditData = new ResponseAuditData();

		responseAuditData.setResponse(response);

		if (asyncLogging != null) {
			LOGGER.debug("Invoking AuditLogSerializer.asyncLogRequestResponseAspectAuditData()");
			asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, responseAuditData, ResponseAuditData.class,
					messageSeverity, t);
		}
	}

	/**
	 * Resolve the throwable to an {@link OcpPartnerRuntimeException} (or subclass of OcpPartnerRuntimeException).
	 *
	 * @param throwable the throwable
	 * @return OcpPartnerRuntimeException the runtime exception
	 */
	protected OcpPartnerRuntimeException resolvePartnerRuntimeException(final Throwable throwable) {
		// custom exception type to represent the error
		OcpPartnerRuntimeException resolvedRuntimeException = null;

		if (OcpPartnerRuntimeException.class.isAssignableFrom(throwable.getClass())) {
			// have to cast so the "Throwable throwable" variable can be returned as-is
			resolvedRuntimeException = castToOcpPartnerRuntimeException(throwable);

		} else if (OcpExceptionExtender.class.isAssignableFrom(throwable.getClass())) {
			resolvedRuntimeException = convertFromOcpExceptionExtender((OcpExceptionExtender) throwable);

		} else {
			// make a new OcpRuntimeException from the non-OCP throwable
			resolvedRuntimeException =
					new OcpPartnerRuntimeException("", throwable.getMessage(), MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR,
							throwable);
		}

		return resolvedRuntimeException;
	}

	private OcpPartnerRuntimeException convertFromOcpExceptionExtender(final OcpExceptionExtender ocp) {
		String message = null;
		Throwable cause = null;
		try {
			message = ((Throwable) ocp).getMessage();
			cause = ((Throwable) ocp).getCause();
		} catch (ClassCastException e) {
			LOGGER.error(new OcpBanner("ResolveRuntimeException Failed", Level.ERROR),
					"Could not acquire message and/or cause values from throwable " + ocp.getClass().getName(), e);
		}
		return new OcpPartnerRuntimeException(ocp.getKey(), message, ocp.getSeverity(), ocp.getStatus(), cause);
	}

	private OcpPartnerRuntimeException castToOcpPartnerRuntimeException(final Throwable throwable) { // method added for testability
		OcpPartnerRuntimeException resolvedRuntimeException = null;
		try {
			resolvedRuntimeException = (OcpPartnerRuntimeException) throwable;
		} catch (ClassCastException e) {
			String msg = "Could not cast " + throwable.getClass().getName() + " to OcpPartnerRuntimeException";
			LOGGER.error(new OcpBanner("ResolvePartnerRuntimeException Failed", Level.ERROR), msg, e);
			throw new OcpPartnerRuntimeException("", msg, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resolvedRuntimeException;
	}
}
