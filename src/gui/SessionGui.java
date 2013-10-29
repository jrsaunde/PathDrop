package gui;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
 
public class SessionGui extends Application implements Runnable {
	String srcIP;
	String username;
	String password;
    public SessionGui(String srcIP, String username, String password) {
    	this.srcIP = srcIP;
    	this.username = username;
    	this.password = password;
    	System.out.println("Lauching gui");
		launch();
	}
/*
	public static void main(String[] args) {
		System.out.println("SessionGui App");
        launch();
    }*/
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
@Override
public void run() {
	// TODO Auto-generated method stub
	
}
}
