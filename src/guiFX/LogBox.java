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

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class is used to create a log of what happens during the program operation. It will also save the log to
 * a file
 */
public class LogBox implements Runnable{

	Stage stage;
	VBox root;
	private static PrintWriter writer;
	private Button logBoxButton;
	private static TextArea textArea;
	private int width;
	private int height;
	
	/**
	 * Constructor for the LogBox
	 * @param primaryStage - Stage for the main Application, used to determine where to place the logBox window
	 * @param logBoxButton - reference to the LogBox button in the GUI
	 */
	public LogBox(Stage primaryStage, Button logBoxButton) {
		width = 1008;
		height = 100;
		this.logBoxButton = logBoxButton;
		
		stage = new Stage();		
		stage.setResizable(false);
        stage.setTitle("Log Box");
		root = new VBox();
		textArea = new TextArea("All logs will appear here\n");
		
		root.getChildren().addAll(textArea);
		
		textArea.setStyle("-fx-background-color: WHITE;"
				+ "-fx-text-fill: BLACK;"
				+ "-fx-font-size: 14pt;"
				+ "-fx-border-radius: 0 0 0 0;");
		textArea.setEditable(false);
		textArea.setWrapText(true);
	
		try {
			writer = new PrintWriter("logfile.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		stage.setResizable(true);

		stage.setScene(new Scene(root, width, height));


	}
	
	/**
	 * This method will show the logBox under the main window
	 * @param primaryStage - reference to the main application window
	 */
	public void show(Stage primaryStage){
		stage.setX(primaryStage.getX() - 4);
		stage.setY(primaryStage.getY() + primaryStage.getHeight());
		stage.show();
	}
	
	/**
	 * Appends the input string to the logBox and file
	 * @param str - Line(s) to be written to the log
	 */
	public static void println(String str) {
		textArea.appendText(str + "\n");
		writer.println(str);
		writer.flush();
	}
		
	@Override
	public void run() {
		System.out.println("LogBox Thread running!");
		LogBox.println("Check point !");
		
		// close operation
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				logBoxButton.setDisable(false);
			}
		});
	}
}
