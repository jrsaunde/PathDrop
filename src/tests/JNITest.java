package tests;

import datapath.NodePuppet;

public class JNITest {
	public static void main(String args[]){
		String start = args[0];
		String dest  = args[1];
		String username = args[2];
		String password = args[3];
		String protocol = args[4];
		
		NodePuppet puppet = new NodePuppet();
		puppet.ProgramNode(start, username, password, protocol);
		System.out.println("In Java, int is " + puppet.number);
		System.exit(0);
	}
}
