package gov.va.bip.framework.security.csrf;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
public class CsrfTokenProvider {

	/**
	 * Respond to CSRF protection token and return null since CSRF is disabled in BIP framework. This solution is chosen because
	 * disabling CSRF is not currently being supported in SwaggerUI as given in https://github.com/springfox/springfox/pull/2639
	 *
	 * @return the CsrfToken being returned
	 */
	@RequestMapping("/csrf")
	public CsrfToken csrf() {
		return null;
		// logic to return the CSRF token:
		// return null since no CSRF token is required
	}

}
