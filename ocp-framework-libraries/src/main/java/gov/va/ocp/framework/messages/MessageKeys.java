package gov.va.ocp.framework.messages;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * A message @PropertySource for Service Ocp*Exception and *Message list.
 * <p>
 * This class derives its values from the framework-messages.properties file.
 * that is added to the spring context as an {@code @PropertySource}.
 * Primarily used by
 * <p>
 * <u>Usage and Maintenance</u><br/>
 * Any change in framework-messages.properties must be reflected in this class.<br/>
 * Any change in this class must be reflected in framework-messages.properties.
 *
 * @author aburkholder
 */
public enum MessageKeys implements MessageKey {

	/** No key provided or specified; no args */
	NO_KEY("NO_KEY", "Unknown, no key provided."),
	/** Key for warning messages; {0} = warning message */
	WARN_KEY("WARN", ""),
	/** Key for propagating exceptions as OCPExeptionExtender; {0} = exception message */
	PROPAGATE("PROPAGATE", ""),

	/** Problem with reflection; {0} = class simple name */
	OCP_DEV_ILLEGAL_INSTANTIATION("ocp.dev.illegal.instantiation", "Do not instantiate static classes."),
	/**
	 * Problem with reflection; {0} = class being instantiated; {1} = action being taken; {2} = type being acted against; {3}
	 * super-interface
	 */
	OCP_DEV_ILLEGAL_INVOCATION("ocp.dev.illegal.invocation", "Could not find or instantiate class."),

	/** Last resort, unexpected exception; {0} = exception class simple name; {1} = exception message */
	OCP_GLOBAL_GENERAL_EXCEPTION("ocp.global.general.exception", "Unexpected exception."),
	/** Exception handler cast failed; {0} = class name */
	OCP_EXCEPTION_HANDLER_ERROR_VALUES("ocp.exception.handler.error.values.",
			"Could not instantiate OcpRuntimeException."),
	/** Exception handler cast failed; {0} = class name */
	OCP_EXCEPTION_HANDLER_ERROR_CAST("ocp.exception.handler.error.cast",
			"Could not cast throwable to OcpRuntimeException."),
	/** MethodArgumentNotValidException; {0} = "field" or "object"; {1} = codes; {2} = default message */
	OCP_GLOBAL_VALIDATOR_METHOD_ARGUMENT_NOT_VALID("ocp.global.validator.method.argument.not.valid", "Argument not valid."),
	/** HttpClientErrorException; {0} = http status code; {1} = exception message */
	OCP_GLOBAL_HTTP_CLIENT_ERROR("ocp.global.http.client.error", "Client Error."),
	/** MethodArgumentTypeMismatchException; {0} = argument name; {1} = expected class name */
	OCP_GLOBAL_REST_API_TYPE_MISMATCH("ocp.global.rest.api.type.mismatch", "API argument type could not be resolved."),
	/** ConstraintViolationException; {0} = bean class name; {1} = property name; {2} = violation message */
	OCP_GLBOAL_VALIDATOR_CONSTRAINT_VIOLATION("ocp.global.validator.constraint.violation", "Validation constraint was violated."),

	/** JAXB Marshaller configuration failed; no args */
	OCP_REST_CONFIG_JAXB_MARSHALLER_FAIL("ocp.rest.config.jaxb.marshaller.failed", "Error configuring JAXB marshaller."),
	/** WebserviceTemplate configuration failed; no args */
	OCP_REST_CONFIG_WEBSERVICE_TEMPLATE_FAIL("ocp.rest.config.webservice.template.failed",
			"Unexpected exception thrown by WebServiceTemplate."),
	/** Propogate message from other service; {0} = message key; {1} = message text */
	OCP_FEIGN_MESSAGE_RECEIVED("ocp.feign.message.received", "External service returned error message."),

	/** JWT token is invalid; no args */
	OCP_SECURITY_TOKEN_INVALID("ocp.security.token.invalid", "Invalid Token."),
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
	/** Encryption failed for some reason; {0} = kind of object that was being encrypted */
	OCP_SECURITY_ENCRYPT_FAIL("ocp.security.encrypt.failed", "Encryption failed."),
	/** Signing failed for some reason; {0} = kind of object that was being signed */
	OCP_SECURITY_SIGN_FAIL("ocp.security.sign.failed", "Could not sign."),
	/** Encryption failed for some reason; {0} = action being taken on the attribute; {1} attribute name */
	OCP_SECURITY_ATTRIBUTE_FAIL("ocp.security.attribute.failed", "Could not modify attribute."),
	/** SAML insertion failed; no args */
	OCP_SECURITY_SAML_INSERT_FAIL("ocp.security.saml.insert.failed", "SAML insertion failed."),
	/** SSL initialization failed {0} = exception simple class name; {1} = exception message */
	OCP_SECURITY_SSL_CONTEXT_FAIL("ocp.security.ssl.context.failed", "Could not establish SSL context."),

	/** Sanitizing filename failed; no args */
	OCP_SECURITY_SANITIZE_FAIL("ocp.security.sanitize.failed", "Unexpected error."),

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
	OCP_VALIDATOR_ASSERTION("ocp.validator.assertion", "Assertion failed."),
	/** Object cannot be null; {0} the object that cannot be null */
	OCP_VALIDATOR_NOT_NULL("ocp.validator.not.null", "Object cannot be null."),
	/** {0} = validated object class name; {1} = expected class name */
	OCP_VALIDATOR_TYPE_MISMATCH("ocp.validator.type.mismatch", "Validated object is not of excpected type."),

	/** Simulator could not find mock response file; {0} = XML file name; {1} = key used to construct file name */
	OCP_REMOTE_MOCK_NOT_FOUND("ocp.remote.mock.not.found",
			"Could not read mock XML file. Please make sure the correct response file exists in the main/resources directory."),
	/**
	 * RemoteServiceCallMock is not set up to process a type; {0} = the RemoteServiceCallMock class; {1} = the class used in the
	 * request
	 */
	OCP_REMOTE_MOCK_UNKNOWN("ocp.remote.mock.unknown.type",
			"RemoteServiceCallMock getKeyForMockResponse(..) does not have a file naming block for request type.")

	;

	/** The filename "name" part of the properties file to get from the classpath */
	private static final String propertiesFile = "framework-messages";
	/** The spring message source */
	private static ReloadableResourceBundleMessageSource messageSource;
	/* Populate the message source from the properties file */
	static {
		messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:" + propertiesFile);
		messageSource.setDefaultEncoding("UTF-8");
	}

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
	public String getMessage(Object... params) {
		return messageSource.getMessage(this.key, params, this.defaultMessage, Locale.getDefault());
	}
}
