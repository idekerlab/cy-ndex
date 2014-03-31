package org.cytoscape.io.ndex.internal;

import org.cytoscape.io.ndex.internal.ui.NdexSearchPanel;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;
import org.cytoscape.service.util.AbstractCyActivator;

import java.util.Properties;



public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {

		CyNetworkManager cyNetworkManagerServiceRef = getService(bc,CyNetworkManager.class);
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc,CyNetworkNaming.class);
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc,CyNetworkFactory.class);
		DialogTaskManager taskManager = getService(bc, DialogTaskManager.class);
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,
				CyNetworkViewFactory.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc, CyNetworkViewManager.class);
		CyRootNetworkManager cyRootNetworkManagerServiceRef = getService(bc,
				CyRootNetworkManager.class);

		// TODO create search and import Panel
		 NdexSearchPanel panel = new NdexSearchPanel(taskManager, cyNetworkManagerServiceRef, cyNetworkViewFactoryServiceRef, cyNetworkViewManagerServiceRef);
		
		// TODO create a web client
		String uri = "http://localhost/";
		String displayName = "NDEx client";
		String description = "this is NDEx client";
		NdexWebServiceClient ndexWebServiceClient = new NdexWebServiceClient(
				uri, displayName, description, cyNetworkFactoryServiceRef,
				cyNetworkViewFactoryServiceRef, cyNetworkManagerServiceRef,
				cyRootNetworkManagerServiceRef, null,panel);
		


		panel.setNdexClient(ndexWebServiceClient);
		registerAllServices(bc, ndexWebServiceClient, new Properties());
		
		/*
		CreateNetworkTaskFactory createNetworkTaskFactory = new CreateNetworkTaskFactory(cyNetworkManagerServiceRef,cyNetworkNamingServiceRef,cyNetworkFactoryServiceRef);
				
		Properties sample05TaskFactoryProps = new Properties();
		sample05TaskFactoryProps.setProperty("preferredMenu","Apps.Samples");
		sample05TaskFactoryProps.setProperty("title","NDEx 2 Create Network");
		registerService(bc,createNetworkTaskFactory,TaskFactory.class, sample05TaskFactoryProps);
		*/
	}
}

