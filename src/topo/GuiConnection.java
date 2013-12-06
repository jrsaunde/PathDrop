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

/** This class is used to hold information about a connection in the topology.
 *  There should be one GuiConnection for each physical connection between two routers
 * 
 */
public class GuiConnection {
	private String 	sourceNode;
	private String 	sourceInterface;
	private String 	destNode;
	private String 	destInterface;
	@SuppressWarnings("unused")
	private int		packetLoss;		//currently unused in this release
	private int		packetsIn;
	private int		packetsOut;
	
	/**
	 * Constructor for the GuiConnection object
	 * @param srcRouter - String of source router's hostname
	 * @param srcInt - String of source router's interface
	 * @param dstRouter - String of destination router's hostname
	 * @param dstInt - String of source router's interface
	 * @param pktLoss - (int) Starting packetloss percentage (should always be 0)
	 */
	public  GuiConnection(String srcRouter, String srcInt, String dstRouter, String dstInt, int pktLoss) {
		this.sourceNode = srcRouter;
		this.sourceInterface =	srcInt;
		this.destNode		 = 	dstRouter;
		this.destInterface	 = 	dstInt;
		this.packetLoss		 =	pktLoss;
		this.packetsIn = 0;
		this.packetsOut = 0;
	}
	
	/**
	 * This method is used to get the current packet loss of a connection based on the difference between the 
	 * packetIn and packetOut counters for each connection
	 * @return (int) - Percentage of packet loss (0-100%)
	 */
	public int getLoss(){
		double lossIn = (((double) this.packetsIn - (double) this.packetsOut) / ((double) this.packetsIn))*100;
		double lossOut = (((double) this.packetsOut - (double) this.packetsIn) / ((double) this.packetsOut))*100;
		if((this.packetsIn != 0) && (this.packetsOut != 0)){
			if(this.packetsIn > this.packetsOut){
				return (int) lossIn;
			}else{
				return (int) lossOut;
			}
		}else{
			return 0;
		}
	}
	
	/**
	 * This method simply checks to see if the connection has seen any packets by checking if the
	 * packetsIn or packetsOut counters are greater than 0
	 * @return boolean - true if the connection has seen a packet in OR out
	 */
	public boolean isActive(){
		if((this.packetsIn > 0) || (this.packetsOut > 0)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * This method is used to increase the packetLoss by 1%. Unused at the moment
	 */
	public void increaseLoss(){
		this.packetLoss +=1;
	}
	
	/**
	 * This method is used to increase the packetsIn counter by 1
	 */
	public void packetIn(){
		//System.out.println(this.sourceNode + " to " + this.destNode + " packetIn");
		this.packetsIn++;
	}
	/**
	 * This method is used to increase the packetsOut counter by 1
	 */
	public void packetOut(){
		//System.out.println(this.sourceNode + " to " + this.destNode + " packetOut");
		this.packetsOut++;
	}
	/**
	 * This method is used to get the current connection's status in Cytoscape.js format.
	 * It will check to see if the connection is active and if so what the packet loss is
	 * @return String - current connection in Cytoscape.js format
	 */
	public String getConnection(){
		String connection = "''";
		String color = "";

		if(this.isActive()){
			color = getHue(this.getLoss());
			if(this.getLoss() > 0){
				connection ="'   "+ this.getLoss() + "%'";
			}
		}
		else{
			color= grayedOut();
		}
		
				
				
		return( "{ data: { source: '" +
						  this.sourceNode + "', target: '" +
						  this.destNode + "', label: " +
						  connection +
						  ", faveColor: " +
						  color + ", strength: 70 " 
						   +" } }");
		
		
	}
	
	/**
	 * This method is used for logging and information only. It will display the current connection information
	 * @return String - connection information 
	 */
	public String getInfo(){
		return( "Source: " + this.sourceNode + " [" + this.sourceInterface
				+ "] Dest: " + this.destNode + " [" + this.destInterface
				+ "] packetsIn: " + this.packetsIn + " packetsOut: " + this.packetsOut + " loss: " + this.getLoss());
	}
	
	/**
	 * This method calculates the color the connection should be based on the loss percentage. It takes that loss number
	 * and converts it to a log scale of colors in the HSL format for the Cytoscape.js topology
	 * @param loss - (int) percentage between 0-100% of packetLoss
	 * @return - String of the conneciton's color in Cytoscape.js format
	 */
	 public static String getHue(int loss) {
		 int max = 360;
		 int min = 140;
		 
		 // Logarithm scale
		 double scale = Math.log10(loss+1)/2;
				 
		 // linear scale
		 // double scale = (double) ratio/100;
		 
		 int hue = (int) Math.round((max-min)*scale) + min;
		 
		 // boundary check
		 if (hue>max)
			 hue = max;
		 if (hue<min)
			 hue = min;
		 //System.out.print("Ratio: " + loss + "; Hue: " + hue);
		 return "'hsl("+ (int) hue+", 95%, 76%)'";
	 }
	 
	 /**
	  * This method is used to set the color of a connection to grey. It is called initally in Network Discovery
	  * when no traffic has been monitored yet
	  * @return String of the connection color in Cytoscape.js format
	  */
	 public static String grayedOut(){
		 return "'hsl(360, 0%, 50%)', active: 0.25";
	 }
}
