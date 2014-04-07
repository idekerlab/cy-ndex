package org.cytoscape.io.ndex.internal.helpers;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.work.AbstractTaskFactory;
import org.cytoscape.work.TaskIterator;

public class CyNetworkToNdexNetworkTranslationTaskFactory extends
		AbstractTaskFactory {
	
	private CyApplicationManager manager;
	
	public CyNetworkToNdexNetworkTranslationTaskFactory(CyApplicationManager cyApplicationManager){
		 manager = cyApplicationManager;
	}

	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new CyNetworkToNdexNetworkTranslationTask(manager));
	}

}
