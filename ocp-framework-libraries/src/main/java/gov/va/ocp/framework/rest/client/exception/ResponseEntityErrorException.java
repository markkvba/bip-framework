package gov.va.ocp.framework.rest.client.exception;

import org.springframework.http.ResponseEntity;

import gov.va.ocp.framework.service.DomainResponse;

/**
 * Exception Class for REST Template calls for ServiceResponse
 *
 * @author akulkarni
 *
 */
public class ResponseEntityErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	/** The error response. */
	private ResponseEntity<DomainResponse> errorResponse;
	
	/**
	 * Instantiates a new response entity error exception.
	 *
	 * @param errorResponse the error response
	 */
	public ResponseEntityErrorException(ResponseEntity<DomainResponse> errorResponse) {
	     this.errorResponse = errorResponse;
	}
	  
  	/**
  	 * Gets the error response.
  	 *
  	 * @return the error response
  	 */
  	public ResponseEntity<DomainResponse> getErrorResponse() {
	      return errorResponse;
	  }
	}
