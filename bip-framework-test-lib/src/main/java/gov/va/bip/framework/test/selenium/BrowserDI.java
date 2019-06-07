package gov.va.bip.framework.test.selenium;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;

import gov.va.bip.framework.test.service.RESTConfigService;

/**
 * It's a wrapper for selenium browser that gets injected to all Step
 * definitions. It configures selenium webdriver, setting up all the
 * capabilities and SSL configurations.
 * 
 * 
 */
public class BrowserDI {
	private WebDriver driver;
	public static final Logger LOGGER = LoggerFactory.getLogger(BrowserDI.class);

	/**
	 * Constructor that initialize web driver with all the configuration. It gets
	 * called when object is loaded in memory so the driver with all configuration
	 * is set to use.
	 */
	public BrowserDI() {
		setDriver();
	}

	/**
	 * Configures webdriver capabilities for chrome and HTML unit driver.
	 * 
	 * @return
	 */
	public void setDriver() {

		try {
			DesiredCapabilities dcHtml = DesiredCapabilities.htmlUnit();
			dcHtml.setCapability("ignoreProtectedModeSettings", true);
			dcHtml.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			dcHtml.setCapability("acceptInsecureCerts", true);
			dcHtml.setCapability("handlesAlerts", true);
			dcHtml.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
			driver = getHtmlUnitDriver(dcHtml);
			dcHtml.setJavascriptEnabled(false);
			((HtmlUnitDriver) driver).setJavascriptEnabled(false);
			setBrowserAttributes();

		} catch (Exception e) {
			LOGGER.error("ERROR", "Could not launch the WebDriver selenium", e);
		}

	}

	/**
	 * It configures HtmlUnitDriver with SSL certificate. Keystore and
	 * keyStorePassword are loaded from property file.
	 * 
	 * @param dcHtml
	 * @return
	 */
	private HtmlUnitDriver getHtmlUnitDriver(DesiredCapabilities dcHtml) {
		String pathToKeyStore = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStore", true);
		String password = RESTConfigService.getInstance().getProperty("javax.net.ssl.keyStorePassword", true);
		if (StringUtils.isBlank(pathToKeyStore)) {
			return new HtmlUnitDriver(dcHtml);
		} else {
			return new HtmlUnitDriver(dcHtml) {
				@Override
				protected WebClient modifyWebClient(WebClient client) {
					try {
						File certificateFile = new File(pathToKeyStore);
						client.getOptions().setSSLClientCertificate(certificateFile.toURI().toURL(), password, "jks");
					} catch (MalformedURLException e) {
						LOGGER.error("Unable to load JKS", e);
						return null;
					}
					return client;
				}

			};
		}
	}

	/**
	 * It configures some of the browser attributes such as maximize window, wait
	 * time.
	 */
	private void setBrowserAttributes() {
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	/**
	 * Delete cookies and close browser.
	 */
	public void closeBrowser() {
		driver.manage().deleteAllCookies();
		driver.close();

	}

	public WebDriver getDriver() {
		return driver;
	}

}