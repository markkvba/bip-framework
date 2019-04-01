package gov.va.ocp.framework.messages;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * The official source for framework message keys and their messages.
 * <p>
 * This class derives its values from the *messages.properties file. It
 * is added to the spring context in
 *
 * @author aburkholder
 */
@Component
public enum MessageKeys implements MessageKey {

	/** No key provided or specified; no args */
	NO_KEY("NO_KEY", "Unknown, no key provided."),

	/** Auditing error during cache operations; {0} = advice name, {1} = operation attempted */
	OCP_AUDIT_CACHE_ERROR_UNEXPECTED("ocp.audit.cache.error.unexpected",
			"An unexpected error occurred while auditing cache retrieval."),
	/** Auditing error produced by the aspect/interceptor itself; {0} = advice name, {1} = operation attempted */
	OCP_AUDIT_ASPECT_ERROR_UNEXPECTED("ocp.audit.aspect.error.unexpected",
			"An unexpected error occurred while auditing from aspect/interceptor."),
	/** Auditing appears to be broken; {0} = advice name, {1} = originating throwable that called writeAuditError */
	OCP_AUDIT_ASPECT_ERROR_CANNOT_AUDIT("ocp.audit.aspect.error.cannot.audit",
			"Cannot write audit error for throwables."),

	/** Validator initialization; no args */
	OCP_VALIDATOR_INITIALIZE_ERROR_UNEXPECTED("ocp.validator.initialize.error.unexpected",
			"Could not initialize standard validator."),

	OCP_DEV_ILLEGAL_INSTANTIATION("ocp.dev.illegal.instantiation", "Do not instantiate static classes."),

	/** Exception handler cast failed; {0} = class name */
	OCP_EXCEPTION_HANDLER_ERROR_VALUES("ocp.exception.handler.error.values",
			"Could not instantiate OcpRuntimeException."),
	/** Exception handler cast failed; {0} = class name */
	OCP_EXCEPTION_HANDLER_ERROR_CAST("ocp.exception.handler.error.cast",
			"Could not cast throwable to OcpRuntimeException.");

	/** The key - must be identical to the key in framework-messages.properties */
	private String key;
	/** A default message, in case the key is not found in framework-messages.properties */
	private String defaultMessage;

	/**
	 * Construct keys with their property file counterpart key and a default message.
	 *
	 * @param key - the key as declared in the properties file
	 * @param defaultMessage - in case the key cannot be found
	 */
	private MessageKeys(String key, String defaultMessage) {
		this.key = key;
		this.defaultMessage = defaultMessage;
	}

	/** The spring message source */
	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.ocp.framework.messages.MessageKey#getKey()
	 */
	@Override
	public String getKey() {
		return this.key;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.ocp.framework.messages.MessageKey#getMessage(java.lang.Object[])
	 */
	@Override
	public String getMessage(Object[] params) {
		return messageSource.getMessage(this.key, params, this.defaultMessage, Locale.getDefault());
	}
}
