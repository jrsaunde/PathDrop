package tests;

import guiFX.FlowBuffer;
import guiFX.LogBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import topo.ConnectionList;
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
		ConnectionList list = new ConnectionList();
		ArrayList<NodePuppet> puppetList = new ArrayList<NodePuppet>();
		//Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
		FlowBuffer buffer = new FlowBuffer(list);
		LogBox logBox = new LogBox(null, null);
		String[] sArray = {"10.192.10.120", "10.192.40.140"};
		//puppetList.add(new NodePuppet(sArray, username, password, protocol, sourceIP, sourcePort, destIP, destPort));
		//puppetList.add(new NodePuppet("10.192.40.140", "cisco", "cisco", 6, "10.192.1.1", 0, "10.192.40.140", 80));
		puppetList.add(new NodePuppet("10.192.10.120", "cisco", "cisco", 6, "192.168.56.1", 0, "10.192.40.140", 80, buffer, true, logBox));
		puppetList.add(new NodePuppet("10.192.10.110", "cisco", "cisco", 6, "192.168.56.1", 0, "10.192.40.140", 80, buffer, true, logBox));
		puppetList.add(new NodePuppet("10.192.40.140", "cisco", "cisco", 6, "192.168.56.1", 0, "10.192.40.140", 80, buffer, true, logBox));		
		
		for(NodePuppet puppet: puppetList){
			new Thread(puppet).start();
		}
		int last_size = 0;
		while(true){
			try{
				Thread.sleep(3000);
				if(FlowBuffer.getSize() != last_size){
					FlowBuffer.printBuffer();
				}
				//System.out.println(buffer.getSize());
//				Iterator it = map.keySet().iterator();
//				while(it.hasNext()){
//					int key = (int) it.next();
//					String value = map.get(key).toString();
//					System.out.println(key + " " +value);
//					
//				}
//				System.out.println("");
				//System.out.println(map);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		//System.out.println("In Java, pktLoss is " + puppetList.get(0).pktLoss);
		//puppet.ProgramNode(start, username, password, protocol, sourceIP, sourcePort, destIP, destPort);
		//System.out.println("In Java, int is " + puppet.number);
		//System.exit(0);

		
	}
}
