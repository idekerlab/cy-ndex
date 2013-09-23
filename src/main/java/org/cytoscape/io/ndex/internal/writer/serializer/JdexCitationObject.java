package org.cytoscape.io.ndex.internal.writer.serializer;

import java.util.List;

public class JdexCitationObject {
	private String id;
	private String identifier;
	private String type;
	private String title;
	private List<Integer> edges;
	private List<String> contributors;
	
	public JdexCitationObject(String id,String identifier, String type, String title,
			List<Integer> edges, List<String> contributors) {
		this.id = id;
		this.identifier = identifier;
		this.type = type;
		this.title = title;
		this.edges = edges;
		this.contributors = contributors;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Integer> getEdges() {
		return edges;
	}
	public void setEdges(List<Integer> edges) {
		this.edges = edges;
	}
	public List<String> getContributors() {
		return contributors;
	}
	public void setContributors(List<String> contributors) {
		this.contributors = contributors;
	}
	
	
}
