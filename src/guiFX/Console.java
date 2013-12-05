package guiFX;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vty.VTYSession;

public class Console implements Runnable{
	String ip;
	String username;
	String password;
	VTYSession vty;
	ArrayList<String> cmds;
	ListIterator itr;

	LogBox logBox;
	TextArea textArea;
	TextField promptField;
	TextField commandField;
	String prompt = "";
	String banner = "Warning: This system is for the use of autorhized clients only.";
	int lineSpace = 10;
	public Console(final String ip, final String username, final String password, LogBox _logBox) {
		this.ip = ip;
		this.username = username;
		this.password = password;
		this.cmds = new ArrayList<String>();
		this.logBox = _logBox;

		Stage stage = new Stage();		
		stage.setResizable(false);
        stage.setTitle("VTY Session: "+ username + "@" + ip);
		VBox root = new VBox();
		HBox textFieldBox = new HBox();
		textArea = new TextArea(banner);
		promptField = new TextField();
		promptField.setMinWidth(50);
		promptField.setPrefWidth(50);;
		commandField = new TextField();
		commandField.setPrefWidth(510 - 50);
		textFieldBox.getChildren().addAll(promptField, commandField);
		root.getChildren().addAll(textArea, textFieldBox);
		
		textArea.setStyle("-fx-background-color: DARKGRAY;"
				+ "-fx-text-fill: BLACK;"
				+ "-fx-font-size: 14pt;"
				+ "-fx-border-radius: 0 0 0 0;");
		textArea.setPrefSize(500, 416);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		
		promptField.setStyle("-fx-background-color: DARKGRAY;"
				+ "-fx-text-fill: BLACK;"
				+ "-fx-font-size: 14pt;");
		commandField.setEditable(false);
		
		commandField.setStyle("-fx-background-color: DARKGRAY;"
				+ "-fx-text-fill: BLACK;"
				+ "-fx-font-size: 14pt;");
		commandField.setEditable(true);
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	            commandField.requestFocus();
	        }
	    });
			
		stage.setScene(new Scene(root, 500, 430));
		stage.show();
	}

	@Override
	public void run() {
		LogBox.println("Console Thread: " + Thread.currentThread().getName());

		this.vty = new VTYSession(ip, username, password, logBox);
		prompt = this.vty.open();
		
		
		AffineTransform affinetransform = new AffineTransform();     
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
		Font font = new Font("Verdana", Font.PLAIN, 18);
		
		int textwidth = (int)(font.getStringBounds(prompt, frc).getWidth());
		promptField.setText(prompt);
		
		promptField.setPrefWidth(textwidth);
		commandField.setPrefWidth(510-textwidth);

		// event listeners
		textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, 
			new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
		            commandField.requestFocus();
				}
		});
		
        final String keyComb1 = "_CONTROL_C";
        final StringBuilder key = new StringBuilder();
        
		commandField.addEventHandler(KeyEvent.KEY_PRESSED, 
				new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					LogBox.println("Current Thread: " + Thread.currentThread().getName());
					String cmd = commandField.getText();
					cmds.add(0, cmd);
					if (cmd == null)
						cmd = "";
					String[] results = vty.write(cmd); // return a string with one or more lines
					//String output = cmd+"\n"+cmd;
					prompt = results[0];
					promptField.setText(prompt);
					

					AffineTransform affinetransform = new AffineTransform();     
					FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
					Font font = new Font("Verdana", Font.PLAIN, 18);
					System.out.println("x" + prompt + "x");
					int textwidth = (int)(font.getStringBounds(prompt, frc).getWidth());
					
					promptField.setPrefWidth(textwidth);
					commandField.setPrefWidth(510-textwidth);
					
					textArea.appendText(results[1]+"\n");
					textArea.positionCaret(textArea.getLength());
					commandField.setText(null);
					itr = cmds.listIterator();
				} 
				
				if(event.getCode() == KeyCode.UP) {
					if(itr.hasNext())
						commandField.setText((String) itr.next());
				} 
				
				if(event.getCode() == KeyCode.DOWN) {
					if(itr.hasPrevious())
						commandField.setText((String) itr.previous());
				} 
				
				String codeStr = event.getCode().toString();
                if(!key.toString().endsWith("_"+codeStr)){
                        key.append("_"+codeStr);
                }
				
			};
		});
		
		commandField.addEventHandler(KeyEvent.KEY_RELEASED, 
				new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if(key.length()>0) {
					if(key.toString().equals(keyComb1)){
						LogBox.println("Key Combination 1 pressed");
                        commandField.setText("");
        				String codeStr = event.getCode().toString();
                        int index = key.lastIndexOf("_"+codeStr);
                        key.delete(index, key.length());
    					itr = cmds.listIterator();
					} else {
						key.setLength(0);
					}
				}
			};
		});
	}
}
