package org.cytoscape.io.ndex.internal.writer.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class TermName extends JdexTermObject {

	String namespaceId;
	boolean isParameter;

	public TermName(String name, String id, String namespaceId,
			boolean isParameter) {
		super(name, id);
		this.namespaceId = namespaceId;
		this.isParameter = isParameter;
	}

	@Override
	public void serializeSelf(JsonGenerator jgen) {
		// TODO Auto-generated method stub
		try {
			jgen.writeObjectFieldStart(getId());
				jgen.writeFieldName(JdexToken.TERM_NAME.getName());
				jgen.writeString(getName());
				
				jgen.writeFieldName(JdexToken.TERM_NAMESPACE.getName());
				jgen.writeString("test");

			jgen.writeEndObject();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
