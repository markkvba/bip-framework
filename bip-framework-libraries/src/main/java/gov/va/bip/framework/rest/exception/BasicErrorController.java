package gov.va.bip.framework.rest.exception;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import gov.va.bip.framework.rest.provider.ProviderResponse;
import gov.va.bip.framework.util.HttpHeadersUtil;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The Class BasicErrorController.
 */
@RestController
@ApiIgnore
public class BasicErrorController implements ErrorController {

	/** Constant for the logger for this class */
	private static final BipLogger LOGGER = BipLoggerFactory.getLogger(BasicErrorController.class);

	/** The error attributes. */
	@Autowired(required = false)
	private ErrorAttributes errorAttributes;

	/**
	 * Handle error.
	 *
	 * @param webRequest
	 *            the web request
	 * @param response
	 *            the response
	 * @return the response entity
	 */
	@RequestMapping(value = "/error")
	public ResponseEntity<ProviderResponse> handleError(WebRequest webRequest, HttpServletResponse response) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		String message = MessageKeys.BIP_GLOBAL_GENERAL_EXCEPTION.getMessage();

		if (response != null) {
			try {
				httpStatus = HttpStatus.valueOf(response.getStatus());
			} catch (IllegalArgumentException e) {
				LOGGER.warn(
						"IllegalArgumentException raised for the specified numeric value. Setting as Internal Error {}",
						e);
				// for invalid status code, set it to internal error
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		final Map<String, Object> error = getErrorAttributes(webRequest, false);
		if (error != null) {
			message = (String) error.getOrDefault("message", MessageKeys.BIP_GLOBAL_GENERAL_EXCEPTION.getMessage());
		}

		ProviderResponse providerResponse = new ProviderResponse();
		providerResponse.addMessage(MessageSeverity.ERROR, httpStatus.name(), message, httpStatus);
		
		return new ResponseEntity<>(providerResponse, HttpHeadersUtil.buildHttpHeadersForError(), httpStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.boot.web.servlet.error.ErrorController#getErrorPath()
	 */
	@Override
	public String getErrorPath() {
		return "/error";
	}

	/**
	 * Gets the error attributes.
	 *
	 * @param webRequest
	 *            the web request
	 * @param includeStackTrace
	 *            the include stack trace
	 * @return the error attributes
	 */
	private Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
		if (errorAttributes != null) {
			return errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
		} else {
			return null;
		}
	}
}