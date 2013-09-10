package org.cytoscape.io.ndex.internal.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple REST Client for NDEX web service.
 * 
 * TODO: support user authentication!
 * 
 */
public class NdexRestClient {

	CyNetworkFactory factory;

	public NdexRestClient(CyNetworkFactory factory) {
		this.factory = factory;
	}

	/**
	 * 
	 * Find a network by keyword(s):
	 * https://github.com/cytoscape/NDEx-Site/wiki/
	 * REST-API-Documentation#findnetworks
	 * 
	 * @param query
	 * @return
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public Collection<String> findNetworks(final String query)
			throws JsonProcessingException, IOException {

		ObjectMapper mapper = new ObjectMapper();

		URL request = new URL(
				"http://localhost:3333/networks/?searchExpression=" + query
						+ "&limit=100&offset=0");
		JsonNode searchNode = mapper.readTree(request);

		searchNode.path("networks").elements();

		ArrayList<String> result = new ArrayList<String>();
		for (final JsonNode node : searchNode.path("networks")) {
			String resultItem = node.path("jid") + "" + node.path("title");
			System.out.println(resultItem);
			result.add(resultItem);
		}

		return result;
	}

	/**
	 * Get a network data from NDEX and create new CyNetwork.
	 * 
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#
	 * getnetwork
	 */
	public CyNetwork getNetwork(final String ndexNetworkId) {
		return null;
	}

	/**
	 * Delete the specified network
	 * 
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#
	 * deletenetwork
	 * 
	 * @param networkId
	 * @return
	 */
	public void deleteNetwork(final String ndexNetworkId) {
	}

	/**
	 * 
	 * Create new network entry in NDEX from existing CyNetwork.
	 * 
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#
	 * createnetwork
	 * 
	 * @param query
	 * @return
	 */
	public void createNetwork(final CyNetwork network) {

	}

	/**
	 * Synchronize local CyNetwork and remote data in NDEX.
	 * 
	 * https://github.com/cytoscape/NDEx-Site/wiki/REST-API-Documentation#
	 * updatenetwork
	 */
	public void updateNetwork(final CyNetwork localNetwork,
			final String ndexNetworkId) {

	}
}