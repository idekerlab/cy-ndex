package org.cytoscape.io.ndex.internal.writer.serializer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JdexModule extends SimpleModule {

	public JdexModule() {
		super("JdexModule", new Version(1, 0, 0, null, null, null));
		addSerializer(new JdexCyNetworkSerializer());
	}
}
