package gov.va.ocp.framework.rest.client.exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import gov.va.ocp.framework.exception.OcpRuntimeException;
import gov.va.ocp.framework.log.OcpLogger;
import gov.va.ocp.framework.log.OcpLoggerFactory;
import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.rest.provider.ProviderResponse;

/**
 * The Class OcpRestGlobalExceptionHandler.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OcpRestGlobalExceptionHandler {

	/** The Constant LOGGER. */
	private static final OcpLogger logger = OcpLoggerFactory.getLogger(OcpRestGlobalExceptionHandler.class);

	// 400

	/**
	 * Handle illegal argument exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = IllegalArgumentException.class)
	@ResponseStatus(value= HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleIllegalArgumentException(HttpServletRequest req, IllegalArgumentException ex) {
		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.BAD_REQUEST.name(),
				ex.getMessage(),
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle method argument not valid exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseStatus(value= HttpStatus.BAD_REQUEST)
	public final ResponseEntity<Object> handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException ex) {
		final List<String> errors = new ArrayList<String>();
		for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}

		final String finalError = String.join(",", errors);
		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.BAD_REQUEST.name(),
				finalError,
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle http client error exception.
	 *
	 * @param req the req
	 * @param httpClientErrorException the http client error exception
	 * @return the response entity
	 */
	@ExceptionHandler({ HttpClientErrorException.class })
	@ResponseStatus(value= HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleHttpClientErrorException(HttpServletRequest req, final HttpClientErrorException httpClientErrorException) {
		logger.info(httpClientErrorException.getClass().getName());
		logger.error("error", httpClientErrorException);
		//
		ProviderResponse apiError = new ProviderResponse();

		byte[] responseBody = httpClientErrorException.getResponseBodyAsByteArray();

		try {
			apiError = new ObjectMapper().readValue(responseBody, ProviderResponse.class);
		} catch (IOException e) {
			apiError.addMessage(MessageSeverity.ERROR, httpClientErrorException.getStatusCode().name(),
					new String(responseBody),
					httpClientErrorException.getStatusCode());
		}

		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}


	/**
	 * Handle method argument type mismatch.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(HttpServletRequest req, final MethodArgumentTypeMismatchException ex) {
		logger.info(ex.getClass().getName());
		//
		final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();

		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.BAD_REQUEST.name(),
				error,
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handle constraint violation.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(HttpServletRequest req, final ConstraintViolationException ex) {
		logger.info(ex.getClass().getName());
		//
		final List<String> errors = new ArrayList<String>();
		for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
		}

		final String finalError = String.join(",", errors);
		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.BAD_REQUEST.name(),
				finalError,
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	// 404

	/**
	 * Handle no handler found exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	protected ResponseEntity<Object> handleNoHandlerFoundException(HttpServletRequest req, final NoHandlerFoundException ex) {
		logger.info(ex.getClass().getName());
		//
		final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.NOT_FOUND.name(),
				error,
				HttpStatus.NOT_FOUND);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	// 405

	/**
	 * Handle http request method not supported.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpServletRequest req, final HttpRequestMethodNotSupportedException ex) {
		logger.info(ex.getClass().getName());
		//
		final StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod());
		builder.append(" method is not supported for this request. Supported methods are ");
		ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.METHOD_NOT_ALLOWED.name(),
				ex.getLocalizedMessage(),
				HttpStatus.METHOD_NOT_ALLOWED);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
	}

	// 415

	/**
	 * Handle http media type not supported.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(MediaTypeNotSupportedStatusException.class)
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpServletRequest req, final HttpMediaTypeNotSupportedException ex) {
		logger.info(ex.getClass().getName());
		//
		final StringBuilder builder = new StringBuilder();
		builder.append(ex.getContentType());
		builder.append(" media type is not supported. Supported media types are ");
		ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.UNSUPPORTED_MEDIA_TYPE.name(),
				ex.getLocalizedMessage(),
				HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	// 500

	/**
	 * Handle ocp runtime exception.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler(value = OcpRuntimeException.class)
	@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
	public final ResponseEntity<Object> handleOcpRuntimeException(HttpServletRequest req, OcpRuntimeException ex) {
		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.name(),
				ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Handle all.
	 *
	 * @param req the req
	 * @param ex the ex
	 * @return the response entity
	 */
	@ExceptionHandler({ Exception.class })
	@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleAll(HttpServletRequest req, final Exception ex) {
		logger.info(ex.getClass().getName());
		logger.error("error", ex);
		//
		final ProviderResponse apiError = new ProviderResponse();
		apiError.addMessage(MessageSeverity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR.name(),
				ex.getLocalizedMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
