package org.cytoscape.io.ndex.internal.helpers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.io.ndex.internal.NdexInterface;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.ndexbio.model.object.BaseTerm;
import org.ndexbio.model.object.Edge;
import org.ndexbio.model.object.MetadataObject;
import org.ndexbio.model.object.Network;
import org.ndexbio.model.object.Node;
import org.ndexbio.rest.NetworkBuilder;
//import org.cytoscape.io.internal.read.xgmml.ObjectTypeMap;
//import org.cytoscape.io.internal.util.GroupUtil;
//import org.cytoscape.io.internal.util.UnrecognizedVisualPropertyManager;
//import org.cytoscape.io.internal.util.session.SessionUtil;

import com.fasterxml.jackson.databind.ObjectMapper;



public class CyNetworkToNdexNetworkTranslationTask extends AbstractTask {
	
		private CyNetwork cyNetwork;
		private NetworkBuilder builder;

		private static final String SPLIT_PATTERN = "[()]";
		private static final Pattern SPLIT = Pattern.compile(SPLIT_PATTERN);

	    // File format version. For compatibility.
	    private static final String DOCUMENT_VERSION_NAME = "cy:documentVersion";

	    // Node types
	    protected static final String NORMAL = "normal";
	    protected static final String METANODE = "group";
	    protected static final String REFERENCE = "reference";

	    public static final String ENCODE_PROPERTY = "cytoscape.encode.xgmml.attributes";

	    protected Set<CySubNetwork> subNetworks;
	    protected CyNetworkView networkView;
	    protected VisualStyle visualStyle;
//	    protected final VisualLexicon visualLexicon;
//	    protected final UnrecognizedVisualPropertyManager unrecognizedVisualPropertyMgr;
//	    protected final CyNetworkManager networkMgr;
	    protected final CyApplicationManager cyApplicationManager;
//	    private final GroupUtil groupUtil;

	    protected final Map<CyNode, Node> nodeMap = new HashMap<CyNode, Node>();

	    
	    public CyNetworkToNdexNetworkTranslationTask (final CyApplicationManager cyApplicationManager){
	    	//this.cyNetwork = cy;
	    	this.builder = new NetworkBuilder();
	    	this.cyApplicationManager = cyApplicationManager;
	    }
/*	    
	    final static private Logger logger = LoggerFactory.getLogger(GenericXGMMLWriter.class);

	    public GenericXGMMLWriter(final OutputStream outputStream,
	                              final RenderingEngineManager renderingEngineMgr,
	                              final CyNetworkView networkView,
	                              final UnrecognizedVisualPropertyManager unrecognizedVisualPropertyMgr,
	                              final CyNetworkManager networkMgr,
	                              final CyRootNetworkManager rootNetworkMgr,
	                              final VisualMappingManager vmMgr,
	                              final GroupUtil groupUtil) {
	        this(outputStream, renderingEngineMgr, networkView.getModel(), unrecognizedVisualPropertyMgr, networkMgr,
	                rootNetworkMgr, groupUtil);
	        this.networkView = networkView;
	        
	        setVisualStyle(vmMgr.getVisualStyle(networkView));
	    }
	    
	    public GenericXGMMLWriter(final OutputStream outputStream,
	                              final RenderingEngineManager renderingEngineMgr,
	                              final CyNetwork network,
	                              final UnrecognizedVisualPropertyManager unrecognizedVisualPropertyMgr,
	                              final CyNetworkManager networkMgr,
	                              final CyRootNetworkManager rootNetworkMgr,
	                              final GroupUtil groupUtil) {
	        this.outputStream = outputStream;
	        this.unrecognizedVisualPropertyMgr = unrecognizedVisualPropertyMgr;
	        this.networkMgr = networkMgr;
	        this.rootNetworkMgr = rootNetworkMgr;
	        this.visualLexicon = renderingEngineMgr.getDefaultVisualLexicon();
	        this.groupUtil = groupUtil;
	        
	        if (network instanceof CyRootNetwork) {
	            this.network = this.rootNetwork = (CyRootNetwork) network;
	            this.subNetworks = getSerializableSubNetworks(rootNetwork);
	        } else {
	            this.network = network;
	            this.rootNetwork = rootNetworkMgr.getRootNetwork(network);
	            this.subNetworks = new HashSet<CySubNetwork>();
	        }
	        
	        // Create our indent string (480 blanks);
	        for (int i = 0; i < 20; i++)
	            indentString += "                        ";
	        
	        doFullEncoding = Boolean.valueOf(System.getProperty(ENCODE_PROPERTY, "true"));
	    }
*/
	    //@Override
	    public void run(TaskMonitor taskMonitor) throws Exception {
	    	
			cyNetwork = cyApplicationManager.getCurrentNetwork();
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			if (cyNetwork == null){		
				JOptionPane.showMessageDialog(null, "No current network to store in NDEx");
				return;
			}
			
			
	        try {
	        	taskMonitor.setProgress(0.0);
	        	taskMonitor.showMessage(TaskMonitor.Level.INFO, "Checking Credentials");
	        	NdexInterface.INSTANCE.setCredential("dexterpratt", "insecure");
	        	NdexInterface.INSTANCE.checkCredential();
	        	
				taskMonitor.setProgress(0.1);
				taskMonitor.showMessage(TaskMonitor.Level.INFO, "Setting Network Properties");
				populateNetworkProperties();
				
				taskMonitor.setProgress(0.2);
				populateNetworkPresentationProperties();
				
				taskMonitor.setProgress(0.3);
				taskMonitor.showMessage(TaskMonitor.Level.INFO, "Adding Nodes");
				addNodes();
				taskMonitor.setProgress(0.5);
				taskMonitor.showMessage(TaskMonitor.Level.INFO, "Adding Edges");
				addEdges();
				taskMonitor.setProgress(0.8);
				
				// Network is ready to store
				taskMonitor.showMessage(TaskMonitor.Level.INFO, "Storing Network");
				System.out.println(objectMapper.writeValueAsString(builder.getNetwork()));
				NdexInterface.INSTANCE.storeNetwork(builder.getNetwork());
				taskMonitor.setProgress(1.0);
				
			} catch (Exception e) {
				taskMonitor.showMessage(TaskMonitor.Level.ERROR, "Error while attempting to store network " + e.getLocalizedMessage());
				e.printStackTrace();
			}

	        
	        // 
	        //if (groupUtil != null) {
	        //    groupUtil.groupsSerialized(Collections.singletonList(network), null);
	        //}
	        
	    }

		private void populateNetworkProperties() throws IOException {
	        builder.addNetworkProperty("id", cyNetwork.getSUID());
	        
	        String label = networkView != null ? getLabel(networkView) : getLabel(cyNetwork, cyNetwork);
	        builder.addNetworkProperty("label", label);
	        builder.getNetwork().setName(label);
	        
	        builder.addNetworkProperty("directed", getDirectionality());
	        
	        Date now = new Date();
	        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        
	        builder.addNetworkProperty("dc:type", "Protein-Protein Interaction");
	        builder.addNetworkProperty("dc:description", "N/A");
	        builder.addNetworkProperty("dc:identifier", "N/A");
	        builder.addNetworkProperty("dc:date", df.format(now));
	        builder.addNetworkProperty("dc:title", label);
	        builder.addNetworkProperty("dc:source", "http://www.cytoscape.org/");
	        builder.addNetworkProperty("dc:format", "Cytoscape-XGMML");
	        
	        addPropertiesFromRow(builder.getNetwork(), cyNetwork.getRow(cyNetwork));
	        addPropertiesFromRow(builder.getNetwork(), cyNetwork.getRow(cyNetwork, CyNetwork.HIDDEN_ATTRS));
	        
		}
		
	    protected String getLabel(final CyNetworkView view) {
	        String label = view.getVisualProperty(BasicVisualLexicon.NETWORK_TITLE);
	        
	        if (label == null || label.isEmpty())
	            label = Long.toString(view.getSUID());
	        
	        return label;
	    }
	    
	    protected String getLabel(final CyNetwork network, final CyIdentifiable entry) {
	        String label = getRowFromNetOrRoot(network, entry, null).get(CyNetwork.NAME, String.class);
	        
	        if (label == null || label.isEmpty())
	            label = Long.toString(entry.getSUID());
	        
	        return label;
	    }
	    
	    protected CyRow getRowFromNetOrRoot(final CyNetwork network, final CyIdentifiable entry, String namespace) {
	        CyRow row = null;
	        
	        try {
	            row = namespace == null ? network.getRow(entry) : network.getRow(entry, namespace);
	        } catch (final IllegalArgumentException e) {
	        	// Ignore this exception
	        } catch (final RuntimeException e) {
	        	//logger.error("Cannot get \"" + namespace +"\" row for entry \"" + entry + "\" in network \"" + network + 
	        	//		"\".", e);
	        }
	        
	        if (row == null && network instanceof CySubNetwork) {
	        	// Doesn't exist in subnetwork? Try to get it from the root network.
	            final CyRootNetwork root = ((CySubNetwork)network).getRootNetwork();
	            row = namespace == null ? root.getRow(entry) : root.getRow(entry, namespace);
	        }
	        
	        return row;
	    }
		
		private void populateNetworkPresentationProperties() {
			// TODO Auto-generated method stub
			
		}
		
	    protected void addNodes() throws IOException, ExecutionException {
	        List<CyNode> pointerNodes = new ArrayList<CyNode>();

	        for (CyNode cyNode : cyNetwork.getNodeList()) {
	            // Identify nodes with network pointers
	            // In this version, we do not support embedded 
	            // networks in the NDEx data model
	            // so we will throw an exception and discontinue
	            if (cyNode.getNetworkPointer() != null) {
	                pointerNodes.add(cyNode);
	                continue;
	            }

	            // Only if not already written inside a nested graph
	            if (!nodeMap.containsKey(cyNode))
	                addNode(cyNetwork, cyNode);
	        }
	        /*
	        for (CyNode node : pointerNodes) {
	            if (!writtenNodeMap.containsKey(node))
	                addNode(cyNetwork, node);
	        }
	        */

	        // Now, output nodes for expanded groups.  We'll clean
	        // these up on import...
	        /*
	        if (groupUtil != null) {
	            for (CyNode node : groupUtil.getExpandedGroups(network)) {
	                if (!writtenNodeMap.containsKey(node))
	                    writeNode(network, node);
	            }
	        }
	        */
	    }

	    protected void addNode(final CyNetwork net, final CyNode cyNode) throws IOException, ExecutionException {
	    	// create the BaseTerm
	    	String nodeLabel = getLabel(net, cyNode);
	    	BaseTerm representedTerm = builder.findOrCreateBaseTerm(nodeLabel);
	    	
	    	// create the Node
	    	Node node = builder.findOrCreateNode(representedTerm);
	    	
	    	// add the properties
	    	builder.addProperty(node, "id", cyNode.getSUID());
	    	builder.addProperty(node, "label", nodeLabel);
	    	
	    	addPropertiesFromRow(node, net.getRow(cyNode));
	    	addPropertiesFromRow(node, net.getRow(cyNode, CyNetwork.HIDDEN_ATTRS));
	    	
	    	// put it in the map to find again when creating edges
	    	nodeMap.put(cyNode, node);
	    	
	        
	        /* Later: handle sub-graphs linked to nodes
	            
	            // Write node's sub-graph
	            final CyNetwork netPointer = node.getNetworkPointer();
	            
	            if (netPointer != null && isSerializable(netPointer))
	                writeSubGraph(netPointer);
	            
	            // Output the node graphics if we have a view
	            if (networkView != null && network.containsNode(node))
	                writeGraphics(networkView.getNodeView(node), false);
			*/
	    }
	    
	    
	    protected void addPropertiesFromRow(MetadataObject object, final CyRow row) throws IOException {
	        if (row != null) {
		        final CyTable table = row.getTable();
		
		        for (final CyColumn column : table.getColumns()) {
		        	String col = column.getName();
		            if (!CyIdentifiable.SUID.equals(col)){
		            	String value = getAttribute(row, column, col);
		                object.getMetadata().put(col, value);
		            }
		        }
	        }
	    }
	    
	    protected String getAttribute(CyRow row, CyColumn column, String attName){
	    	
	        final Class<?> attType = column.getType();

	        if (attType == Double.class) {
	            Double dAttr = row.get(attName, Double.class);
	            if (null != dAttr) return dAttr.toString();
	        } else if (attType == Integer.class) {
	            Integer iAttr = row.get(attName, Integer.class);
	            if (null != iAttr) return iAttr.toString();
	        } else if (attType == Long.class) {
	            Long lAttr = row.get(attName, Long.class);
	            if (null != lAttr) return lAttr.toString();
	        } else if (attType == String.class) {
	            String sAttr = row.get(attName, String.class);
	            if (null != sAttr) return sAttr;
	        } else if (attType == Boolean.class) {
	            Boolean bAttr = row.get(attName, Boolean.class);
	            if (null != bAttr) return bAttr.toString();
	        } else if (attType == List.class) {
	            final List<?> listAttr = row.getList(attName, column.getListElementType());
	            if (listAttr != null) {
	            	// Concatenate values to string for now...
	            	StringBuffer sb = new StringBuffer();
	                for (Object obj : listAttr) {
	                	sb.append(obj.toString() + ", ");
	                }
	                if (sb.length() > 2){
	                	sb.setLength(sb.length() - 2);
	                }
	                return sb.toString(); 
	            }            
	            
	        }
	        return null;
	    }
		

	    protected void addEdges() throws IOException, ExecutionException {
	        for (CyEdge edge : cyNetwork.getEdgeList()) {
	            addEdge(cyNetwork, edge);
	        }

	        // Now, output hidden edges groups.  
	        // For collapsed groups, we need to output external edges,
	        // for expanded groups, we need to output edges to the group node
	        /*
	        if (groupUtil != null) {
	            // Handle edges to the group node
	            for (CyEdge edge : groupUtil.getGroupNodeEdges(network)) {
	                if (!writtenEdgeMap.containsKey(edge))
	                    writeEdge(network, edge);
	            }
	            // Now handle external edges
	            for (CyEdge edge : groupUtil.getExternalEdges(network)) {
	                if (!writtenEdgeMap.containsKey(edge))
	                    writeEdge(network, edge);
	            }
	        }
	        */
	    }
		
	    protected void addEdge(final CyNetwork net, final CyEdge cyEdge) throws IOException, ExecutionException {
	    	// find subject, predicate, object
	    	Node subjectNode = nodeMap.get(cyEdge.getSource());
	    	Node objectNode = nodeMap.get(cyEdge.getTarget());
	    	
	    	String label = getLabel(net, cyEdge);
	    	String interaction = "interacts";
			if (label != null) {
				// parts[0] = source alias
				// parts[1] = interaction
				// parts[2] = target alias

				final String[] parts = SPLIT.split(label);

				if (parts.length == 3) {
					//sourceAlias = parts[0];
					interaction = parts[1];
					//targetAlias = parts[2];
				}
			}
	    	BaseTerm predicate = builder.findOrCreateBaseTerm(interaction);
	    	
	    	// create edge
	    	Edge edge = builder.createEdge(subjectNode, objectNode, predicate);
	    	
	    	// add properties
	    	builder.addProperty(edge, "id", cyEdge.getSUID());
	    	builder.addProperty(edge, "label", label);
	    	builder.addProperty(edge, "source", cyEdge.getSource().getSUID());
	    	builder.addProperty(edge, "target", cyEdge.getTarget().getSUID());
	    	builder.addProperty(edge, "cy:directed", ObjectTypeMap.toXGMMLBoolean(cyEdge.isDirected()));

	    	addPropertiesFromRow(edge, getRowFromNetOrRoot(net, cyEdge, null));
	    	addPropertiesFromRow(edge, getRowFromNetOrRoot(net, cyEdge, CyNetwork.HIDDEN_ATTRS));


	    }

	    /**
	     * Check directionality of edges, return directionality string to use in xml
	     * file as attribute of graph element.
	     *
	     * Set isMixed field true if network is a mixed network (contains directed
	     * and undirected edges), and false otherwise (if only one type of edges are
	     * present.)
	     *
	     * @returns flag to use in XGMML file for graph element's 'directed' attribute
	     */
	    private String getDirectionality() {
	        boolean directed = false;

	        // Either only directed or mixed -> Use directed as default
	        for (CyEdge edge : cyNetwork.getEdgeList()) {
	            if (edge.isDirected()) {
	                directed = true;
	                break;
	            }
	        }

	        return  ObjectTypeMap.toXGMMLBoolean(directed);
	    }
	    
	    /**
	     * Used in Cytoscape XGMML writer when exporting to XGMML file.
	     */
	    /*
	    protected void prepareGroupsForSerialization() {
	        // Don't do this when saving the session-type XGMML files
	        if (groupUtil != null) {
	            groupUtil.prepareGroupsForSerialization(Collections.singletonList(network));
	        }
	    }
*/
	


	    /**
	     * Output any network attributes we have defined, including
	     * the network graphics information we encode as attributes:
	     * backgroundColor, zoom, and the graph center.
	     *
	     * @throws IOException
	     */
	    /*


	    protected void writeSubGraph(final CyNetwork net) throws IOException {
	        if (net == null)
	            return;
	        
	        if (writtenNetMap.containsKey(net)) {
	            // This sub-network has already been written
	            writeSubGraphReference(net);
	        } else {
	            // Check if this network is from the same root network as the base network
	            final CyRootNetwork otherRoot = rootNetworkMgr.getRootNetwork(net);
	            boolean sameRoot = rootNetwork.equals(otherRoot);
	            
	            if (sameRoot) {
	                // Write it for the first time
	                writtenNetMap.put(net, net);
	                
	                writeElement("<att>\n");
	                depth++;
	                writeElement("<graph");
	                // Always write the network ID
	                writeAttributePair("id", net.getSUID());
	                // Save the label to make it more human readable
	                writeAttributePair("label", getLabel(net, net));
	                writeAttributePair("cy:registered", ObjectTypeMap.toXGMMLBoolean(isRegistered(net)));
	                write(">\n");
	                depth++;
	        
	                writeAttributes(net.getRow(net));
	                writeAttributes(net.getRow(net, CyNetwork.HIDDEN_ATTRS));

	                for (CyNode childNode : net.getNodeList())
	                    writeNode(net, childNode);
	                for (CyEdge childEdge : net.getEdgeList())
	                    writeEdge(net, childEdge);
	        
	                depth--;
	                writeElement("</graph>\n");
	                depth--;
	                writeElement("</att>\n");
	            }
	        }
	    }
	    
	    protected void writeSubGraphReference(CyNetwork net) throws IOException {
	        if (net == null)
	            return;
	        
	        String href = "#" + net.getSUID();
	        final CyRootNetwork otherRoot = rootNetworkMgr.getRootNetwork(net);
	        final boolean sameRoot = rootNetwork.equals(otherRoot);
	        
	        if (!sameRoot) {
	            // This network belongs to another XGMML file,
	            // so add the other root-network's file name to the XLink URI
	            final String fileName = SessionUtil.getXGMMLFilename(otherRoot);
	            href = fileName + href;
	        }
	        
	        writeElement("<att>\n");
	        depth++;
	        writeElement("<graph");
	        writeAttributePair("xlink:href", href);
	        write("/>\n");
	        depth--;
	        writeElement("</att>\n");
	    }
*/

	    /**
	     * Output a Cytoscape edge as XGMML
	     *
	     * @param edge the edge to output
	     * @throws IOException
	     */
	    /*
	    protected void writeEdge(final CyNetwork net, final CyEdge edge) throws IOException {
	        writeElement("<edge");
	        final boolean written = writtenEdgeMap.containsKey(edge);
	        
	        if (written) {
	            // Write as an XLink only
	            writeAttributePair("xlink:href", "#" + edge.getSUID());
	            write("/>\n");
	        } else {
	            // Remember that we've wrote this edge
	            writtenEdgeMap.put(edge, edge);
	            
	            writeAttributePair("id", edge.getSUID());
	            writeAttributePair("label", getLabel(net, edge));
	            writeAttributePair("source", edge.getSource().getSUID());
	            writeAttributePair("target", edge.getTarget().getSUID());
	            writeAttributePair("cy:directed",  ObjectTypeMap.toXGMMLBoolean(edge.isDirected()));
	            
	            write(">\n");
	            depth++;

	            // Write the edge attributes
	            writeAttributes(getRowFromNetOrRoot(net, edge, null));
	            writeAttributes(getRowFromNetOrRoot(net, edge, CyNetwork.HIDDEN_ATTRS));
	    
	            // Write the edge graphics
	            if (networkView != null)
	                writeGraphics(networkView.getEdgeView(edge), false);

	            depth--;
	            writeElement("</edge>\n");
	        }
	    }
	    */
	    /**
	     * Writes a graphics tag under graph, node, edge.
	     * @param view
	     * @param groupLockedProperties Whether or not locked visual properties must be grouped under a list-type att tag.
	     * @throws IOException
	     */
	    /*
	    @SuppressWarnings({"unchecked", "rawtypes"})
	    protected void writeGraphics(View<? extends CyIdentifiable> view, final boolean groupLockedProperties)
	            throws IOException {
	        if (view == null)
	            return;
	        
	        writeElement("<graphics");
	        
	        CyIdentifiable element = view.getModel();
	        final VisualProperty<?> root;
	        
	        if (element instanceof CyNode)
	            root = BasicVisualLexicon.NODE;
	        else if (element instanceof CyEdge)
	            root = BasicVisualLexicon.EDGE;
	        else
	            root = BasicVisualLexicon.NETWORK;
	        
	        final Collection<VisualProperty<?>> visualProperties = visualLexicon.getAllDescendants(root);
	        final List<VisualProperty<?>> attProperties = new ArrayList<VisualProperty<?>>(); // To be written as att tags
	        final List<VisualProperty<?>> lockedProperties = new ArrayList<VisualProperty<?>>();
	        final Set<String> writtenKeys = new HashSet<String>();
	        
	        for (VisualProperty vp : visualProperties) {
	            // If network, ignore node and edge visual properties,
	            // because they are also returned as NETWORK's descendants
	            if (root == BasicVisualLexicon.NETWORK && vp.getTargetDataType() != CyNetwork.class)
	                continue;
	            
	            // It doesn't have to write the property if the value is null
	            Object value = view.getVisualProperty(vp);
	            
	            if (value == null)
	                continue;
	            
	            if (groupLockedProperties && view.isDirectlyLocked(vp)) {
	                lockedProperties.add(vp);
	                continue;
	            } else {
	                // If not a bypass, write only leaf nodes
	                final VisualLexiconNode node = visualLexicon.getVisualLexiconNode(vp);

	                if (!node.getChildren().isEmpty())
	                    continue;
	            }
	            
	            // Use XGMML graphics attribute names for some visual properties
	            final String[] keys = getGraphicsKey(vp);
	            
	            if (keys != null && keys.length > 0) {
	                // XGMML graphics attributes...
	                value = vp.toSerializableString(value);
	                
	                if (value != null) {
	                    for (int i = 0; i < keys.length; i++) {
	                        final String k = keys[i];
	                        
	                        if (!writtenKeys.contains(k) && !ignoreGraphicsAttribute(element, k)) {
	                            writeAttributePair(k, value);
	                            writtenKeys.add(k); // to avoid writing the same key twice, because of dependencies!
	                        }
	                    }
	                }
	            } else if (!ignoreGraphicsAttribute(element, vp.getIdString())) {
	                // So it can be written as nested att tags
	                attProperties.add(vp);
	            }
	        }
	        
	        Map<String, String> unrecognizedMap = unrecognizedVisualPropertyMgr
	                .getUnrecognizedVisualProperties(networkView, view);

	        if (attProperties.isEmpty() && lockedProperties.isEmpty() && unrecognizedMap.isEmpty()) {
	            write("/>\n");
	        } else {
	            write(">\n");
	            depth++;
	            
	            // write Cy3-specific properties 
	            for (VisualProperty vp : attProperties) {
	                writeVisualPropertyAtt(view, vp);
	            }
	            
	            // also save unrecognized visual properties
	            for (Map.Entry<String, String> entry : unrecognizedMap.entrySet()) {
	                String k = entry.getKey();
	                String v = entry.getValue();
	                
	                if (v != null)
	                    writeAttributeXML(k, ObjectType.STRING, v, false, true);
	            }
	            
	            // serialize locked properties as <att> tags inside <graphics>
	            if (!lockedProperties.isEmpty()) {
	                writeAttributeXML("lockedVisualProperties", ObjectType.LIST, null, false, false);
	                depth++;
	                
	                for (VisualProperty vp : lockedProperties) {
	                    writeVisualPropertyAtt(view, vp);
	                }
	                
	                depth--;
	                writeElement("</att>\n");
	            }
	            
	            depth--;
	            writeElement("</graphics>\n");
	        }
	    }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
	    private void writeVisualPropertyAtt(View<? extends CyIdentifiable> view, VisualProperty vp) throws IOException {
	    	Object value = view.getVisualProperty(vp);
	    	
	    	try {
		        value = vp.toSerializableString(value);
	    	} catch (final ClassCastException e) {
	        	logger.error("Error getting serializable string of Visual Property \"" + vp.getIdString() + "\" (value: " +
	        			value + ")", e);
	        	return;
	        }
		        
	        if (value != null)
	            writeAttributeXML(vp.getIdString(), ObjectType.STRING, value, false, true);
	    }
	    */

	    /**
	     * Do not use this method with locked visual properties.
	     * @param element
	     * @param attName
	     * @return
	     */
	    /*
	    protected boolean ignoreGraphicsAttribute(final CyIdentifiable element, String attName) {
	        return false;
	    }
	    
	    private String[] getGraphicsKey(VisualProperty<?> vp) {
	        //Nodes
	        if (vp.equals(BasicVisualLexicon.NODE_X_LOCATION)) return new String[]{"x"};
	        if (vp.equals(BasicVisualLexicon.NODE_Y_LOCATION)) return new String[]{"y"};
	        if (vp.equals(BasicVisualLexicon.NODE_Z_LOCATION)) return new String[]{"z"};
	        if (vp.equals(BasicVisualLexicon.NODE_SIZE)) return new String[]{"w", "h"};
	        if (vp.equals(BasicVisualLexicon.NODE_WIDTH)) return new String[]{"w"};
	        if (vp.equals(BasicVisualLexicon.NODE_HEIGHT)) return new String[]{"h"};
	        if (vp.equals(BasicVisualLexicon.NODE_FILL_COLOR)) return new String[]{"fill"};
	        if (vp.equals(BasicVisualLexicon.NODE_SHAPE)) return new String[]{"type"};
	        if (vp.equals(BasicVisualLexicon.NODE_BORDER_WIDTH)) return new String[]{"width"};
	        if (vp.equals(BasicVisualLexicon.NODE_BORDER_PAINT)) return new String[]{"outline"};

	        // Edges
	        if (vp.equals(BasicVisualLexicon.EDGE_WIDTH)) return new String[]{"width"};
	        if (vp.equals(BasicVisualLexicon.EDGE_STROKE_UNSELECTED_PAINT)) return new String[]{"fill"};
	        
	        // TODO: also write these attributes to keep compatibility with 2.x an Cytoscape Web (?):
	        // cy:nodeTransparency cy:nodeLabelFont cy:nodeLabel cy:borderLineType
	        // cy:sourceArrow cy:targetArrow cy:sourceArrowColor cy:targetArrowColor cy:edgeLabelFont cy:edgeLabel cy:edgeLineType cy:curved

	        return new String[]{};
	    }
	    
	    protected void writeAttributes(final CyRow row) throws IOException {
	        if (row != null) {
		        final CyTable table = row.getTable();
		
		        for (final CyColumn column : table.getColumns()) {
		            if (!CyIdentifiable.SUID.equals(column.getName()))
		                writeAttribute(row, column.getName());
		        }
	        }
	    }
	    */
	    /**
	     * Creates an attribute to write into XGMML file.
	     *
	     * @param row CyRow to load
	     * @param attName attribute name
	     * @return att Att to return (gets written into xgmml file - CAN BE NULL)
	     * @throws IOException
	     */
	    /*
	    protected void writeAttribute(final CyRow row, final String attName) throws IOException {
	        // create an attribute and its type:
	        final CyTable table = row.getTable();
	        final CyColumn column = table.getColumn(attName);
	        
	        if (column == null)
	            return;
	        
	        final boolean hidden = !table.isPublic();
	        final Class<?> attType = column.getType();

	        if (attType == Double.class) {
	            Double dAttr = row.get(attName, Double.class);
	            writeAttributeXML(attName, ObjectType.REAL, dAttr, hidden, true);
	        } else if (attType == Integer.class) {
	            Integer iAttr = row.get(attName, Integer.class);
	            writeAttributeXML(attName, ObjectType.INTEGER, iAttr, hidden, true);
	        } else if (attType == Long.class) {
	            Long lAttr = row.get(attName, Long.class);
	            writeAttributeXML(attName, ObjectType.REAL, lAttr, hidden, true);
	        } else if (attType == String.class) {
	            String sAttr = row.get(attName, String.class);
	            // Protect tabs and returns
	            if (sAttr != null) {
	                sAttr = sAttr.replace("\n", "\\n");
	                sAttr = sAttr.replace("\t", "\\t");
	            }

	            writeAttributeXML(attName, ObjectType.STRING, sAttr, hidden, true);
	        } else if (attType == Boolean.class) {
	            Boolean bAttr = row.get(attName, Boolean.class);
	            writeAttributeXML(attName, ObjectType.BOOLEAN, ObjectTypeMap.toXGMMLBoolean(bAttr), hidden, true);
	        } else if (attType == List.class) {
	            final List<?> listAttr = row.getList(attName, column.getListElementType());
	            writeAttributeXML(attName, ObjectType.LIST, null, hidden, false);

	            if (listAttr != null) {
	                depth++;
	                // iterate through the list
	                for (Object obj : listAttr) {
	                    String sAttr = null;
	                    
	                    if (obj instanceof Boolean) {
	                        sAttr = ObjectTypeMap.toXGMMLBoolean((Boolean) obj);
	                    } else {
	                        // Protect tabs and returns (if necessary)
	                        sAttr = obj.toString();
	                        if (sAttr != null) {
	                            sAttr = sAttr.replace("\n", "\\n");
	                            sAttr = sAttr.replace("\t", "\\t");
	                        }
	                    }
	                    // set child attribute value & label
	                    writeAttributeXML(attName, checkType(obj), sAttr, hidden, true);
	                }
	                depth--;
	            }
	            
	            writeAttributeXML(null, null, null, hidden, true);
	        }
	    }
*/
	    
	    /**
	     * writeAttributeXML outputs an XGMML attribute
	     *
	     * @param name is the name of the attribute we are outputting
	     * @param type is the XGMML type of the attribute
	     * @param value is the value of the attribute we're outputting
	     * @param end is a flag to tell us if the attribute should include a tag end
	     * @throws IOException
	     */
	    /*
	    protected void writeAttributeXML(String name, ObjectType type, Object value, boolean hidden, boolean end) throws IOException {
	        if (name == null && type == null)
	            writeElement("</att>\n");
	        else {
	            writeElement("<att");

	            if (name != null)
	                writeAttributePair("name", name);
	            if (value != null)
	                writeAttributePair("value", value);

	            writeAttributePair("type", type);
	            
	            if (hidden)
	                writeAttributePair("cy:hidden", ObjectTypeMap.toXGMMLBoolean(hidden));
	            
	            if (end)
	                write("/>\n");
	            else
	                write(">\n");
	        }
	    }
*/


	    /**
	     * Check the type of Attributes.
	     *
	     * @param obj
	     * @return Attribute type in string.
	     */
	    /*
	    private ObjectType checkType(final Object obj) {
	        final Class<?> type = obj.getClass();
	        
	        if (type == String.class)
	            return ObjectType.STRING;
	        else if (type == Integer.class)
	            return ObjectType.INTEGER;
	        else if (type == Double.class || type == Float.class || type == Long.class)
	            return ObjectType.REAL;
	        else if (type == Boolean.class)
	            return ObjectType.BOOLEAN;

	        return null;
	    }
	    

	    


	   
*/

	    /**
	     * Used when saving the view-type XGMML. 
	     * @param visualStyleName
	     */
	    private void setVisualStyle(final VisualStyle visualStyle) {
	        this.visualStyle = visualStyle;
	    }
	    
	    /**
	     * @param rootNet
	     * @return A set with all the subnetworks that should be serialized.
	     */
	    protected Set<CySubNetwork> getSerializableSubNetworks(final CyRootNetwork rootNet) {
	        final Set<CySubNetwork> serializableSet = new LinkedHashSet<CySubNetwork>();
	        final List<CySubNetwork> subNetList = rootNet.getSubNetworkList();
	        final CySubNetwork baseNetwork = rootNet.getBaseNetwork();
	        
	        // The base network must be the first one!
	        if (isSerializable(baseNetwork))
	            serializableSet.add(baseNetwork);
	        
	        for (final CySubNetwork sn : subNetList) {
	            if (isSerializable(sn))
	                serializableSet.add(sn);
	        }
	        
	        return serializableSet;
	    }
	    
	    protected boolean isSerializable(final CyNetwork net) {
	        return net.getSavePolicy() != SavePolicy.DO_NOT_SAVE;
	    }
	    
	 /*   
		protected boolean isRegistered(final CyNetwork net) {
	        return networkMgr.networkExists(net.getSUID());
	    }
	    */
	}



