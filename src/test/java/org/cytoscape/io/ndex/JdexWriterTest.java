package org.cytoscape.io.ndex;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JdexWriterTest {
	
	private NetworkViewTestSupport support = new NetworkViewTestSupport();
	private TaskMonitor tm;

	private Map<Long, CyNode> suid2nodeMap;
	private Map<Long, CyEdge> suid2edgeMap;
	
	@Before
	public void setUp() throws Exception {
		this.tm = mock(TaskMonitor.class);
		suid2nodeMap = new HashMap<Long, CyNode>();
		suid2edgeMap = new HashMap<Long, CyEdge>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		CyNetwork network1 = support.getNetwork();

		network1 = support.getNetwork();
		CyNode n1 = network1.addNode();
		CyNode n2 = network1.addNode();
		CyNode n3 = network1.addNode();

		// Not connected
		CyNode n4 = network1.addNode();

		CyNode n5 = network1.addNode();

		suid2nodeMap.put(n1.getSUID(), n1);
		suid2nodeMap.put(n2.getSUID(), n2);
		suid2nodeMap.put(n3.getSUID(), n3);
		suid2nodeMap.put(n4.getSUID(), n4);
		suid2nodeMap.put(n5.getSUID(), n5);

		CyEdge e1 = network1.addEdge(n1, n2, true);
		CyEdge e2 = network1.addEdge(n2, n3, true);
		CyEdge e3 = network1.addEdge(n1, n3, true);
		CyEdge e1self = network1.addEdge(n1, n1, true);

		suid2edgeMap.put(e1.getSUID(), e1);
		suid2edgeMap.put(e2.getSUID(), e2);
		suid2edgeMap.put(e3.getSUID(), e3);
		suid2edgeMap.put(e1self.getSUID(), e1self);

		network1.getRow(n1).set(CyNetwork.NAME, "n1");
		network1.getRow(n2).set(CyNetwork.NAME, "n2: 日本語テスト");
		network1.getRow(n3).set(CyNetwork.NAME, "n3");
		network1.getRow(n4).set(CyNetwork.NAME, "n4: Alone");
		network1.getRow(n5).set(CyNetwork.NAME, "n5");

		network1.getRow(e1).set(CyNetwork.NAME, "e1");
		network1.getRow(e2).set(CyNetwork.NAME, "エッジ2");
		network1.getRow(e3).set(CyNetwork.NAME, "e3");
		network1.getRow(e1self).set(CyNetwork.NAME, "e1self");

	}

}
