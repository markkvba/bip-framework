package gov.va.ocp.framework.constants;

/**
 * Collection of constants for use with annotations. This will help avoid duplicate literals
 * in the code.
 *
 * @author jluck
 * @version 1.0
 *
 */
public final class OcpConstants {
	/*
	 * OcpConstants for use with java.lang.SuppressWarnings to
	 * ignore unchecked class casting
	 *
	 * @see java.lang.SuppressWarnings
	 */

	/** Constant to suppress unchecked */
	public static final String UNCHECKED = "unchecked";

	/*
	 * OcpConstants specifically useful in exceptions
	 */

	/** Constant for Interceptor Exception banner text */
	public static final String INTERCEPTOR_EXCEPTION = "Interceptor Exception";
	/** Constant for ExceptionHandlingUtils ResolveRuntimeException banner text */
	public static final String RESOLVE_EXCEPTION = "ResolveRuntimeException Failed";
	/** */
	public static final String ILLEGALSTATE_STATICS = " is a class for statics. Do not instantiate it.";

	/*
	 * OcpConstants for MIME and Media Types
	 */

	/** MIME multipart/mixed */
	public static final String MIME_MULTIPART_MIXED = "multipart/mixed";

	/**
	 * This is a class for statics. Do not instantiate it.
	 */
	private OcpConstants() {
		throw new IllegalStateException(
				OcpConstants.class.getSimpleName() + " is a class for statics.  Do not instantiate it.");
	}
}
