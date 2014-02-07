package org.cytoscape.io.ndex.internal.reader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cytoscape.io.ndex.internal.writer.serializer.JdexToken;
import org.cytoscape.io.read.AbstractCyNetworkReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.omg.CORBA.CTX_RESTRICT_SCOPE;

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
	private JsonNode ndexNetwork = null;

	public NdexBundleReader(JsonNode ndexNetwork,
			CyNetworkViewFactory cyNetworkViewFactory,
			CyNetworkFactory cyNetworkFactory,
			CyNetworkManager cyNetworkManager,
			CyRootNetworkManager cyRootNetworkManager) {
		super(null, cyNetworkViewFactory, cyNetworkFactory,
				cyNetworkManager, cyRootNetworkManager);
		if (ndexNetwork == null) {
			throw new NullPointerException("ndexNetwork cannot be null.");
		}
		this.ndexNetwork = ndexNetwork;

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

	// TODO refactor
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		CyNetwork tempNetwork = cyNetworkFactory.createNetwork();

		ObjectMapper mapper = new ObjectMapper();
		

		// create a namespace prefix map
		final JsonNode nsJsNode = this.ndexNetwork.path("namespaces");
		Map<String, String> nsPrefixMap = createPrefixMap(nsJsNode);

		// create a term map
		final JsonNode termJsNode = this.ndexNetwork.path(JdexToken.TERMS.getName());
		Map<String, String> termMap = createTermMap(nsPrefixMap, termJsNode);

		// add nodes and create a node map
		final JsonNode nodeJsNode = this.ndexNetwork.path(JdexToken.NODES.getName());
		Map<String, CyNode> nodeMap = addNodes(tempNetwork, termMap, nodeJsNode);

		// add edges and create a edge map
		final JsonNode edgeJsNode = this.ndexNetwork.path(JdexToken.EDGES.getName());
		Map<String, CyEdge> edgeMap = addEdges(tempNetwork, termMap, nodeMap,
				edgeJsNode);

		// create citations columns
		final JsonNode citationJsNode = this.ndexNetwork.path(JdexToken.CITATIONS.getName());
		addCitations(tempNetwork, edgeMap, citationJsNode);

		//create supports columns
		final JsonNode supportJsNode = this.ndexNetwork.path(JdexToken.SUPPORTS.getName());
		addSupports(tempNetwork, edgeMap, supportJsNode);
		
		this.network = tempNetwork;

	}

	private void addSupports(CyNetwork tempNetwork,
			Map<String, CyEdge> edgeMap, final JsonNode supportJsNode) {
		// TODO decide column name
		tempNetwork.getDefaultEdgeTable().createColumn(JdexToken.COLUMN_TEXT.getName(),
				String.class, true);
		for (final JsonNode jsNode : supportJsNode) {
			final String text = jsNode.get(JdexToken.TEXT.getName()).asText();
			if (jsNode.has(JdexToken.EDGES.getName())) {
				for(final Iterator<JsonNode> edgeIte = jsNode.get(JdexToken.EDGES.getName()).elements();edgeIte.hasNext();){
					CyEdge edge = edgeMap.get(edgeIte.next().asText());
					CyRow row = tempNetwork.getRow(edge);
					row.set(JdexToken.COLUMN_TEXT.getName(), text);
				}
			}

		}
	}

	private void addCitations(CyNetwork tempNetwork,
			Map<String, CyEdge> edgeMap, final JsonNode citationJsNode) {
		// TODO decide column name
		tempNetwork.getDefaultEdgeTable().createColumn(JdexToken.COLUMN_IDENTIFIER.getName(),
				String.class, true);
		tempNetwork.getDefaultEdgeTable().createColumn(JdexToken.COLUMN_TYPE.getName(),
				String.class, true);
		tempNetwork.getDefaultEdgeTable().createColumn(JdexToken.COLUMN_TITLE.getName(),
				String.class, true);
		tempNetwork.getDefaultEdgeTable().createListColumn(
				JdexToken.COLUMN_CONTRIBUTORS.getName(), String.class, true);

		for (final JsonNode jsNode : citationJsNode) {
			final String identifier = jsNode.get(JdexToken.IDENTIFIER.getName()).asText();
			final String type = jsNode.get(JdexToken.TYPE.getName()).asText();
			final String title = jsNode.get(JdexToken.TITLE.getName()).asText();

			List<String> contributors = new ArrayList<String>();

			for (Iterator<JsonNode> contributorIte = jsNode.get(JdexToken.CONTRIBUTORS.getName())
					.elements(); contributorIte.hasNext();) {
				String temp = contributorIte.next().asText();
				contributors.add(temp);
			}

			for (Iterator<JsonNode> edgeIte = jsNode.get("edges").elements(); edgeIte
					.hasNext();) {
				CyEdge edge = edgeMap.get(edgeIte.next().asText());
				CyRow row = tempNetwork.getRow(edge);
				row.set(JdexToken.COLUMN_IDENTIFIER.getName(), identifier);
				row.set(JdexToken.COLUMN_TYPE.getName(), type);
				row.set(JdexToken.COLUMN_TITLE.getName(), title);
				row.set(JdexToken.COLUMN_CONTRIBUTORS.getName(), contributors);
			}

		}
	}

	private Map<String, CyEdge> addEdges(CyNetwork tempNetwork,
			Map<String, String> termMap, Map<String, CyNode> nodeMap,
			final JsonNode edgeJsNode) {
		Map<String, CyEdge> edgeMap = new HashMap<String, CyEdge>();
		// TODO decide column name
		tempNetwork.getDefaultEdgeTable().createColumn(
				JdexToken.EDGE_PREDICATE.getName(), String.class, true);

		for (Iterator<String> edgeIds = edgeJsNode.fieldNames(); edgeIds
				.hasNext();) {
			final String edgeId = edgeIds.next();
			JsonNode tempJsNode = edgeJsNode.get(edgeId);

			final CyNode soueceNode = nodeMap.get(tempJsNode.get(
					JdexToken.EDGE_SOURCE.getName()).asText());
			final CyNode targetNode = nodeMap.get(tempJsNode.get(
					JdexToken.EDGE_TARGET.getName()).asText());

			final CyEdge edge = tempNetwork.addEdge(soueceNode, targetNode, true);

			final String predicate = termMap.get(tempJsNode.get(
					JdexToken.EDGE_PREDICATE.getName()).asText());
			tempNetwork.getRow(edge).set(JdexToken.EDGE_PREDICATE.getName(),
					predicate);

			edgeMap.put(edgeId, edge);
		}
		return edgeMap;
	}

	private Map<String, CyNode> addNodes(CyNetwork tempNetwork,
			Map<String, String> termMap, final JsonNode nodeJsNode) {
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

		// TODO decide column name
		tempNetwork.getDefaultNodeTable().createColumn(
				JdexToken.NODE_REPRESENT.getName(), String.class, true);

		for (Iterator<String> nodeIds = nodeJsNode.fieldNames(); nodeIds
				.hasNext();) {
			final String nodeId = nodeIds.next();
			final String name = nodeJsNode.get(nodeId)
					.get(JdexToken.NODE_NAME.getName()).asText();
			final String represent = termMap.get(nodeJsNode.get(nodeId)
					.get(JdexToken.NODE_REPRESENT.getName()).asText());
			final CyNode node = tempNetwork.addNode();

			tempNetwork.getRow(node).set(CyNetwork.NAME, name);
			tempNetwork.getRow(node).set(JdexToken.NODE_REPRESENT.getName(),
					represent);
			nodeMap.put(nodeId, node);
		}
		return nodeMap;
	}

	private Map<String, String> createTermMap(Map<String, String> nsPrefixMap,
			final JsonNode termJsNode) {
		Map<String, String> termMap = new HashMap<String, String>();
		for (Iterator<String> termIds = termJsNode.fieldNames(); termIds
				.hasNext();) {
			final String termId = termIds.next();
			termMap.put(termId, getTermString(termId, termJsNode, nsPrefixMap));
		}
		return termMap;
	}

	private Map<String, String> createPrefixMap(final JsonNode nsJsNode) {
		Map<String, String> nsPrefixMap = new HashMap<String, String>();
		for (Iterator<String> nsIds = nsJsNode.fieldNames(); nsIds.hasNext();) {
			final String nsId = nsIds.next();
			nsPrefixMap.put(nsId, nsJsNode.get(nsId).get("prefix").asText());
		}
		return nsPrefixMap;
	}

	// TODO refactor
	private String getTermString(String termId, JsonNode terms,
			Map<String, String> prefixMap) {
		String result = "";
		if (terms.get(termId).has("name")) {
			if (terms.get(termId).has("ns"))
				return prefixMap.get(terms.get(termId).get("ns").asText())
						+ ":" + terms.get(termId).get("name").asText();
			else
				return terms.get(termId).get("name").asText();
		} else if (terms.get(termId).has("termFunction")) {
			String functionNameId = terms.get(termId).get("termFunction")
					.asText();
			String functionName = getTermString(functionNameId, terms,
					prefixMap);

			result = functionName + "(";

			JsonNode params = terms.get(termId).get("parameters");
			Iterator<JsonNode> ite = params.elements();
			while (ite.hasNext()) {
				JsonNode paramNode = ite.next();
				if (paramNode.has("term")) {
					String paramId = paramNode.get("term").asText();
					result = result + getTermString(paramId, terms, prefixMap);
				} else {
					result = result + paramNode.asText();
				}
				if (ite.hasNext())
					result = result + ",";
			}
			result = result + ")";
		}
		return result;
	}

}
