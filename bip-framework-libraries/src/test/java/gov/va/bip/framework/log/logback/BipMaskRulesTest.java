package gov.va.bip.framework.log.logback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class BipMaskRulesTest {

	@Test
	public void shouldValidateAllRules() throws Exception {
		BipMaskRules rules = new BipMaskRules();
		rules.addRule(new BipMaskRule.Definition("Credit Card", "\\d{13,18}"));
		rules.addRule(new BipMaskRule.Definition("SSN", "\\d{3}-?\\d{2}-?\\d{4}"));

		String output =
				rules.apply("My credit card number is 4111111111111111 and my social security number is 123-12-1234");
		assertThat(output).isEqualTo("My credit card number is **************** and my social security number is ***********");
	}
}