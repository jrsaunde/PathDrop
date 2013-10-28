package topo;

import java.awt.Dimension;
import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;

import topo.RouterShape;

public class JGui extends JPanel
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	public JGui(Collection<String> nodes, ArrayList<String> connections)
	{

		mxGraph graph = new mxGraph();
		graph.setMinimumGraphSize(new mxRectangle(0,0, 800,600));
		mxGraphics2DCanvas.putShape("routerShape", new RouterShape() );
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try
		{
			HashMap<String, Object> routers = new HashMap<String, Object>();
			
			int[] xVal = {280,180,380,80,280,480};
			int[] yVal = {20,120,120,220,220,220};
			int i =0;
			for(String node: nodes){
				Object router = graph.insertVertex(parent,node.toString(),node.toString(), xVal[i], yVal[i], 160, 60, "shape=routerShape");
				routers.put(node.toString(), router);
				i++;
			}
			
			for(String connection: connections){
				String[] device = connection.split("<>");
				graph.insertEdge(parent, null, null, routers.get(device[0]), routers.get(device[1]));
				graph.insertEdge(parent, null, null, routers.get(device[1]), routers.get(device[0]));
			}			
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphComponent.setSize(new Dimension(800, 800));
		this.add(graphComponent);
		this.setMinimumSize(new Dimension(800, 800));
	}


	public static void main(String[] args)
	{

		JGui frame = new JGui(null, null);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 800);
		frame.setVisible(true);
	}

}
