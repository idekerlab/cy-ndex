package org.cytoscape.io.ndex.internal.writer.serializer;

import java.util.List;

public class JdexSupportObject {
	String id;
	String text;
	int citation;
	List<Integer> edges;
	public JdexSupportObject(String id,String text, int citation, List<Integer> edges) {
		this.id = id;
		this.text = text;
		this.citation = citation;
		this.edges = edges;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getCitation() {
		return citation;
	}
	public void setCitation(int citation) {
		this.citation = citation;
	}
	public List<Integer> getEdges() {
		return edges;
	}
	public void setEdges(List<Integer> edges) {
		this.edges = edges;
	}

	
	/*
	 * "supports": { "72": { "text":
	 * "Oxidation and nitration of macromolecules, such as\nproteins, DNA and lipids, are prominent in atherosclerotic\narteries."
	 * , "citation": 69, "edges": [ 86, 82 ] },
	 */
}
