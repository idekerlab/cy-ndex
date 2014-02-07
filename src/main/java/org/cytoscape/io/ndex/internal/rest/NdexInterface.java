package org.cytoscape.io.ndex.internal.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.cytoscape.io.ndex.internal.reader.NdexBundleReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.ndexbio.rest.NdexRestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Simple REST Client for NDEX web service.
 * 
 * TODO: support user authentication!
 * 
 */
public class NdexInterface {

	CyNetworkFactory factory;
	CyNetworkViewFactory viewFactory;
	CyNetworkManager networkManager;
	CyRootNetworkManager rootNetworkManager;
	TaskManager tm;
	TaskMonitor monitor;
	NdexRestClient client;

	// for authorization
	String userId = null;
	String password = null;

	public NdexInterface(CyNetworkFactory factory,
			CyNetworkViewFactory viewFactory, CyNetworkManager networkManager,
			CyRootNetworkManager rootNetworkManager, TaskMonitor monitor) {
		this.factory = factory;
		this.viewFactory = viewFactory;
		this.networkManager = networkManager;
		this.rootNetworkManager = rootNetworkManager;
		this.monitor = monitor;
		this.client = new NdexRestClient("dexterpratt", "insecure");

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
	public Collection<String> findNetworks(final String searchString, String searchType, Integer maxNetworks)
			throws JsonProcessingException, IOException {
		
		String route = "/networks/search/" + searchType; 
		//String searchString = "[" + property + "]" + operator + "\"" + value + "\"";
		ObjectMapper mapper = new ObjectMapper();
		JsonNode searchParameters = mapper.createObjectNode(); // will be of type ObjectNode
		((ObjectNode) searchParameters).put("searchString", searchString);
		((ObjectNode) searchParameters).put("top", maxNetworks.toString());
		((ObjectNode) searchParameters).put("skip", "0");

		JsonNode response = client.post(route, searchParameters);
		Iterator<JsonNode> networks = response.elements();
		ArrayList<String> result = new ArrayList<String>();	
		while (networks.hasNext()){
			JsonNode network = networks.next();
			String resultItem = network.path("jid").asText() + "," + network.path("title").asText();
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
	 * @throws Exception
	 */
	public CyNetwork getNetwork(final String ndexNetworkId) throws Exception {
		CyNetwork[] networks;
		CyNetwork network;
		try {
			JsonNode networkInfo = this.client.get("/networks/" + ndexNetworkId, "");
			NdexBundleReader reader = new NdexBundleReader(networkInfo, viewFactory,
					factory, networkManager, rootNetworkManager);
			reader.run(monitor);
			networks = reader.getNetworks();
			network = networks[0];
			//System.out.println("node count is" + network.getNodeCount());
		} catch (IOException e) {
			// TODO determine what to return when an error is responded
			// TODO determine output when error is occurred
			network = null;
		}

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
		// String credentials = "dexterpratt:insecure";
		String credentials = userId + ":" + password;
		String basicAuth = "Basic "
				+ new String(new Base64().encode(credentials.getBytes()));
		con.setRequestProperty("Authorization", basicAuth);
	}

	public void setCredential(String id, String password) {
		this.userId = id;
		this.password = password;
	}

}