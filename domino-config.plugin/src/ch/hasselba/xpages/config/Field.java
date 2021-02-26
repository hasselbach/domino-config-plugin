package ch.hasselba.xpages.config;

import java.io.Serializable;

public class Field implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private boolean required;
	private String defaultValue;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
}
