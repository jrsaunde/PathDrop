package guiFX;

import java.io.File;
import java.net.MalformedURLException;

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

class Browser extends Region {
 
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    File file;
    ImageView image;
    
    public Browser(String fileName, Image loaderImage) throws MalformedURLException {
        file = new File(fileName);
    	
    	//apply the styles
        getStyleClass().add("browser");
        
        //add the web view to the sscene
        getChildren().add(browser);
        
        // load default image
        image = new ImageView();
        image.setImage(loaderImage);
        getChildren().add(image);
        
    }
    
	public void loadTopo() throws MalformedURLException {
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