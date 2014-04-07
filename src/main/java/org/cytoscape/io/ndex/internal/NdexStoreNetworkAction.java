package org.cytoscape.io.ndex.internal;


import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.io.ndex.internal.helpers.CyNetworkToNdexNetworkTranslationTask;
import org.cytoscape.io.ndex.internal.helpers.CyNetworkToNdexNetworkTranslationTaskFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskIterator;
import org.ndexbio.model.object.Network;


public class NdexStoreNetworkAction extends AbstractCyAction {

	//private static final long serialVersionUID = -597481727043928800L;
	private static final long serialVersionUID = -1012L;
	
	private final CyApplicationManager cyApplicationManager;

	public NdexStoreNetworkAction(CyApplicationManager cyApplicationManager){
		super("Store Current Network on NDEx");
		setPreferredMenu("Apps.NDEx");
		this.cyApplicationManager = cyApplicationManager;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		CyNetwork currentCyNetwork = cyApplicationManager.getCurrentNetwork();
		if (currentCyNetwork == null){		
			JOptionPane.showMessageDialog(null, "No current network to store in NDEx");
			return;
		}
		
/*		
		//TaskIterator taskIterator = factory.createTaskIterator();
		//taskIterator.
		
		CyNetworkToNdexNetworkTranslationTask translator = new CyNetworkToNdexNetworkTranslationTask(cyNetwork);
		Network ndexNetwork = translator.run(monitor);
		
		if (NdexInterface.INSTANCE.storeCyNetwork(cyApplicationManager.getCurrentNetwork())){
			JOptionPane.showMessageDialog(null, "Successfully stored network in NDEx");
		} else {
			JOptionPane.showMessageDialog(null, "Failed to store network in NDEx");
		}

		// TODO : allow saving of partial network based on selected nodes...
		//List<CyNode> nodes = CyTableUtil.getNodesInState(cyApplicationManager.getCurrentNetwork(),"selected",true);
		
*/		
	}


}
