package gov.va.bip.framework.test.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.junit.Test;

import gov.va.bip.framework.test.util.PropertiesUtil;

public class PropertiesUtilTest {

	@Test(expected = UnsupportedOperationException.class)
	public void utilityClassTest() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
		final Constructor<PropertiesUtil> constructor = PropertiesUtil.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
		} catch (InvocationTargetException e) {
			throw (UnsupportedOperationException) e.getTargetException();
		}
	}

	@Test
	public void testreadFile_Success() {

		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("test-properties.properties");
		Properties properties = PropertiesUtil.readFile(urlConfigFile);
		assertThat("reference", equalTo(properties.get("project")));
	}

	@Test
	public void testreadEmptyFile_Success() {

		final URL urlConfigFile = PropertiesUtilTest.class.getClassLoader().getResource("empty-properties.properties");
		Properties properties = PropertiesUtil.readFile(urlConfigFile);
		assertThat(true, equalTo(properties.isEmpty()));
	}

	@Test
	public void testreadFile_failure() throws MalformedURLException {
		URL urlConfigFile = new URL("file:/E:/Program Files/IBM/SDP/runtimes/base");
		Properties properties = PropertiesUtil.readFile(urlConfigFile);
		assertThat(true, equalTo(properties == null));

	}

}
