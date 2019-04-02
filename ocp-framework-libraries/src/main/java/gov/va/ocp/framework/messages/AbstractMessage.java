package gov.va.ocp.framework.messages;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ServiceMessage is a generic abstraction of a "message" or "notification" which is layer agnostic and can be used to communicate
 * status or
 * other sorts of information during method calls between components/layers. This is serializable and can be used in SOAP or REST
 * calls. This class has param names and valsues as lists.
 * <p>
 * This class can be extended to provide expression names and values used in messages
 * that have replaceable parameters, e.g. "Some {0} message with {1} parameters".
 * <p>
 * Current use-case is for message transmission back to JSR303 validation constraint messages.
 *
 * @author vanapalliv
 */
public abstract class AbstractMessage implements Serializable {

	/** The Constant serialVersisonUID. */
	private static final long serialVersionUID = -1711431368372127556L;

	private ConstraintParam[] constraintParameters; // NOSONsAR cannot be final

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private Date timestamp; // NOSONAR cannot be final

	/**
	 * Construct default (empty) message object.
	 */
	public AbstractMessage() {
		super();
		this.timestamp = new Date();
	}

	/**
	 * Construct a message object containing an array of contraint parameters.
	 */
	public AbstractMessage(ConstraintParam[] constraintParams) {
		this(); // set the timestamp
		this.constraintParameters = constraintParams;
	}

	/**
	 * An array of the constraint parameter names in a message.
	 * The names are those found between the curly braces of replaceable parameters.
	 *
	 * @return ConstraintParam the constraint parameter
	 */
	@JsonIgnore
	@JsonProperty(value = "constraintParameters")
	public ConstraintParam[] getConstraintParams() {
		return this.constraintParameters;
	}

	/**
	 * Number of elements in the getParamNames() and getParamValues() arrays.
	 *
	 * @return Integer the number of elements in the arrays
	 */
	@JsonIgnore
	@JsonProperty(value = "parameterCount")
	public Integer getParamCount() {  	// NOSONAR not duplicate
		return constraintParameters == null ? 0 : constraintParameters.length;
	}

	/**
	 * Gets the timestamp to be part of message payload.
	 *
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp for the message payload.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}