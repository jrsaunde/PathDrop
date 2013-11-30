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
						  this.sourceInterface + "					" + 
						  this.destInterface + "', faveColor: '" +
						  getHue(this.packetLoss) + "', strength: 70 " 
						   +" } }");
	}
	
	 public static String getHue(int loss) {
		 int max = 360;
		 int min = 140;
		 
		 // logorithm scale
		 double scale = Math.log10(loss+1)/2;
				 
		 // linear scale
		 // double scale = (double) ratio/100;
		 
		 int hue = (int) Math.round((max-min)*scale) + min;
		 
		 // boundary check
		 if (hue>max)
			 hue = max;
		 if (hue<min)
			 hue = min;
		 System.out.print("Ratio: " + loss + "; Hue: " + hue);
		 return "hsl("+ (int) hue+", 95%, 76%)";
	 }
}
