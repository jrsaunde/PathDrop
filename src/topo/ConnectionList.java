package topo;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionList {

	private ArrayList<GuiConnection> connections;
	private String 				newLine 				= System.getProperty("line.separator");
	private  HashMap<Integer,Integer>	list;
	private int connectionNumber;
	
	
	public ConnectionList(){
		this.connections = new ArrayList<GuiConnection>();
		this.list = new HashMap<Integer,Integer>();
		this.connectionNumber = 0;
		System.out.println("Created ConnectionList");
	}
	
	public void addConnection(String srcRouter, String srcInterface, String dstRouter, String dstInterface){
		GuiConnection connect = new GuiConnection(srcRouter, srcInterface, dstRouter, dstInterface, 0);
		this.list.put((srcRouter + "||" + srcInterface).hashCode(), this.connectionNumber);
		this.list.put((dstRouter + "||" + dstInterface).hashCode(), this.connectionNumber);
		this.connections.add(this.connectionNumber, connect);

		this.connectionNumber+=1;
		return;
	}
	
	public String printConnections(){
		String output = "";
		int j =1;
		for(GuiConnection connect : this.connections){
			if(j++ < this.connectionNumber){
				output = output + "		" +connect.getConnection() + "," + newLine;
			}else{
				output = output + "		" +connect.getConnection() + newLine;
			}
		}
		output = output + "	]" + newLine +    "  }" + newLine;
		return output;
	}
	
	public String printLoss(){
		String result = "";
		for(GuiConnection connect : this.connections){
			result += (connect.getInfo() + "\n");
		}
		return result;
	}
	
	public void addPacketIn(String Router, String Interface){
		//System.out.println(("in with" + Router + "||" + Interface + (Router + "||" + Interface).hashCode()));
		int index = this.list.get((Router + "||" + Interface).hashCode());
		this.connections.get(index).packetIn();
		return;
	}
	
	public void addPacketOut(String Router, String Interface){
		//System.out.println(("out with" + Router + "||" + Interface));
		int index = this.list.get((Router + "||" + Interface).hashCode());
		this.connections.get(index).packetOut();
		return;
	}
	
	
}
