package gov.va.ocp.framework.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.va.ocp.framework.messages.Message;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.transfer.AbstractTransferObject;
import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;

/**
 * A base Response object capable of representing the payload of a service response.
 *
 * @see gov.va.ocp.framework.transfer.AbstractTransferObject
 */
public class DomainResponse extends AbstractTransferObject implements DomainTransferObjectMarker {

	private static final long serialVersionUID = -3937937807439785385L;

	/** The messages. */
	private List<Message> messages;

	/*
	 * cacheResponse
	 *
	 * Must be ignored in the serialization and de-serialization
	 */
	@JsonIgnore
	private boolean doNotCacheResponse = false;

	/**
	 * Instantiates a new rest response.
	 */
	public DomainResponse() {
		super();
	}

	/**
	 * Adds a {@link Message} to the messages list on the response.
	 * <p>
	 * Messages made with this constructor CANNOT be used in a JSR303 context.
	 *
	 * @param severity the severity of the message
	 * @param key the key "code word" for support calls
	 * @param text the text of the message
	 * @param httpStatus the http status associated with the message
	 */
	public final void addMessage(final MessageSeverity severity, final String key, final String text,
			final HttpStatus httpStatus) {
		if (messages == null) {
			messages = new LinkedList<>();
		}
		final Message message = new Message();
		message.setSeverity(severity);
		message.setKey(key);
		message.setText(text);
		message.setHttpStatus(httpStatus);
		messages.add(message);
	}

	/**
	 * Adds a {@link Message} to the messages list on the response.
	 * <p>
	 * Messages made with this constructor CAN be used in a JSR303 context.
	 *
	 * @param severity the severity of the message
	 * @param key the key "code word" for support calls
	 * @param text the text of the message
	 * @param httpStatus the http status associated with the message
	 * @param paramCount the number of replaceable parameters in the message
	 * @param paramNames the names of the replaceable parameters in the message
	 * @param paramValues the values of the replaceable parameters in the message
	 */
	public final void addMessage(final MessageSeverity severity, final String key, final String text,
			final HttpStatus httpStatus,
			Integer paramCount, String[] paramNames, String[] paramValues) {
		if (messages == null) {
			messages = new LinkedList<>();
		}
		final Message message = new Message();
		message.setSeverity(severity);
		message.setKey(key);
		message.setText(text);
		message.setHttpStatus(httpStatus);
		message.setParamCount(paramCount);
		message.setParamNames(paramNames);
		message.setParamValues(paramValues);
		messages.add(message);
	}

	/**
	 * Adds all messages.
	 *
	 * @param newMessages the newMessages
	 */
	public final void addMessages(final List<Message> newMessages) {
		if (messages == null) {
			messages = new LinkedList<>();
		}
		messages.addAll(newMessages);
	}

	/**
	 * Gets the messages.
	 *
	 * @return the messages
	 */
	public final List<Message> getMessages() {
		if (messages == null) {
			messages = new LinkedList<>();
		}
		return this.messages;
	}

	/**
	 * Sets the messages.
	 *
	 * @param messages the new messages
	 */
	public final void setMessages(final List<Message> messages) {
		this.messages = messages;
	}

	/**
	 * Checks for messages of type.
	 *
	 * @param severity the severity
	 * @return true, if successful
	 */
	private boolean hasMessagesOfType(final MessageSeverity severity) {
		for (final Message message : getMessages()) {
			if (severity.equals(message.getSeverity())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for fatals.
	 *
	 * @return true, if successful
	 */
	public final boolean hasFatals() {
		return hasMessagesOfType(MessageSeverity.FATAL);
	}

	/**
	 * Checks for errors.
	 *
	 * @return true, if successful
	 */
	public final boolean hasErrors() {
		return hasMessagesOfType(MessageSeverity.ERROR);
	}

	/**
	 * Checks for warnings.
	 *
	 * @return true, if successful
	 */
	public final boolean hasWarnings() {
		return hasMessagesOfType(MessageSeverity.WARN);
	}

	/**
	 * Checks for infos.
	 *
	 * @return true, if successful
	 */
	public final boolean hasInfos() {
		return hasMessagesOfType(MessageSeverity.INFO);
	}

	/**
	 *
	 * @return
	 */
	public boolean isDoNotCacheResponse() {
		return doNotCacheResponse;
	}

	/**
	 *
	 * @param doNotcacheResponse
	 */
	public void setDoNotCacheResponse(final boolean doNotCacheResponse) {
		this.doNotCacheResponse = doNotCacheResponse;
	}

}