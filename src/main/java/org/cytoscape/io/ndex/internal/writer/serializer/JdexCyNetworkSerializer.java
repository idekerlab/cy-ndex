package org.cytoscape.io.ndex.internal.writer.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.ArrayBuilders;

public class JdexCyNetworkSerializer extends JsonSerializer<CyNetwork> {

	@Override
	public void serialize(CyNetwork value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();

		// start from here
		jgen.writeStartObject();
		CyNetwork network = value;

		// TODO create id number variable
		int idNum = 0;

		// create create prefix to namespaceObject map
		Map<String, JdexNamescapeObject> namespaceMap = new HashMap<String, JdexNamescapeObject>();
		List<JdexNamescapeObject> namespaceList = new ArrayList<JdexNamescapeObject>();

		// create term list
		List<JdexTermObject> termList = new ArrayList<JdexTermObject>();
		// create term string to term id map
		Map<String, String> termMap = new HashMap<String, String>();
		// create CyNode to id map
		Map<CyNode, Integer> nodeMap = new HashMap<CyNode, Integer>();

		// create citation identifier to citationObject Map
		Map<String, JdexCitationObject> citationMap = new HashMap<String, JdexCitationObject>();
		List<JdexCitationObject> citationList = new ArrayList<JdexCitationObject>();

		// create support text to supportObject Map
		Map<String, JdexSupportObject> supportMap = new HashMap<String, JdexSupportObject>();
		List<JdexSupportObject> supportList = new ArrayList<JdexSupportObject>();

		// create termParser
		TermParser parser = new TermParser(termList, termMap, namespaceList,
				namespaceMap);

		//
		// serialize nodes
		//
		jgen.writeObjectFieldStart(JdexToken.NODES.getName());
		List<CyNode> nodes = network.getNodeList();
		for (CyNode node : nodes) {
			int nodeId = idNum;
			idNum++;
			jgen.writeObjectFieldStart(String.valueOf(nodeId));
			nodeMap.put(node, nodeId);

			// jgen.writeStartObject();
			// write name
			jgen.writeFieldName(JdexToken.NODE_NAME.getName());
			jgen.writeString(network.getRow(node).get(CyNetwork.NAME,
					String.class));
			// write represent
			jgen.writeFieldName(JdexToken.NODE_REPRESENT.getName());
			int termId = Integer.valueOf(parser.parse(network.getRow(node)
					.get(JdexToken.NODE_REPRESENT.getName(), String.class),idNum));
			jgen.writeNumber(termId);
			idNum = ++termId;
			jgen.writeEndObject();
		}
		jgen.writeEndObject();

		//
		// serialize Edges
		//
		jgen.writeObjectFieldStart(JdexToken.EDGES.getName());
		List<CyEdge> edges = network.getEdgeList();
		for (CyEdge edge : edges) {
			int edgeId = idNum;
			idNum++;
			jgen.writeObjectFieldStart(String.valueOf(edgeId));
			// write s
			jgen.writeFieldName(JdexToken.EDGE_SOURCE.getName());
			jgen.writeNumber(nodeMap.get(edge.getSource()));
			// write p
			jgen.writeFieldName(JdexToken.EDGE_PREDICATE.getName());
			int termId = Integer.valueOf(parser.parse(network.getRow(edge)
					.get(JdexToken.EDGE_PREDICATE.getName(), String.class),idNum));
			jgen.writeNumber(termId);
			idNum = ++termId;
			// write o
			jgen.writeFieldName(JdexToken.EDGE_TARGET.getName());
			jgen.writeNumber(nodeMap.get(edge.getTarget()));
			jgen.writeEndObject();

			if (network.getRow(edge).get(JdexToken.COLUMN_IDENTIFIER.getName(),
					String.class) != null) {
				final String identifier = network.getRow(edge).get(
						JdexToken.COLUMN_IDENTIFIER.getName(), String.class);
				int citationId;
				if (citationMap.containsKey(identifier)) {
					final JdexCitationObject object = citationMap
							.get(identifier);
					object.getEdges().add(edgeId);
					citationId = Integer.valueOf(object.getId());
				} else {
					citationId = idNum;
					idNum++;
					final String type = network.getRow(edge).get(
							JdexToken.COLUMN_TYPE.getName(), String.class);
					final String title = network.getRow(edge).get(
							JdexToken.COLUMN_TITLE.getName(), String.class);
					final List<Integer> edgeIdList = new ArrayList<Integer>();
					final List<String> contributors = network.getRow(edge)
							.getList(JdexToken.COLUMN_CONTRIBUTORS.getName(),
									String.class);
					JdexCitationObject citation = new JdexCitationObject(String.valueOf(citationId),
							identifier, type, title, edgeIdList, contributors);
					citation.getEdges().add(edgeId);
					citationList.add(citation);
					//citationId = citationList.lastIndexOf(citation);
					citationMap.put(identifier, citation);
				}

				final String text = network.getRow(edge).get(
						JdexToken.COLUMN_TEXT.getName(), String.class);
				int supportId;
				if (supportMap.containsKey(text)) {
					final JdexSupportObject object = supportMap.get(text);
					object.getEdges().add(edgeId);
					// supportId = supportList.indexOf(object);
				} else {
					supportId = idNum;
					idNum++;
					final List<Integer> edgeIdList = new ArrayList<Integer>();

					JdexSupportObject support = new JdexSupportObject(String.valueOf(supportId),text,
							citationId, edgeIdList);
					support.getEdges().add(edgeId);
					supportList.add(support);
					supportMap.put(text, support);

				}
			}
		}
		jgen.writeEndObject();

		//
		// TODO serialize namespaces
		//
		jgen.writeObjectFieldStart(JdexToken.NAMESPACES.getName());
		/*for (int i = 0; i < namespaceList.size(); i++) {
			jgen.writeObjectFieldStart(String.valueOf(i));
			namespaceList.get(i);
			// write uri
			jgen.writeFieldName(JdexToken.URI.getName());
			jgen.writeString("");
			// write prefix
			jgen.writeFieldName(JdexToken.PREFIX.getName());
			jgen.writeString(namespaceList.get(i));
			// write description
			jgen.writeFieldName(JdexToken.DESCRIPTION.getName());
			jgen.writeString("");
			jgen.writeEndObject();
		}*/
		for(JdexNamescapeObject object : namespaceList){
			jgen.writeObjectFieldStart(object.getId());
			
			jgen.writeFieldName(JdexToken.URI.getName());
			jgen.writeString(object.getUri());
			
			jgen.writeFieldName(JdexToken.PREFIX.getName());
			jgen.writeString(object.getPrefix());
			
			jgen.writeFieldName(JdexToken.DESCRIPTION.getName());
			jgen.writeString(object.getDescription());
			
			jgen.writeEndObject();
		}
		jgen.writeEndObject();

		//
		// serialize terms
		//
		jgen.writeObjectFieldStart(JdexToken.TERMS.getName());
		for (JdexTermObject term : termList) {
			// write name
			// write ns
			// ---
			// write termFunction
			// write parameters
			try {
				term.serializeSelf(jgen);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		jgen.writeEndObject();

		jgen.writeObjectFieldStart(JdexToken.NODETYPES.getName());
		jgen.writeEndObject();

		jgen.writeObjectFieldStart(JdexToken.PROPERTIES.getName());
		jgen.writeEndObject();

		// write citations
		jgen.writeObjectFieldStart(JdexToken.CITATIONS.getName());
		//for (int i = 0; i < citationList.size(); i++) {
		for(JdexCitationObject object: citationList){
			//final JdexCitationObject object = citationList.get(i);
			jgen.writeObjectFieldStart(object.getId());
			// write identifier
			jgen.writeFieldName(JdexToken.IDENTIFIER.getName());
			jgen.writeString(object.getIdentifier());
			// write type
			jgen.writeFieldName(JdexToken.TYPE.getName());
			jgen.writeString(object.getType());
			// write title
			jgen.writeFieldName(JdexToken.TITLE.getName());
			jgen.writeString(object.getTitle());
			// write edges
			jgen.writeArrayFieldStart(JdexToken.EDGES.getName());
			for (int edgeId : object.getEdges()) {
				jgen.writeNumber(edgeId);
			}
			jgen.writeEndArray();

			// write contributors
			jgen.writeArrayFieldStart(JdexToken.CONTRIBUTORS.getName());
			for (String c : object.getContributors()) {
				jgen.writeString(c);
			}
			jgen.writeEndArray();

			jgen.writeEndObject();
		}
		jgen.writeEndObject();

		// write supports
		jgen.writeObjectFieldStart(JdexToken.SUPPORTS.getName());
		//for (int i = 0; i < supportList.size(); i++) {
		//	final JdexSupportObject object = supportList.get(i);
		for(JdexSupportObject object:supportList){
			jgen.writeObjectFieldStart(object.getId());
			// write text
			jgen.writeFieldName(JdexToken.TEXT.getName());
			jgen.writeString(object.getText());
			// write citation
			jgen.writeFieldName(JdexToken.CITATION.getName());
			jgen.writeNumber(object.getCitation());
			// write edges
			jgen.writeArrayFieldStart(JdexToken.EDGES.getName());
			for (int edgeId : object.getEdges()) {
				jgen.writeNumber(edgeId);
			}
			jgen.writeEndArray();

			jgen.writeEndObject();
		}
		jgen.writeEndObject();

		// end
		jgen.writeEndObject();

	}

	@Override
	public Class<CyNetwork> handledType() {
		return CyNetwork.class;
	}

}
