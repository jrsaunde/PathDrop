package datapath;

import guiFX.FlowBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NodePuppet implements Runnable{
	static{
		System.loadLibrary("ProgramNode");
	}
	
	public int number = 88;
	public String message= "Hello from Java";
	private String errBuf = "";
	public int[] loss = {0,0,0,0,0,0,0,0,0,0};
	private String 				newLine 				= System.getProperty("line.separator");
	
	public int pktLoss;
	
	/**
	 *  Call the native C program to program a node to watch for traffic
	 * @param address - address of the node we want to program
	 * @param user - username to login
	 * @param pass - password to login
	 * @param protocol - protocol of the traffic we are looking for (TCP = 6, UDP = 17)
	 * @param source_ip - Source IP address for traffic
	 * @param source_port - Source port number for traffic 
	 * @param dest_ip - Destination IP address for traffic
	 * @param dest_port - Destination port number for traffic
	 * @return status if ok? need to double check
	 */
	public native int ProgramNode(String address,
								   String user,
								   String pass,
								   int protocol,
								   String source_ip,
								   int source_port,
								   String dest_ip,
								   int dest_port);
	private String address;
	private String user;
	private String pass;
	private int protocol;
	private String source_ip;
	private int source_port;
	private String dest_ip;
	private int dest_port;
	
	FlowBuffer buffer;
	
	
	public NodePuppet(String _address,
					   String _user,
					   String _pass,
					   int _protocol,
					   String _source_ip,
					   int _source_port,
					   String _dest_ip,
					   int _dest_port,
					   FlowBuffer buffer){
		this.address = _address;
		this.user = _user;
		this.pass = _pass;
		this.protocol = _protocol;
		this.source_ip = _source_ip;
		this.source_port = _source_port;
		this.dest_ip = _dest_ip;
		this.dest_port = _dest_port;
		this.buffer = buffer;
		
	}
	
	   
	public static void main(String[] args){
		//NodePuppet puppet = new NodePuppet("10.192.10.110", "cisco", "cisco", 6, "192.168.56.1", 0, "10.192.40.140", 80);		//invoke the native method
		//new Thread(puppet).start();
	}

	@Override
	public void run() {
		ProgramNode(this.address,
					this.user,
					this.pass,
					this.protocol,
					this.source_ip,
					this.source_port,
					this.dest_ip,
					this.dest_port);
	}
	
	public void setLoss(int index){
		this.loss[index] += 1;
		return;
	}
	
	public int getLoss(int index){
		return this.loss[index];
	}
	
	public void removeIncoming(int ID, String name){
		//if(this.buffer.containsKey(ID)){
			//this.buffer.remove(ID);
			//System.out.println("Removed " + ID + " from " + name + newLine);
			FlowBuffer.removeFrombuffer(ID, name);
		//}
	}
	
	public void sendOutgoing(int ID, String name, String intf){
		
	
		//if(this.buffer.containsKey(ID)){
			
		//}else if((name.equals("Router1")) && (intf.equals("GigabitEthernet0/0"))){
		//	System.out.println("packet: " + ID + " is leaving the network from " + name + "[" + intf + "]"+ newLine);
		//}else{
		//	System.out.println("Added " + ID + " on " + name + " " + intf + newLine);
			List<String> info = new ArrayList<String>();
			info.add(name);
			info.add(intf);
			//this.buffer.put(ID, info);
		//}
		
		FlowBuffer.addToBuffer(ID, info);
		return;

	}
}
