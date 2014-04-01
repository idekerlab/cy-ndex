package org.cytoscape.io.ndex.internal;


import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;


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
		if (cyApplicationManager.getCurrentNetwork() == null){			
			return;
		}
		
		if (NdexInterface.INSTANCE.storeCyNetwork(cyApplicationManager.getCurrentNetwork())){
			JOptionPane.showMessageDialog(null, "Successfully stored network in NDEx");
		} else {
			JOptionPane.showMessageDialog(null, "Failed to store network in NDEx");
		}

		// TODO : allow saving of partial network based on selected nodes...
		//List<CyNode> nodes = CyTableUtil.getNodesInState(cyApplicationManager.getCurrentNetwork(),"selected",true);
		
		
	}


}
