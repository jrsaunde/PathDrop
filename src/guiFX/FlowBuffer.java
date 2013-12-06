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
package guiFX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import topo.ConnectionList;

//private Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
/**
 * The FlowBuffer class will hold a master list of packets we have seen in the network, as reported from the DPSS
 * service in the C backend
 *
 */
public class FlowBuffer {
	private static Map<Integer,List<String>> synchronizedMap = Collections.synchronizedMap(new HashMap<Integer, List<String>>());
	static ConnectionList connections;
	
	/**
	 * The constructor method for FlowBuffer, which initializes the reference to the ConnectionList
	 * @param _connections - Reference to the ConnectionList object
	 */
	public FlowBuffer(ConnectionList _connections){
		FlowBuffer.connections = _connections;
		}
	
	/**
	 * This method will add a packet to the FlowBuffer, when we see it leave a router
	 * @param ID - unique ID number of the packet
	 * @param value - List<String> {Router hostname, Router interface name}
	 */
	public static void addToBuffer(int ID, List<String> value){
		synchronized(synchronizedMap){
			if(synchronizedMap.containsKey(ID)){
				//Do we care?
			}else if((value.get(0).equals("Router1")) && (value.get(1).equals("GigabitEthernet0/0")) ){
				//System.out.println("packet: " + ID + " is leaving the network from " + value.get(0) + "[" + value.get(1) + "]");
			}else{
			//System.out.println("Added " + ID + " from " + value.get(0) + "[" + value.get(1) + "]");
			synchronizedMap.put(ID, value);
			connections.addPacketOut(value.get(0), value.get(1));
			}
		}
		return;
	}
	
	/**
	 * This method will remove a packet from the Flowbuffer, when we see it come into a router
	 * @param ID - ID number of each packet
	 * @param value - List<String> {Router hostname, Router interfaceName}
	 */
	public static void removeFrombuffer(int ID, List<String> value){
		synchronized(synchronizedMap){
			if(synchronizedMap.containsKey(ID)){
				//System.out.println("Removed " + ID + " from " + name);
				if((value.get(0).equals("Router1")) && (value.get(1).equals("GigabitEthernet0/0")) ){

				}else{
					synchronizedMap.remove(ID);
					connections.addPacketIn(value.get(0), value.get(1));
				}
			}
			return;
		}
	}
	/**
	 * This method checks the size of the FlowBuffer
	 * @return (int) the number of objects in the FlowBuffer
	 */
	public static int getSize(){
		return synchronizedMap.size();
	}
	
	/**
	 * This method will print the buffer to the console in string format
	 */
	public static void printBuffer(){
		System.out.println(synchronizedMap.toString());
		return;	
	}
	
	/**
	 * This method will walk through the FlowBuffer and print out the current FlowBuffer contents
	 */
	@SuppressWarnings("rawtypes")
	public static void getItem(){
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("~~~~~~~~~~~~Current Buffer Contents~~~~~~~~~~~~");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		Iterator it = synchronizedMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			System.out.println(pairs.getKey() + " = " + pairs.getValue());
			//it.remove();
			}
	}
	
	/**
	 * This method will check the loss of objects in the FlowBuffer
	 * **UNUSED at the moment
	 * @return - ArrayList<String> of packets that were lost
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList<String> checkLoss(){
		ArrayList<String> contents = new ArrayList<String>();
		Iterator it = synchronizedMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			contents.add(pairs.getValue().toString());
			//System.out.println(pairs.getKey() + " = " + pairs.getValue());
			//it.remove();
			}
		
		return contents;
		
	}
}
