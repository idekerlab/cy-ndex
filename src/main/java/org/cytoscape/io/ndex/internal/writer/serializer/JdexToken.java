package org.cytoscape.io.ndex.internal.writer.serializer;

public enum JdexToken {

	NODES("nodes"),NODE_NAME("name"),NODE_REPRESENT("represents"),
	
	EDGES("edges"),EDGE_SOURCE("s"),EDGE_PREDICATE("p"),EDGE_TARGET("o"),
	
	TERMS("terms"),TERM_NAME("name"),TERM_NAMESPACE("ns"),TERM_FUNCTION("termFunction"),
	TERM_PARAMETERS("parameters"),TERM("term"),
	
	NAMESPACES("namespaces"),URI("uri"),PREFIX("prefix"),DESCRIPTION("description"),
	
	NODETYPES("nodeTypes"),
	
	PROPERTIES("properties"),
	
	CITATIONS("citations"),IDENTIFIER("identifier"),TYPE("type"),TITLE("title"),
	CONTRIBUTORS("contributors"),
	
	SUPPORTS("supports"),TEXT("text"),CITATION("citation");
	
	
	
	private final String name;
	
	private JdexToken(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
