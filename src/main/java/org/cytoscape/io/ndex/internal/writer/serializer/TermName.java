package org.cytoscape.io.ndex.internal.writer.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class TermName extends JdexTermObject {

	String namespaceId;
	String prefix;
	String unprefixedName;
	boolean isParameter;

	public TermName(String name, String id, String namespaceId, String prefix,
			String unprefixedName, boolean isParameter) {
		super(name, id);
		this.namespaceId = namespaceId;
		this.prefix = prefix;
		this.unprefixedName = unprefixedName;
		this.isParameter = isParameter;
	}

	/*
	 * public TermName(String name, String id, String namespaceId, boolean
	 * isParameter) { super(name, id); this.namespaceId = namespaceId;
	 * this.isParameter = isParameter; }
	 */

	@Override
	public void serializeSelf(JsonGenerator jgen) {
		// TODO Auto-generated method stub
		try {
			jgen.writeObjectFieldStart(getId());
			jgen.writeFieldName(JdexToken.TERM_NAME.getName());
			jgen.writeString(unprefixedName);

			if (this.namespaceId != null) {
				jgen.writeFieldName(JdexToken.TERM_NAMESPACE.getName());
				jgen.writeNumber(Integer.valueOf(namespaceId));
			}
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
