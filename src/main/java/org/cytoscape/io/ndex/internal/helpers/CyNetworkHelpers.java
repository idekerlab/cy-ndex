package org.cytoscape.io.ndex.internal.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.ndexbio.model.object.Edge;
import org.ndexbio.model.object.Network;
import org.ndexbio.model.object.Node;

public class CyNetworkHelpers {
	
	public static void populateNetworkFromNdex(Network ndexNetwork, CyNetwork cyNet) throws IllegalArgumentException, Exception{
		
		
		Map<String, CyNode>cyNodeMap = new HashMap<String, CyNode>();
		
		System.out.println("Populating from ndex network. Keys in edge map = " + ndexNetwork.getEdges().keySet().size());
		
		for (Entry<String,Edge> entry : ndexNetwork.getEdges().entrySet()){
			Edge ndexEdge = entry.getValue();
			String ndexId = entry.getKey();
			
			printEdge(ndexId, ndexEdge);
			
			// Handle subject
			CyNode subject = findOrCreateCyNode(ndexEdge.getS(), ndexNetwork, cyNet, cyNodeMap);
						
			// Handle object
			CyNode object = findOrCreateCyNode(ndexEdge.getO(), ndexNetwork, cyNet, cyNodeMap);
			
			CyEdge edge = cyNet.addEdge(subject, object, true);
			
			// Handle predicate		
		}
		

	}

	private static void printEdge(String ndexId, Edge ndexEdge) {
		System.out.println("Ndex Edge id = " + ndexId + 
				" S = " + ndexEdge.getS() + 
				" P = " + ndexEdge.getP() +
				" O = " + ndexEdge.getO());
		
	}

	private static CyNode findOrCreateCyNode(String nodeNdexId, Network ndexNetwork, CyNetwork cyNet, Map<String, CyNode> cyNodeMap) {
		CyNode cyNode = cyNodeMap.get(nodeNdexId);
		if (null != cyNode) {
			System.out.println("Found cyNode by nodeId in map = " + nodeNdexId);
			return cyNode;
		}
		System.out.println("Creating cyNode for nodeId = " + nodeNdexId);
		Node ndexNode = ndexNetwork.getNodes().get(nodeNdexId);
		cyNode = cyNet.addNode();
		cyNodeMap.put(nodeNdexId, cyNode);
		cyNet.getDefaultNodeTable().getRow(cyNode.getSUID()).set("name", nodeNdexId);
		return cyNode;
	}

}
