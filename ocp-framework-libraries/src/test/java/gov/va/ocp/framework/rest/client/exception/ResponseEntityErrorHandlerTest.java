package gov.va.ocp.framework.rest.client.exception;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;

import gov.va.ocp.framework.rest.client.exception.ResponseEntityErrorHandler;

public class ResponseEntityErrorHandlerTest {

	@Test
	public void hasErrorTest() {

		ResponseEntityErrorHandler errorHandler = new ResponseEntityErrorHandler();
		errorHandler.setMessageConverters(new LinkedList<HttpMessageConverter<?>>() {
		});

		ClientHttpResponse response = new ClientHttpResponse() {

			@Override
			public HttpHeaders getHeaders() {
				return null;
			}

			@Override
			public InputStream getBody() throws IOException {
				return null;
			}

			@Override
			public String getStatusText() throws IOException {
				return null;
			}

			@Override
			public HttpStatus getStatusCode() throws IOException {
				return HttpStatus.ACCEPTED;
			}

			@Override
			public int getRawStatusCode() throws IOException {
				return 0;
			}

			@Override
			public void close() {
			}
		};
		try {
			assertFalse(errorHandler.hasError(response));
		} catch (IOException e) {
			fail("Failed to denote the precense of an error");
		}

	}
}
