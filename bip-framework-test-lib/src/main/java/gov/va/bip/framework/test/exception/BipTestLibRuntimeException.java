package gov.va.bip.framework.test.exception;

public class BipTestLibRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BipTestLibRuntimeException(final String message, final Throwable t) {
		super(message, t);
	}

	public BipTestLibRuntimeException(final String message) {
		super(message);
	}

}
