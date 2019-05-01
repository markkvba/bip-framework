package gov.va.bip.framework.log.logback;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BinaryOperator;

/**
 * The Class BipMaskRules.
 */
public class BipMaskRules {

	/** The Constant NO_OP. */
	private static final BinaryOperator<String> NO_OP = (in, out) -> {
		throw new UnsupportedOperationException("Only needed for parallel streams!");
	};

	/** The rule. */
	private final Set<BipMaskRule> rules = new LinkedHashSet<>();

	/**
	 * Adds the rule.
	 *
	 * @param definition
	 *            the definition
	 */
	public void addRule(BipMaskRule.Definition definition) {
		rules.add(definition.rule());
	}

	/**
	 * Apply.
	 *
	 * @param input
	 *            the input
	 * @return the string
	 */
	public String apply(String input) {
		return rules.stream().reduce(input, (out, rule) -> rule.apply(out), NO_OP);
	}
}
