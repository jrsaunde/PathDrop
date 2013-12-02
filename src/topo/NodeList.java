package topo;

import java.util.ArrayList;

public class NodeList {

	private ArrayList<GuiNode> nodes;
	private String 				newLine 				= System.getProperty("line.separator");
	
	public NodeList(){
		this.nodes = new ArrayList<GuiNode>();
		System.out.println("Created NodeList");
	}
	
	public void addNode(String name){
		this.nodes.add(new GuiNode(name,name));
	}
	public String printNodes(){
		String output = "elements: { " + newLine + "	nodes: [" + newLine;
		int i =1;
		for(GuiNode node: this.nodes){
			if(i++ < this.nodes.size()){
				output = output + "		" + node.getNode() + "," + newLine;
			}else{
				output = output + "		" + node.getNode() + newLine;
			}
		}
		output = output + "	]," + newLine + "	edges: [" + newLine;
		return output;
	}
}
