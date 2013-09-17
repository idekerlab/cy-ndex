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

	// TODO refactor
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		this.network = cyNetworkFactory.createNetwork();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(inputStream);

		// create namespace prefix map
		Map<String, String> nsPrefixMap = new HashMap<String, String>();
		final JsonNode nsJsNode = rootNode.path("namespaces");
		for (Iterator<String> nsIds = nsJsNode.fieldNames(); nsIds.hasNext();) {
			final String nsId = nsIds.next();
			nsPrefixMap.put(nsId, nsJsNode.get(nsId).get("prefix").asText());
		}

		// create term map
		Map<String, String> termMap = new HashMap<String, String>();
		final JsonNode termJsNode = rootNode.path(JdexToken.TERMS.getName());

		for (Iterator<String> termIds = termJsNode.fieldNames(); termIds
				.hasNext();) {
			final String termId = termIds.next();
			termMap.put(termId, getTermString(termId, termJsNode, nsPrefixMap));

		}

		// add nodes and create a node map
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
		final JsonNode nodeJsNode = rootNode.path(JdexToken.NODES.getName());

		// TODO decide column name
		network.getDefaultNodeTable().createColumn(
				JdexToken.NODE_REPRESENT.getName(), String.class, true);

		for (Iterator<String> nodeIds = nodeJsNode.fieldNames(); nodeIds
				.hasNext();) {
			final String nodeId = nodeIds.next();
			final String name = nodeJsNode.get(nodeId)
					.get(JdexToken.NODE_NAME.getName()).asText();
			final String represent = termMap.get(nodeJsNode.get(nodeId)
					.get(JdexToken.NODE_REPRESENT.getName()).asText());
			final CyNode node = network.addNode();

			network.getRow(node).set(CyNetwork.NAME, name);
			network.getRow(node).set(JdexToken.NODE_REPRESENT.getName(),
					represent);
			nodeMap.put(nodeId, node);
		}

		// add edges and create a edge map
		Map<String, CyEdge> edgeMap = new HashMap<String, CyEdge>();
		final JsonNode edgeJsNode = rootNode.path(JdexToken.EDGES.getName());
		// TODO decide column name
		network.getDefaultEdgeTable().createColumn(
				JdexToken.EDGE_PREDICATE.getName(), String.class, true);

		for (Iterator<String> edgeIds = edgeJsNode.fieldNames(); edgeIds
				.hasNext();) {
			final String edgeId = edgeIds.next();
			JsonNode tempJsNode = edgeJsNode.get(edgeId);

			final CyNode soueceNode = nodeMap.get(tempJsNode.get(
					JdexToken.EDGE_SOURCE.getName()).asText());
			final CyNode targetNode = nodeMap.get(tempJsNode.get(
					JdexToken.EDGE_TARGET.getName()).asText());

			final CyEdge edge = network.addEdge(soueceNode, targetNode, true);

			final String predicate = termMap.get(tempJsNode.get(
					JdexToken.EDGE_PREDICATE.getName()).asText());
			network.getRow(edge).set(JdexToken.EDGE_PREDICATE.getName(),
					predicate);

			edgeMap.put(edgeId, edge);
		}

		// create citations columns

		// TODO decide column name
		this.network.getDefaultEdgeTable().createColumn("citation identifier",
				String.class, true);
		this.network.getDefaultEdgeTable().createColumn("citation type",
				String.class, true);
		this.network.getDefaultEdgeTable().createColumn("citation title",
				String.class, true);
		this.network.getDefaultEdgeTable().createListColumn(
				"citation contributors", String.class, true);

		final JsonNode citationJsNode = rootNode.path("citations");

		for (final JsonNode jNode : citationJsNode) {
			final String identifier = jNode.get("identifier").asText();
			final String type = jNode.get("type").asText();
			final String title = jNode.get("title").asText();

			List<String> contributors = new ArrayList<String>();

			for (Iterator<JsonNode> contributorIte = jNode.get("contributors")
					.elements(); contributorIte.hasNext();) {
				String temp = contributorIte.next().asText();
				contributors.add(temp);
			}

			for (Iterator<JsonNode> edgeIte = jNode.get("edges").elements(); edgeIte
					.hasNext();) {
				CyEdge edge = edgeMap.get(edgeIte.next().asText());
				CyRow row = this.network.getRow(edge);
				row.set("citation identifier", identifier);
				row.set("citation type", type);
				row.set("citation title", title);
				row.set("citation contributors", contributors);
			}

		}
		//create supports column
		// TODO decide column name
		this.network.getDefaultEdgeTable().createColumn("support text",
				String.class, true);

		final JsonNode supportJsNode = rootNode.path("supports");
		for (final JsonNode jNode : supportJsNode) {
			final String text = jNode.get("text").asText();
			if (jNode.has("edges")) {
				for(final Iterator<JsonNode> edgeIte = jNode.get("edges").elements();edgeIte.hasNext();){
					CyEdge edge = edgeMap.get(edgeIte.next().asText());
					CyRow row = this.network.getRow(edge);
					row.set("support text", text);
				}
			}

		}

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
