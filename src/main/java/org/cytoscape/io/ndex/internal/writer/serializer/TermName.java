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

	@Override
	public void serializeSelfAsParameter(JsonGenerator jgen, int paramNum)
			throws Exception {

		if (namespaceId!=null) {
			jgen.writeObjectFieldStart(String.valueOf(paramNum));
			jgen.writeFieldName(JdexToken.TERM.getName());
			jgen.writeNumber(Integer.valueOf(getId()));
			jgen.writeEndObject();
		} else {
			jgen.writeFieldName(String.valueOf(paramNum));
			jgen.writeString(getName());
		}
	}

	public String getNamespaceId() {
		return namespaceId;
	}

	public void setNamespaceId(String namespaceId) {
		this.namespaceId = namespaceId;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getUnprefixedName() {
		return unprefixedName;
	}

	public void setUnprefixedName(String unprefixedName) {
		this.unprefixedName = unprefixedName;
	}

	public boolean isParameter() {
		return isParameter;
	}

	public void setParameter(boolean isParameter) {
		this.isParameter = isParameter;
	}
	
}
