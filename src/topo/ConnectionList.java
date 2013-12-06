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
import java.util.HashMap;

/** This class holds the information for all the connections in the network 
 *  
 */
public class ConnectionList {

	private ArrayList<GuiConnection> connections;
	private String 				newLine 				= System.getProperty("line.separator");
	private  HashMap<Integer,Integer>	list;
	private int connectionNumber;
	
	
	/**
	 * Constructor method for ConnectionList. The will create the blank list and Hashmap for indexes, and initalize
	 * the counter of connecitons
	 */
	public ConnectionList(){
		this.connections = new ArrayList<GuiConnection>();
		this.list = new HashMap<Integer,Integer>();
		this.connectionNumber = 0;
		System.out.println("Created ConnectionList");
	}
	
	/**
	 * This method will add a connection to the ConnectionList based on the input parameters, then increment the 
	 * number of connections. It will initalize the packet loss of this connection to 0%, as well as add a entry in 
	 * the hashmap of indexes for both the source and destination router/inferface pair
	 * @param srcRouter - String of the source Router hostname
	 * @param srcInterface - String of the source router interface
	 * @param dstRouter - String of the destination router hostname
	 * @param dstInterface - String of the destination router interface
	 */
	public void addConnection(String srcRouter, String srcInterface, String dstRouter, String dstInterface){
		GuiConnection connect = new GuiConnection(srcRouter, srcInterface, dstRouter, dstInterface, 0);
		this.list.put((srcRouter + "||" + srcInterface).hashCode(), this.connectionNumber);
		this.list.put((dstRouter + "||" + dstInterface).hashCode(), this.connectionNumber);
		this.connections.add(this.connectionNumber, connect);

		this.connectionNumber+=1;
		return;
	}
	
	/**
	 * This method will return all the connections in the ConnectionList in Cytoscape.js format
	 * @return String of all the connections and packetloss in the Cytoscape.js format
	 */
	public String printConnections(){
		String output = "";
		int j =1;
		for(GuiConnection connect : this.connections){
			if(j++ < this.connectionNumber){
				output = output + "		" +connect.getConnection() + "," + newLine;
			}else{
				output = output + "		" +connect.getConnection() + newLine;
			}
		}
		output = output + "	]" + newLine +    "  }" + newLine;
		return output;
	}
	
	/**
	 * This method will return a string of each connections packetloss, only to be used for informational/logging
	 * purposes
	 * @return String of information for each connection 
	 */
	public String printLoss(){
		String result = "";
		for(GuiConnection connect : this.connections){
			result += (connect.getInfo() + "\n");
		}
		return result;
	}
	
	/**
	 * This method increments the PacketIn counter for the specified connection
	 * @param Router - String of the router's hostname
	 * @param Interface - String of the router's interface
	 */
	public void addPacketIn(String Router, String Interface){
		//System.out.println(("in with" + Router + "||" + Interface + (Router + "||" + Interface).hashCode()));
		int index = this.list.get((Router + "||" + Interface).hashCode());
		this.connections.get(index).packetIn();
		return;
	}
	/**
	 * This method increments the PacketOut counter for the specified connection
	 * @param Router - String of the router's hostname
	 * @param Interface - String of the router's interface
	 */
	public void addPacketOut(String Router, String Interface){
		//System.out.println(("out with" + Router + "||" + Interface));
		int index = this.list.get((Router + "||" + Interface).hashCode());
		this.connections.get(index).packetOut();
		return;
	}
	
	
}
