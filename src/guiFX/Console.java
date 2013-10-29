package guiFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vty.VTYSession;

public class Console extends Application implements Runnable {
	String ip;
	String username;
	String password;
	String command;
	String history;
	String output;
	public Console(String ip, String username, String password) {
		this.ip = ip;
		this.username = username;
		this.password = password;
	}

	public Console() {
	}

	private EventHandler<KeyEvent> keyListener = new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent event) {

		}
	};

	@Override
	public void run() {
		launch();
		System.out.println("New thread running with IP: " + ip);
	}

	public static void main(String args[]) {
		new Thread(new Console()).start();
		//new Thread(new Console()).start();
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Hello World!");
		VBox root = new VBox();
		final TextArea textArea = new TextArea();
		final TextField textField = new TextField();
		root.getChildren().addAll(textArea, textField);
		textArea.setStyle("-fx-background-color: DARKGRAY;"
				+ "-fx-text-fill: BLACK;"
				+ "-fx-font-size: 14pt;");
		textArea.setPrefSize(400, 310);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		
//		ScrollBar scrollBarv = (ScrollBar)textArea.lookup(".scroll-bar:vertical");
//		scrollBarv.setDisable(true);  

		textField.setStyle("-fx-background-color: DARKGRAY;"
				+ "-fx-text-fill: BLACK;"
				+ "-fx-font-size: 14pt;");
		textField.setEditable(true);
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	            textField.requestFocus();
	        }
	    });
		
		// event listeners
		textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, 
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent arg0) {
			            textField.requestFocus();
						
					}
		});
		
		textField.addEventHandler(KeyEvent.KEY_PRESSED, 
				new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					VTYSession vty = new VTYSession();
					vty.openVTY(ip, username, password);
					output = vty.write(textField.getText().toString());
					textArea.setText(textArea.getText().concat(output+"\n"));
					textArea.setScrollTop(Double.MAX_VALUE);
					System.out.println(output);
					textField.setText(null);
				} 
			};
		});

		primaryStage.setScene(new Scene(root, 400, 330));
		primaryStage.show();
	}
}
