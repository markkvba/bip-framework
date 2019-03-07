package gov.va.ocp.framework.rest.client.exception;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import gov.va.ocp.framework.messages.MessageSeverity;
import gov.va.ocp.framework.service.DomainResponse;

import gov.va.ocp.framework.rest.client.exception.ResponseEntityErrorHandler;

public class ResponseEntityErrorHandlerTest {

	@Test
	public void hasErrorTest() {

		ResponseEntityErrorHandler errorHandler = new ResponseEntityErrorHandler();
		errorHandler.setMessageConverters(new LinkedList<HttpMessageConverter<?>>() {
		});

		ClientHttpResponse response = new BasicValidClientHttpResponse();
		try {
			assertFalse(errorHandler.hasError(response));
		} catch (IOException e) {
			fail("Failed to denote the precense of an error");
		}
	}

	@Test(expected = ResponseEntityErrorException.class)
	public void handleErrorTest() throws IOException {
		ResponseEntityErrorHandler errorHandler = new ResponseEntityErrorHandler();
		LinkedList<HttpMessageConverter<?>> converters = new LinkedList<HttpMessageConverter<?>>() {
		};
		BasicHttpMessageConverterForTesting httpMessageConverter = new BasicHttpMessageConverterForTesting();

		List<MediaType> supportedMediaTypes = new LinkedList<MediaType>();
		supportedMediaTypes.add(MediaType.TEXT_PLAIN);
		httpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
		converters.add(httpMessageConverter);
		errorHandler.setMessageConverters(converters);
		ClientHttpResponse response = new BasicValidClientHttpResponse();

		errorHandler.handleError(response);

	}

	private class BasicHttpMessageConverterForTesting extends AbstractHttpMessageConverter<DomainResponse> {

		@Override
		protected boolean supports(final Class clazz) {
			return true;
		}

		@Override
		protected DomainResponse readInternal(final Class<? extends DomainResponse> clazz, final HttpInputMessage inputMessage)
				throws IOException, HttpMessageNotReadableException {
			DomainResponse domainResponse = new DomainResponse();
			domainResponse.addMessage(MessageSeverity.INFO, "test.key", "text fo test.key", HttpStatus.ACCEPTED);
			return domainResponse;
		}

		@Override
		protected void writeInternal(final DomainResponse text, final HttpOutputMessage outputMessage)
				throws IOException, HttpMessageNotWritableException {

		}

	}

	private final class BasicValidClientHttpResponse implements ClientHttpResponse {
		@Override
		public HttpHeaders getHeaders() {
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.TEXT_PLAIN);
			return httpHeaders;
		}

		@Override
		public InputStream getBody() throws IOException {
			return new ByteArrayInputStream("sample body".getBytes());
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
	}
}
