package org.cytoscape.io.ndex.internal.reader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

		// create term map
		Map<String, String> termMap = new HashMap<String, String>();
		final JsonNode terms = rootNode.path("terms");

		for (Iterator<String> termNames = terms.fieldNames(); termNames
				.hasNext();) {
			final String termName = termNames.next();
			termMap.put(termName, terms.get(termName).get("name").asText());
		}

		// add nodes
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

		final JsonNode nodes = rootNode.path("nodes");

		// immutable value is not fixed
		network.getDefaultNodeTable().createColumn("readable name",
				String.class, true);

		for (Iterator<String> nodeNames = nodes.fieldNames(); nodeNames
				.hasNext();) {
			final String interId = nodeNames.next();
			final String name = nodes.get(interId).get("name").asText();
			final String readableName = termMap.get(nodes.get(interId)
					.get("represents").asText());
			// System.out.println(name +" "+readableName);

			final CyNode node = network.addNode();

			network.getRow(node).set(CyNetwork.NAME, name);
			network.getRow(node).set("readable name", readableName);

			nodeMap.put(interId, node);

		}

		// add edges
		final JsonNode edges = rootNode.path("edges");
		network.getDefaultEdgeTable().createColumn("predicate id",
				String.class, true);
		for (final JsonNode jNode : edges) {
			final CyNode soueceNode = nodeMap.get(jNode.get("s").asText());
			final CyNode targetNode = nodeMap.get(jNode.get("o").asText());

			final CyEdge edge = network.addEdge(soueceNode, targetNode, true);
			network.getRow(edge).set("predicate id", jNode.get("p").asText());
		}

	}
}
