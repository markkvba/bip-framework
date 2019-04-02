package gov.va.ocp.framework.messages;

public class ConstraintParam {

	private String name;
	private String value;

	/**
	 * Construct a constraint parameter.
	 * <p>
	 * Typically useful with passing message that need to include violation constraint paramters and the like.
	 * 
	 * @param name - the name of the parameter
	 * @param value - the value of the parameter
	 */
	public ConstraintParam(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * The name for the constraint.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The name for the constraint.
	 * 
	 * @param name - the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The value for the constraint.
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * The value for the constraint.
	 * 
	 * @param value - the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
