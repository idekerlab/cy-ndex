package org.cytoscape.io.ndex.internal.reader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cytoscape.io.ndex.internal.writer.serializer.JdexToken;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reader for NDEX data bundle. For the first version, it supports network
 * section only.
 * 
 */
public class NdexBundleReader extends AbstractCyNetworkReader {

	// Supports only one CyNetwork per file.
	private CyNetwork network = null;

	public NdexBundleReader(InputStream inputStream,
			CyNetworkViewFactory cyNetworkViewFactory,
			CyNetworkFactory cyNetworkFactory,
			CyNetworkManager cyNetworkManager,
			CyRootNetworkManager cyRootNetworkManager) {
		super(inputStream, cyNetworkViewFactory, cyNetworkFactory,
				cyNetworkManager, cyRootNetworkManager);
		if (inputStream == null) {
			throw new NullPointerException("Input Stream cannot be null.");
		}

	}

	@Override
	public CyNetwork[] getNetworks() {
		CyNetwork[] result = new CyNetwork[1];
		result[0] = network;
		return result;
	}

	@Override
	public CyNetworkView buildCyNetworkView(CyNetwork network) {
		return null;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		this.network = cyNetworkFactory.createNetwork();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(inputStream);


		// create namespace prefix map
		Map<String, String> nsPrefixMap = new HashMap<String, String>();
		final JsonNode namespaces = rootNode.path("namespaces");
		for (Iterator<String> nsIds = namespaces.fieldNames(); nsIds
				.hasNext();) {
			final String nsId = nsIds.next();
			nsPrefixMap.put(nsId, namespaces.get(nsId).get("prefix").asText());
		}
		
		
		// create term map
		Map<String, String> termMap = new HashMap<String, String>();
		final JsonNode terms = rootNode.path(JdexToken.TERMS.getName());

		for (Iterator<String> termNames = terms.fieldNames(); termNames
				.hasNext();) {
			final String termName = termNames.next();
			termMap.put(termName, getTermString(termName, terms,nsPrefixMap));

		}

		// add nodes and create a node map
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
		final JsonNode nodes = rootNode.path(JdexToken.NODES.getName());
	
		//// column name is not fixed
		network.getDefaultNodeTable().createColumn(
				JdexToken.NODE_REPRESENT.getName(), String.class, true);

		for (Iterator<String> nodeNames = nodes.fieldNames(); nodeNames
				.hasNext();) {
			final String interId = nodeNames.next();
			final String name = nodes.get(interId)
					.get(JdexToken.NODE_NAME.getName()).asText();
			final String readableName = termMap.get(nodes.get(interId)
					.get(JdexToken.NODE_REPRESENT.getName()).asText());
			final CyNode node = network.addNode();

			network.getRow(node).set(CyNetwork.NAME, name);
			network.getRow(node).set(JdexToken.NODE_REPRESENT.getName(),
					readableName);
			nodeMap.put(interId, node);
		}

		// add edges and create a edge map
		Map<String, CyEdge> edgeMap = new HashMap<String,CyEdge>();
		final JsonNode edges = rootNode.path(JdexToken.EDGES.getName());
		
		network.getDefaultEdgeTable().createColumn(
				JdexToken.EDGE_PREDICATE.getName(), String.class, true);
		
		for(Iterator<String> edgeNames = edges.fieldNames();edgeNames.hasNext();){
			final String interId = edgeNames.next();
			JsonNode jNode = edges.get(interId);
			
			final CyNode soueceNode = nodeMap.get(jNode.get(
					JdexToken.EDGE_SOURCE.getName()).asText());
			final CyNode targetNode = nodeMap.get(jNode.get(
					JdexToken.EDGE_TARGET.getName()).asText());

			final CyEdge edge = network.addEdge(soueceNode, targetNode, true);

			final String readableName = termMap.get(jNode.get(
					JdexToken.EDGE_PREDICATE.getName()).asText());
			network.getRow(edge).set(JdexToken.EDGE_PREDICATE.getName(),
					readableName);
			
			edgeMap.put(interId, edge);
		}
		
		/*
		for (final JsonNode jNode : edges) {
			final CyNode soueceNode = nodeMap.get(jNode.get(
					JdexToken.EDGE_SOURCE.getName()).asText());
			final CyNode targetNode = nodeMap.get(jNode.get(
					JdexToken.EDGE_TARGET.getName()).asText());

			final CyEdge edge = network.addEdge(soueceNode, targetNode, true);

			final String readableName = termMap.get(jNode.get(
					JdexToken.EDGE_PREDICATE.getName()).asText());
			network.getRow(edge).set(JdexToken.EDGE_PREDICATE.getName(),
					readableName);
		}
		*/
		
		//TODO create citations column
		
		//TODO create supports column

	}

	private String getTermString(String termId, JsonNode terms, Map<String,String> prefixMap) {
		String result = "";
		if (terms.get(termId).has("name")) {
			if(terms.get(termId).has("ns"))
				return prefixMap.get(terms.get(termId).get("ns").asText())+":"+terms.get(termId).get("name").asText();
			else
				return terms.get(termId).get("name").asText();
		} else if (terms.get(termId).has("termFunction")) {
			String functionNameId = terms.get(termId).get("termFunction")
					.asText();
			String functionName = getTermString(functionNameId, terms, prefixMap);

			result = functionName + "(";

			JsonNode params = terms.get(termId).get("parameters");
			Iterator<JsonNode> ite = params.elements();
			while (ite.hasNext()) {
				JsonNode paramNode = ite.next();
				if (paramNode.has("term")) {
					String paramId = paramNode.get("term").asText();
					result = result+getTermString(paramId, terms, prefixMap);
				}else{
					result = result+paramNode.asText();
				}
				if(ite.hasNext())
					result = result+",";
			}
			result = result + ")";
		}
		return result;
	}

}
