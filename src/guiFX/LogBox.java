package guiFX;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.ListIterator;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import vty.VTYSession;

public class LogBox implements Runnable{

	Stage stage;
	VBox root;
	private static PrintWriter writer;
	private Button logBoxButton;
	private static TextArea textArea;
	private int width;
	private int height;
	
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
	
	public void show(Stage primaryStage){
		stage.setX(primaryStage.getX() - 4);
		stage.setY(primaryStage.getY() + primaryStage.getHeight());
		stage.show();
	}
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
