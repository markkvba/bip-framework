package gov.va.ocp.framework.test.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

public class BearerTokenServiceTest {

	WireMockServer wireMockServer;
	
	@Before
	public void setup() {
		wireMockServer = new WireMockServer(9999);
		wireMockServer.start();
		setupStub();
	}

	@After
	public void teardown() {
		wireMockServer.stop();
	}

	public void setupStub() {		
		addPostBearerStub();		
	}

	private void addPostBearerStub() {
		wireMockServer.stubFor(post(urlEqualTo("/token"))
				.willReturn(aResponse().withStatus(200).withBodyFile("bearer/post-bearer-response.txt")));
	}

	@Test
	public void test_getToken_Success() {
		String token = BearerTokenService.getInstance().getBearerToken();
		assertThat(true, equalTo(!token.isEmpty()));
	}
}
