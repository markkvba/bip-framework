package gov.va.bip.framework.test.exception;

public class BipTestLibException extends Exception {

	private static final long serialVersionUID = 1L;

	public BipTestLibException(final String message, final Throwable t) {
		super(message, t);
	}

	public BipTestLibException(final String message) {
		super(message);
	}

}
