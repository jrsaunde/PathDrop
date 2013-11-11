package datapath;


public class NodePuppet {
	static{
		System.loadLibrary("ProgramNode");
	}
	
	public int number = 88;
	public String message= "Hello from Java";
	private String errBuf = "";
	
	public native void ProgramNode(String address, String user, String pass, String protocol);
	
	public static void main(String[] args){
		new NodePuppet().ProgramNode("10.192.10.110", "cisco", "cisco", "tcp");		//invoke the native method
	}
}
