package gov.va.ocp.framework.test.restassured;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import gov.va.ocp.framework.test.util.RESTUtil;

public class BaseStepDefTest {

	BaseStepDef subject = new BaseStepDef();


	WireMockServer wireMockServer;
	RESTUtil restUtil = new RESTUtil();
	
	@Before
    public void setup() {
        wireMockServer = new WireMockServer(9999);
        wireMockServer.start();
        setupStub();
        
		Map<String, String> tblHeader = new HashMap<>();
		tblHeader.put("Acept", "application/json");
		tblHeader.put("Content-Type", "application/json");
		subject.passHeaderInformation(tblHeader);	
		subject.initREST();

    }
 
	@After
    public void teardown () {
        wireMockServer.stop();
    }
	
	public void setupStub() {
		addGetPersonStub();	
		addPostPersonStub();
		addDeletePersonStub();
		addPostBearerStub();
	}
	
	private void addGetPersonStub() {
        wireMockServer.stubFor(get(urlEqualTo("/person"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("json/get-person-response.json")));        
    }
	
	private void addPostPersonStub() {
        wireMockServer.stubFor(post(urlEqualTo("/person"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("json/post-person-response.json")));        
    }
	private void addDeletePersonStub() {
        wireMockServer.stubFor(delete(urlEqualTo("/person"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBodyFile("json/delete-person-response.json")));        
    }
	
	private void addPostBearerStub() {
		wireMockServer.stubFor(post(urlEqualTo("/token"))
				.willReturn(aResponse().withStatus(200).withBodyFile("bearer/post-bearer-response.txt")));
	}
	
	@Test
	public void test_passHeaderInformation_Success() {
		assertThat(2, equalTo(subject.headerMap.size()));
	}
	
	@Test
	public void test_invokeAPIUsingDelete_Success() {
		subject.invokeAPIUsingDelete("http://localhost:9999/person", false);
	}
	
	@Test
	public void test_invokeAPIUsingPost_Success() {
		subject.invokeAPIUsingPost("http://localhost:9999/person", false);
		String response = restUtil.postResponse("http://localhost:9999/person");
		assertThat(true, equalTo(!response.isEmpty()));
	}
	
	@Test
	public void test_invokeAPIUsingGet_Success() {
		subject.invokeAPIUsingGet("http://localhost:9999/person", false);
	}
	
	@Test
	public void test_invokeAPIUsingGet__WithBearerToken_Success() {
		subject.invokeAPIUsingGet("http://localhost:9999/person", true);
	}
	
	
	@Test
	public void test_setHeader_Success() throws Exception {
		subject.setHeader("dev-janedoe");
		assertThat(2, equalTo(subject.headerMap.size()));
	}
	
	@Test
	public void test_compareExpectedResponseWithActual_Success() throws Exception {	
		final URL urlFilePath = BaseStepDefTest.class.getClassLoader().getResource("response/test.response");
		final File strFilePath = new File(urlFilePath.toURI());
		subject.strResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		assertThat(true, equalTo(subject.compareExpectedResponseWithActual("test.response")));
	}
	
	@Test
	public void test_compareExpectedResponseWithActualByRow_Success() throws Exception {
		final URL urlFilePath = BaseStepDefTest.class.getClassLoader().getResource("response/test.response");
		final File strFilePath = new File(urlFilePath.toURI());
		subject.strResponse = FileUtils.readFileToString(strFilePath, "ASCII");
		assertThat(true, equalTo(subject.compareExpectedResponseWithActualByRow("test.response")));
	}
		
	@Test
	public void test_SetEnvQA_readProperty_Success() {
		System.setProperty("test.env", "qa");
	}
	

}
