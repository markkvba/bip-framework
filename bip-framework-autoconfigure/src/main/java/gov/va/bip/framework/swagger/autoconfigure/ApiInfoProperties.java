package gov.va.bip.framework.swagger.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "info")
public class ApiInfoProperties {

	/** The title. */
	private String title = "";

	/** The description. */
	private String description = "";

	/** The version. */
	private String version = "";

	/** The terms of service url. */
	private String termsOfService = "";

	/** The contact. */
	private Contact contact = new Contact();

	/** The license. */
	private License license = new License();

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTermsOfService() {
		return termsOfService;
	}

	public void setTermsOfService(String termsOfService) {
		this.termsOfService = termsOfService;
	}

	/**
	 * The Class Contact.
	 */
	public static class Contact {
		/** The contact name. */
		private String name = "";

		/** The contact url. */
		private String email = "";

		/** The contact email. */
		private String url = "";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
	}

	/**
	 * The Class License.
	 */
	public static class License {

		/** The license name. */
		private String name = "";

		/** The license url. */
		private String url = "";

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}
