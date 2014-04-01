package org.cytoscape.io.ndex.internal;

import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskMonitor;
import org.ndexbio.model.object.Network;
import org.ndexbio.rest.NdexRestClient;
import org.ndexbio.rest.NdexRestClientModelAccessLayer;

public class NdexInterface {
	private NdexRestClient client; 
    private NdexRestClientModelAccessLayer mal; 
    private CyNetworkViewFactory viewFactory;
    private CyNetworkManager networkManager;
    private TaskMonitor monitor;
    private CyNetworkFactory factory;
    private CyRootNetworkManager rootNetworkManager;
    
    public static final NdexInterface INSTANCE = null;
	
	public NdexInterface(CyNetworkFactory factory,
			CyNetworkViewFactory viewFactory, CyNetworkManager networkManager,
			CyRootNetworkManager rootNetworkManager, TaskMonitor monitor) {
		this.factory = factory;
		this.viewFactory = viewFactory;
		this.networkManager = networkManager;
		this.rootNetworkManager = rootNetworkManager;
		this.monitor = monitor;
		this.client = new NdexRestClient("dexterpratt", "insecure");
		System.out.println("initializing NdexRestClientModelAccessLayer");
		mal = new NdexRestClientModelAccessLayer(client);

	}
	
	public void setCredential(String username, String password) {
		client.setCredential(username, password);	
	}
	
	public boolean checkCredential(){
		return mal.checkCredential();
	}
    
	
	public List<Network> findNetworksByName(String networkName){
		return mal.findNetworks(networkName);
	}

	public Network getSmallNetworkByName(String networkName) throws IllegalArgumentException, Exception {
        try
        {
            List<Network> networks = mal.findNetworks(networkName);
            System.out.println("Found networks, count = " + networks.size());
            
            Network network = networks.get(0);
            System.out.println("First network name = " + network.getName());
            
            if (null != network)
            	return getSmallNetwork(network);
        
        }
        catch (Exception e)
        {
        	System.out.println("Error getting network: " + e.getLocalizedMessage());
        	e.printStackTrace();
        }
		return null;
	}
	
    public Network getSmallNetwork(Network network) throws IllegalArgumentException, Exception
    {
    	int edgesPerBlock = 500;
    	int nodesPerBlock = 500;
    	int skipBlocks = 0;
    	
    	// Get the first block of edges from the source network
    	System.out.println("Getting edges in blocks of " + edgesPerBlock + " at offset " + skipBlocks);
    	return mal.getEdges(network.getId(), skipBlocks, edgesPerBlock);

    }

	public NdexRestClientModelAccessLayer getMal() {
		return mal;
	}

	public CyNetwork createCyNetwork() {
		return factory.createNetwork();
	}

	public boolean storeCyNetwork(CyNetwork currentNetwork) {
		Network ndexNetwork = new Network();
		populateNdexNetwork(currentNetwork);
		try {
			if (null != mal.createNetwork(ndexNetwork)) return true;
			return false;
		} catch (Exception e) {
			System.out.println("Error while attempting to store network " + e.getLocalizedMessage());
			return false;
		}
	}

	private void populateNdexNetwork(CyNetwork currentNetwork) {
		// TODO Auto-generated method stub
		
	}  

}
