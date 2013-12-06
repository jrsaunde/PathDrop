/* PathDrop - Topology Visualizer and Packet Loss Indicator
 * Copyright (c) 2013 
 * Jamie Saunders <jrsaunde@ncsu.edu>
 * Thomas Paradis <tmparadi@ncsu.edu>
 * Hank Liu <hliu9@ncsu.edu>
 * Ryan Coble <rlcoble@ncsu.edu>
 * Isaac Choe <ichoe@ncsu.edu>
 * 
 * All rights reserved
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */
package guiFX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Scanner;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * This class is for the topology portion of the GUI
 */
public class Browser extends Region {
 
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    String topSlice;
    String botSlice;
    ImageView image;
    Image loaderImage;
    
    /**
     * Constructor for the browser object
     * @param topSlice - file where the top of the Cytoscape.js data is
     * @param botSlice - file where the bottom of the Cytoscape.js data is
     * @param loaderImage - loading image location
     * @throws MalformedURLException
     */
    public Browser(String topSlice, String botSlice, Image loaderImage) throws MalformedURLException {
    	
    	//initialize globals
		this.topSlice = (new File(topSlice)).toURI().toURL().toString();
		this.botSlice = (new File(botSlice)).toURI().toURL().toString();
    	this.loaderImage = loaderImage;
    	
    	//apply the styles
        getStyleClass().add("browser");
        
        //add the web view to the scene
        getChildren().add(browser);
    }
    
    /**
     * Loads the temporary loading image while network discovery is happening
     */
    public void loadLoader() {
    	// load default image
        image = new ImageView();
        image.setImage(this.loaderImage);
        image.relocate((740-165)/2, (800-165)/2);
		webEngine.load("");
        getChildren().add(image);
    }
    
    /**
     * This method will load the topology based on the topSlice, midSlice, and bottomSlice of 
     * the Cytoscape.js files 
     * @param midSlice - The GuiNodes and GuiConnections in Cytoscape.js format
     * @throws MalformedURLException
     * @throws FileNotFoundException
     */
	public void loadTopo(String midSlice) throws MalformedURLException, FileNotFoundException {
		// gets the string topo from parameters
		//Scanner midSliceScn = new Scanner(new File("src/web/topo.json"));
		Scanner topSliceScn = new Scanner(new File("src/web/topSlice.html"));
		Scanner botSliceScn = new Scanner(new File("src/web/botSlice.html"));

		//String midSliceSample = midSliceScn.useDelimiter("\\Z").next();
		String topSlice = topSliceScn.useDelimiter("\\Z").next();
		String botSlice = botSliceScn.useDelimiter("\\Z").next();
		
		//midSliceScn.close();
		topSliceScn.close();
		botSliceScn.close();

		//String web = topSlice + midSliceSample + botSlice;
		String web = topSlice + midSlice + botSlice;
		
		try {
		PrintWriter out = new PrintWriter("src/web/web.html");
		out.write(web);
		out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// hard coded data for testing purposes
		File file = (new File("src/web/web.html"));
		
		// load the web page
		getChildren().remove(image);
		webEngine.load(file.toURI().toURL().toString());
    }
	
	/**
	 * This will create a space for each node
	 * @return Node object
	 */
    @SuppressWarnings("unused")
	private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    /**
     * This method will setup the layout of the children
     */
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
}