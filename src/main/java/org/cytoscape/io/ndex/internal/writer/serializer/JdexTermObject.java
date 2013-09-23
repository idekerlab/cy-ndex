package org.cytoscape.io.ndex.internal.writer.serializer;

import com.fasterxml.jackson.core.JsonGenerator;

public class JdexTermObject {

	private String name;
	private String id;
	
	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public JdexTermObject(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}



	public void serializeSelf(JsonGenerator jgen) {
		// TODO Auto-generated method stub

	}
}
