package gov.va.ocp.framework.exception;

/**
 * Custom extension of RuntimeException so that we can raise this for exceptions we have no intention
 * of handling and need to raise but for some reason cannot raise
 * java's RuntimeException or allow the original exception to simply propagate.
 *
 * @author
 */
public class OcpFeignRuntimeException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2598842813684506356L;


	/** The key. */
	private String key;

	/** The message. */
	private String text;

	/** The Http status. */
	private String status;

	/** The message severity. */
	private String severity;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public OcpFeignRuntimeException(String key, String text, String status, String severity) {
		super();
		this.key = key;
		this.text = text;
		this.status = status;
		this.severity = severity;
	}




}
