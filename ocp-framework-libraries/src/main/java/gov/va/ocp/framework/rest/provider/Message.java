package gov.va.ocp.framework.rest.provider;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.http.HttpStatus;

import gov.va.ocp.framework.messages.MessageSeverity;

public class Message {

	/** The Constant serialVersionUID. */

	/**
	 * The text is excluded from equals and hash as the key+severity are to jointly indicate a unique message. The text is supplemental
	 * information.
	 */
	private static final String[] EQUALS_HASH_EXCLUDE_FIELDS = new String[] { "text" };

	/** The key. */
	@NotNull
	private String key;

	/** The message. */
	private String text;

	/** The Http status. */
	private Integer status;

	/** The message severity. */
	@NotNull
	private String severity;

	/**
	 * Instantiates a new message.
	 */
	public Message() { // NOSONAR @NotNull is a validation annotation, not a usage annotation
		super(); // NOSONAR @NotNull is a validation annotation, not a usage annotation
	} // NOSONAR @NotNull is a validation annotation, not a usage annotation

	/**
	 * Instantiates a new message.
	 * <p>
	 * Severity <b>must</b> be a case-sensitive match for a member of the {@link MessageSeverity} enum.
	 * <br/>
	 * Key <b>must</b> match a key from ???? properties, as declared in {@link ????} swagger constants class.
	 * <br/>
	 * HttpStatus <b>must</b> match an int value contained in the {@link HttpStatus} enum.
	 *
	 * @param severity the severity for the cause of the message
	 * @param key the key representing the "error code" for the message
	 * @param text the text of the message
	 * @param httpStatus the http status associated with the cause of the message
	 */
	public Message(final String severity, final String key, final String text, final Integer httpStatus) {
		super();
		this.severity = severity;
		this.key = key;
		this.text = text;
		this.status = httpStatus;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public final String getKey() {
		return this.key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public final void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Gets the Http status code.
	 *
	 * @return the Http status code
	 */
	public String getStatus() {
		// Since this method is used by introspection based serialisation, it would need to return the status code number instead of
		// the default (enum name), which is why the toString() method is used
		return status == null ? null : status.toString();
	}

	/**
	 * Sets the HttpStatus.
	 *
	 * @param status the new HttpStatus
	 */
	public void setStatus(final Integer status) {
		this.status = status;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public final String getText() {
		return this.text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public final void setText(final String text) {
		this.text = text;
	}

	/**
	 * Gets the message severity.
	 *
	 * @return the message severity
	 */
	public final String getSeverity() {
		return this.severity;
	}

	/**
	 * Sets the message severity.
	 *
	 * @param severity the new message severity
	 */
	public final void setSeverity(final String severity) {
		this.severity = severity;
	}

	/**
	 * Get the {@link HttpStatus} enumeration for the status Integer.
	 * If the status integer is null, then this method returns
	 * 201 {@link HttpStatus#CREATED}.
	 *
	 * @param status the integer status
	 * @return the HttpStatus
	 */
	public final HttpStatus getHttpStatus(Integer status) {
		if (status == null) {
			return HttpStatus.CREATED;
		}
		return HttpStatus.valueOf(status);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.ocp.framework.transfer.AbstractTransferObject#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, EQUALS_HASH_EXCLUDE_FIELDS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.ocp.framework.transfer.AbstractTransferObject#hashCode()
	 */
	@Override
	public final int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, EQUALS_HASH_EXCLUDE_FIELDS);
	}

	/**
	 * Returns a String that shows the full content of the Message.
	 */
	@Override
	public final String toString() {
		return severity + " " + key + "(" + status + "): " + text;
	}

}
