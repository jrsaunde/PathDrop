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



import guiFX.LogBox;
import vty.VTYSession;

/**
 * This is a test function for development. It will open a VTY connection to router, and run 
 * "sh ip int br | i 0/0" and "sh ip int br" and then close the connection
 * 
 *
 */
public class VTYtest {
	public static void main(String args[]){
		String start = "10.192.10.110";
		String username = "cisco";
		String password = "cisco";
		LogBox logBox = new LogBox(null, null);
		try{
			System.out.println("Connecting to " + start);
		
			VTYSession vty = new VTYSession(start, username, password, logBox);
			vty.open();
			System.out.println(vty.write("sh ip int br | i 0/0"));
			
			System.out.println(vty.write("sh ip int br"));
			
			vty.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}
}