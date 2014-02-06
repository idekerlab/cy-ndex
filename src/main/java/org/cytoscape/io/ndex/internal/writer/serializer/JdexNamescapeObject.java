package org.cytoscape.io.ndex.internal.writer.serializer;

public class JdexNamescapeObject {
	String id;
	String uri;
	String prefix;
	String description;
	public JdexNamescapeObject(String id, String uri, String prefix,
			String description) {
		super();
		this.id = id;
		this.uri = uri;
		this.prefix = prefix;
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
