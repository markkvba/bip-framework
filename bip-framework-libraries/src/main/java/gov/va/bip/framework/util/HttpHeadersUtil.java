package gov.va.bip.framework.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Utility class for HTTP Headers in BIP framework
 */
public final class HttpHeadersUtil {

	/**
	 * Constructor to prevent instantiation.
	 */
	private HttpHeadersUtil() {
		throw new IllegalAccessError("HttpHeadersUtil is a static class. Do not instantiate it.");
	}
	
	
	/**
	 * Builds the error http headers.
	 *
	 * @return the http headers
	 */
	public static HttpHeaders buildHttpHeadersForError() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_PROBLEM_JSON);
		return responseHeaders;
	}
}
