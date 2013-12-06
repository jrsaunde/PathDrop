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
package datapath;

import guiFX.Browser;
import guiFX.FlowBuffer;
import guiFX.LogBox;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javafx.application.Platform;
import topo.ConnectionList;
import topo.NodeList;

/**
 * This class is for the DPSS service to monitor each router and to watch for the specified traffic flow
 *
 */
public class TrafficWatch implements Runnable{

	public ConnectionList connections;
	public NodeList nodes;
	public Browser browser;
	public ArrayList<String> devices;
	public ArrayList<NodePuppet> puppetList;
	private String 				newLine 				= System.getProperty("line.separator");
	
	//FlowBuffer buffer;
	FlowBuffer buffer;
	LogBox logBox;
	public Boolean run;
	
	private int protocol;
	private String sourceIP;
	private int sourcePort;
	private String destIP;
	private int destPort;
	
	/**
	 * Constructor for the Traffic watch object
	 * @param nodes - reference to the NodeList object
	 * @param connections - reference to the ConnectionList object
	 * @param _logBox - reference to the Logbox object
	 * @param browser - reference to the Browser object
	 * @param devices - ArrayList<String> of the IP addresses of each router in the network
	 * @param _protocol - String of the protocol to watch for 
	 * @param _sourceIP - String of the source IP Address
	 * @param _sourcePort - (int) of the source port number
	 * @param _destIP - String of the destination IP Address
	 * @param _destPort - (int) of the destination port number
	 */
	public TrafficWatch(NodeList nodes,
						ConnectionList connections,
						LogBox _logBox,
					    Browser browser, 
					    ArrayList<String> devices, 
					    String _protocol,
					    String _sourceIP,
					    int _sourcePort,
					    String _destIP,
					    int		_destPort){
		
		LogBox.println("Hello from Traffic Watch");
		
		this.browser = browser;
		this.connections = connections;
		this.nodes = nodes;
		this.devices = devices;
		this.puppetList = new ArrayList<NodePuppet>();
		this.run = true;
		this.sourceIP = _sourceIP;
		this.sourcePort = _sourcePort;
		this.destIP = _destIP;
		this.destPort = _destPort;
		this.buffer = new FlowBuffer(connections);
		String proto = _protocol.toLowerCase();
		//Protocol
		switch(proto){
		case "icmp" : this.protocol = 1; this.sourcePort = 0; this.destPort = 0; break;
		case "igmp" : this.protocol = 2; break;
		case "tcp" : this.protocol = 6; break;
		case "egp" : this.protocol = 8; break;
		case "igrp" : this.protocol = 9; break;
		case "udp" : this.protocol = 17; break;
		case "rsvp" : this.protocol = 46; break;
		case "gre" : this.protocol = 47; break;
		case "ah" : this.protocol = 51; break;
		default : this.protocol = 256; break;
		

		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		LogBox.println("Starting Traffic Watch");
		LogBox.println(this.devices.toString());

		for(String node : devices){
			LogBox.println("Adding " + node);
			puppetList.add(new NodePuppet(node, "cisco", "cisco", protocol, sourceIP, sourcePort, destIP, destPort, buffer, run, logBox ));
		}
		

			try {
				for(NodePuppet puppet: puppetList){
					new Thread(puppet).start();
					Thread.sleep(2000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LogBox.println(e.toString());
			}
			
			while(this.run){
				
				try {
					Thread.sleep(10000);
				/*	//FlowBuffer.getItem();
					//ArrayList<String> contents = FlowBuffer.checkLoss();
					//System.out.println("Buffer is ");
					//for(String packet: contents){
						//System.out.println(packet);
						//String[] parts = packet.split(",");
						//System.out.println("Lost packet from " + parts[0] +" on interface " + parts[1]);
					}
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + newLine +
									   "~~~~~~~~~~~~Current Gui Connections~~~~~~~~~~~~" + newLine +
									   "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					for(GuiConnection connect : this.connections){
						System.out.println(connect.getInfo());
					}*/
					//System.out.println(nodes.printNodes() + newLine + connections.printConnections());
					//System.out.println(connections.printConnections());
					LogBox.println("~~~~~~~~~~~~~Current Loss~~~~~~~~~~~~~");
					LogBox.println(connections.printLoss()); //TODO:Move to LogBox
					
					Platform.runLater(new Runnable(){
						@Override
						public void run(){
							try {
								browser.loadTopo(nodes.printNodes() + newLine + connections.printConnections());
							
								//System.out.println(connections.printConnections());
								//browser.loadTopo(getJsonTopo());
							} catch (MalformedURLException | FileNotFoundException e) {
								// TODO Auto-generated catch block
								LogBox.println(e.toString());
							}
						}
					});
					
				} catch (InterruptedException  e) {
					// TODO Auto-generated catch block
					LogBox.println(e.toString());
				}

			}
		for(NodePuppet puppet : puppetList){
			puppet.stop();
		}
		LogBox.println("Finished with Traffic watch");
	}

}
