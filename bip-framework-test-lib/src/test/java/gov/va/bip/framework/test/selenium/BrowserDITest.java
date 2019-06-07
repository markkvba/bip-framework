package gov.va.bip.framework.test.selenium;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;

import org.junit.Test;

import gov.va.bip.framework.test.service.RESTConfigService;

public class BrowserDITest {

	@Test
	public void test_invokeBrowser_htmlunit_failure()
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field instance = RESTConfigService.class.getDeclaredField("instance");
		instance.setAccessible(true);
		instance.set(null, null);
		System.setProperty("test.env", "");
		BrowserDI subject = new BrowserDI();
		assertThat(true, equalTo(subject.getDriver() != null));
		subject.closeBrowser();
	}

	@Test
	public void test_invokeBrowser_htmlunit_Success() {
		BrowserDI subject = new BrowserDI();
		assertThat(true, equalTo(subject.getDriver() != null));
		subject.closeBrowser();
	}

	@Test
	public void test_invokeBrowser_htmlunit_success() {
		System.setProperty("test.env", "qa");
		BrowserDI subject = new BrowserDI();
		assertThat(true, equalTo(subject.getDriver() != null));
		subject.closeBrowser();
	}

}
