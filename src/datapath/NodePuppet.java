package datapath;


public class NodePuppet {
	static{
		System.loadLibrary("ProgramNode");
	}
	
	public int number = 88;
	public String message= "Hello from Java";
	private String errBuf = "";
	
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
