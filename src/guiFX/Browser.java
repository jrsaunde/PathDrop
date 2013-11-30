package guiFX;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Browser extends Region {
 
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    String topSlice;
    String botSlice;
    ImageView image;
    Image loaderImage;
    
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
    
    public void loadLoader() {
    	// load default image
        image = new ImageView();
        image.setImage(this.loaderImage);
        image.relocate((540-165)/2, (600-165)/2);
		webEngine.load("");
        getChildren().add(image);
    }
    
	public void loadTopo(String midSlice) throws MalformedURLException, FileNotFoundException {
		// gets the string topo from parameters
		//Scanner midSliceScn = new Scanner(new File("src/web/topo.json"));
		Scanner topSliceScn = new Scanner(new File("src/web/topSlice.html"));
		Scanner botSliceScn = new Scanner(new File("src/web/botSlice.html"));

		//String midSlice = midSliceScn.useDelimiter("\\Z").next();
		String topSlice = topSliceScn.useDelimiter("\\Z").next();
		String botSlice = botSliceScn.useDelimiter("\\Z").next();
		
		//midSliceScn.close();
		topSliceScn.close();
		botSliceScn.close();

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
	
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }

}