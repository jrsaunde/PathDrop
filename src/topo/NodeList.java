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
package topo;

import java.util.ArrayList;

/** This class keeps a list of nodes in the topology that is used for displaying in the GUI
 * 
 */
public class NodeList {

	private ArrayList<GuiNode> nodes;
	private String 				newLine 				= System.getProperty("line.separator");
	
	/**
	 * Constructor method for NodeList. This method will create the blank list
	 */
	public NodeList(){
		this.nodes = new ArrayList<GuiNode>();
		System.out.println("Created NodeList");
	}
	
	/**
	 * This method adds a node with the hostname to the master NodeList
	 * @param name - String of the router hostname, used for the label as well as the ID number
	 */
	public void addNode(String name){
		this.nodes.add(new GuiNode(name,name));
	}
	/**
	 * This method will return all the current nodes in the topology that have been added the the NodeList.
	 * 
	 * @return String of all nodes, in the Cytoscape.js format for use in the topology window 
	 */
	public String printNodes(){
		String output = "elements: { " + newLine + "	nodes: [" + newLine;
		int i =1;
		for(GuiNode node: this.nodes){
			if(i++ < this.nodes.size()){
				output = output + "		" + node.getNode() + "," + newLine;
			}else{
				output = output + "		" + node.getNode() + newLine;
			}
		}
		output = output + "	]," + newLine + "	edges: [" + newLine;
		return output;
	}
}
