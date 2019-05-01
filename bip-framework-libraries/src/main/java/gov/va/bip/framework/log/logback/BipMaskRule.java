package gov.va.bip.framework.log.logback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.MULTILINE;
import static java.util.regex.Pattern.compile;

/**
 * Rule to masks sensitive information in logs.
 */
public class BipMaskRule {
	@SuppressWarnings("unused")
	private String name;
	private Pattern pattern;
	private int unmasked;

	/**
	 *
	 * @param name
	 *            a friendly name for the rule.
	 * @param prefix
	 *            a literal prefix preceding the actual search pattern.
	 * @param suffix
	 *            a literal suffix preceding the actual search pattern.
	 * @param pattern
	 *            a regular expression pattern to identify the personally
	 *            identifiable information.
	 * @param unmasked
	 *            the number of characters to leave unmasked.
	 */
	BipMaskRule(String name, String prefix, String suffix, String pattern, int unmasked) {
		this.name = parse(name);
		this.pattern = parse(prefix, suffix, pattern);
		this.unmasked = unmasked;
	}

	private String parse(String name) {
		if (nullOrBlank(name)) {
			throw new IllegalArgumentException("Name cannot be null blank!");
		}
		return name.trim();
	}

	private static String repeat(String input, int times) {
		if (times <= 0) {
			return "";
		} else if (times % 2 == 0) {
			return repeat(input + input, times / 2);
		} else
			return input + repeat(input + input, times / 2);
	}

	private static Pattern parse(String prefix, String suffix, String pattern) {
		String parsedPrefix = nullOrBlank(prefix) ? "" : "(?<=" + prefix + ")(?:\\s*)";
		String parsedSuffix = nullOrBlank(suffix) ? "" : "(?:\\s*)(?=" + suffix + ")";
		return compile(parsedPrefix + validated(pattern) + parsedSuffix, DOTALL | MULTILINE);
	}

	private static String validated(String pattern) {
		if (nullOrBlank(pattern)) {
			throw new IllegalArgumentException("Need a non-blank pattern value!");
		}
		return pattern.startsWith("(") ? pattern : "(" + pattern + ")";
	}

	private static boolean nullOrBlank(String input) {
		return input == null || "".equals(input.trim());
	}

	/**
	 * Applies the masking rule to the input.
	 * 
	 * @param input
	 *            the PII that needs to be masked.
	 * @return the masked version of the input.
	 */
	public String apply(String input) {
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			String match = matcher.group(1);
			String mask = repeat("X", Math.min(match.length(), match.length() - unmasked));
			String replacement = mask + match.substring(mask.length());
			return input.replace(match, replacement);
		}
		return input;
	}

	/**
	 * Helper to create a new rule instance.
	 * 
	 * @see BipMaskRule
	 * @see ch.qos.logback.classic.joran.JoranConfigurator
	 */
	public static class Definition {

		private String name;
		private String prefix = "";
		private String suffix = "";
		private String pattern;
		private int unmasked = 0;

		/**
		 * Instantiates a new definition.
		 */
		public Definition() {
		}

		/**
		 * Instantiates a new definition.
		 *
		 * @param name
		 *            the name
		 * @param pattern
		 *            the pattern
		 */
		public Definition(String name, String pattern) {
			this(name, "", "", pattern, 0);
		}

		/**
		 * Instantiates a new definition.
		 *
		 * @param name
		 *            the name
		 * @param prefix
		 *            the prefix
		 * @param suffix
		 *            the suffix
		 * @param pattern
		 *            the pattern
		 * @param unmasked
		 *            the unmasked
		 */
		public Definition(String name, String prefix, String suffix, String pattern, int unmasked) {
			this.name = name;
			this.prefix = prefix;
			this.suffix = suffix;
			this.pattern = pattern;
			this.unmasked = unmasked;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the prefix
		 */
		public String getPrefix() {
			return prefix;
		}

		/**
		 * @param prefix
		 *            the prefix to set
		 */
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		/**
		 * @return the suffix
		 */
		public String getSuffix() {
			return suffix;
		}

		/**
		 * @param suffix
		 *            the suffix to set
		 */
		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}

		/**
		 * @return the pattern
		 */
		public String getPattern() {
			return pattern;
		}

		/**
		 * @param pattern
		 *            the pattern to set
		 */
		public void setPattern(String pattern) {
			this.pattern = pattern;
		}

		/**
		 * @return the unmasked
		 */
		public int getUnmasked() {
			return unmasked;
		}

		/**
		 * @param unmasked
		 *            the unmasked to set
		 */
		public void setUnmasked(int unmasked) {
			this.unmasked = unmasked;
		}

		/**
		 * Rule.
		 *
		 * @return the mask rule
		 */
		public BipMaskRule rule() {
			return new BipMaskRule(name, prefix, suffix, pattern, unmasked);
		}
	}
}