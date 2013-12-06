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
package vty;

import guiFX.LogBox;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.vty.VtyService;

/**
 * VTY class - this class is used to open a VTY session to a router
 * 
 *
 */
public class VTYSession {

	private InetAddress address;
	private String username;
	private String password;
	
	public VtyService vtyService;
	LogBox logBox;
	
	/**
	 * Constructor method for VTY session.
	 * This will convert IP address to InetAddress and set local variables
	 * @param ip - IP address of router to connect to in string format
	 * @param user - username to login to router
	 * @param pass - password to login to router
	 * @param _logBox - global reference to logBox for logging
	 */
	public VTYSession(String ip, String user, String pass, LogBox _logBox) {

		this.logBox = _logBox;
		try {
			this.address = InetAddress.getByName(ip);
			this.username = user;
			this.password = pass;
		} catch (UnknownHostException e) {
			LogBox.println(e.toString());
		}       
        	}

	/**
	 * This method will open a VTY session to a previously constructed VTYSession object.
	 * It will open a VTY session and run the "whoami" command in order to get the current prompt
	 * @return returns a String with the current prompt for this device
	 */
	public String open() {
		NetworkApplication networkApplication = NetworkApplication.getInstance();
		NetworkElement networkElement;
		String prompt = "";
		try {
			networkElement = networkApplication.getNetworkElement(this.address);
			networkElement.connect(this.username, this.password);
			this.vtyService = new VtyService(networkElement);
	        this.vtyService.open();
		
	        //Get initial prompt
			this.write("whoami");
			prompt = this.vtyService.getParserState().getPrompt();
		} catch (Exception e) {
			LogBox.println(e.toString());
		}
		
		return prompt;

	}

	/**
	 * This method sends the input command String to the router and receives back the current prompt 
	 * as well as the results from the command that was run 
	 * @param command - String of the command that you want to send
	 * @return String[] {prompt after the command, result from the command}
	 */
	public String[] write(String command) {

		String result="";
		String prompt="";
		try {
			result = this.vtyService.write(command);
			prompt = this.vtyService.getParserState().getPrompt();
		} catch (Exception e) {
			LogBox.println(e.toString());
		}
		
		return new String[]{prompt,result};
		
	}
	
	/**
	 * This method closes the connection to the router and destroys the VTYService for this VTYSession 
	 */
	public void close(){
		try {
			this.vtyService.close();
	        this.vtyService.destroy();
		} catch (Exception e) {
			LogBox.println(e.toString());
		}
		return;
	}
}
