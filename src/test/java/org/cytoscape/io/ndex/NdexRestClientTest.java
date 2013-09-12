package org.cytoscape.io.ndex;

import static org.junit.Assert.*;

import java.util.Collection;

import javax.naming.spi.DirStateFactory.Result;

import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.io.ndex.internal.rest.NdexRestClient;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class NdexRestClientTest {

	private NdexRestClient client;
	private NetworkViewTestSupport support = new NetworkViewTestSupport();
	private final CyNetworkFactory networkFactory = support.getNetworkFactory();
	private final CyNetworkViewFactory networkViewFactory = support.getNetworkViewFactory();
	private final CyNetworkManager networkManager = support.getNetworkManager();
	private final CyRootNetworkManager rootNetworkManager = support.getRootNetworkFactory();
	
	
	@Before
	public void setUp() throws Exception {
		client = new NdexRestClient(networkFactory, networkViewFactory, networkManager, rootNetworkManager);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearch() throws Exception {
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

	}

	@Test
	public void testImportNetwork() throws Exception {
		//String url = "http://localhost:3333/networks/C11R0";
		
		//CyNetwork network = client.getNetwork("C11R0");
		//assertNotNull(network);
	}

	@Test
	public void testPostNetwork() throws Exception {
	}
	
	// TODO: Add test cases for each method in NdexRestClient class.
}
