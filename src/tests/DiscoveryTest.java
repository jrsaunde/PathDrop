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


import discovery.NetworkDiscovery;

/**
 * This is testing class for developers, testing the NetworkDiscovery class. It will discover the network based on
 * the start node and display the network in String format 
 *
 */
public class DiscoveryTest {

	
	public static void main(String args[]){
		String start = args[0];
		String username = args[2];
		String password = args[3];
		
		try{
			System.out.println("Start node is " + start +
							   " with " + username + 
							   "/" + password);
			NetworkDiscovery network = new NetworkDiscovery(null, null, null, null, null, null, null, start, username, password, password);
			
			//Print out topology in JS format
			System.out.println(network.getJsonTopo());
			
		} catch (Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}
}
