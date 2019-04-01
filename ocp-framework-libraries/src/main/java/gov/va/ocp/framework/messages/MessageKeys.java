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

	/** Auditing error during cache operations */
	OCP_AUDIT_CACHE_ERROR("ocp.audit.cache.error", "An unexpected error occurred while auditing cache retrieval");

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
