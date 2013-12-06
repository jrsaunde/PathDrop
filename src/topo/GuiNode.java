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

/**
 * This is the class for generating the javascript string for each node in the topology
 * 
 *
 */
public class GuiNode {
	private String name;
	private String id;
	
	/**
	 * This will generate a string that will be passed to the JavaFX GUI
	 * @param id - The internal ID for each router, used to reference connections
	 * @param name - the name of the router, this will show up on the topology
	 */
	public  GuiNode(String id, String name) {
		this.name	= name;
		this.id  	= id;
	}

	/**
	 * This method will return the node in Cytoscape.js format
	 * @return String of the node's information in Cytoscape.js formate
	 */
	public String getNode(){
		return ("{ data: { id: '" +
					this.id + "', name: '" +
					this.name + "', weight: 65, faveColor: '#0066CC', faveShape: 'ellipse' } }");
	}
}
