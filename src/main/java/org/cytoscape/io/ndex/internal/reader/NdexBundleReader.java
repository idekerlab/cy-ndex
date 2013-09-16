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

		// create term map
		Map<String, String> termMap = new HashMap<String, String>();
		final JsonNode terms = rootNode.path(JdexToken.TERMS.getName());

		for (Iterator<String> termNames = terms.fieldNames(); termNames
				.hasNext();) {
			final String termName = termNames.next();
			termMap.put(termName, terms.get(termName).get(JdexToken.TERM_NAME.getName()).asText());
		}

		// add nodes
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();

		final JsonNode nodes = rootNode.path(JdexToken.NODES.getName());

		// field name is not fixed
		network.getDefaultNodeTable().createColumn(JdexToken.NODE_REPRESENT.getName(),
				String.class, true);

		for (Iterator<String> nodeNames = nodes.fieldNames(); nodeNames
				.hasNext();) {
			final String interId = nodeNames.next();
			final String name = nodes.get(interId).get(JdexToken.NODE_NAME.getName()).asText();
			final String readableName = termMap.get(nodes.get(interId)
					.get(JdexToken.NODE_REPRESENT.getName()).asText());
			// System.out.println(name +" "+readableName);

			final CyNode node = network.addNode();

			network.getRow(node).set(CyNetwork.NAME, name);
			network.getRow(node).set(JdexToken.NODE_REPRESENT.getName(), readableName);

			nodeMap.put(interId, node);

		}

		// add edges
		final JsonNode edges = rootNode.path(JdexToken.EDGES.getName());
		network.getDefaultEdgeTable().createColumn(JdexToken.EDGE_PREDICATE.getName(),
				String.class, true);
		for (final JsonNode jNode : edges) {
			final CyNode soueceNode = nodeMap.get(jNode.get(JdexToken.EDGE_SOURCE.getName()).asText());
			final CyNode targetNode = nodeMap.get(jNode.get(JdexToken.EDGE_TARGET.getName()).asText());

			final CyEdge edge = network.addEdge(soueceNode, targetNode, true);
			
			final String readableName = termMap.get(jNode.get(JdexToken.EDGE_PREDICATE.getName()).asText());
			network.getRow(edge).set(JdexToken.EDGE_PREDICATE.getName(), readableName);
		}

	}
	
}
