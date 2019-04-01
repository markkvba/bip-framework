package gov.va.ocp.framework.rest.provider.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.AuditLogger;
import gov.va.ocp.framework.constants.OcpConstants;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageKeys;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.rest.provider.ProviderResponse;

/**
 * This aspect performs Audit logging before and after the endpoint operation is executed.
 * Additionally, any exceptions thrown back to the endpoint operation will be intercepted
 * and converted to appropriate JSON object with a FATAL message.
 *
 * @author akulkarni
 * @see gov.va.ocp.framework.rest.provider.aspect.BaseHttpProviderAspect
 */
@Aspect
@Order(-9998)
public class ProviderHttpAspect extends BaseHttpProviderAspect {

	/** Class logger */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(ProviderHttpAspect.class);
	/** Identity of the before advice */
	private static final String BEFORE_ADVICE = "beforeAuditAdvice";
	/** Identity of the after advice */
	private static final String AFTER_ADVICE = "afterreturningAuditAdvice";
	/** Identity of the afterThrowing advice */
	private static final String AFTER_THROWING_ADVICE = "afterThrowingAdvice";

	/** Attempting to write the request to the audit logs */
	private static final String ATTEMPTING_WRITE_REQUEST = "writeRequestInfoAudit";
	/** Attempting to write the response to the audit logs */
	private static final String ATTEMPTING_WRITE_RESPONSE = "writeResponseAudit";

	/**
	 * Perform audit logging on the request, before the operation is executed.
	 *
	 * @param joinPoint
	 */
	@Before("!auditableAnnotation() && publicServiceResponseRestMethod()")
	public void beforeAuditAdvice(final JoinPoint joinPoint) {
		LOGGER.debug(BEFORE_ADVICE + " joinpoint: " + joinPoint.toLongString());

		List<Object> requestArgs = null;
		AuditEventData auditEventData = null;

		if (joinPoint.getArgs().length > 0) {
			requestArgs = Arrays.asList(joinPoint.getArgs());
		}

		try {
			Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

			auditEventData = new AuditEventData(AuditEvents.API_REST_REQUEST, method.getName(), method.getDeclaringClass().getName());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request: {}", requestArgs);
				LOGGER.debug("Method: {}", method);
				LOGGER.debug("AuditEventData: {}", auditEventData.toString());
			}
			writeRequestInfoAudit(requestArgs, auditEventData);

		} catch (final Throwable throwable) { // NOSONAR intentionally catching throwable
			handleInternalException(BEFORE_ADVICE, ATTEMPTING_WRITE_REQUEST, auditEventData, throwable);
		} finally {
			LOGGER.debug(BEFORE_ADVICE + " finished.");
		}
	}

	/**
	 * Perform audit logging on the response, after the operation is executed.
	 *
	 * @param joinPoint
	 * @param responseToConsumer
	 */
	@AfterReturning(pointcut = "!auditableAnnotation() && publicServiceResponseRestMethod()",
			returning = "responseToConsumer")
	public void afterreturningAuditAdvice(JoinPoint joinPoint, ProviderResponse responseToConsumer) {
		LOGGER.debug(AFTER_ADVICE + " joinpoint: " + joinPoint.toLongString());
		LOGGER.debug(AFTER_ADVICE + " responseToConsumer: "
				+ ReflectionToStringBuilder.toString(responseToConsumer, null, true, true, ProviderResponse.class));

		AuditEventData auditEventData = null;
		ProviderResponse providerResponse = null;

		try {
			if (responseToConsumer == null) {
				providerResponse = new ProviderResponse();
			} else {
				providerResponse = responseToConsumer;
			}

			Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

			auditEventData = new AuditEventData(AuditEvents.API_REST_RESPONSE, method.getName(), method.getDeclaringClass().getName());

			writeResponseAudit(providerResponse, auditEventData, MessageSeverity.INFO, null);

		} catch (Throwable throwable) { // NOSONAR intentionally catching throwable
			handleInternalException(AFTER_ADVICE, ATTEMPTING_WRITE_RESPONSE, auditEventData, throwable);
		} finally {
			LOGGER.debug(AFTER_ADVICE + " finished.");
		}
	}

	/**
	 * Perform audit logging after application has thrown an exception.
	 * Any exceptions thrown back to the endpoint operation will be intercepted
	 * by this advice, and converted to appropriate JSON object with FATAL message.
	 *
	 * @param joinPoint the intersection for the pointcut
	 * @param throwable the exception thrown by the application code
	 */
	@AfterThrowing(pointcut = "!auditableAnnotation() && publicServiceResponseRestMethod()",
			throwing = "throwable")
	public ResponseEntity<ProviderResponse> afterThrowingAdvice(JoinPoint joinPoint, Throwable throwable) {
		LOGGER.debug(AFTER_THROWING_ADVICE + " joinpoint: " + joinPoint.toLongString());
		LOGGER.debug(AFTER_THROWING_ADVICE + " throwable: {}" + throwable);

		AuditEventData auditEventData = null;
		ResponseEntity<ProviderResponse> providerResponse = null;

		try {
			if (throwable == null) {
				// null throwable almost certain not to happen, but check nonetheless
				throwable = new Throwable("Unknown problem. Thrown exception was null.");
			}

			providerResponse = writeAuditError(AFTER_THROWING_ADVICE, throwable, auditEventData);

		} catch (Throwable t) { // NOSONAR intentionally catching throwable
			providerResponse = handleInternalException(AFTER_THROWING_ADVICE, ATTEMPTING_WRITE_RESPONSE, auditEventData, t);
		} finally {
			LOGGER.debug(AFTER_THROWING_ADVICE + " finished.");
		}
		return providerResponse;
	}

	/**
	 * Standard handling of exceptions that are thrown from within the advice
	 * (not exceptions thrown by application code).
	 *
	 * @param adviceName the name of the advice method in which the exception was thrown
	 * @param attemptingTo the attempted task that threw the exception
	 * @param auditEventData the audit event data object
	 * @param throwable the exception that was thrown
	 */
	private ResponseEntity<ProviderResponse> handleInternalException(String adviceName, String attemptingTo,
			AuditEventData auditEventData, Throwable throwable) {
		ResponseEntity<ProviderResponse> entity = null;
		try {
			MessageKeys key = MessageKeys.OCP_AUDIT_ASPECT_ERROR_UNEXPECTED;
			String msg = key.getMessage(new Object[] { adviceName, attemptingTo });
			final OcpRuntimeException ocpRuntimeException = new OcpRuntimeException(key.getKey(), msg,
					MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR, throwable);
			entity = writeAuditError(adviceName, ocpRuntimeException, auditEventData);

		} catch (Throwable e) { // NOSONAR intentionally catching throwable
			entity = handleAnyRethrownExceptions(adviceName, throwable, e);
		}
		return entity;
	}

	private ResponseEntity<ProviderResponse> handleAnyRethrownExceptions(String adviceName, Throwable originatingThrowable,
			Throwable e) {
		ResponseEntity<ProviderResponse> entity;
		MessageKeys key = MessageKeys.OCP_AUDIT_ASPECT_ERROR_CANNOT_AUDIT;
		String msg = key.getMessage(new Object[] { adviceName, originatingThrowable.getClass().getSimpleName() });
		LOGGER.error(OcpBanner.newBanner(OcpConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
				msg, e);

		ProviderResponse body = new ProviderResponse();
		body.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.name(),
				msg, HttpStatus.INTERNAL_SERVER_ERROR);
		entity = new ResponseEntity<ProviderResponse>(body, HttpStatus.INTERNAL_SERVER_ERROR);
		return entity;
	}

	/**
	 * Write into Audit when exceptions occur while attempting to log audit records.
	 *
	 * @param adviceName
	 * @param exception
	 * @param auditEventData
	 * @return
	 */
	private ResponseEntity<ProviderResponse> writeAuditError(final String adviceName, final Throwable exception,
			final AuditEventData auditEventData) {
		String msg = "Error ServiceMessage: " + exception;
		AuditLogger.error(auditEventData, msg, exception);
		LOGGER.error(adviceName + " auditing uncaught exception.", exception);

		final ProviderResponse providerResponse = new ProviderResponse();
		providerResponse.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		return new ResponseEntity<>(providerResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
