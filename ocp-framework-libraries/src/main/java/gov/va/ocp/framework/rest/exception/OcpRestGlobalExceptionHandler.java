package gov.va.ocp.framework.rest.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MediaTypeNotSupportedStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.va.ocp.framework.exception.OcpExceptionExtender;
import gov.va.ocp.framework.exception.OcpPartnerException;
import gov.va.ocp.framework.exception.OcpPartnerRuntimeException;
import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.messages.MessageKey;
import gov.va.ocp.framework.messages.MessageKeys;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.rest.provider.ProviderResponse;

/**
 * A global exception handler as the last line of defense before sending response to the service consumer.
 * This class converts exceptions to appropriate {@link gov.va.ocp.framework.rest.provider.Message} objects and puts them on the
 * response.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OcpRestGlobalExceptionHandler {

	/** The Constant LOGGER. */
	private static final Logger logger = LoggerFactory.getLogger(OcpRestGlobalExceptionHandler.class);

	/**
	 * Return value if no exception exists to provide a message.
	 * To get default message text, use {@link #deriveMessage(Exception)}.
	 */
	private static final String NO_EXCEPTION_MESSAGE = "Source exception has no message.";

	/**
	 * For java.lang.Exception and all subclasses.
	 * If exception message is empty, gets message of the cause if it exists.
	 *
	 * @param ex the Exception
	 * @return String the message
	 */
	private String deriveMessage(final Exception ex, final MessageKey key, final Object... params) {
		MessageKey msgkey = deriveKey(key);
		String msg = msgkey.getMessage(params);
		if (StringUtils.isBlank(msg) || msg.matches("\\{[a-zA-Z0-9]{0,64}\\}")) {
			msg = msg + " :: "
					+ ((ex != null) && !StringUtils.isBlank(ex.getMessage())
							? ex.getMessage()
									: ((ex != null) && (ex.getCause() != null) && !StringUtils.isBlank(ex.getCause().getMessage())
											? ex.getCause().getMessage()
													: NO_EXCEPTION_MESSAGE));
		}
		return msg;
	}

	/**
	 * If key is null, returns the "NO-KEY" key.
	 *
	 * @param key - the initial string intended to represent the key
	 * @return MessageKey - the key, or NO_KEY
	 */
	private MessageKey deriveKey(final MessageKey key) {
		return key == null ? MessageKeys.NO_KEY : key;
	}

	/**
	 * INFO logs the exception and its details.
	 *
	 * @param ex - the exception
	 * @param key - the key to use for reporting to support/maintenance
	 * @param severity - the MessageSeverity to report for the exception
	 * @param status - the status to report for the exception
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 */
	private void log(final Exception ex, final MessageKey key, final MessageSeverity severity, final HttpStatus status, final Object... params) {
		log(Level.INFO, ex, key, severity, status, params);
	}

	/**
	 * Logs the exception and its details.
	 *
	 * @param level - the Log Level to log at
	 * @param ex - the exception
	 * @param key - the key to use for reporting to support/maintenance
	 * @param severity - the MessageSeverity to report for the exception
	 * @param status - the status to report for the exception
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 */
	private void log(final Level level, final Exception ex, final MessageKey key, final MessageSeverity severity, final HttpStatus status, final Object... params) {
		String msg = status + "-" + severity + " "
				+ (ex == null ? "null" : ex.getClass().getName()) + " "
				+ deriveKey(key) + ":" + key.getMessage(params);
		if (Level.ERROR.equals(level)) {
			logger.error(msg, ex);
		} else if (Level.WARN.equals(level)) {
			logger.warn(msg, ex);
		} else if (Level.INFO.equals(level)) {
			logger.info(msg, ex);
		} else {
			logger.debug(msg, ex);
		}
	}

	/**
	 * A last resort to return a (somewhat) meaningful response to the consumer when there is no source exception.
	 *
	 * @return ResponseEntity the HTTP Response Entity
	 */
	protected ResponseEntity<Object> failSafeHandler() {
		log(Level.ERROR, null, MessageKeys.NO_KEY, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
		ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.FATAL, MessageKeys.NO_KEY.getKey(), MessageKeys.NO_KEY.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Standard exception handler for any exception that implements {@link OcpExceptionExtender}.
	 *
	 * @param ex - the exception that implements OcpExceptionExtender
	 * @param httpResponseStatus - the status to put on the HTTP Response Entity.
	 * @return ResponseEntity - the HTTP Response Entity
	 */
	protected ResponseEntity<Object> standardHandler(final OcpExceptionExtender ex, final HttpStatus httpResponseStatus) {
		if ((ex == null) || (ex.getMessageKey() == null)) {
			return failSafeHandler();
		}
		return standardHandler((Exception) ex, ex.getMessageKey(), ex.getSeverity(), httpResponseStatus);
	}

	/**
	 * Standard exception handler for any Exception.
	 *
	 * @param ex - the Exception
	 * @param key - the key to use for reporting to support/maintenance
	 * @param severity - the MessageSeverity to report for the exception
	 * @param httpResponseStatus - the status to put on the HTTP Response Entity.
	 * @param params - arguments to fill in any params in the MessageKey message (e.g. value for {0})
	 * @return ResponseEntity the HTTP Response Entity
	 */
	protected ResponseEntity<Object> standardHandler(final Exception ex, final MessageKey key, final MessageSeverity severity,
			final HttpStatus httpResponseStatus, final Object... params) {
		if (ex == null) {
			return failSafeHandler();
		}
		ProviderResponse apiError = new ProviderResponse();

		MessageKey msgkey = deriveKey(key);
		log(ex, msgkey, severity, httpResponseStatus, params);
		apiError.addMessage(severity, msgkey.getKey(), deriveMessage(ex, msgkey, params), httpResponseStatus);

		return new ResponseEntity<>(apiError, httpResponseStatus);
	}

	// 400

	/**
	 * Handle OcpPartnerRuntimeException.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = OcpPartnerRuntimeException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleOcpPartnerRuntimeException(final HttpServletRequest req, final OcpPartnerRuntimeException ex) {
		return standardHandler(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle OcpPartnerException.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = OcpPartnerException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleOcpPartnerCheckedException(final HttpServletRequest req, final OcpPartnerException ex) {
		return standardHandler(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle illegal argument exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleIllegalArgumentException(final HttpServletRequest req, final IllegalArgumentException ex) {
		return standardHandler(ex, MessageKeys.NO_KEY, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle illegal state exceptions - developer attempting to instantiate a class that is for statics.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = IllegalStateException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleIllegalStateException(final HttpServletRequest req, final IllegalStateException ex) {
		return standardHandler(ex, MessageKeys.OCP_DEV_ILLEGAL_INSTANTIATION, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle {@code @Valid} failures when method argument is not valid.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleMethodArgumentNotValidException(final HttpServletRequest req,
			final MethodArgumentNotValidException ex) {

		final ProviderResponse apiError = new ProviderResponse();
		if ((ex == null) || (ex.getBindingResult() == null)) {
			return failSafeHandler();
		} else {
			MessageKey key = MessageKeys.OCP_GLOBAL_VALIDATOR_METHOD_ARGUMENT_NOT_VALID;
			for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
				String errorCodes = String.join(", ", error.getCodes());
				Object[] params = new Object[] { "field", errorCodes, error.getDefaultMessage() };
				log(ex, key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, params);
				apiError.addMessage(MessageSeverity.ERROR, errorCodes,
						error.getDefaultMessage(), HttpStatus.BAD_REQUEST);
			}
			for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
				String errorCodes = String.join(", ", error.getCodes());
				Object[] params = new Object[] { "object", errorCodes, error.getDefaultMessage() };
				log(ex, key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, params);
				apiError.addMessage(MessageSeverity.ERROR, errorCodes,
						error.getDefaultMessage(), HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle HTTP 4xx response in http client.
	 *
	 * @param req the req
	 * @param httpClientErrorException the http client error exception
	 * @return the response entity
	 */
	@ExceptionHandler(value = HttpClientErrorException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleHttpClientErrorException(final HttpServletRequest req,
			final HttpClientErrorException httpClientErrorException) {

		ProviderResponse apiError = new ProviderResponse();
		if (httpClientErrorException == null) {
			return failSafeHandler();
		} else {
			MessageKey key = MessageKeys.OCP_GLOBAL_HTTP_CLIENT_ERROR;
			HttpStatus status = httpClientErrorException.getStatusCode();
			String statusReason = httpClientErrorException.getStatusCode().getReasonPhrase();
			Object[] params = new Object[] { statusReason, httpClientErrorException.getMessage() };

			log(httpClientErrorException, key, MessageSeverity.ERROR, status, params);

			byte[] responseBody = httpClientErrorException.getResponseBodyAsByteArray();

			try {
				apiError = new ObjectMapper().readValue(responseBody, ProviderResponse.class);
			} catch (IOException e) {
				log(e, MessageKeys.OCP_GLOBAL_GENERAL_EXCEPTION, MessageSeverity.ERROR, status,
						params);
				apiError.addMessage(MessageSeverity.ERROR, key.getKey(),
						new String(responseBody),
						httpClientErrorException.getStatusCode());
			}
		}

		return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle method argument type mismatch.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleMethodArgumentTypeMismatch(final HttpServletRequest req,
			final MethodArgumentTypeMismatchException ex) {

		MessageKey key = MessageKeys.OCP_GLOBAL_REST_API_TYPE_MISMATCH;
		Object[] params = new Object[] { ex.getName(), ex.getRequiredType().getName() };

		log(Level.INFO, ex, key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, params);

		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, key.getKey(),
				key.getMessage(params), HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle constraint violation.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = ConstraintViolationException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleConstraintViolation(final HttpServletRequest req, final ConstraintViolationException ex) {

		final ProviderResponse apiError = new ProviderResponse();
		if ((ex == null) || (ex.getConstraintViolations() == null)) {
			return failSafeHandler();
		} else {
			MessageKey key = MessageKeys.OCP_GLBOAL_VALIDATOR_CONSTRAINT_VIOLATION;
			for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
				Object[] params =
						new Object[] { violation.getRootBeanClass().getName(), violation.getPropertyPath(), violation.getMessage() };
				log(ex, key, MessageSeverity.ERROR, HttpStatus.BAD_REQUEST, params);
				apiError.addMessage(MessageSeverity.ERROR, key.getKey(),
						key.getMessage(params), HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle http message not readable exception.
	 *
	 * @param req the req
	 * @param httpMessageNotReadableException the http message not readable exception
	 * @return the response entity
	 */
	@ExceptionHandler(value = HttpMessageNotReadableException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleHttpMessageNotReadableException(final HttpServletRequest req,
			final HttpMessageNotReadableException httpMessageNotReadableException) {
		return standardHandler(httpMessageNotReadableException, MessageKeys.NO_KEY, MessageSeverity.ERROR,
				HttpStatus.BAD_REQUEST);
	}

	// 404

	/**
	 * Handle no handler found exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = NoHandlerFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public final ResponseEntity<Object> handleNoHandlerFoundException(final HttpServletRequest req, final NoHandlerFoundException ex) {
		return standardHandler(ex, MessageKeys.NO_KEY, MessageSeverity.ERROR, HttpStatus.NOT_FOUND);
	}

	// 405

	/**
	 * Handle http request method not supported.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public final ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpServletRequest req,
			final HttpRequestMethodNotSupportedException ex) {
		return standardHandler(ex, MessageKeys.NO_KEY, MessageSeverity.ERROR, HttpStatus.METHOD_NOT_ALLOWED);
	}

	// 415

	/**
	 * Handle http media type not supported.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = MediaTypeNotSupportedStatusException.class)
	@ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	public final ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpServletRequest req,
			final HttpMediaTypeNotSupportedException ex) {
		return standardHandler(ex, MessageKeys.NO_KEY, MessageSeverity.ERROR, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	// Handle all

	/**
	 * Handle ocp runtime exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = OcpRuntimeException.class)
	public final ResponseEntity<Object> handleOcpRuntimeException(final HttpServletRequest req, final OcpRuntimeException ex) {
		return standardHandler(ex, ex.getStatus());
	}

	/**
	 * Handle all.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public final ResponseEntity<Object> handleAll(final HttpServletRequest req, final Exception ex) {
		return standardHandler(ex, MessageKeys.NO_KEY, MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
