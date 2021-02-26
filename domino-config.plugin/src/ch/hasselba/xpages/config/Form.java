package ch.hasselba.xpages.config;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Form implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private ConcurrentMap<String, Field> fields = new ConcurrentHashMap<String, Field>();
	
	public ConcurrentMap<String, Field> getFields() {
		return fields;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setFields(ConcurrentMap<String, Field> fields) {
		this.fields = fields;
	}
	

}
