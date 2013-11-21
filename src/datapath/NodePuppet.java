package datapath;


public class NodePuppet {
	static{
		System.loadLibrary("ProgramNode");
	}
	
	public int number = 88;
	public String message= "Hello from Java";
	private String errBuf = "";
	
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
	
	public static void main(String[] args){
		new NodePuppet().ProgramNode("10.192.10.110", "cisco", "cisco", 6, "192.168.56.1", 0, "10.192.40.140", 80);		//invoke the native method
		
	}
}
