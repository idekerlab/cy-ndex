package org.cytoscape.io.ndex.internal;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class NdexSignInTaskFactory extends AbstractTaskFactory {
	private final CyNetworkManager netMgr;
	private final CyNetworkFactory cnf;
	private final CyNetworkNaming namingUtil; 
	
	public NdexSignInTaskFactory(final CyNetworkManager netMgr, 
			final CyNetworkNaming namingUtil, final CyNetworkFactory cnf){
		this.netMgr = netMgr;
		this.namingUtil = namingUtil;
		this.cnf = cnf;
	}
	
	public TaskIterator createTaskIterator(){
		return new TaskIterator(new NdexSignInTask(netMgr, namingUtil, cnf));
	}
}
