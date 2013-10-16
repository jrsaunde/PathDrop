package topo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
	public NetworkApplication networkApplication = NetworkApplication.getInstance();
	
	
	public static void main(String args[]) {
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
	}
	

	public NetworkDiscovery(InetAddress startAddress, String username, String password) {
		try {
			addresses = new ArrayList<InetAddress>();
			NetworkElement node = networkApplication.getNetworkElement(startAddress);
			System.out.println("Start Node is " + startAddress);
			Graph master = getNeighbors(node, username, password);
			
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			List<Node>  nodes = master.getNodeList();
			List<Edge> edges = master.getEdgeList(Edge.EdgeType.UNDIRECTED);
			
			for(Node device: nodes){
				System.out.println(device.getName());
			}
			for(Edge edge: edges){
				System.out.println(edge.getHeadNode().getName() 
								 + "[" + edge.getHeadNodeConnector().getName() + "] <> [" 
								 + edge.getTailNodeConnector().getName() + "] "
								 + edge.getTailNode().getName());
				
			}
		} catch (OnepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
		//return;
	}
	
	private Graph getNeighbors(NetworkElement node, String username, String password) throws OnepException{
		
		//SessionConfig nodeConfig = new SessionConfig(SessionTransportMode.SOCKET);
		//nodeConfig.setPort(OnepConstants.ONEP_PORT);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("getNeighbors: " + node.getAddress() + " hash: " + node.hashCode());
		//Connect to the node
		//SessionHandle nodeSession = node.connect(username, password, nodeConfig);
		SessionHandle nodeSession2 = node.connect(username, password);
		
		//System.out.println("UDI is: " + node.getProperty().getUDI());
		List <NetworkInterface> interfaceList = node.getInterfaceList(new InterfaceFilter());
		
		for(NetworkInterface inter : interfaceList){
			if(inter.getAddressList().size() > 0 ){
				System.out.println(inter.getName() + " " + inter.getAddressList().get(0));
				addresses.add(inter.getAddressList().get(0));
			}
		}
		//Get Topology object
		Topology topology = new Topology(node, TopologyType.CDP);
		
		List<Node> daughters = topology.getGraph().getNodeList();
		if(graph == null){	//This is our first node
			graph = topology.getGraph();
		}
		List<Edge> edges = topology.getGraph().getEdgeList(Edge.EdgeType.UNDIRECTED);
		
		for(Edge edge: edges){
			System.out.println("Edge node :" + edge.getTailNode().getName() + " Hash: " + edge.getTailNode().hashCode());
			//System.out.println("TailNode ConnectorHash: " + edge.getTailNodeConnector().hashCode());
			if(!addressInNetwork(edge.getTailNodeConnector().getAddressList().get(0))){
				System.out.println("Daughter: " + edge.getTailNode().toString());
				System.out.println("Connecting to Daughter: " + edge.getTailNodeConnector().getAddressList().get(0).toString());
				NetworkElement daughterNode = networkApplication.getNetworkElement(edge.getTailNodeConnector().getAddressList().get(0));
				Graph daughterGraph = getNeighbors(daughterNode, username, password);
				this.graph.concatenate(daughterGraph);
				System.out.println("Edge List: " + daughterGraph.getEdgeList(Edge.EdgeType.UNDIRECTED).toString());

			}
//			if(!daughterInNetwork(edge.getTailNode())){	//if new node discovered
//				System.out.println("Daughter: " + edge.getTailNode().toString());
//				System.out.println("Connecting to Daughter: " + edge.getTailNodeConnector().getAddressList().get(0).toString());
//				NetworkElement daughterNode = networkApplication.getNetworkElement(edge.getTailNodeConnector().getAddressList().get(0));
//				//this.graph.concatenate(topology.getGraph());
//				Graph daughterGraph = getNeighbors(daughterNode, username, password);
//				System.out.println("Edge List: " + daughterGraph.getEdgeList(Edge.EdgeType.UNDIRECTED).toString());
//			}
		}
		System.out.println("***WE JUST STEPPED OUT***");
//		for(Node daughter: daughters){
//				System.out.println(daughter.toString());
//			if(!daughterInNetwork(daughter)){	//if new node discovered
//				System.out.println(daughter.getAddressList().toString());
//				NetworkElement daughterNode = networkApplication.getNetworkElement(daughter.getAddressList().get(0));
//				this.graph.concatenate(topology.getGraph());
//				getNeighbors(daughterNode, username, password);
//			}
//		}
		
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
		System.out.println("checking: IP address: " + address.toString());
		if(addresses.contains(address)){
			System.out.println("Found IP address - true");
			return true;
		}
		System.out.println("New IP address - false");
		return false;
		
	}
}


