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

	/** JWT token cannot be blank; no args */
	OCP_SECURITY_TOKEN_BLANK("ocp.security.token.blank", "No JWT Token in Header."),
	/**
	 * JWT token cannot be blank; #{0} = the problem; {1} = the token; {2} = the simple class name of the exception {3} = message from
	 * the exception
	 */
	OCP_SECURITY_TOKEN_BROKEN("ocp.security.token.broken", "JWT Token is not valid."),
	/** Correlation IDs passed in the JWT token cannot be blank; no args */
	OCP_SECURITY_TRAITS_CORRELATIONID_BLANK("ocp.security.traits.correlationid.blank",
			"Cannot process blank correlation id."),
	/** Correlation IDs passed in the JWT token; {0} = ELEMENT_SS_COUNT, {1} = ELEMENT_MAX_COUNT */
	OCP_SECURITY_TRAITS_CORRELATIONID_INVALID("ocp.security.traits.correlationid.invalid",
			"Invalid number of elements in correlation id."),
	/** IdType specified does not exist; {0} = IdTypes.[value] */
	OCP_SECURITY_TRAITS_IDTYPE_INVALID("ocp.security.traits.idtype.invalid",
			"Specified IdType does not exist."),
	/** Issuer specified does not exist; {0} = Issuers.[value] */
	OCP_SECURITY_TRAITS_ISSUER_INVALID("ocp.security.traits.issuer.invalid",
			"Specified Issuer does not exist."),
	/** Source specified does not exist; {0} = Sources.[value] */
	OCP_SECURITY_TRAITS_SOURCE_INVALID("ocp.security.traits.source.invalid",
			"Specified Source does not exist."),
	/** UserStatus specified does not exist; {0} = UserStatus.[value] */
	OCP_SECURITY_TRAITS_USERSTATUS_INVALID("ocp.security.traits.userstatus.invalid",
			"Specified UserStatus does not exist."),

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
	public String getMessage(Object... param0) {
		return messageSource.getMessage(this.key, param0, this.defaultMessage, Locale.getDefault());
	}
}
