package gov.va.bip.framework.test.util;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.net.HttpHeaders;

import gov.va.bip.framework.test.exception.BipTestLibRuntimeException;
import gov.va.bip.framework.test.service.RESTConfigService;

public class RESTUtilTest {

	private static WireMockServer wireMockServer;
	RESTUtil restUtil = new RESTUtil();
	private static final String URL_PERSON = "/person";
	private static final String LOCALHOST_URL_PERSON = "http://localhost:9999/person";
	private static final String LOCALHOST_MULTIPART_URL_PERSON = "http://localhost:9999/multipart/person";
	private static final String SUBMIT_PAYLOAD_TXT = "submitpayload.txt";
	private static final String LOCALHOST_HTTPS_URL_PERSON = "https://localhost:8443/person";
	
	protected static final int HTTP_ENDPOINT_PORT = 8123;
    protected static final int HTTPS_ENDPOINT_PORT = 8443;
    protected static final String BASE_PATH = "/person";
    protected static final String BASE_HTTP_URL =
        "http://localhost:" + HTTP_ENDPOINT_PORT + BASE_PATH;
    protected static final String BASE_HTTPS_URL =
        "https://localhost:" + HTTPS_ENDPOINT_PORT + BASE_PATH;
    protected static final int DEFAULT_TIMEOUT = 5000;
    /* Client keystore and truststore. Self-signed. */
    protected static final String CLIENT_KEYSTORE_PATH = "client/client_keystore.jks";
    protected static final String CLIENT_KEYSTORE_PASSWORD = "secret";
    protected static final String CLIENT_TRUSTSTORE_PATH = "client/client_cacerts.jks";
    protected static final String CLIENT_TRUSTSTORE_PASSWORD = "secret";
    /* Server keystore and truststore. Self-signed. */
    protected static final String SERVER_KEYSTORE_PATH = "client/server/server_keystore.jks";
    protected static final String SERVER_KEYSTORE_PASSWORD = "secret";
    protected static final String SERVER_TRUSTSTORE_PATH = "client/server/server_cacerts.jks";
    protected static final String SERVER_TRUSTSTORE_PASSWORD = "secret";


	@BeforeClass
	public static void setup() {
		wireMockServer = new WireMockServer(9999);
		wireMockServer.start();
		setupStub();
	}
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig());
	@AfterClass
	public static void teardown() {
		wireMockServer.stop();
	}

	

	private WireMockConfiguration wireMockConfig() {
		try {
			System.out.println(new ClassPathResource("client/server/server_keystore.jks").getFile().getCanonicalPath());
			return WireMockConfiguration.options().httpsPort(HTTPS_ENDPOINT_PORT)
	        //.keystorePath(SERVER_KEYSTORE_PATH)
	        //.keystorePassword(SERVER_KEYSTORE_PASSWORD);
					.keystorePath(new ClassPathResource("client/server/server_keystore.jks").getFile().getCanonicalPath())
					.keystorePassword(SERVER_KEYSTORE_PASSWORD).keystoreType("JKS")
					.trustStorePath(new ClassPathResource("client/server/server_cacerts.jks").getFile().getCanonicalPath())
					.trustStorePassword("secret").trustStoreType("JKS").needClientAuth(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	



	public static void setupStub() {
		addHttpsGetPersonStub();
		addGetPersonStub();
		addPostPersonStub();
		addDeletePersonStub();
		addPutPersonStub();
		addPostMultiPart();
	}

	private static void addGetPersonStub() {
		wireMockServer.stubFor(get(urlEqualTo(URL_PERSON))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/get-person-response.json")));
	}

	private static void addHttpsGetPersonStub() {
		wireMockServer.stubFor(get(urlEqualTo(BASE_PATH))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/get-person-response.json")));
	}

	private static void addPostPersonStub() {
		wireMockServer.stubFor(post(urlEqualTo(URL_PERSON))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/post-person-response.json")));
	}

	private static void addPutPersonStub() {
		wireMockServer.stubFor(put(urlEqualTo(URL_PERSON))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/put-person-response.json")));
	}

	private static void addDeletePersonStub() {
		wireMockServer.stubFor(delete(urlEqualTo(URL_PERSON))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/delete-person-response.json")));
	}

	private static void addPostMultiPart() {
		wireMockServer.stubFor(post(urlEqualTo("/multipart/person"))
				.willReturn(aResponse().withStatus(200).withBodyFile("json/post-multipart-person-response.json")));
	}

	@Test
	public void test_setUpRequest_Success() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put(HttpHeaders.AUTHORIZATION, "Bearer abcdef");
		mapHeader.put(HttpHeaders.CONTENT_TYPE, "application/json");
		restUtil.setUpRequest(mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get(HttpHeaders.AUTHORIZATION).get(0)));
	}

	@Test
	public void test_setUpRequest_WithBody_Success() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put(HttpHeaders.AUTHORIZATION, "Bearer abcdef");
		mapHeader.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		restUtil.setUpRequest("janedoe.request", mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get(HttpHeaders.AUTHORIZATION).get(0)));
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_setUpRequest_BadFile_Failed() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put(HttpHeaders.AUTHORIZATION, "Bearer abcdef");
		mapHeader.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		RESTUtil restUtilSpied = org.mockito.Mockito.spy(restUtil);
		try {
			doThrow(new IOException()).when(restUtilSpied).readFile(org.mockito.ArgumentMatchers.any(File.class));
		} catch (IOException e) {
			e.printStackTrace();
			fail("exception not expected");
		}
		restUtilSpied.setUpRequest("janedoebad^^@.%%.request", mapHeader);
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(true, equalTo(isBodyEmpty));
	}

	@Test
	public void test_getResponse_validKeyStore() {
		String response = restUtil.getResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_https_getResponse_validKeyStore() {
		String response = restUtil.getResponse(BASE_HTTPS_URL);
		assertThat(true, equalTo(!response.isEmpty()));
		
	}

	
	@Test
	public void testGetRestTemplate() {
		Constructor<RESTConfigService> constructor;
		try {
			constructor = RESTConfigService.class.getDeclaredConstructor();
			constructor.setAccessible(true);
			RESTConfigService config = constructor.newInstance();
			Properties prop = new Properties();
			prop.setProperty("javax.net.ssl.keyStore", "");
			ReflectionTestUtils.setField(config, "prop", prop);
			Field instanceOfRESTConfigService = RESTConfigService.class.getDeclaredField("instance");
			instanceOfRESTConfigService.setAccessible(true);
			instanceOfRESTConfigService.set(null, config);
			ReflectionTestUtils.invokeMethod(new RESTUtil(), "getRestTemplate");
			// reset the field instance and prop fields
			instanceOfRESTConfigService.set(null,null);
			ReflectionTestUtils.setField(config, "prop", null);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchFieldException | BipTestLibRuntimeException e) {
			e.printStackTrace();
			fail("Exception not expected!");
		}
	}

	@Test
	public void test_getResponse_WithRetry() {
		String response = restUtil.getResponse("http://localhost:9999/urldoesnotexits");
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_setUpRequest_WithBody_Failed() {
		Map<String, String> mapHeader = new HashMap<String, String>();
		try {
			restUtil.setUpRequest("nonexistsfile.request", mapHeader);
		} catch (BipTestLibRuntimeException e) {
			assertTrue(e.getMessage().contains("Requested File Doesn't Exist: request/"));
		}
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(true, equalTo(isBodyEmpty));
	}

	@Test
	public void test_getResponse_Success() {
		String response = restUtil.getResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_getResponse_Failed() {
		restUtil.getResponse("http://localhost:9999/urldoesnotexits");
	}

	@Test
	public void test_postResponse_Success() {
		String response = restUtil.postResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_putResponse_Success() {
		String response = restUtil.putResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_deleteResponse_Success() {
		String response = restUtil.deleteResponse(LOCALHOST_URL_PERSON);
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_postResponseWithMultipart_Success() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				SUBMIT_PAYLOAD_TXT);
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}

	@Test
	public void test_postResponseWithMultipart__mbfile_Success() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "IS_25mb.txt",
				SUBMIT_PAYLOAD_TXT);
		assertThat(true, equalTo(!response.isEmpty()));
		restUtil.validateStatusCode(200);
	}

	@Test
	public void test_postResponseWithMultipart_InvalidFile_Failed() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				"invalidpayload.txt");
		assertNull(response);
	}

	@Test
	public void test_postResponseWithMultipart_ByteArray_Success() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "document.txt",
				"HelloWorld".getBytes());
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_postResponseWithMultipart_ByteArray__mbFileSuccess() throws IOException, URISyntaxException {
		final URL urlSubmitPayload = RESTUtilTest.class.getClassLoader()
				.getResource("payload" + "/" + SUBMIT_PAYLOAD_TXT);
		final File filePathSubmitPayload = new File(urlSubmitPayload.toURI());
		String submitPayload = FileUtils.readFileToString(filePathSubmitPayload, "UTF-8");
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON, "IS_25mb.txt",
				submitPayload.getBytes());
		assertThat(true, equalTo(!response.isEmpty()));
	}

	@Test
	public void test_postResponseWithMultipart_ByteArray_InvalidPart_Failed() {
		String response = restUtil.postResponseWithMultipart(LOCALHOST_MULTIPART_URL_PERSON,
				"invaliddocument.txt", "HelloWorld".getBytes());
		assertNull(response);
	}

	@Test
	public void test_readExpectedResponse_Success() {
		String response = restUtil.readExpectedResponse("test.response");
		boolean isBodyEmpty = response.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_readExpectedResponse_FileNotExist_Success() {
		final URL urlFilePath = RESTUtil.class.getClassLoader().getResource("response/" + "badfile.response");
		File strFilePath;
		try {
			strFilePath = new File(urlFilePath.toURI());
			strFilePath.setReadable(false);
			String response = restUtil.readExpectedResponse("badfile1.response");
			strFilePath.setReadable(true);
			assertThat(null, equalTo(response));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test_readExpectedResponse_FileNotExists_Success() {
		String response = restUtil.readExpectedResponse("nonexistsfile.response");
		assertThat(null, equalTo(response));
	}
}
