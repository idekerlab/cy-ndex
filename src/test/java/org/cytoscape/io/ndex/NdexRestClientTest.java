package org.cytoscape.io.ndex;

import static org.junit.Assert.*;

import java.util.Collection;

import javax.naming.spi.DirStateFactory.Result;

import org.cytoscape.io.ndex.internal.rest.NdexRestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NdexRestClientTest {

	private NdexRestClient client;
	
	
	@Before
	public void setUp() throws Exception {
		client = new NdexRestClient();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearch() throws Exception {
		Collection<String> result = client.findNetworks("dsadsa");
		//assertNotNull(result);
		//assertEquals("network1", result);
		
	}

	@Test
	public void testImportNetwork() throws Exception {
	}

	@Test
	public void testPostNetwork() throws Exception {
	}
	
	// TODO: Add test cases for each method in NdexRestClient class.
}
