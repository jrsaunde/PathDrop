package topo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.cisco.onep.cfgmgr.common.ArrayListMultimap;
import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionConfig;
import com.cisco.onep.element.SessionConfig.SessionTransportMode;
import com.cisco.onep.element.SessionHandle;
import com.cisco.onep.interfaces.InterfaceFilter;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.topology.Edge;
import com.cisco.onep.topology.Graph;
import com.cisco.onep.topology.Node;
import com.cisco.onep.topology.Topology;
import com.cisco.onep.topology.Topology.TopologyType;

public class NetworkDiscovery {

	public Graph graph = null;
	public List<InetAddress> addresses;
	public NetworkApplication discoveryApplication = NetworkApplication.getInstance();
	public SessionConfig nodeConfig;
	
	
	public static void main(String args[]) {
		/*Parse input arguments*/
		String start = args[0];
		String username = args[1];
		String password = args[2];
		
		try {
			System.out.println("Start Node is " + start + " with " + username + "/" + password);

			InetAddress startNode = InetAddress.getByName(start);
			NetworkDiscovery network = new NetworkDiscovery(startNode, username, password);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	

	public NetworkDiscovery(InetAddress startAddress, String username, String password) {
		
		long startTime = System.nanoTime();		//TODO: Testing code (keeping track of startTime)
		try {
			/*Initialize the global variables*/
			initalizeGlobals();
			
			/*Set up the Start Node*/
			NetworkElement node = discoveryApplication.getNetworkElement(startAddress);			
			
			/*Run recursive network discovery*/
			getNeighbors(node, username, password);
			
			/*Print out the Global graph*/
			printNetwork();
			
		} catch (OnepException e) {
			e.printStackTrace();
		}
		
		/*TODO: Testing code for timing the program execution time*/
		long endTime = System.nanoTime();
		System.out.println("Total Program took: " + ((endTime - startTime)/(Math.pow(10, 9))) + " seconds (" + (endTime - startTime) + ") ns");
		
		return;
	}
	
	private Graph getNeighbors(NetworkElement node, String username, String password) throws OnepException{
		
		//System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		//System.out.println("getNeighbors: " + node.getAddress() + " hash: " + node.hashCode());
		
		
		/*Connect to the node */		
		SessionHandle nodeSession = node.connect(username, password, this.nodeConfig);
		
		/* Add all of the local IP addresses to our master list */
		List <NetworkInterface> interfaceList = node.getInterfaceList(new InterfaceFilter());
		
		for(NetworkInterface inter : interfaceList){
			if(inter.getAddressList().size() > 0 ){
				//System.out.println(inter.getName() + " " + inter.getAddressList().get(0));
				this.addresses.add(inter.getAddressList().get(0));
			}
		}
		
		/*Get Topology object */
		Topology topology = new Topology(node, TopologyType.CDP);
		
		/*If this is the first node, we set out global graph equal to this graph*/
		if(this.graph == null){
			this.graph = topology.getGraph();
		}
		
		/* Check all of the neighbors to see if we've been there yet*/
		List<Edge> edges = topology.getGraph().getEdgeList(Edge.EdgeType.UNDIRECTED);
		
		for(Edge edge: edges){
			//System.out.println("Edge node :" + edge.getTailNode().getName() + " Hash: " + edge.getTailNode().hashCode());
			
			InetAddress neighborIP = edge.getTailNodeConnector().getAddressList().get(0);
			/* If we haven't seen this IP address before, we haven't been to this device, we need to go to it*/
			if(!addressInNetwork(neighborIP)){
				//System.out.println("Daughter: " + edge.getTailNode().toString());
				//System.out.println("Connecting to Daughter: " + edge.getTailNodeConnector().getAddressList().get(0).toString());
				
				/*Connect to neighbor and run recursive discovery*/
				NetworkElement daughterNode = discoveryApplication.getNetworkElement(neighborIP);
				Graph daughterGraph = getNeighbors(daughterNode, username, password);
				
				/*Disconnect from the daughterNode NetworkElement since we are done*/
				daughterNode.disconnect();
				
				/*Add the graph we got from the neighbor to the global graph*/
				this.graph.concatenate(daughterGraph);
			}
		}
		//System.out.println("***WE JUST STEPPED OUT***");
		/*Return our graph (not our neighbors)*/
		return topology.getGraph();
	}


	private boolean daughterInNetwork(Node daughter) {
		System.out.println("checking: daughterInNetwork: " + daughter.getName() + " hash: " + daughter.hashCode());
		if(graph == null) {
			System.out.println("return false - graph null");
				return false;
		}
		System.out.println("checking: nodeList");
		List <Node> nodeList = this.graph.getNodeList();
		for (Node node : nodeList) {
			if (node.hashCode() == daughter.hashCode()){
				System.out.println("Found node " + daughter.getName() + ":" + daughter.hashCode() + "==" + node.getName() + ":" + node.hashCode());
				return true;
			}
		}
		return false;
	}
	
	private boolean addressInNetwork(InetAddress address){
		/*Checks to see if the IP address is in our master list of addresses*/
		
		//System.out.println("checking: IP address: " + address.toString());
		
		if(this.addresses.contains(address)){
			//System.out.println("Found IP address - true");
			return true;
		}
		//System.out.println("New IP address - false");
		return false;
		
	}
	
	private void printNetwork() throws OnepException{
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("Network Summary");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		
		/*Print out all of the device names*/
		List<Node>  nodes = this.graph.getNodeList();
		for(Node device: nodes){
			System.out.println(device.getName());
		}
		
		/*Print out all of the connections*/
		List<Edge> edges = this.graph.getEdgeList(Edge.EdgeType.UNDIRECTED);
		for(Edge edge: edges){
			System.out.println(edge.getHeadNode().getName() 
							 + "[" + edge.getHeadNodeConnector().getName() + "] <> [" 
							 + edge.getTailNodeConnector().getName() + "] "
							 + edge.getTailNode().getName());
		}
		
		
		
		//Print all possible routes
		System.out.println("Routes from " + nodes.get(0).getName() + " to " + nodes.get(nodes.size()-1).getName());
		enumerate(nodes.get(0), nodes.get(nodes.size()-1));
	}
	
	private ArrayList<Node> getConnected(List<Edge> connectedEdges){
		ArrayList<Node> connectedNodes = new ArrayList<Node>();
		for(int x=0; x <connectedEdges.size(); x++){
			connectedNodes.add(connectedEdges.get(x).getTailNode());
		}
		return connectedNodes;
	}
	
	private Stack<String> path = new Stack<String>();
	private ArrayList<Node> onPath = new ArrayList<Node>();
	
	/* modified version of enumerate method by Robert Sedgewick and Kevin Wayne in AllPaths.java */
	private void enumerate(Node v, Node t) {
	        // add node v to current path from s
	        path.push(v.getName());
	        onPath.add(v);

	        // found path from s to t - currently prints in reverse order because of stack
	        if (v.equals(t)) System.out.println(path);
	        // consider all neighbors that would continue path with repeating a node
	        else {
	        	ArrayList<Node> connected = getConnected(graph.getEdgeListByNode(Edge.EdgeType.DIRECTED, v));
	            for (Node w : connected) {
	                if (!onPath.contains(w)) enumerate(w, t);
	            }
	        }
	        
	        // done exploring from v, so remove from path
	        path.pop();
	        onPath.remove(v);
	}
	
	
	private void initalizeGlobals(){
		this.nodeConfig = new SessionConfig(SessionTransportMode.SOCKET);
		this.nodeConfig.setPort(OnepConstants.ONEP_PORT);
		this.addresses = new ArrayList<InetAddress>();
	}
}


