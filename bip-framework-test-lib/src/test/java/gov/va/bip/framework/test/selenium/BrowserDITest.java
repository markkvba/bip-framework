package gov.va.bip.framework.test.selenium;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class BrowserDITest {

	
	
	//@Test
	public void test_invokeBrowser_htmlunit_Success() {
		BrowserDI subject = new BrowserDI();
		assertThat(true, equalTo(subject.driver != null));
	}
	
	//@Test
	public void test_invokeBrowser_chromebrowser_Success() {
		System.setProperty("browser", "chrome");
		System.setProperty("webdriverPath", "/Users/sravi/browserdriver/chromedriver");
		BrowserDI subject = new BrowserDI();
		assertThat(true, equalTo(subject.driver != null));
	}

	
}
