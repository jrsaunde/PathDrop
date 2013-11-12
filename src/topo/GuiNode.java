package topo;

/**
 * This is the class for generating the javascript string for each node in the topology
 * @author Jamie
 *
 */
public class GuiNode {
	private String name;
	private String id;
	
	/**
	 * This will generate a string that will be passed to the JavaFX GUI
	 * @param id - The internal ID for each router, used to reference connections
	 * @param name - the name of the router, this will show up on the topology
	 */
	public  GuiNode(String id, String name) {
		this.name	= name;
		this.id  	= id;
	}

	public String getNode(){
		return ("{ data: { id: '" +
					this.id + "', name: '" +
					this.name + "', weight: 65, faveColor: '#0066CC', faveShape: 'ellipse' } }");
	}
}
