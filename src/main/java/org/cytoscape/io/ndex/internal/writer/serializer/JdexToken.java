package org.cytoscape.io.ndex.internal.writer.serializer;

public enum JdexToken {

	NODES("nodes"),NODE_NAME("name"),NODE_REPRESENT("represents"),
	
	EDGES("edges"),EDGE_SOURCE("s"),EDGE_PREDICATE("p"),EDGE_TARGET("o"),
	
	TERMS("terms"),TERM_NAME("name"),TERM_NAMESPACE("ns");
	
	
	
	private final String name;
	
	private JdexToken(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
