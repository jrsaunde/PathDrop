package tests;


import java.net.InetAddress;

import vty.VTYSession;
import discovery.NetworkDiscovery;

public class VTYtest {
	public static void main(String args[]){
		String start = "10.192.10.110";
		String dest = "10.192.40.140";
		String username = "cisco";
		String password = "cisco";
		try{
			System.out.println("Connecting to " + start);
			InetAddress startNode 	= InetAddress.getByName(start);
			InetAddress destNode	= InetAddress.getByName(dest);
			
			VTYSession vty = new VTYSession(start, username, password);
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