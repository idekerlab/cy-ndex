package org.cytoscape.io.ndex.internal;

import org.cytoscape.io.webservice.swing.AbstractWebServiceGUIClient;
import org.cytoscape.work.TaskIterator;

public class NdexWebServiceClient extends AbstractWebServiceGUIClient {

	public NdexWebServiceClient(String uri, String displayName, String description) {
		super(uri, displayName, description);
	}

	@Override
	public TaskIterator createTaskIterator(Object query) {
		return null;
	}

}
