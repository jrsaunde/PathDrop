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



import javafx.application.Application;
import javafx.stage.Stage;

/** This is a testing class to test the console window
 * 
 */
public class ConsoleTest extends Application {

	LogBox logBox = new LogBox(null, null);
	@Override public void start(Stage stage) throws Exception {
		Console console = new Console("10.192.10.110", "cisco", "cisco", logBox);
		Thread thread = new Thread(console);
		thread.start();
	}
	
	// tests
	public static void main(String[] args) {
		launch(args); 
	}
}


/*AffineTransform affinetransform = new AffineTransform();     
FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
Font font = new Font("Tahoma", Font.PLAIN, 12);
int textwidth = (int)(font.getStringBounds(text, frc).getWidth());
int textheight = (int)(font.getStringBounds(text, frc).getHeight());

 myComponent.setStyle("-fx-text-fill: white;"+
    "-fx-background-color: black;"+
    "-fx-font: Courier New;"+
    "-fx-font-family: Courier New;"+
    "-fx-font-weight: bold;"+
    "-fx-font-size: 30;");
    
   */
