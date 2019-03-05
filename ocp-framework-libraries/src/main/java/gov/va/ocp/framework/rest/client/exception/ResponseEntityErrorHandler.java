package gov.va.ocp.framework.rest.client.exception;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.ResponseErrorHandler;

import gov.va.ocp.framework.service.DomainResponse;

/**
 * Error handler for RestTemplate to determine whether a particular response has an error or not.
 *
 * @author akulkarni
 *
 */
public class ResponseEntityErrorHandler implements ResponseErrorHandler {
	
	private List<HttpMessageConverter<?>> messageConverters;

	  /* (non-Javadoc)
  	 * @see org.springframework.web.client.ResponseErrorHandler#hasError(org.springframework.http.client.ClientHttpResponse)
  	 */
  	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
	    return hasError(response.getStatusCode());
	}

	  /**
  	 * Checks for error.
  	 *
  	 * @param statusCode the status code
  	 * @return true, if successful
  	 */
  	protected boolean hasError(HttpStatus statusCode) {
	    return (statusCode.is4xxClientError() || statusCode.is5xxServerError());
	  }

	  /* (non-Javadoc)
  	 * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.springframework.http.client.ClientHttpResponse)
  	 */
  	@Override
	  public void handleError(ClientHttpResponse response) throws IOException {
	    HttpMessageConverterExtractor<DomainResponse> errorMessageExtractor =
	      new HttpMessageConverterExtractor<DomainResponse>(DomainResponse.class, messageConverters);
	    DomainResponse errorObject = errorMessageExtractor.extractData(response);
	   throw new ResponseEntityErrorException(ResponseEntity.status(response.getRawStatusCode()).headers(response.getHeaders()).body(errorObject));
	  }

	  /**
  	 * Sets the message converters.
  	 *
  	 * @param messageConverters the new message converters
  	 */
  	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
	    this.messageConverters = messageConverters;
	  }

}
