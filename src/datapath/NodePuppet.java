package datapath;


public class NodePuppet {
	static{
		System.loadLibrary("ProgramNode");
	}
	
	public int number = 88;
	public String message= "Hello from Java";
	private String errBuf = "";
	
	/* Packet Loss percentage (0 - 100) */
	public int pktLoss = 0;
	
	
	/**
	 * Call the native C program to program a node to watch for traffic
	 * @param address - Address of the node we are programming
	 * @param user - Username for authentication
	 * @param pass - Password for authentication
	 * @param protocol - TCP/UDP
	 */
	public native void ProgramNode(String address, String user, String pass, String protocol);
	
	public static void main(String[] args){
		new NodePuppet().ProgramNode("10.192.10.110", "cisco", "cisco", "tcp");		//invoke the native method
	}
}
