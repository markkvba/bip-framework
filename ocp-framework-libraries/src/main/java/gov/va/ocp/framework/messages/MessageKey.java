package gov.va.ocp.framework.messages;

/**
 * The interface for OCP message keys and their messages.
 * <p>
 * Implementations of this interface should use the spring MessageSource to retrieve message values.
 *
 * @author aburkholder
 */
public interface MessageKey {

	/**
	 * Get the key for this enumeration.
	 *
	 * @return String - the key
	 */
	public String getKey();

	/**
	 * Get the message resolved from the properties file, using the supplied params.
	 * <p>
	 * If the key for this enumeration is not found in the properties file,
	 * the default message is returned.
	 *
	 * @param params - an array of arguments that will be filled in for params within the message (params look like "{0}", "{1,date}",
	 *            "{2,time}" within a message), or null if none.
	 * @return String - the resolved message, or default message
	 */
	public String getMessage(Object... param0);
}
