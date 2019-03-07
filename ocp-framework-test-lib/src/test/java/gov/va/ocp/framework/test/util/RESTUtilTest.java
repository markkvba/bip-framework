package gov.va.ocp.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RESTUtilTest {

	@Test
	public void test_setUpRequest_Success() {

		RESTUtil restUtil = new RESTUtil();
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put("authorization", "Bearer abcdef");
		mapHeader.put("content-type", "application/json");
		restUtil.setUpRequest(mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get("authorization")));
	}

	@Test
	public void test_setUpRequest_WithBody_Success() {

		RESTUtil restUtil = new RESTUtil();
		Map<String, String> mapHeader = new HashMap<String, String>();
		mapHeader.put("authorization", "Bearer abcdef");
		mapHeader.put("content-type", "application/json");
		restUtil.setUpRequest("janedoe.request", mapHeader);
		assertThat("Bearer abcdef", equalTo(restUtil.getRequest().get("authorization")));
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_setUpRequest_WithBody_Failed() {

		RESTUtil restUtil = new RESTUtil();
		Map<String, String> mapHeader = new HashMap<String, String>();
		restUtil.setUpRequest("nonexistsfile.request", mapHeader);
		boolean isBodyEmpty = restUtil.jsonText.isEmpty();
		assertThat(true, equalTo(isBodyEmpty));
	}

	@Test
	public void test_getResponse_Success() {
		// Need to be done.

	}

	@Test
	public void test_readExpectedResponse_Success() {
		RESTUtil restUtil = new RESTUtil();
		String response = restUtil.readExpectedResponse("test.response");
		boolean isBodyEmpty = response.isEmpty();
		assertThat(false, equalTo(isBodyEmpty));
	}

	@Test
	public void test_readExpectedResponse_FileNotExists_Success() {
		RESTUtil restUtil = new RESTUtil();
		String response = restUtil.readExpectedResponse("nonexistsfile.response");
		assertThat(null, equalTo(response));
	}

}
