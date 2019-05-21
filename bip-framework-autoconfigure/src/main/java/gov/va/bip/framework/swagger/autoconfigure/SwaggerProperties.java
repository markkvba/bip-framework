package gov.va.bip.framework.swagger.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by vgadda on 8/3/17.
 */

@ConfigurationProperties(prefix = "bip.framework.swagger")
public class SwaggerProperties {

	private boolean enabled = true;

	/** The secure paths. */
	private String securePaths = "[Api secure paths via bip.framework.swagger.securePaths]";

	/** The group name. */
	private String groupName = "";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSecurePaths() {
		return securePaths;
	}

	public void setSecurePaths(String securePaths) {
		this.securePaths = securePaths;
	}
}
