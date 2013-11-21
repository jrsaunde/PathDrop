package tests;

import java.net.InetAddress;

import discovery.NetworkDiscovery;

public class DiscoveryTest {

	
	public static void main(String args[]){
		String start = args[0];
		String dest  = args[1];
		String username = args[2];
		String password = args[3];
		
		try{
			System.out.println("Start node is " + start +
							   " with " + username + 
							   "/" + password);
			/*InetAddress startNode 	= InetAddress.getByName(start);
			InetAddress destNode	= InetAddress.getByName(dest);*/
			NetworkDiscovery network = new NetworkDiscovery(start, dest, username, password);
			
			//Print out topology in JS format
			System.out.println(network.convertTopology());
			
		} catch (Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}
}
