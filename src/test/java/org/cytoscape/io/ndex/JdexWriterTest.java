package org.cytoscape.io.ndex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cytoscape.ding.NetworkViewTestSupport;
import org.cytoscape.io.ndex.internal.writer.NdexBundleWriter;
import org.cytoscape.io.ndex.internal.writer.serializer.JdexModule;
import org.cytoscape.io.ndex.internal.writer.serializer.JdexToken;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public void test() throws Exception {

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
		// CyEdge e1self = network1.addEdge(n1, n1, true);

		suid2edgeMap.put(e1.getSUID(), e1);
		suid2edgeMap.put(e2.getSUID(), e2);
		suid2edgeMap.put(e3.getSUID(), e3);
		// suid2edgeMap.put(e1self.getSUID(), e1self);

		// 1
		// "76": {
		// "name": "bel:pathology(MESHD:Atherosclerosis)",
		// "represents": 75
		// },

		// 2
		// "207": {
		// "name":
		// "bel:peptidaseActivity(bel:complexAbundance(bel:proteinAbundance(HGNC:F3), bel:proteinAbundance(HGNC:F7)))",
		// "represents": 206
		// },

		// 3
		// "257": {
		// "name":
		// "bel:proteinAbundance(HGNC:AKT1, bel:proteinModification(P, T, 308))",
		// "represents": 256
		// },
		network1.getDefaultNodeTable().createColumn(
				JdexToken.NODE_REPRESENT.getName(), String.class, true);

		network1.getRow(n1).set(CyNetwork.NAME,
				"bel:pathology(MESHD:Atherosclerosis)");
		network1.getRow(n1).set(JdexToken.NODE_REPRESENT.getName(),
				"bel:pathology(MESHD:Atherosclerosis)");

		network1.getRow(n2)
				.set(CyNetwork.NAME,
						"bel:peptidaseActivity(bel:complexAbundance(bel:proteinAbundance(HGNC:F3), bel:proteinAbundance(HGNC:F7)))");
		network1.getRow(n2)
				.set(JdexToken.NODE_REPRESENT.getName(),
						"bel:peptidaseActivity(bel:complexAbundance(bel:proteinAbundance(HGNC:F3), bel:proteinAbundance(HGNC:F7)))");

		network1.getRow(n3)
				.set(CyNetwork.NAME,
						"bel:proteinAbundance(HGNC:AKT1, bel:proteinModification(P, T, 308))");
		network1.getRow(n3)
				.set(JdexToken.NODE_REPRESENT.getName(),
						"bel:proteinAbundance(HGNC:AKT1, bel:proteinModification(P, T, 308))");

		network1.getRow(n4).set(CyNetwork.NAME, "n4 Alone");
		network1.getRow(n4).set(JdexToken.NODE_REPRESENT.getName(), "n4 Alone");

		network1.getRow(n5).set(CyNetwork.NAME, "n5");
		network1.getRow(n5).set(JdexToken.NODE_REPRESENT.getName(), "n5");

		network1.getDefaultEdgeTable().createColumn(
				JdexToken.EDGE_PREDICATE.getName(), String.class, true);
		network1.getRow(e1).set(JdexToken.EDGE_PREDICATE.getName(),
				"bel:increases");
		network1.getRow(e2).set(JdexToken.EDGE_PREDICATE.getName(),
				"bel:positiveCorrelation");
		network1.getRow(e3).set(JdexToken.EDGE_PREDICATE.getName(),
				"bel:directlyIncreases");
		// network1.getRow(e1self).set(CyNetwork.NAME, "e1self");

		network1.getDefaultEdgeTable().createColumn(
				JdexToken.COLUMN_IDENTIFIER.getName(), String.class, true);
		network1.getDefaultEdgeTable().createColumn(
				JdexToken.COLUMN_TYPE.getName(), String.class, true);
		network1.getDefaultEdgeTable().createColumn(
				JdexToken.COLUMN_TITLE.getName(), String.class, true);
		network1.getDefaultEdgeTable().createListColumn(
				JdexToken.COLUMN_CONTRIBUTORS.getName(), String.class, true);

		network1.getDefaultEdgeTable().createColumn(
				JdexToken.COLUMN_TEXT.getName(), String.class, true);

		network1.getRow(e1).set(JdexToken.COLUMN_IDENTIFIER.getName(),
				"12928037");
		network1.getRow(e1).set(JdexToken.COLUMN_TYPE.getName(), "PubMed");
		network1.getRow(e1).set(JdexToken.COLUMN_TITLE.getName(),
				"Trends in molecular medicine");

		/*
		 * "de Nigris F", "Lerman A", "Ignarro LJ", "Williams-Ignarro S",
		 * "Sica V", "Baker AH", "Lerman LO", "Geng YJ", "Napoli C"
		 */
		network1.getRow(e1).set(
				JdexToken.COLUMN_CONTRIBUTORS.getName(),
				new ArrayList<String>(Arrays.asList("de Nigris F", "Lerman A",
						"Ignarro LJ", "Williams-Ignarro S", "Sica V",
						"Baker AH", "Lerman LO", "Geng YJ", "Napoli C")));
		network1.getRow(e1)
				.set(JdexToken.COLUMN_TEXT.getName(),
						"Furthermore, the kinase activity of Raf is inhibited by its interactions with cholesterol-rich lipid rafts in the cell membrane and phosphorylation by protein kinases A (PKA) and B (PKB/Akt), as shown in Figure 2.95-98");

		network1.getRow(e2).set(JdexToken.COLUMN_IDENTIFIER.getName(),
				"12928037");
		network1.getRow(e2).set(JdexToken.COLUMN_TYPE.getName(), "PubMed");
		network1.getRow(e2).set(JdexToken.COLUMN_TITLE.getName(),
				"Trends in molecular medicine");

		/*
		 * "de Nigris F", "Lerman A", "Ignarro LJ", "Williams-Ignarro S",
		 * "Sica V", "Baker AH", "Lerman LO", "Geng YJ", "Napoli C"
		 */
		network1.getRow(e2).set(
				JdexToken.COLUMN_CONTRIBUTORS.getName(),
				new ArrayList<String>(Arrays.asList("de Nigris F", "Lerman A",
						"Ignarro LJ", "Williams-Ignarro S", "Sica V",
						"Baker AH", "Lerman LO", "Geng YJ", "Napoli C")));
		network1.getRow(e2)
				.set(JdexToken.COLUMN_TEXT.getName(),
						"The mammalian raf family consists of the following three genes: A-raf, B-raf, and C-raf, which are located on chromosomes Xp11, 7q32, and 3p25, respectively. ");

		network1.getRow(e3).set(JdexToken.COLUMN_IDENTIFIER.getName(),
				"19151761");
		network1.getRow(e3).set(JdexToken.COLUMN_TYPE.getName(), "PubMed");
		network1.getRow(e3).set(JdexToken.COLUMN_TITLE.getName(),
				"Oncogene");

		/*
		 * "de Nigris F", "Lerman A", "Ignarro LJ", "Williams-Ignarro S",
		 * "Sica V", "Baker AH", "Lerman LO", "Geng YJ", "Napoli C"
		 */
		network1.getRow(e3).set(
				JdexToken.COLUMN_CONTRIBUTORS.getName(),
				new ArrayList<String>(Arrays.asList("de Nigris F", "Lerman A",
						"Ignarro LJ", "Williams-Ignarro S", "Sica V",
						"Baker AH", "Lerman LO", "Geng YJ", "Napoli C")));
		network1.getRow(e3)
		.set(JdexToken.COLUMN_TEXT.getName(),
				"With regard to differences in signaling between the Raf isoforms, A-Raf is a weaker activator of MEK than B-Raf or C-Raf. Furthermore, A-Raf can activate MEK1 only, whereas C-Raf activates both MEK1 and MEK2.123-125  ");

		
		final ObjectMapper jsMapper = new ObjectMapper();
		jsMapper.registerModule(new JdexModule());

		File temp = File.createTempFile("network1", ".jdex");
		temp.deleteOnExit();

		OutputStream os = new FileOutputStream(temp);
		NdexBundleWriter writer = new NdexBundleWriter(os, network1, jsMapper);
		writer.run(tm);

		testCytoscapeJdexFileContent(temp, network1);

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

		final FileInputStream fileInputStream = new FileInputStream(temp);

		// TODO: Find better way to test JSON file in JAVA

		final ObjectMapper mapper = new ObjectMapper();
		final JsonNode rootNode = mapper.readTree(fileInputStream);

		assertNotNull(rootNode);

		final JsonNode nodes = rootNode.get(JdexToken.NODES.getName());
		assertNotNull(nodes);
		assertEquals(5, nodes.size());

		int hasNameField = 0;
		int hasRepresentField = 0;

		int foundNameNum = 0;

		for (Iterator<JsonNode> jnodes = nodes.elements(); jnodes.hasNext();) {
			JsonNode node = jnodes.next();
			if (node.has(JdexToken.NODE_NAME.getName())) {
				++hasNameField;
				if (node.get(JdexToken.NODE_NAME.getName()).asText()
						.equals("bel:pathology(MESHD:Atherosclerosis)"))
					++foundNameNum;
			}
			if (node.has(JdexToken.NODE_REPRESENT.getName())) {
				++hasRepresentField;
			}
		}
		assertEquals(5, hasNameField);
		assertEquals(1, foundNameNum);

		assertEquals(5, hasRepresentField);
		// assertEquals(1, foundRepresentNum);

		/*
		 * final JsonNode elements = rootNode.get("elements");
		 * assertNotNull(elements); assertTrue(elements.isObject());
		 * 
		 * Iterator<String> itr = elements.fieldNames();
		 * 
		 * final List<String> nodesAndEdgesList = new ArrayList<String>(); while
		 * (itr.hasNext()) { String val = itr.next();
		 * nodesAndEdgesList.add(val); System.out.println("Field name: " + val);
		 * 
		 * } assertEquals(2, nodesAndEdgesList.size());
		 * assertTrue(nodesAndEdgesList.contains("nodes"));
		 * assertTrue(nodesAndEdgesList.contains("edges"));
		 * 
		 * JsonNode nodes = elements.get("nodes"); JsonNode edges =
		 * elements.get("edges");
		 * 
		 * assertTrue(nodes.isArray()); assertTrue(edges.isArray());
		 * 
		 * assertEquals(5, nodes.size()); assertEquals(4, edges.size());
		 * 
		 * for (JsonNode node : nodes) { JsonNode data = node.get("data");
		 * System.out.println("Node Data = " + data.toString());
		 * 
		 * final String nodeName = data.get("name").asText();
		 * System.out.println("Node Name = " + nodeName);
		 * 
		 * assertEquals(nodeName,
		 * network.getRow(suid2nodeMap.get(data.get("SUID"
		 * ).asLong())).get(CyNetwork.NAME, String.class));
		 * assertNotNull(node.get("position")); }
		 */

		fileInputStream.close();
	}

}
