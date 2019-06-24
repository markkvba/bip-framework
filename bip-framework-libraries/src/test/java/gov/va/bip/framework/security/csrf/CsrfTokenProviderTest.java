package gov.va.bip.framework.security.csrf;

import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CsrfTokenProviderTest {

	@Test
	public void csrfTokenTest() {
		CsrfTokenProvider provider = new CsrfTokenProvider();
		assertNull(provider.csrf());
	}
}
