package gov.va.ocp.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import gov.va.ocp.framework.test.service.RESTConfigService;

/**
 * It is a wrapper for rest assured API for making HTTP calls, parse JSON and
 * xml responses and status code check.
 *
 * @author sravi
 */

public class RESTUtility { 
	

	private static final String DOCUMENTS_FOLDER_NAME = "documents";
	private static final String PAYLOAD_FOLDER_NAME = "payload";
	private static final String SUBMIT_PAYLOAD = "submitPayload";
	private static final Logger LOGGER = LoggerFactory.getLogger(RESTUtility.class);

	// stores request headers
	private MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>(); 
	String jsonText = new String();
	ResponseEntity<String> response = null;
	int httpResponseCode;

	/**
	 * Reads file content for a given file resource using URL object.
	 *
	 * @param strRequestFile
	 * @param mapHeader
	 * @throws Exception
	 */
	public void setUpRequest(final String strRequestFile, final Map<String, String> mapHeader) {
		try {
			requestHeaders.setAll(mapHeader);
			if (strRequestFile != null) {
				LOGGER.info("Request File {}", strRequestFile);
				final URL urlFilePath = RESTUtility.class.getClassLoader().getResource("request/" + strRequestFile);
				if (urlFilePath == null) {
					LOGGER.error("Requested File Doesn't Exist: {}", "request/" + strRequestFile);
				} else {
					// Note - Enhance the code so if Header.Accept is xml, then it
					// should use something like convertToXML function
					jsonText = readFile(new File(urlFilePath.toURI()));
				}
			}
		} catch (final URISyntaxException ex) {
			LOGGER.error("Unable to do setUpRequest", ex);
		}
	}

	/**
	 * Assigns given header object into local header map.
	 *
	 * @param mapHeader
	 * @throws Exception
	 */
	public void setUpRequest(final Map<String, String> mapHeader) {
		requestHeaders.setAll(mapHeader);
	}

	/**
	 * Gets  header object.
	 *
	 * @return mapHeader
	 */
	public MultiValueMap<String, String> getRequest() {
		return requestHeaders;
	}
	
	/**
	 * Invokes REST end point for a GET method using REST Template API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	public String getResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(headers);				
		return executeAPI(serviceURL, request, HttpMethod.GET);	
	}
	
	/**
	 * Invokes REST end point for a POST method using  REST Template API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	
	public String postResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(jsonText, headers);
		return executeAPI(serviceURL, request, HttpMethod.POST);
	}
	
	/**
	 * Invokes REST end point for a PUT method using  REST Template API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	
	public String putResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(headers);				
		return executeAPI(serviceURL, request, HttpMethod.PUT);	
	}
	/**
	 * Invokes REST end point for a DELETE method using  REST Template API and return
	 * response JSON object.
	 *
	 * @param serviceURL
	 * @return
	 */
	
	public String deleteResponse(final String serviceURL) {
		HttpHeaders headers = new HttpHeaders(requestHeaders);
		HttpEntity<?> request = new HttpEntity<>(headers);		
		return executeAPI(serviceURL, request, HttpMethod.DELETE);	
	}
	
	private String executeAPI(final String serviceURL, HttpEntity<?> request, HttpMethod httpMethod) {
		RestTemplate restTemplate = getRestTemplate();
		try {
			response = restTemplate.exchange(serviceURL, httpMethod, request, String.class);
			httpResponseCode = response.getStatusCodeValue();
			return response.getBody();
		}
		catch(HttpClientErrorException clientError) {
			LOGGER.error(clientError.getMessage());
			httpResponseCode = clientError.getRawStatusCode();
			return clientError.getResponseBodyAsString();
		}		
	}
	
	private RestTemplate getRestTemplate() {
		String pathToKeyStore = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStore", true);
		if (StringUtils.isBlank(pathToKeyStore)) {
			return new RestTemplate();
		} else {
			KeyStore keyStore = null;
			String password = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStorePassword", true);

			try (FileInputStream instream = new FileInputStream(pathToKeyStore)) {
				keyStore = KeyStore.getInstance("jks");
				keyStore.load(instream, password.toCharArray());
				SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, password.toCharArray()).
						loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
				
			    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
			    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
			    ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			    return new RestTemplate(requestFactory);				
			} catch (Exception e) {
				LOGGER.error("Issue with the certificate or password", e);
			}			
		}
		return null;
	}
		
	
	/**
	 * Loads the expected results from source folder and returns as string.
	 *
	 * @param filename
	 * @return
	 */
	public String readExpectedResponse(final String filename) {
		String strExpectedResponse = null;
		try {
			LOGGER.info("Response File: {}", filename);
			final URL urlFilePath = RESTUtility.class.getClassLoader().getResource("response/" + filename);
			if (urlFilePath == null) {
				LOGGER.error("Requested File Doesn't Exist: {}", "response/" + filename);
			} else {
				final File strFilePath = new File(urlFilePath.toURI());
				strExpectedResponse = FileUtils.readFileToString(strFilePath, "ASCII");
			}
		} catch (URISyntaxException | IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}

		return strExpectedResponse;
	}

	protected String readFile(final File filename) {
		String content = null;
		final File file = filename;
		try (FileReader reader = new FileReader(file)) {
			final char[] chars = new char[(int) file.length()];
			reader.read(chars);
			content = new String(chars);
		} catch (final IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return content;

	}

	/**
	 * Asserts the response status code with the given status code.
	 *
	 * @param intStatusCode
	 */
	public void validateStatusCode(final int intStatusCode) {
		assertThat(httpResponseCode, equalTo(intStatusCode));
		
	}

	
}

