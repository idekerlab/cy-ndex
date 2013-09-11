package org.cytoscape.io.ndex.internal.reader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

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
		if(inputStream == null) {
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
		
		
		HashMap<String, CyNode> nodeMap = new HashMap<String, CyNode>();
		//add nodes
		final JsonNode nodes = rootNode.path("nodes");
		//final Iterator<String> nodeNames = nodes.fieldNames();
		for(Iterator<String> nodeNames = nodes.fieldNames();nodeNames.hasNext();){
			final CyNode node = network.addNode();
			nodeMap.put(nodeNames.next(), node);
		}
		
		/*
		for (final JsonNode jNode : nodes.fieldNames()) {
			final CyNode node = network.addNode();
			nodeMap.put(jNode., value)
			
		}
		*/
		
		//add edges
		//HashMap<String, CyEdge> edgeMap = new HashMap<String, CyEdge>();
		
		final JsonNode edges = rootNode.path("edges");
		for (final JsonNode jNode : edges) {
			//System.out.println(nodeMap.get(jNode.get("s").asText()));
			final CyEdge edge = network.addEdge(nodeMap.get(jNode.get("s").asText()),nodeMap.get(jNode.get("o").asText()) , true);
		}
		
	}
}
