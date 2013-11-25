package tests;

import java.util.ArrayList;

import datapath.NodePuppet;

public class JNITest{
	public static void main(String args[]){
		String start = args[0];
		String username = args[1];
		String password = args[2];
		int protocol = 256; //default to all
		if(args[3].equals("tcp")){
			protocol = 6;
		}else if (args[3].equals("udp")){
			protocol = 17;
		}
		String sourceIP = args[4];
		int sourcePort = Integer.parseInt(args[5]);
		String destIP = args[6];
		int destPort = Integer.parseInt(args[7]);
		
		ArrayList<NodePuppet> puppetList = new ArrayList<NodePuppet>();
		String[] sArray = {"10.192.10.120", "10.192.40.140"};
		puppetList.add(new NodePuppet(sArray, username, password, protocol, sourceIP, sourcePort, destIP, destPort));
		//puppetList.add(new NodePuppet("10.192.40.140", "cisco", "cisco", 6, "10.192.1.1", 0, "10.192.40.140", 80));
	
		
		for(NodePuppet puppet: puppetList){
			new Thread(puppet).start();
		}
		//puppet.ProgramNode(start, username, password, protocol, sourceIP, sourcePort, destIP, destPort);
		//System.out.println("In Java, int is " + puppet.number);
		//System.exit(0);

		
	}
}
