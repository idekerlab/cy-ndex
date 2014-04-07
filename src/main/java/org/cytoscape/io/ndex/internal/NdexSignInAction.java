package org.cytoscape.io.ndex.internal;


import java.awt.Dialog;
import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;


public class NdexSignInAction extends AbstractCyAction {

	//private static final long serialVersionUID = -597481727043928800L;
	private static final long serialVersionUID = -1011L;
	
	private static final String APP_MENU_TITLE ="Connect to NDEx Server";
	private static final String PARENT_MENU ="Apps.NDEx";
	
	private final CySwingApplication swingApp;
	private final CyNetworkManager cnm;
	private final CyNetworkNaming cnn;
	private final TaskManager taskManager;


	public NdexSignInAction(CySwingApplication swingApp, CyApplicationManager cam, CyNetworkManager cnm,
			CyNetworkViewManager cnvm, CyNetworkNaming cnn, TaskManager taskManager
			) {
		//super(APP_MENU_TITLE, cam, "network", cnvm);
		super(APP_MENU_TITLE);
		setPreferredMenu(PARENT_MENU);
		setMenuGravity((float)0.0);
		
		this.swingApp = swingApp;
		this.cnm = cnm;
		this.cnn = cnn;
		this.taskManager = taskManager;

	}

	/**
	 * This method is called when the user selects the menu item.
	 */
	@Override
	public void actionPerformed(final ActionEvent ae) {

		final NdexSignInDialog dialog = new NdexSignInDialog(swingApp.getJFrame());
		dialog.setLocationRelativeTo(swingApp.getJFrame());
		dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		dialog.setVisible(true);
	}

}
