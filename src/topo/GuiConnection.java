package topo;

public class GuiConnection {
	private String 	sourceNode;
	private String 	sourceInterface;
	private String 	destNode;
	private String 	destInterface;
	private int		packetLoss;
	
	public  GuiConnection(String srcRouter, String srcInt, String dstRouter, String dstInt, int pktLoss) {
		this.sourceNode = srcRouter;
		this.sourceInterface =	srcInt;
		this.destNode		 = 	dstRouter;
		this.destInterface	 = 	dstInt;
		this.packetLoss		 =	pktLoss;
	}
	
	public void setLoss(int pktLoss){
		this.packetLoss = pktLoss;
	}
	
	public String getConnection(){
		return( "{ data: { source: '" +
						  this.sourceNode + "', target: '" +
						  this.destNode + "', label: '" +
						  this.sourceInterface + " " + 
						  this.destInterface + "', faveColor: '" +
						  getHue(this.packetLoss) + "', strength: " +
						  this.packetLoss +" } }");
	}
	
	 private String getHue(int ratio) {
		 int max = 360;
		 int min = 140;
		 double percent = (double) ratio/100;
		 double hue = (max-min)*percent + min;
		 return "hsl("+ (int) hue+", 95%, 76%)";
		 }
}
