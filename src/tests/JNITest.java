package tests;

import datapath.NodePuppet;

public class JNITest {
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
		
		NodePuppet puppet = new NodePuppet();
		puppet.ProgramNode(start, username, password, protocol, sourceIP, sourcePort, destIP, destPort);
		System.out.println("In Java, we have " + puppet.totalPackets + " packets with " + puppet.packetLoss + "% loss");
		System.exit(0);
	}
}
