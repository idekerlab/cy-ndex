package org.cytoscape.io.ndex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.io.ndex.internal.rest.NdexRestClient;
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

	private NdexRestClient client;
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
		tm = mock(TaskMonitor.class);
		client = new NdexRestClient(networkFactory, networkViewFactory, networkManager, rootNetworkManager,tm);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearch() throws Exception {
		//search response has not been mocked
		/*
		Collection<String> result = client.findNetworks("A");
		assertNotNull(result);
		assertEquals(4, result.size());
		
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
		String credentials = "dexterpratt:insecure";
		String basicAuth = "Basic "
				+ new String(new Base64().encode(credentials.getBytes()));
	    stubFor(get(urlEqualTo("/networks/C11R0"))
	            .withHeader("Authorization", equalTo(basicAuth))
	            .willReturn(aResponse()
	                .withStatus(200)
	                .withHeader("Content-Type", "application/json")
	                .withBodyFile("testData/NCI_NATURE.FoxO family signaling.517135.jdex")));
	               
				
		CyNetwork network = client.getNetwork("C11R0");
		assertNotNull(network);
		
		// ノード51
		assertEquals(51, network.getNodeCount());
		// エッジ152
		assertEquals(152, network.getEdgeCount());
	}

	@Test
	public void testPostNetwork() throws Exception {
	}
	
	// TODO: Add test cases for each method in NdexRestClient class.
}
