/* PathDrop - Topology Visualizer and Packet Loss Indicator
 * Copyright (c) 2013 
 * Jamie Saunders <jrsaunde@ncsu.edu>
 * Thomas Paradis <tmparadi@ncsu.edu>
 * Hank Liu <hliu9@ncsu.edu>
 * Ryan Coble <rlcoble@ncsu.edu>
 * Isaac Choe <ichoe@ncsu.edu>
 * 
 * All rights reserved
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */
package discovery;

import guiFX.Browser;
import guiFX.LogBox;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javafx.application.Platform;
import javafx.scene.control.Button;
import topo.ConnectionList;
import topo.GuiConnection;
import topo.GuiNode;
import topo.NodeList;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionConfig;
import com.cisco.onep.element.SessionHandle;
import com.cisco.onep.element.SessionConfig.SessionTransportMode;
import com.cisco.onep.interfaces.InterfaceFilter;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.topology.Edge;
import com.cisco.onep.topology.Graph;
import com.cisco.onep.topology.Node;
import com.cisco.onep.topology.Topology;
import com.cisco.onep.topology.Topology.TopologyType;

/**
 * This class will discovery the specificed network and display it on the GUI
 *
 */
public class NetworkDiscovery implements Runnable {

	public Graph 				graph 					= null;
	public List<InetAddress> 	addresses;
	public NetworkApplication 	discoveryApplication 	= NetworkApplication.getInstance();
	public SessionConfig 		nodeConfig;		
	public Collection<String> 	nodeNames 				= new TreeSet<String>(Collator.getInstance());
	public ArrayList<String> 	connectionStrings 		= new ArrayList<String>();
	public ArrayList<String>	paths 					= new ArrayList<String>();
	private String 				newLine 				= System.getProperty("line.separator");

	Browser						browser;
	LogBox						logBox;
	ArrayList<String> 			discoveredIPs;
	ArrayList<String>			nodeIPs;
	ConnectionList				guiConnections;
	NodeList					guiNodes;
	NetworkElement				node;
	InetAddress					startAddress;
	InetAddress					destAddress;
	String						username;
	String						password;
	
	Button [] buttons;


/**
 * Discovers the network from the startAddress and generates a graph object
 * @param browser - Global reference to Browser object to display network topology
 * @param discoveredIPs - Global reference to ArrayList<String> of discovered IP addresses
 * @param nodeIPs - 
 * @param nodes - Global reference to NodeList of nodes
 * @param connections - Global reference to ConnectionList of all connections
 * @param _logBox - Global reference to the LogBox for logging
 * @param buttons - Button[] reference to buttons in the Gui
 * @param srcIP - String of the source IP address
 * @param dstIP - String of the destination IP address
 * @param username - the username for all devices in the network
 * @param password - the password for all devices in the network
 * @throws Exception
 */
	public NetworkDiscovery(Browser browser, ArrayList<String> discoveredIPs, ArrayList<String> nodeIPs, NodeList nodes, ConnectionList connections, LogBox _logBox, Button [] buttons, String srcIP, String dstIP, String username, String password) throws Exception{

		/*Initialize globals*/
		this.browser = browser;
		this.discoveredIPs = discoveredIPs;
		this.nodeIPs = nodeIPs;
		this.logBox = _logBox;
		this.guiConnections = connections;
		this.guiNodes = nodes;
		this.startAddress = InetAddress.getByName(srcIP);
		this.destAddress = InetAddress.getByName(dstIP);
		this.buttons = buttons;
		this.username = username;
		this.password = password;
		this.nodeConfig = new SessionConfig(SessionTransportMode.SOCKET);
		this.nodeConfig.setPort(OnepConstants.ONEP_PORT);
		this.addresses = new ArrayList<InetAddress>();		
		
		return;	
	}
	
	/** This will recursively run through each router in the network and get a list of neighbors and add those neighbors 
	 * to the master topology
	 * 
	 * @param node				The network element we are checking
	 * @param username			The username for the device
	 * @param password			The password for the device
	 * @return ONEpk Graph object
	 * @throws OnepException
	 */
	private Graph getNeighbors(NetworkElement node, String username, String password) throws OnepException{
		/*Connect to the node */		
		@SuppressWarnings("unused")
		SessionHandle nodeSession = node.connect(username, password, this.nodeConfig);
		
		/* Add all of the local IP addresses to our master list */
		List <NetworkInterface> interfaceList = node.getInterfaceList(new InterfaceFilter());
		
		int interfaceNumber = 0;
		for(NetworkInterface inter : interfaceList){				
			if(inter.getAddressList().size() > 0 ){
				if(interfaceNumber++ == 0){
					this.nodeIPs.add(inter.getAddressList().get(0).toString().substring(1));
				}
				this.addresses.add(inter.getAddressList().get(0));
				this.discoveredIPs.add(inter.getAddressList().get(0).toString().substring(1));

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
			
			InetAddress neighborIP = edge.getTailNodeConnector().getAddressList().get(0);
			/* If we haven't seen this IP address before, we haven't been to this device, we need to go to it*/
			if(!addressInNetwork(neighborIP)){
				/*Connect to neighbor and run recursive discovery*/
				NetworkElement daughterNode = discoveryApplication.getNetworkElement(neighborIP);
				Graph daughterGraph = getNeighbors(daughterNode, username, password);
				
				/*Disconnect from the daughterNode NetworkElement since we are done*/
				daughterNode.disconnect();
				
				/*Add the graph we got from the neighbor to the global graph*/
				this.graph.concatenate(daughterGraph);
			}
		}
		/*Return our graph (not our neighbors)*/
		return topology.getGraph();
	}
		
	/**
	 * Checks to see if the IP address is in our master list of addresses
	 * @param address - We check to see if this address in the global blacklist of addresses
	 * @return True if we already have seen this address
	 */
	private boolean addressInNetwork(InetAddress address){
		
		if(this.addresses.contains(address)){
			return true;
		}
		return false;
		
	}
	
	/**
	 * Prints the discovered network topology and saves it to the ConnectionList and NodeList objects
	 * @throws Exception
	 */
	private void printNetwork() throws Exception{
		
		LogBox.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		LogBox.println("Network Summary");
		LogBox.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		
		/*Get a list of the device names*/
		List<Node>  nodes = this.graph.getNodeList();

		for(Node device: nodes){
			GuiNode node = new GuiNode(device.getName(), device.getName());
			//System.out.println(node.getNode());
			this.nodeNames.add(node.getNode());
			this.guiNodes.addNode(device.getName());
		}
		
		/*Print out all of the connections*/
		List<Edge> edges = this.graph.getEdgeList(Edge.EdgeType.UNDIRECTED);
		
		int pktLoss = -10;
		for(Edge edge: edges){
			pktLoss+=10;
//			GuiConnection connection = new GuiConnection(edge.getHeadNode().getName(),
//														 shortenName(edge.getHeadNodeConnector().getName()),
//														 edge.getTailNode().getName(),
//														 shortenName(edge.getTailNodeConnector().getName()),
//														 pktLoss);
			GuiConnection connection = new GuiConnection(edge.getHeadNode().getName(),
					 edge.getHeadNodeConnector().getName(),
					 edge.getTailNode().getName(),
					 edge.getTailNodeConnector().getName(),
					 pktLoss);

			//System.out.println(connection.getConnection());
			//this.connections.add(connection);
			this.guiConnections.addConnection(edge.getHeadNode().getName(), edge.getHeadNodeConnector().getName(), edge.getTailNode().getName(), edge.getTailNodeConnector().getName());
			this.connectionStrings.add(connection.getConnection());
		}
		
		

		//Generate JSON for GUI
 		//generateJson();
	}
	
/**
 * Prepare topology for the JavaFX Gui
 */
	public String getJsonTopo(){
		String devices = "elements: { " + newLine + "	nodes: [" + newLine;

		/*Prepare nodes */
		int i =1;
		for(String node : this.nodeNames){
			if(i++ < this.nodeNames.size()){
				devices = devices + "		" + node + "," + newLine;
			}else{
				devices = devices + "		" + node + newLine;
			}
		}
		
		devices = devices + "	]," + newLine + "	edges: [" + newLine;

		/* Prepare connections */
		devices = devices + this.guiConnections.printConnections();
		/*int j = 1;
		for(String connection: this.connectionStrings){
			if(j++ < this.connectionStrings.size()){
				devices = devices + "		" +connection + "," + newLine;
			}else{
				devices = devices + "		" +connection + newLine;
			}
		}*/
		//devices = devices + "	]" + newLine +    "  }" + newLine;
		
		return(devices);
	}
	
	/**
	 * This method will return all the nodes in the topology
	 * @return - all the nodes in the Cytoscape.js format
	 */
	public String getNodes(){
		String devices = "elements: { " + newLine + "	nodes: [" + newLine;

		/*Prepare nodes */
		int i =1;
		for(String node : this.nodeNames){
			if(i++ < this.nodeNames.size()){
				devices = devices + "		" + node + "," + newLine;
			}else{
				devices = devices + "		" + node + newLine;
			}
		}
		
		devices = devices + "	]," + newLine + "	edges: [" + newLine;
		
		return(devices);
	}
	
	/**
	 * This will find all the paths from start to destination
	 * @param srcIP - IP address of the start node
	 * @param dstIP  - IP address of the destination node
	 * @throws Exception 
	 */
	public void findPaths(String srcIP, String dstIP) throws Exception{

		InetAddress start = InetAddress.getByName(srcIP);
		InetAddress dest  = InetAddress.getByName(dstIP);
		//Print all possible routes
		Node startNode 	= null;
		Node destNode 	= null;
		List<Edge> edges = this.graph.getEdgeList(Edge.EdgeType.DIRECTED);
		
		for(Edge edge: edges){
			
			if(edge.getHeadNodeConnector().getAddressList().contains(start)){
				startNode = edge.getHeadNode();
			}else if(edge.getHeadNodeConnector().getAddressList().contains(dest)){
				destNode = edge.getHeadNode();
			}
		}
		LogBox.println("Routes from " + startNode.getName() + " to " + destNode.getName());
		PathDiscovery pathTrace = new PathDiscovery(this.graph, startNode, destNode);
		pathTrace.getPaths();
	}
	
	/**
	 * Shortens the interface name from "GigabitEthernet0/0" -> "Gig0/0" etc
	 * @param longName
	 * @return shortened name
	 */
	@SuppressWarnings("unused")
	private String shortenName(String longName){
		
		if(longName.startsWith("Eth")){
			longName = "Eth"+longName.substring(8);
		}else if(longName.startsWith("Fas")){
			longName = "Fa"+longName.substring(12);
		}else if(longName.startsWith("Gig")){
			longName = "Gi"+longName.substring(15);
		}else if(longName.startsWith("Ten")){
			longName = "Ten"+longName.substring(18);
		}
		return longName;

		}
	
	public void writeTopology(){
		
	}
	

	@Override
	public void run(){
		LogBox.println("NetworkDiscovery Thread: " + Thread.currentThread().getName());
		
		//TODO: Testing code (keeping track of startTime)
		long startTime = System.nanoTime();	

		try {
			//Set up the Start Node
			this.node = discoveryApplication.getNetworkElement(startAddress);
			
			//Run recursive network discovery
			getNeighbors(node, username, password);
			
			//Print out the Global graph
			printNetwork();
		} catch (Exception e) {
			LogBox.println("NetworkDiscovery Failed");
			LogBox.println(e.toString());
			
			for (Button button: buttons)
				button.setDisable(false);
		}

		/*// simulating the discoverying *delay
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			System.out.println("Could not sleep");
		} */
		
		/*TODO: Testing code for timing the program execution time*/
		long endTime = System.nanoTime();
		
		LogBox.println("Total Program took: " + ((endTime - startTime)/(Math.pow(10, 9))) + " seconds (" + (endTime - startTime) + ") ns");
		
		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				try {
					browser.loadTopo(getJsonTopo());
					for (Button button: buttons)
						button.setDisable(false);
					//browser.loadTopo(getJsonTopo());
				} catch (MalformedURLException | FileNotFoundException e) {
					// TODO Auto-generated catch block
					LogBox.println(e.toString());
				}
			}
		});
		
	}
}
