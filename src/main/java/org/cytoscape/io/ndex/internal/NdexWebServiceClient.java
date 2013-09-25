package org.cytoscape.io.ndex.internal;

import java.awt.Container;

import org.cytoscape.io.ndex.internal.rest.NdexRestClient;
import org.cytoscape.io.ndex.internal.ui.NdexSearchPanel;
import org.cytoscape.io.webservice.NetworkImportWebServiceClient;
import org.cytoscape.io.webservice.SearchWebServiceClient;
import org.cytoscape.io.webservice.swing.AbstractWebServiceGUIClient;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

public class NdexWebServiceClient extends AbstractWebServiceGUIClient implements
		NetworkImportWebServiceClient, SearchWebServiceClient {

	private NdexRestClient client;

	public NdexWebServiceClient(String uri, String displayName,
			String description, CyNetworkFactory factory,
			CyNetworkViewFactory viewFactory, CyNetworkManager networkManager,
			CyRootNetworkManager rootNetworkManager, TaskMonitor tm,NdexSearchPanel panel) {
		super(uri, displayName, description);

		this.client = new NdexRestClient(factory, viewFactory, networkManager,
				rootNetworkManager, tm);
		
		this.gui = panel;

	}

	@Override
	public TaskIterator createTaskIterator(Object query) {
		return null;
	}

	@Override
	public Container getQueryBuilderGUI() {
		// TODO Auto-generated method stub
		return gui;
	}

	public NdexRestClient getClient() {
		return client;
	}

}
