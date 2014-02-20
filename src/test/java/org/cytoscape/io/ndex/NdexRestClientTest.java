package org.cytoscape.io.ndex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.List;

import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.io.ndex.internal.rest.NdexInterface;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;


public class NdexRestClientTest {

	private NdexInterface client;
	private NetworkViewTestSupport support = new NetworkViewTestSupport();
	private final CyNetworkFactory networkFactory = support.getNetworkFactory();
	private final CyNetworkViewFactory networkViewFactory = support.getNetworkViewFactory();
	private final CyNetworkManager networkManager = support.getNetworkManager();
	private final CyRootNetworkManager rootNetworkManager = support.getRootNetworkFactory();

	private TaskMonitor tm;
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(3333); // No-args constructor defaults to port 8080
	
	@Before
	public void setUp() throws Exception {
		//tm = mock(TaskMonitor.class);
		client = new NdexInterface(networkFactory, networkViewFactory, networkManager, rootNetworkManager,tm);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearch() throws Exception {
		//search response is not mocked
		
		Collection<String> result = client.findNetworks("AKT", "starts-with", 10);
		assertNotNull(result);
		//assertEquals(4, result.size());
		
		/*
		result = client.findNetworks("A");
		assertNotNull(result);
		assertEquals(4, result.size());
		
		//assertEquals("network1", result);
		result = client.findNetworks("NCI");
		assertNotNull(result);
		assertEquals(1, result.size());
		
		result = client.findNetworks("AA");
		assertNotNull(result);
		assertEquals(0, result.size());
		*/

	}

	@Test
	public void testImportNetwork() throws Exception {
		
		//prepare mock server
		/*
		String credentials = "dexterpratt:insecure";
		String basicAuth = "Basic "
				+ new String(new Base64().encode(credentials.getBytes()));
	    stubFor(get(urlEqualTo("/networks/C11R0"))
	            .withHeader("Authorization", equalTo(basicAuth))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBodyFile("testData/NCI_NATURE.FoxO family signaling.517135.jdex")));
	    
	    stubFor(get(urlEqualTo("/networks/C11R0"))
	            .withHeader("Authorization", notMatching(basicAuth))
	            .willReturn(aResponse()
	                .withStatus(401)
	                //.withHeader("Content-Type", "application/json")
	                .withBody("Unauthorized")));     
		*/	
		List<String> networkSearchResult = client.findNetworks("AKT phosphorylates targets in the nucleus", "exact-match", 1);
		assert(networkSearchResult.size() == 1);
		String networkString = networkSearchResult.get(0);
		String networkId = networkString.substring(0, networkString.indexOf(","));
		int skip = 0;
		int top = 20;
	    client.setCredential(null, null);
		CyNetwork network = client.getNetworkByEdges(networkId, skip, top);
		//assertEquals(network,null);
		
		client.setCredential("dexterpratt", "insecure");
		network = client.getNetworkByEdges(networkId, skip, top);
		assertNotNull(network);
		
		// ノード51
		assertEquals(6, network.getNodeCount());
		// エッジ152
		assertEquals(12, network.getEdgeCount());
	}

	/*
	@Test
	public void testCreateNetwork() throws Exception {
	}

	// TODO: Add test cases for each method in NdexRestClient class.
	 */
	 
}
