package gov.va.ocp.framework.cache.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.audit.AuditEventData;
import gov.va.ocp.framework.audit.AuditEvents;
import gov.va.ocp.framework.audit.AuditLogSerializer;
import gov.va.ocp.framework.audit.AuditLogger;
import gov.va.ocp.framework.audit.ResponseAuditData;
import gov.va.ocp.framework.constants.AnnotationConstants;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.service.DomainResponse;

/**
 * Audit cache GET operations.
 * <p>
 * This interceptor is equivalent to an Around aspect, so the implementation
 * must call joinPoint.proceed(). This is accomplished by calling
 * {@code super.invoke(invocation)}.
 *
 * @author aburkholder
 */
public class OcpCacheInterceptor extends CacheInterceptor {
	private static final long serialVersionUID = -7142978196480878033L;

	/** Class logger */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(OcpCacheInterceptor.class);

	private static final String INVOKE_INTERCEPTOR = "CacheInterceptorInvoke";

	/** The {@link AuditLogSerializer} for async logging */
	@Autowired
	protected AuditLogSerializer asyncLogging;

	/**
	 * Instantiate an OcpCacheInterceptor.
	 */
	public OcpCacheInterceptor() {
		LOGGER.debug("Instantiating " + OcpCacheInterceptor.class.getName());
	}

	/**
	 * Perform audit logging after the method has been called.
	 * <p>
	 * This interceptor is equivalent to an Around aspect, so the implementation
	 * must call joinPoint.proceed(). This is accomplished by calling
	 * {@code super.invoke(invocation)}.
	 */
	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		DomainResponse domainResponse = null;

		Class<?> underAudit = invocation.getThis().getClass();
		AuditEventData auditEventData = new AuditEventData(AuditEvents.CACHED_SERVICE_RESPONSE, "cacheGet", underAudit.getName());

		try {
			Object returning = super.invoke(invocation);
			if (returning == null) {
				domainResponse = new DomainResponse();
			} else if (!DomainResponse.class.isAssignableFrom(returning.getClass())) {

			}

			Method method = invocation.getMethod();
			LOGGER.debug(this.getClass().getSimpleName() + ".invoke(..)"
					+ "\n\tmethod: " + method.getName()
					+ "\n\targs: " + Arrays.toString(invocation.getArguments())
					+ "\n\treturning: " + ReflectionToStringBuilder.toString(returning));

			ResponseAuditData auditData = new ResponseAuditData();
			auditData.setResponse(returning);
			asyncLogging.asyncLogRequestResponseAspectAuditData(auditEventData, auditData, ResponseAuditData.class,
					MessageSeverity.INFO, null);
		} catch (Throwable throwable) { // NOSONAR intentionally catching throwable
			this.handleInternalException(domainResponse, INVOKE_INTERCEPTOR, INVOKE_INTERCEPTOR, auditEventData, throwable);
		} finally {
			LOGGER.debug(INVOKE_INTERCEPTOR + " finished.");
		}

		return domainResponse;
	}

	/**
	 * Standard handling of exceptions that are thrown from within the advice
	 * (not exceptions thrown by application code).
	 *
	 * @param responseToProvider the DomainResponse being returned to the provider
	 * @param adviceName the name of the advice method in which the exception was thrown
	 * @param attemptingTo the attempted task that threw the exception
	 * @param auditEventData the audit event data object
	 * @param throwable the exception that was thrown
	 */
	protected void handleInternalException(final DomainResponse responseToProvider,
			final String adviceName, final String attemptingTo,
			final AuditEventData auditEventData, final Throwable throwable) {

		try {
			String msg = adviceName + " - Exception occured while attempting to " + attemptingTo + ".";
			LOGGER.error(msg, throwable);
			final OcpRuntimeException ocpRuntimeException =
					new OcpRuntimeException("", msg,
							MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR,
							throwable);
			writeAuditError(responseToProvider, adviceName, ocpRuntimeException, auditEventData);

		} catch (Throwable e) { // NOSONAR intentionally catching throwable
			handleAnyRethrownExceptions(responseToProvider, adviceName, e);
		}
	}

	/**
	 * If - after attempting to audit an internal error - another exception is thrown,
	 * then put the whole mess on a response entity Message and return it.
	 *
	 * @param adviceName
	 * @param e
	 * @return ResponseEntity
	 */
	private void handleAnyRethrownExceptions(final DomainResponse responseToProvider,
			final String adviceName, final Throwable e) {

		String msg = adviceName + " - Throwable occured while attempting to writeAuditError for Throwable.";
		LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
				msg, e);

		responseToProvider.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.name(),
				msg, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Write into Audit when exceptions occur while attempting to log audit records.
	 *
	 * @param ocpRuntimeException
	 * @param auditEventData
	 * @return
	 */
	private void writeAuditError(final DomainResponse responseToProvider,
			final String adviceName, final OcpRuntimeException ocpRuntimeException,
			final AuditEventData auditEventData) {

		LOGGER.error(adviceName + " encountered uncaught exception.", ocpRuntimeException);

		responseToProvider.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
				ocpRuntimeException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		AuditLogger.error(auditEventData,
				"Error ServiceMessage: " + ocpRuntimeException.getMessage(),
				ocpRuntimeException);
	}
}
