package org.cytoscape.io.ndex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.io.ndex.internal.reader.NdexBundleReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JdexReaderTest {

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
	public void test() throws Exception {
		File cyjdex1 = new File(
				"./src/test/resources/testData/NCI_NATURE.FoxO family signaling.517135.jdex");

		InputStream is = new FileInputStream(cyjdex1);
		NdexBundleReader reader = new NdexBundleReader(is, viewFactory,
				networkFactory, networkManager, rootNetworkManager);
		reader.run(tm);
		final CyNetwork[] networks = reader.getNetworks();
		testLoadedNetwork(networks);
		is.close();
	}

	public void testLoadedNetwork(CyNetwork[] networks) {
		assertNotNull(networks);
		assertEquals(1, networks.length);
		CyNetwork network = networks[0];
		assertNotNull(network);
		// ノード51
		assertEquals(51, network.getNodeCount());
		// エッジ152
		assertEquals(152, network.getEdgeCount());

		CyTable nodeTable = network.getDefaultNodeTable();
		CyTable edgeTable = network.getDefaultEdgeTable();		
		// テーブルが正しく作られているか

		int nameIndex = nodeTable.getColumn(CyNetwork.NAME)
				.getValues(String.class).indexOf("539302");
		assertNotEquals(-1, nameIndex);

		assertNotNull(nodeTable.getColumn("readable name"));
		int readableIndex = nodeTable.getColumn("readable name")
				.getValues(String.class).indexOf("Ran/GTP");
		assertNotEquals(-1, readableIndex);

		//display the node table
		for(CyRow row : nodeTable.getAllRows()){
			final Map<String,Object> map = row.getAllValues();
			System.out.println(map.get(CyNetwork.SUID) + " " + map.get(CyNetwork.NAME) + " " + map.get("readable name"));
		}
		
		//display the edge table
		for(CyRow row : edgeTable.getAllRows()){
			final Map<String,Object> map = row.getAllValues();
			System.out.println(map.get(CyNetwork.SUID) + " " + map.get("readable name"));
		}


		// term56
		/* ここから先はまだ */
		// nameが"539302"のノードのreadable nameが"Ran/GTP"になってるか


	}
}
