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
package tests;

import guiFX.FlowBuffer;
import guiFX.LogBox;

import java.util.ArrayList;

import topo.ConnectionList;
import datapath.NodePuppet;

/**
 * This is a test class for developers, testing the C ProgramNode program from Java using JNI.
 * It will program 3 nodes to watch for traffic 
 *
 */
public class JNITest{
	public static void main(String args[]){
		
		ConnectionList list = new ConnectionList();
		ArrayList<NodePuppet> puppetList = new ArrayList<NodePuppet>();
		FlowBuffer buffer = new FlowBuffer(list);
		LogBox logBox = new LogBox(null, null);
		//String[] sArray = {"10.192.10.120", "10.192.40.140"};
		//puppetList.add(new NodePuppet(sArray, username, password, protocol, sourceIP, sourcePort, destIP, destPort));
		//puppetList.add(new NodePuppet("10.192.40.140", "cisco", "cisco", 6, "10.192.1.1", 0, "10.192.40.140", 80));
		puppetList.add(new NodePuppet("10.192.10.120", "cisco", "cisco", 6, "192.168.56.1", 0, "10.192.40.140", 80, buffer, true, logBox));
		puppetList.add(new NodePuppet("10.192.10.110", "cisco", "cisco", 6, "192.168.56.1", 0, "10.192.40.140", 80, buffer, true, logBox));
		puppetList.add(new NodePuppet("10.192.40.140", "cisco", "cisco", 6, "192.168.56.1", 0, "10.192.40.140", 80, buffer, true, logBox));		
		
		for(NodePuppet puppet: puppetList){
			new Thread(puppet).start();
		}
		int last_size = 0;
		while(true){
			try{
				Thread.sleep(3000);
				if(FlowBuffer.getSize() != last_size){
					FlowBuffer.printBuffer();
				}
				//System.out.println(buffer.getSize());
//				Iterator it = map.keySet().iterator();
//				while(it.hasNext()){
//					int key = (int) it.next();
//					String value = map.get(key).toString();
//					System.out.println(key + " " +value);
//					
//				}
//				System.out.println("");
				//System.out.println(map);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		//System.out.println("In Java, pktLoss is " + puppetList.get(0).pktLoss);
		//puppet.ProgramNode(start, username, password, protocol, sourceIP, sourcePort, destIP, destPort);
		//System.out.println("In Java, int is " + puppet.number);
		//System.exit(0);

		
	}
}
