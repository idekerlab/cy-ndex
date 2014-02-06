package org.cytoscape.io.ndex;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.io.ndex.internal.reader.NdexBundleReader;
import org.cytoscape.io.ndex.internal.writer.NdexBundleWriter;
import org.cytoscape.io.ndex.internal.writer.serializer.JdexModule;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JdexReadWriteTest {

	private NetworkViewTestSupport support = new NetworkViewTestSupport();
	private final CyNetworkFactory networkFactory = support.getNetworkFactory();
	private final CyNetworkViewFactory viewFactory = support
			.getNetworkViewFactory();
	private final CyNetworkManager networkManager = support.getNetworkManager();
	private final CyRootNetworkManager rootNetworkManager = support
			.getRootNetworkFactory();

	private TaskMonitor tm;

	@Before
	public void setUp() throws Exception {
		this.tm = mock(TaskMonitor.class);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSmallNetwork() throws Exception {
		File cyjdex1 = new File(
				//"./src/test/resources/testData/NCI_NATURE.FoxO family signaling.517135.jdex");
				"./src/test/resources/testData/small_corpus.jdex");
				
		InputStream is = new FileInputStream(cyjdex1);
		NdexBundleReader reader = new NdexBundleReader(is, viewFactory,
				networkFactory, networkManager, rootNetworkManager);
		reader.run(tm);
		final CyNetwork[] networks = reader.getNetworks();

		is.close();

		final ObjectMapper jsMapper = new ObjectMapper();
		jsMapper.registerModule(new JdexModule());

		//File temp = File.createTempFile("network1", ".jdex");
		//temp.deleteOnExit();
		File temp = new File("./output_test.jdex");
		

		OutputStream os = new FileOutputStream(temp);
		NdexBundleWriter writer = new NdexBundleWriter(os, networks[0],
				jsMapper);
		writer.run(tm);

		testCytoscapeJdexFileContent(temp, networks[0]);
	}

	private void testCytoscapeJdexFileContent(File temp, CyNetwork network)
			throws Exception {

		// Read contents
		System.out.println("Temp = " + temp.getAbsolutePath());

		// write jdex contents//////////////////////////

		final FileInputStream testInputStream = new FileInputStream(temp);
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(testInputStream), 1);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);
		}
		testInputStream.close();
		bufferedReader.close();

		// /////////////////////////////////////////////
	}
}