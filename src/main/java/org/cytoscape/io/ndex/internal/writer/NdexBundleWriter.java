package org.cytoscape.io.ndex.internal.writer;

import java.io.OutputStream;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NdexBundleWriter extends AbstractNetworkTask implements CyWriter {

	protected final OutputStream os;
	protected final CyNetwork network;
	protected final ObjectMapper jdexMapper;
	public NdexBundleWriter(OutputStream os,CyNetwork network,ObjectMapper jdexMapper) {
		super(network);
		this.os = os;
		this.network = network;
		this.jdexMapper = jdexMapper;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub

		jdexMapper.writeValue(os, network);
		os.close();
	}

}
