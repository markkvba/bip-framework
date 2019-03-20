package gov.va.ocp.framework.ws.client.remote.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.event.Level;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.AuditLogger;
import gov.va.ocp.framework.constants.AnnotationConstants;
import gov.va.ocp.framework.exception.OcpExceptionExtender;
import gov.va.ocp.framework.exception.OcpPartnerRuntimeException;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.rest.provider.ProviderResponse;
import gov.va.ocp.framework.transfer.PartnerTransferObjectMarker;
import gov.va.ocp.framework.ws.client.remote.RemoteServiceCall;

/**
 * This aspect performs Audit logging before and after the {@link RemoteServiceCall} operation is executed.
 * Additionally, any exceptions thrown back to the RemoteServiceCall operation will be intercepted
 * and converted to appropriate {@link OcpExceptionExtender} types.
 *
 * @author akulkarni, aburkholder
 * @see gov.va.ocp.framework.rest.provider.aspect.BaseWsClientAspect
 */
@Aspect
@Order(-9998)
public class WsClientAspect extends BaseWsClientAspect {

	/** Class logger */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(WsClientAspect.class);

	/**
	 * Perform audit logging on the request, before the operation is executed.
	 *
	 * @param joinPoint
	 */
	@Before("!auditableAnnotation() && remoteServiceCall()")
	public void beforeAuditAdvice(final JoinPoint joinPoint) {
		LOGGER.debug("beforeAuditAdvice joinpoint: " + joinPoint.toLongString());

		List<Object> requestArgs = null;
		AuditEventData auditEventData = null;

		if (joinPoint.getArgs().length > 0) {
			requestArgs = Arrays.asList(joinPoint.getArgs());
		}

		try {
			Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

			auditEventData =
					new AuditEventData(AuditEvents.PARTNER_SOAP_REQUEST, method.getName(), method.getDeclaringClass().getName());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Request: {}", requestArgs);
				LOGGER.debug("Method: {}", method);
				LOGGER.debug("AuditEventData: {}", auditEventData.toString());
			}
			super.writeRequestInfoAudit(requestArgs, auditEventData);

		} catch (final Throwable throwable) { // NOSONAR intentionally catching throwable
			this.handleInternalException("beforeAuditAdvice", "writeRequestInfoAudit", auditEventData, throwable);
		} finally {
			LOGGER.debug("beforeAuditAdvice finished.");
		}
	}

	/**
	 * Perform audit logging on the response, after the operation is executed.
	 *
	 * @param joinPoint
	 * @param responseToConsumer
	 */
	@AfterReturning(pointcut = "!auditableAnnotation() && remoteServiceCall()",
			returning = "responseToService")
	public void afterreturningAuditAdvice(JoinPoint joinPoint, PartnerTransferObjectMarker responseToService) {
		LOGGER.debug("afterreturningAuditAdvice joinpoint: " + joinPoint.toLongString());
		LOGGER.debug(
				"afterreturningAuditAdvice responseToConsumer: "
						+ ReflectionToStringBuilder.toString(responseToService, null, true, true, PartnerTransferObjectMarker.class));

		AuditEventData auditEventData = null;
		PartnerTransferObjectMarker partnerResponse = null;

		try {
			Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

			auditEventData =
					new AuditEventData(AuditEvents.PARTNER_SOAP_RESPONSE, method.getName(), method.getDeclaringClass().getName());

			super.writeResponseAudit(partnerResponse, auditEventData, MessageSeverity.INFO, null);

		} catch (Throwable throwable) { // NOSONAR intentionally catching throwable
			this.handleInternalException("afterreturningAuditAdvice", "writeResponseAudit", auditEventData, throwable);
		} finally {
			LOGGER.debug("afterreturningAuditAdvice finished.");
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
	@AfterThrowing(pointcut = "!auditableAnnotation() && remoteServiceCall()",
			throwing = "throwable")
	public void afterThrowingAdvice(JoinPoint joinPoint, Throwable throwable) {
		LOGGER.debug("afterThrowingAdvice joinpoint: " + joinPoint.toLongString());
		LOGGER.debug("afterThrowingAdvice throwable: {}" + throwable);

		AuditEventData auditEventData = null;
		OcpPartnerRuntimeException ocpException = null;

		try {
			if (throwable == null) {
				// null throwable almost certain not to happen, but check nonetheless
				throwable = new Throwable("Unknown (null) exception received in " + this.getClass().getName()
						+ ".afterTrhowingAdvice(..). Please check " + joinPoint.getSignature().getDeclaringTypeName()
						+ " to determine how a null Throwable could occur.");
			}

			if (OcpPartnerRuntimeException.class.isAssignableFrom(throwable.getClass())) {
				LOGGER.info("WsClientAspect ignores exceptions of type ["
						+ throwable.getClass().getName() + "] - not translating this exception.");
				ocpException = (OcpPartnerRuntimeException) throwable;
			} else {
				// figure out what exception type should replace throwable
				ocpException = super.resolvePartnerRuntimeException(throwable);
			}

			this.writeAuditError("afterThrowingAdvice", ocpException, auditEventData);

		} catch (Throwable t) { // NOSONAR intentionally catching throwable
			this.handleInternalException("afterThrowingAdvice", "writeResponseAudit", auditEventData, t);
		} finally {
			LOGGER.debug("afterThrowingAdvice finished.");
		}

		// rethrow the exception if possible
		if (ocpException != null) {
			throw ocpException;
		}
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
	private void handleInternalException(String adviceName, String attemptingTo,
			AuditEventData auditEventData, Throwable throwable) {
		try {
			final OcpRuntimeException ocpRuntimeException =
					new OcpRuntimeException("", adviceName + " - Exception occured while attempting to " + attemptingTo + ".",
							MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR,
							throwable);
			this.writeAuditError(adviceName, ocpRuntimeException, auditEventData);

		} catch (Throwable e) { // NOSONAR intentionally catching throwable
			this.handleAnyRethrownExceptions(adviceName, e);
		}
	}

	private void handleAnyRethrownExceptions(String adviceName, Throwable e) {
		LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
				adviceName + " - Throwable occured while attempting to writeAuditError for Throwable.", e);
		ProviderResponse body = new ProviderResponse();
		body.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.name(),
				adviceName + " - Throwable occured while attempting to writeAuditError for Throwable.",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Write into Audit when exceptions occur while attempting to log audit records.
	 *
	 * @param ocpRuntimeException
	 * @param auditEventData
	 * @return
	 */
	private void writeAuditError(final String adviceName, final OcpRuntimeException ocpRuntimeException,
			final AuditEventData auditEventData) {
		LOGGER.error(adviceName + " encountered uncaught exception.", ocpRuntimeException);
		final ProviderResponse providerResponse = new ProviderResponse();
		providerResponse.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				ocpRuntimeException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		final StringBuilder sb = new StringBuilder();
		sb.append("Error ServiceMessage: ").append(ocpRuntimeException);
		AuditLogger.error(auditEventData, sb.toString(), ocpRuntimeException);
	}

}
