package org.cytoscape.io.ndex.internal;

import java.util.Properties;

import org.cytoscape.service.util.AbstractCyActivator;
import org.osgi.framework.BundleContext;
/**
 * Imports and exports OSGi services.
 */
public class CyActivator extends AbstractCyActivator {

	@Override
	public void start(BundleContext context) throws Exception {
	
	Properties properties = new Properties();
//		registerService(context, sampleAnalyzer, SampleAnalyzer.class, properties);;
	}
}
