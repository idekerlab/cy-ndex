package org.cytoscape.io.ndex.internal.writer.serializer;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

public class TermFunction extends JdexTermObject {

	JdexTermObject functionName;
	List<JdexTermObject> parameters;

	public TermFunction(String name, String id, JdexTermObject functionName,
			List<JdexTermObject> parameters) {
		super(name, id);
		this.functionName = functionName;
		this.parameters = parameters;
	}

	@Override
	public void serializeSelf(JsonGenerator jgen) throws Exception {
		// TODO Auto-generated method stub
		// super.serializeSelf(jgen);
		try {
			jgen.writeObjectFieldStart(getId());
			jgen.writeFieldName(JdexToken.TERM_FUNCTION.getName());
			jgen.writeNumber(Integer.valueOf(functionName.getId()));

			jgen.writeObjectFieldStart(JdexToken.TERM_PARAMETERS.getName());
			for (int i = 0; i < parameters.size(); i++) {
				/*jgen.writeObjectFieldStart(String.valueOf(i));
				jgen.writeFieldName(JdexToken.TERM.getName());
				jgen.writeNumber(Integer.valueOf(parameters.get(i).getId()));
				jgen.writeEndObject();*/
				parameters.get(i).serializeSelfAsParameter(jgen, i);
			}
			jgen.writeEndObject();
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
		// TODO Auto-generated method stub
		jgen.writeObjectFieldStart(String.valueOf(paramNum));
		jgen.writeFieldName(JdexToken.TERM.getName());
		jgen.writeNumber(Integer.valueOf(getId()));
		jgen.writeEndObject();
	}

}
