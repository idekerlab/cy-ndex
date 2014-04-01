package org.cytoscape.io.ndex.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.io.ndex.internal.ui.NdexSearchPanel;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.ServiceProperties;
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
		
		CyNetworkNaming cyNetworkNamingServiceRef = getService(bc, CyNetworkNaming.class);
		CyNetworkFactory cyNetworkFactoryServiceRef = getService(bc, CyNetworkFactory.class);
		
		CySwingApplication cySwingApplicationServiceRef = getService(bc, CySwingApplication.class);
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc, CyApplicationManager.class);
		CyNetworkManager cyNetworkManagerServiceRef = getService(bc, CyNetworkManager.class);
		CyNetworkViewManager cyNetworkViewManagerServiceRef = getService(bc, CyNetworkViewManager.class);
		DialogTaskManager taskManager = getService(bc, DialogTaskManager.class);
		CyNetworkViewFactory cyNetworkViewFactoryServiceRef = getService(bc,
				CyNetworkViewFactory.class);
		CyRootNetworkManager cyRootNetworkManagerServiceRef = getService(bc,
				CyRootNetworkManager.class);

		// search and import Panel
		 NdexSearchPanel panel = new NdexSearchPanel(
				 taskManager, 
				 cyNetworkManagerServiceRef, 
				 cyNetworkViewFactoryServiceRef, 
				 cyNetworkViewManagerServiceRef);
		
		// web client
		//NdexInterface.INSTANCE = 
		String uri = "http://localhost/";
		String displayName = "NDEx client";
		String description = "this is NDEx client";
		NdexWebServiceClient ndexWebServiceClient = new NdexWebServiceClient(
				uri, displayName, description, cyNetworkFactoryServiceRef,
				cyNetworkViewFactoryServiceRef, cyNetworkManagerServiceRef,
				cyRootNetworkManagerServiceRef, null,panel);

		panel.setNdexClient(ndexWebServiceClient);
		registerAllServices(bc, ndexWebServiceClient, new Properties());
		
		// NDEx Sign In
		NdexSignInAction ndexSignInAction = new NdexSignInAction(
				cySwingApplicationServiceRef, 
				cyApplicationManagerServiceRef,
				cyNetworkManagerServiceRef, 
				cyNetworkViewManagerServiceRef, 
				cyNetworkNamingServiceRef,
				taskManager);

		final Properties props = new Properties();
		props.setProperty(ServiceProperties.ID, "ndexSignInAction");
		registerService(bc, ndexSignInAction, CyAction.class, props); 
		
		// NDEx Sign In
		NdexStoreNetworkAction ndexStoreNetworkAction = new NdexStoreNetworkAction(
						cyApplicationManagerServiceRef);

		final Properties storeNetworkProps = new Properties();
		storeNetworkProps.setProperty(ServiceProperties.ID, "ndexStoreNetworkAction");
		registerService(bc, ndexStoreNetworkAction, CyAction.class, storeNetworkProps); 
		
		/*
		NdexSignInTaskFactory ndexSignInTaskFactory = new NdexSignInTaskFactory(cyNetworkManagerServiceRef,cyNetworkNamingServiceRef,cyNetworkFactoryServiceRef);
		
		Properties ndexSignInFactoryProps = new Properties();
		ndexSignInFactoryProps.setProperty("preferredMenu","Apps.NDEx");
		ndexSignInFactoryProps.setProperty("title","Connect to NDEx Server");
		registerService(bc,ndexSignInTaskFactory,TaskFactory.class, ndexSignInFactoryProps);
		*/
		
		// Upload Current Network to NDEx
		
		/*
		CreateNetworkTaskFactory createNetworkTaskFactory = new CreateNetworkTaskFactory(cyNetworkManagerServiceRef,cyNetworkNamingServiceRef,cyNetworkFactoryServiceRef);
				
		Properties sample05TaskFactoryProps = new Properties();
		sample05TaskFactoryProps.setProperty("preferredMenu","Apps.Samples");
		sample05TaskFactoryProps.setProperty("title","NDEx 2 Create Network");
		registerService(bc,createNetworkTaskFactory,TaskFactory.class, sample05TaskFactoryProps);
		*/
	}
}

