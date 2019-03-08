package gov.va.ocp.framework.exception.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.slf4j.event.Level;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.constants.AnnotationConstants;
import gov.va.ocp.framework.exception.OcpExceptionExtender;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpBanner;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;

/**
 * This is a configurable interceptor which will catch and translate one exception type into another. This is useful if
 * you wish to &quot;wrap&quot; 3rd party framework exceptions for a &quot;suite&quot; of beans, and/or if you wish to
 * make sure every exception coming from a tier in your application is of a specific exception type.
 *
 * In general, exceptions will be converted to some application specific equivalent.
 *
 *
 * @see org.aopalliance.intercept.MethodInterceptor
 *
 * @author Jon Shrader
 */
public class InterceptingExceptionTranslator implements ThrowsAdvice {

	/** logger for this class. */
	private static final OcpLogger LOGGER = OcpLoggerFactory.getLogger(InterceptingExceptionTranslator.class);

	/**
	 * A set of packages and/or exception class names we should exclude during translation.
	 */
	private Set<String> exclusionSet;

	/**
	 * The default exception to raise in the event its not a success and also not a mapped exception type.
	 */
	private Class<? extends OcpRuntimeException> defaultExceptionType;

	/**
	 * Log the exception, and rethrow a some sort of application exception.
	 *
	 * DO NOT REMOVE "target" ARGUMENT IN THE METHOD, AS REMOVING IT BREAKS END POINT CALLS
	 *
	 * @param method the method
	 * @param args the args
	 * @param target the args
	 * @param throwable the throwable
	 */
	public final void afterThrowing(final Method method, final Object[] args, final Object target, final Throwable throwable) {
		// set default return type if none was specified
		if (defaultExceptionType == null) {
			defaultExceptionType = OcpRuntimeException.class;
		}

		// skip conversion if the throwable is in the exclusion set
		try {
			if (exclusionSet != null
					&& (exclusionSet.contains(throwable.getClass().getPackage().getName()) || exclusionSet
							.contains(throwable.getClass().getName()))) {
				if (LOGGER.isDebugEnabled()) {
					InterceptingExceptionTranslator.LOGGER.debug("Exception translator caught exception ["
							+ throwable.getClass() + "] however per configuration not translating this exception.");
				}
				// let the throwable bubble up through the app untouched
				return;

			} else {
				ExceptionHandlingUtils.logException("InterceptingExceptionTranslator", method, args, throwable);
			}

			// figure out what exception type should replace throwable
			final RuntimeException resolvedRuntimeException = resolveRuntimeException(throwable);
			if (resolvedRuntimeException != null) {
				// override the throwable, and send resolvedRuntimeException instead
				throw resolvedRuntimeException;
			}

		} catch (final InstantiationException e) {
			InterceptingExceptionTranslator.LOGGER.error(
					"InstantiationException likely configuration error, review log/configuration to troubleshoot", e);
			LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
					"InstantiationException likely configuration error, review log/configuration to troubleshoot", e);

		} catch (final IllegalAccessException e) {
			InterceptingExceptionTranslator.LOGGER.error(
					"IllegalAccessException likely configuration error, review log/configuration to troubleshoot", e);
			LOGGER.error(OcpBanner.newBanner(AnnotationConstants.INTERCEPTOR_EXCEPTION, Level.ERROR),
					"InstantiationException likely configuration error, review log/configuration to troubleshoot", e);

		}
	}

	/**
	 * Resolve the runtime exception for the throwable
	 *
	 * @param throwable the throwable
	 * @return the runtime exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private OcpRuntimeException resolveRuntimeException(final Throwable throwable) throws InstantiationException,
			IllegalAccessException {
		// custom exception type to represent the error
		OcpRuntimeException resolvedRuntimeException = null;

		if (OcpRuntimeException.class.isAssignableFrom(throwable.getClass())) {
			// have to cast so the "Throwable throwable" variable can be returned as-is
			try {
				resolvedRuntimeException = (OcpRuntimeException) throwable;
			} catch (ClassCastException e) {
				String msg = "Could not cast " + throwable.getClass().getName() + " to OcpRuntimeException";
				LOGGER.error(msg, e);
				throw new InstantiationException(msg);
			}

		} else if (OcpExceptionExtender.class.isAssignableFrom(throwable.getClass())) {
			try {
				// cast "Throwable throwable" variable to the OCP exception interface
				OcpExceptionExtender ocp = (OcpExceptionExtender) throwable;
				// instantiate the Runtime version of the interface
				resolvedRuntimeException = (OcpRuntimeException) throwable.getClass()
						.getConstructor(String.class, String.class, MessageSeverity.class, HttpStatus.class, Throwable.class)
						.newInstance(ocp.getKey(), throwable.getMessage(), ocp.getSeverity(), ocp.getStatus(), throwable);
			} catch (ClassCastException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				String msg = "Could not instantiate OcpRuntimeException using values from throwable "
						+ throwable.getClass().getName();
				LOGGER.error(msg, e);
				throw new InstantiationException(msg);
			}

		} else {
			// make a new OcpRuntimeException from the non-OCP throwable
			resolvedRuntimeException =
					new OcpRuntimeException("", throwable.getMessage(), MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, throwable);
		}

		return resolvedRuntimeException;
	}

	/**
	 * Sets the default exception type.
	 *
	 * @param defaultExceptionType the defaultExceptionType to set
	 */
	public final void setDefaultExceptionType(final Class<? extends OcpRuntimeException> defaultExceptionType) {
		this.defaultExceptionType = defaultExceptionType;
	}

	/**
	 * Sets the exclusion set.
	 *
	 * @param exclusionSet the exclusionSet to set
	 */
	public final void setExclusionSet(final Set<String> exclusionSet) {
		this.exclusionSet = exclusionSet;
	}

}