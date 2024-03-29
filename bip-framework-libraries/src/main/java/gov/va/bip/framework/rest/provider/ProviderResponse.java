package gov.va.bip.framework.rest.provider;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpStatus;

import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.transfer.AbstractResponseObject;
import gov.va.bip.framework.transfer.ProviderTransferObjectMarker;

/**
 * A base Response object capable of representing the payload of a provider response.
 *
 * @see ProviderTransferObjectMarker
 *
 */
public class ProviderResponse extends AbstractResponseObject implements ProviderTransferObjectMarker, Serializable {
	private static final long serialVersionUID = -7175439119647120860L;

	/** The messages. */
	private List<Message> messages;

	/**
	 * Instantiates a new rest provider response.
	 */
	public ProviderResponse() {
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
		message.setSeverity(severity == null ? null : severity.name());
		message.setKey(key);
		message.setText(text);
		message.setStatus(httpStatus == null ? null : httpStatus.value());
		messages.add(message);
	}

	/**
	 * Adds all messages.
	 *
	 * @param messages the messages
	 */
	public final void addMessages(final List<Message> messages) {
		if (this.messages == null) {
			this.messages = new LinkedList<>();
		}
		this.messages.addAll(messages);
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
	 * Sets the messages by replacing any existing messages.
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
	@Override
	protected boolean hasMessagesOfType(final MessageSeverity severity) {
		for (final Message message : getMessages()) {
			if (severity.name().equals(message.getSeverity())) {
				return true;
			}
		}
		return false;
	}


}