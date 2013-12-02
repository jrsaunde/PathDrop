package topo;

public class GuiConnection {
	private String 	sourceNode;
	private String 	sourceInterface;
	private String 	destNode;
	private String 	destInterface;
	private int		packetLoss;
	private int		packetsIn;
	private int		packetsOut;
	
	public  GuiConnection(String srcRouter, String srcInt, String dstRouter, String dstInt, int pktLoss) {
		this.sourceNode = srcRouter;
		this.sourceInterface =	srcInt;
		this.destNode		 = 	dstRouter;
		this.destInterface	 = 	dstInt;
		this.packetLoss		 =	pktLoss;
		this.packetsIn = 0;
		this.packetsOut = 0;
	}
	
	public int getLoss(){
		double lossIn = (((double) this.packetsIn - (double) this.packetsOut) / ((double) this.packetsIn))*100;
		double lossOut = (((double) this.packetsOut - (double) this.packetsIn) / ((double) this.packetsOut))*100;
		if((this.packetsIn != 0) && (this.packetsOut != 0)){
			if(this.packetsIn > this.packetsOut){
				return (int) lossIn;
			}else{
				return (int) lossOut;
			}
		}else{
			return 0;
		}
	}
	
	public void increaseLoss(){
		this.packetLoss +=1;
	}
	
	public void packetIn(){
		//System.out.println(this.sourceNode + " to " + this.destNode + " packetIn");
		this.packetsIn++;
	}
	public void packetOut(){
		//System.out.println(this.sourceNode + " to " + this.destNode + " packetOut");
		this.packetsOut++;
	}
	public String getConnection(){
		return( "{ data: { source: '" +
						  this.sourceNode + "', target: '" +
						  this.destNode + "', label: '" +
						  this.sourceInterface + "					" + 
						  this.destInterface + "', faveColor: '" +
						  getHue(this.getLoss()) + "', strength: 70 " 
						   +" } }");
	}
	
	public String getInfo(){
		return( "Source: " + this.sourceNode + " [" + this.sourceInterface
				+ "] Dest: " + this.destNode + " [" + this.destInterface
				+ "] packetsIn: " + this.packetsIn + " packetsOut: " + this.packetsOut + " loss: " + this.getLoss());
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
		 //System.out.print("Ratio: " + loss + "; Hue: " + hue);
		 return "hsl("+ (int) hue+", 95%, 76%)";
	 }
}
