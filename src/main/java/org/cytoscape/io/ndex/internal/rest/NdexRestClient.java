package org.cytoscape.io.ndex.internal.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.cytoscape.io.ndex.internal.reader.NdexBundleReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;

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
	CyNetworkViewFactory viewFactory;
	CyNetworkManager networkManager;
	CyRootNetworkManager rootNetworkManager;

	
	public NdexRestClient(CyNetworkFactory factory,
			CyNetworkViewFactory viewFactory, CyNetworkManager networkManager,
			CyRootNetworkManager rootNetworkManager) {
		this.factory = factory;
		this.viewFactory = viewFactory;
		this.networkManager = networkManager;
		this.rootNetworkManager = rootNetworkManager;
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
		HttpURLConnection con = (HttpURLConnection) request.openConnection();
		addBasicAuth(con);
		/*
		 * String credentials = "dexterpratt:insecure"; String basicAuth =
		 * "Basic " + new String(new Base64().encode(credentials.getBytes()));
		 * con.setRequestProperty("Authorization", basicAuth);
		 */

		InputStream is = con.getInputStream();

		JsonNode searchNode = mapper.readTree(is);

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
	 * 
	 * @throws IOException
	 */
	public CyNetwork getNetwork(final String ndexNetworkId) throws IOException {
		
		URL request = new URL("http://localhost:3333/networks/"+ndexNetworkId);
		HttpURLConnection con = (HttpURLConnection)request.openConnection();
		addBasicAuth(con);
		InputStream is = con.getInputStream();
		
		//NdexBundleReader reader = new NdexBundleReader(is, viewFactory, factory, networkManager, rootNetworkManager);
		CyNetwork network = factory.createNetwork();
		return network;
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

	private void addBasicAuth(HttpURLConnection con) {
		String credentials = "dexterpratt:insecure";
		String basicAuth = "Basic "
				+ new String(new Base64().encode(credentials.getBytes()));
		con.setRequestProperty("Authorization", basicAuth);
	}
	


}