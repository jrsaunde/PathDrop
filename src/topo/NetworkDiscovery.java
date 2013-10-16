package topo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionConfig;
import com.cisco.onep.element.SessionConfig.SessionTransportMode;
import com.cisco.onep.element.SessionHandle;
import com.cisco.onep.topology.Edge;
import com.cisco.onep.topology.Graph;
import com.cisco.onep.topology.Node;
import com.cisco.onep.topology.Topology;
import com.cisco.onep.topology.Topology.TopologyType;

public class NetworkDiscovery {

	public Graph graph = null;
	public NetworkApplication networkApplication = NetworkApplication.getInstance();
	
	
	public static void main(String args[]) {
		String start = args[0];
		String username = args[1];
		String password = args[2];
		try {
			InetAddress startNode = InetAddress.getByName(start);
			NetworkDiscovery network = new NetworkDiscovery(startNode, username, password);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public NetworkDiscovery(InetAddress startAddress, String username, String password) {
		try {
			NetworkElement node = networkApplication.getNetworkElement(startAddress);
			getNeighbors(node, username, password);
		} catch (OnepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return;
	}
	
	private Graph getNeighbors(NetworkElement node, String username, String password) throws OnepException{
		
		SessionConfig nodeConfig = new SessionConfig(SessionTransportMode.SOCKET);
		nodeConfig.setPort(OnepConstants.ONEP_PORT);
		
		//Connect to the node
		SessionHandle nodeSession = node.connect(username, password, nodeConfig);
		
		//Get Topology object
		Topology topology = new Topology(node, TopologyType.CDP);
		
		List<Node> daughters = topology.getGraph().getNodeList();
		
		List<Edge> edges = topology.getGraph().getEdgeList(Edge.EdgeType.UNDIRECTED);
		
		for(Edge edge: edges){
			System.out.println("TailNode Hash: " + edge.getTailNode().hashCode());
			System.out.println("TailNode ConnectorHash: " + edge.getTailNodeConnector().hashCode());
			if(!daughterInNetwork(edge.getTailNode())){	//if new node discovered
				System.out.println("Daughter: " + edge.getTailNode().toString());
				System.out.println("Connecting to Daughter: " + edge.getTailNodeConnector().getAddressList().get(0).toString());
				NetworkElement daughterNode = networkApplication.getNetworkElement(edge.getTailNodeConnector().getAddressList().get(0));
				//this.graph.concatenate(topology.getGraph());
				Graph daughterGraph = getNeighbors(daughterNode, username, password);
				System.out.println(daughterGraph.getEdgeList(Edge.EdgeType.UNDIRECTED).toString());
			}
		}
		for(Node daughter: daughters){
				System.out.println(daughter.toString());
			if(!daughterInNetwork(daughter)){	//if new node discovered
				System.out.println(daughter.getAddressList().toString());
				NetworkElement daughterNode = networkApplication.getNetworkElement(daughter.getAddressList().get(0));
				this.graph.concatenate(topology.getGraph());
				getNeighbors(daughterNode, username, password);
			}
		}
		
		return null;
		
	}


	private boolean daughterInNetwork(Node daughter) {
		System.out.println("checking: daughterInNetwork");
		//System.out.println(graph);
		if(graph == null) {
			System.out.println("return false");
				return false;
		}
		System.out.println("checking: nodeList");
		List <Node> nodeList = this.graph.getNodeList();
		for (Node node : nodeList) {
			if (node.hashCode() == daughter.hashCode())
				return true;
		}
		
		
		return false;
	}
}


