package discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.cisco.onep.topology.Edge;
import com.cisco.onep.topology.Graph;
import com.cisco.onep.topology.Node;

/**
 * The PathDiscovery class will find and print all paths from the start IP address to the destination IP address,
 * based on the Graph object passed into the class
 * 
 *
 */
public class PathDiscovery {
	public Graph graph = null;
	public ArrayList<String> paths = new ArrayList<String>();
	private Stack<String> path = new Stack<String>();
	private ArrayList<Node> onPath = new ArrayList<Node>();
	
	/**
	 * This will discovery all the possible paths from start to destination in the topology
	 * @param topology - The graph topology returned from NetworkDiscovery
	 * @param start - The start node
	 * @param dest - The destination node
	 */
	public PathDiscovery(Graph topology, Node start, Node dest){
		this.graph = topology;
		this.enumerate(start, dest);
	}
	
	/**
	 * This will find all the possible paths from the start node to the destination node
	 * based on the graph
	 * @param start - Node we start from
	 * @param dest  - Node we want to go to 
	 */
	public void enumerate(Node start, Node dest) {
        /*add node v to current path from s */
        path.push(start.getName());
        onPath.add(start);

        /* found path from s to t - currently prints in reverse order because of stack */
        if (start.equals(dest)){
        	this.paths.add(path.toString());
        	//System.out.println(path);
        } else {
        	/* consider all neighbors that would continue path with repeating a node */
        	ArrayList<Node> connected = getConnected(graph.getEdgeListByNode(Edge.EdgeType.DIRECTED, start));
            for (Node next : connected) {
                if (!onPath.contains(next)) enumerate(next, dest);
            }
        }
        
        /* done exploring from v, so remove from path */
        path.pop();
        onPath.remove(start);
	}
	
	/**
	 * This will return an ArrayList of connected nodes based on the connected edges
	 * @param connectedEdges - List of edges from the current node
	 * @return An ArrayList of connected nodes
	 */
	private ArrayList<Node> getConnected(List<Edge> connectedEdges){
		ArrayList<Node> connectedNodes = new ArrayList<Node>();
		for(int x=0; x <connectedEdges.size(); x++){
			connectedNodes.add(connectedEdges.get(x).getTailNode());
		}
		return connectedNodes;
	}
	
	/**
	 * This will print out all the paths we have discovered 
	 */
	public void getPaths(){
		for(String path: this.paths){
			System.out.println(path);
		}
	}
}
