package demo.topology;

import java.net.InetAddress;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionConfig;
import com.cisco.onep.element.SessionHandle;
import com.cisco.onep.element.SessionConfig.SessionTransportMode;
import com.cisco.onep.topology.Edge;
import com.cisco.onep.topology.Graph;
import com.cisco.onep.topology.Node;
import com.cisco.onep.topology.NodeConnector;
import com.cisco.onep.topology.Topology;
import com.cisco.onep.topology.TopologyEvent;
import com.cisco.onep.topology.TopologyFilter;
import com.cisco.onep.topology.TopologyListener;
import com.cisco.onep.topology.Topology.TopologyType;
import com.cisco.onep.topology.TopologyEvent.TopologyEventType;
import com.cisco.onep.tutorials.BaseTutorial;

public class TopologyDiscovery extends BaseTutorial {
	//private static final long SLEEP = 20000L;
	private NetworkElement daughter;
	

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        TopologyDiscovery tutorial = new TopologyDiscovery();

        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("TopologyDiscovery")) {
                System.exit(1);
            }
            Topology topology = tutorial.discoverCDPTopology();
            //tutorial.addTopologyListener(topology);
            /* Shut the interfaces down to see TopologyEventType.EDGES_DELETE */
            //tutorial.simulateShutdown(true);
            //Thread.sleep(SLEEP);
            /* And bring them back up again to see TopologyEventType.EDGES_ADD */
            //tutorial.simulateShutdown(false);
            //Thread.sleep(SLEEP);
        } catch (Exception e) {
            tutorial.disconnect();
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        }
        tutorial.disconnect();
        System.exit(0);
    }

    /**
     * An example TopologyListener.
     */
    // START SNIPPET: ExampleTopologyListener
    public class ExampleTopologyListener implements TopologyListener {
        /**
         * Handles TopologyEvents and will notify the user if edges have been added or deleted from the current Network
         * Element.
         *
         * @param event
         *            The TopologyEvent.
         * @param clientData
         *            Data passed in when the listener was registered.
         *
         * @see com.cisco.onep.topology.TopologyListener#handleEvent(com.cisco.onep .topology.TopologyEvent,
         *      java.lang.Object)
         */
        public void handleEvent(TopologyEvent event, Object clientData) {
            getLogger().info("Received TopologyEvent.");

            if (event.getType().contains(TopologyEventType.EDGES_ADD)) {
                getLogger().info("Some edges have been added.");
            }

            if (event.getType().contains(TopologyEventType.EDGES_DELETE)) {
                getLogger().info("Some edges have been deleted");
            }

            getLogger().info("The number of changed edges in the TopologyEvent is " + event.getEdgeList().size());
            getLogger().info("The number of changed nodes in the TopologyEvent is " + event.getNodeList().size());
        }
    }

    // END SNIPPET: ExampleTopologyListener

    /**
     * Add an ExampleTopologyListener to the Topology passed in.
     *
     * @param topology
     *            A Topology to which the ExampleTopologyListener is added.
     * @throws OnepException
     *             If there is an error.
     */
    public void addTopologyListener(Topology topology) throws OnepException {

        /* The Event Filter object allows the application to listen to specific
         * topology change events corresponding to a topology object.
         * When a topology change event matches with the specified filter
         * criteria the application is notified through the registered callback.
         */
        // START SNIPPET: addTopologyListener
        List<TopologyEventType> eventType = new ArrayList<TopologyEventType>();
        eventType.add(TopologyEventType.EDGES_ADD);
        eventType.add(TopologyEventType.EDGES_DELETE);
        TopologyFilter filter = new TopologyFilter(eventType);
        topology.addTopologyListener(new ExampleTopologyListener(), filter, null);
        // END SNIPPET: addTopologyListener
    }

    /**
     * Obtain a Topology instance from the NetworkElement for TopologyType.CDP
     * and print the Topology graph information.
     *
     * @return A Topology instance.
     * @throws OnepException
     *             If there is an error.
     */
    public Topology discoverCDPTopology() throws OnepException {
        // START SNIPPET: discoverCDPTopology
        /* Create a topology object */
        Topology topology = new Topology(getNetworkElement(), TopologyType.CDP);

        /* Get the graph object */
        Graph graph = topology.getGraph();
        
        /* Get the list of edges from the Graph object. */
        List<Edge> edgeList = graph.getEdgeList(Edge.EdgeType.UNDIRECTED);

        /* Get the Node and Node connectors an Edge is part of, checking
         * that there are any first.
         */
        NodeConnector headNodeConnector = null;
        NodeConnector tailNodeConnector = null;
        Node headNode = null;
        Node tailNode = null;

        if (edgeList.size() > 0) {
            for (Edge edge : edgeList) {
            	//Get the Remote End
                tailNodeConnector = edge.getTailNodeConnector();
                //Check this daughter for connections
                Topology daughterTopo = checkDaughter(tailNodeConnector.getAddressList().get(0));
                //Concatenate the daughter's graph with our graph
                graph.concatenate(daughterTopo.getGraph());
                
            }
            
            //This prints out the concatenated list (only prints the interface from one direction, not both)
            
            edgeList = graph.getEdgeList(Edge.EdgeType.UNDIRECTED);
            Collection<String> devices = new TreeSet<String>(Collator.getInstance());
            for (Edge edge : edgeList){
            	headNodeConnector = edge.getHeadNodeConnector();
                tailNodeConnector = edge.getTailNodeConnector();
                headNode = headNodeConnector.getNode();
                tailNode = tailNodeConnector.getNode();
                
                if( ! devices.contains(headNode.getName())){
                	devices.add(headNode.getName());
                }
                if( ! devices.contains(tailNode.getName())){
                	devices.add(tailNode.getName());
                }
                getLogger().info(headNode.getName() + " " + headNodeConnector.getName() + "  <>  " + tailNodeConnector.getName() + " " + tailNode.getName());
            }
            getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            getLogger().info("Devices:");
            getLogger().info(devices.toString());
        } else {
            getLogger().info("There are no edges.");
        }
        return topology;
        // END SNIPPET: discoverCDPTopology
    }
    
    public Topology checkDaughter(InetAddress daughterAddress) throws OnepException {
    	
    	getLogger().info(daughterAddress.toString());
    	NetworkApplication networkApplication = NetworkApplication.getInstance();
    	daughter = networkApplication.getNetworkElement(daughterAddress);
    	SessionConfig daughterConfig = new SessionConfig(SessionTransportMode.SOCKET);
        daughterConfig.setPort(OnepConstants.ONEP_PORT);
    	SessionHandle daughterSession = daughter.connect("CISCO", "cisco", daughterConfig);
    	
    	/* Create a topology object */
        Topology topology = new Topology(daughter, TopologyType.CDP);

       return topology;
    }
}
