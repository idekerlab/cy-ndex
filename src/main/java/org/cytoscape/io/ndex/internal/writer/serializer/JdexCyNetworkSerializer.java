package org.cytoscape.io.ndex.internal.writer.serializer;

import java.io.IOException;

import org.cytoscape.model.CyNetwork;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JdexCyNetworkSerializer extends JsonSerializer<CyNetwork> {

	@Override
	public void serialize(CyNetwork value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.useDefaultPrettyPrinter();

		//start from here
		jgen.writeStartObject();
		
		//TODO create term list
		//TODO create CyNode to id map
		//TODO create create prefix to namespace map

		
		jgen.writeObjectFieldStart(JdexToken.NODES.getName());
		jgen.writeEndObject();
		
		jgen.writeObjectFieldStart(JdexToken.EDGES.getName());
		jgen.writeEndObject();
		
		jgen.writeObjectFieldStart(JdexToken.NAMESPACES.getName());
		jgen.writeEndObject();
		
		jgen.writeObjectFieldStart(JdexToken.TERMS.getName());
		jgen.writeEndObject();

		/*
		jgen.writeObjectFieldStart(JdexToken.NODETYPES.getName());
		jgen.writeEndObject();
		*/
		/*
		jgen.writeObjectFieldStart(JdexToken.PROPERTIES.getName());
		jgen.writeEndObject();
		 */

		//TODO write citaions
		jgen.writeObjectFieldStart(JdexToken.CITATIONS.getName());
		jgen.writeEndObject();
		
		//TODO write supports
		jgen.writeObjectFieldStart(JdexToken.SUPPORTS.getName());
		jgen.writeEndObject();

		
		
		//end
		jgen.writeEndObject();
		
	}

	@Override
	public Class<CyNetwork> handledType() {
		return CyNetwork.class;
	}
}
