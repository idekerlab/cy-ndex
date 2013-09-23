package org.cytoscape.io.ndex;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.cytoscape.io.ndex.internal.writer.serializer.JdexNamescapeObject;
import org.cytoscape.io.ndex.internal.writer.serializer.JdexTermObject;
import org.cytoscape.io.ndex.internal.writer.serializer.TermParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TermParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		List<JdexTermObject> termList = new ArrayList<JdexTermObject>();
		Map<String,String> termMap = new HashMap<String,String>();
		
		Map<String,JdexNamescapeObject> namespaceMap = new HashMap<String, JdexNamescapeObject>();
		List<JdexNamescapeObject> namespaceList = new ArrayList<JdexNamescapeObject>();
		
		
		String test = "bel:peptidaseActivity(bel:complexAbundance(bel:proteinAbundance(HGNC:F3), bel:proteinAbundance(HGNC:F7)))";
		System.out.println(test);
		
		TermParser parser = new TermParser(termList,termMap,namespaceList,namespaceMap);
		parser.parse(test,0);
		
		assertEquals(9,termList.size());
		
	}

}
