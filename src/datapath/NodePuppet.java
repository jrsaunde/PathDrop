package datapath;


public class NodePuppet {
	static{
		System.loadLibrary("ProgramNode");
	}
	
	public int packetLoss 		= 0;
	public long totalPackets 	= 0;
	
	public String message		= "Hello from Java";
	private String errBuf 		= "";
	
	/**
	 * This method will be called from the ProgramNode library in C, to update the global variable
	 * This number is calculated in C by pktLoss = (lostPackets)/(totalPackets)
	 * @param pktLoss - percentage of packetloss (number from 0 to 100)
	 */
	private void setPacketLoss(int pktLoss){
		this.packetLoss = pktLoss;
		System.out.println("In Java, Packet loss is " + this.packetLoss);
	}
	/**
	 * This method will be called from the ProgramNode library in C, to update the
	 * total amount of packets we have seen come into the node
	 * @param totalPkts - long number that represents packets in
	 */
	private void setTotalPackets(long totalPkts){
		this.totalPackets = totalPkts;
		System.out.println("In Java, TotalPackets is " + this.totalPackets);
	}
	
	/**
	 * This method will be called from Java to get the current packet Loss percentage for this node
	 * @return - packetLoss integer between 0 and 100
	 */
	public int getPacketLoss(){
		return this.packetLoss;
	}
	/**
	 * This method will be called from Java and will return the total number of packets that this node has
	 * seen come in 
	 * @return - long number that represents total incoming packets
	 */
	public long getTotalPackets(){
		return this.totalPackets;
	}
	
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
