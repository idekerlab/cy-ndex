package org.cytoscape.io.ndex.internal.rest;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;

/**
 * Simple REST Client for NDEX web service.
 * 
 *  TODO: support user authentication!
 *
 */
public class NdexRestClient {

	/**
	 * 
	 * Find a network by keyword(s):
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#findnetworks
	 * @param query
	 * @return
	 */
	public Collection<String> findNetworks(final String query) {
		return null;
	}


	/**
	 * Get a network data from NDEX and create new CyNetwork.
	 * 
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#getnetwork
	 */
	public CyNetwork getNetwork(final String ndexNetworkId) {
		return null;
	}
	
	
	/**
	 * Delete the specified network
	 * 
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#deletenetwork
	 * @param networkId
	 * @return
	 */
	public void deleteNetwork(final String ndexNetworkId) {
	}
	
	/**
	 * 
	 * Create new network entry in NDEX from existing CyNetwork.
	 * 
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#createnetwork
	 * 
	 * @param query
	 * @return
	 */
	public void createNetwork(final CyNetwork network) {
		
	}
	
	
	/**
	 * Synchronize local CyNetwork and remote data in NDEX. 
	 * 
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#updatenetwork
	 */
	public void updateNetwork(final CyNetwork localNetwork, final String ndexNetworkId) {
		
	}
}