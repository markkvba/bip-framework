package gov.va.bip.framework.test.selenium;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;

import gov.va.bip.framework.test.service.RESTConfigService;

/**
 * It's a wrapper for selenium browser that gets injected to all Step definitions. 
 * It configures selenium webdriver, setting up all the capabilities and SSL configurations.
 * Browser type is initiated using runtime parameter "browser" and by default it will load HtmlUnitDriver if the value is null.
 * Chrome and Firefox needs webdriver path to be provided as part of configuration. Driver path is provided by runtime parameter "webdriverpath".
 */
public class BrowserDI {
	public WebDriver driver;
	public String BROWSER_NAME = System.getProperty("browser");
	// public static final String BROWSER_NAME = "chrome";
	public String WEBDRIVER_PATH = System.getProperty("webdriverPath");
	public static final Logger LOGGER = LoggerFactory.getLogger(BrowserDI.class);

	/**
	 * Constructor that initialize web driver with all the configuration.
	 * It gets called when object is loaded in memory so the driver with all configuration is set to use.
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
			if (BROWSER_NAME == null) {
				BROWSER_NAME = "HtmlUnitDriver";
			}
			switch (BROWSER_NAME) {
			case "chrome":
				DesiredCapabilities dcChrome = DesiredCapabilities.chrome();
				dcChrome.setJavascriptEnabled(true);
				dcChrome.setCapability(CapabilityType.BROWSER_NAME, "Chrome");
				dcChrome.setCapability("ignoreProtectedModeSettings", true);
				dcChrome.setCapability("acceptInsecureCerts", true);
				dcChrome.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				System.setProperty("webdriver.chrome.driver", WEBDRIVER_PATH);
				// System.setProperty("webdriver.chrome.driver",
				// "/Users/sravi/browserdriver/chromedriver");
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.addArguments("start-maximized");
				chromeOptions.merge(dcChrome);
				driver = new ChromeDriver(chromeOptions);
				break;
			case "IE":
				DesiredCapabilities icOptions = DesiredCapabilities.internetExplorer();
				icOptions.setJavascriptEnabled(true);
				icOptions.setCapability(CapabilityType.BROWSER_NAME, "IE");
				icOptions.setCapability("ignoreProtectedModeSettings", true);
				icOptions.setCapability("acceptInsecureCerts", true);
				icOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				System.setProperty("webdriver.ie.driver", WEBDRIVER_PATH);
				InternetExplorerOptions ieOptions = new InternetExplorerOptions();
				ieOptions.addCommandSwitches("start-maximized");
				ieOptions.merge(icOptions);
				driver = new InternetExplorerDriver(ieOptions);
				break;
			case "firefox":
				DesiredCapabilities ffOptions = DesiredCapabilities.firefox();
				ffOptions.setJavascriptEnabled(true);
				ffOptions.setCapability(CapabilityType.BROWSER_NAME, "firefox");
				ffOptions.setCapability("ignoreProtectedModeSettings", true);
				ffOptions.setCapability("acceptInsecureCerts", true);
				ffOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

				System.setProperty("webdriver.gecko.driver", WEBDRIVER_PATH);
				FirefoxOptions ffoption = new FirefoxOptions();
				ffoption.addArguments("start-maximized");
				ffoption.merge(ffOptions);
				driver = new FirefoxDriver(ffoption);
				break;

			default:
				DesiredCapabilities dcHtml = DesiredCapabilities.htmlUnit();
				dcHtml.setCapability("ignoreProtectedModeSettings", true);
				dcHtml.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				dcHtml.setCapability("acceptInsecureCerts", true);
				dcHtml.setCapability("handlesAlerts", true);
				dcHtml.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);

				driver = getHtmlUnitDriver(dcHtml);
				dcHtml.setJavascriptEnabled(true);
				((HtmlUnitDriver) driver).setJavascriptEnabled(true);
			}
			setBrowserAttributes();

		} catch (Exception e) {
			LOGGER.error("ERROR", "Could not launch the WebDriver selenium", e);
		}

	}

	/**
	 * It configures HtmlUnitDriver with SSL certificate. 
	 * Keystore and keyStorePassword are loaded from property file.
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
	 * It configures some of the browser attributes such as maximize window, wait time.
	 */
	private void setBrowserAttributes() {
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	/**
	 * Wait method used to sync for different objects
	 * 
	 * @param waitMilliSeconds
	 * @return
	 */
	public synchronized WebDriverWait getWebDriverWait(int waitMilliSeconds) {
		return new WebDriverWait(driver, waitMilliSeconds);
	}

	/**
	 * Delete cookies and close browser.
	 */
	public void closeBrowser() {
		driver.manage().deleteAllCookies();
		driver.close();
		
	}

}