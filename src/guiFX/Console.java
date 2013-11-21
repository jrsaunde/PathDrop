package guiFX;

import java.awt.EventQueue;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vty.VTYSession;

class Console implements Runnable{
	String ip;
	String username;
	String password;
	VTYSession vty;
	ArrayList<String> cmds;
	ListIterator itr;

	TextArea textArea;
	TextField textField;
	
	public Console(final String ip, final String username, final String password, ArrayList<Thread>threads) {
		this.ip = ip;
		this.username = username;
		this.password = password;
		
		vty = new VTYSession(ip, username, password);
		vty.open();
		cmds = new ArrayList<String>();
		
		Stage stage = new Stage();		
        stage.setTitle(ip);
		VBox root = new VBox();
		textArea = new TextArea();
		textField = new TextField();
		root.getChildren().addAll(textArea, textField);
		textArea.setStyle("-fx-background-color: DARKGRAY;"
				+ "-fx-text-fill: BLACK;"
				+ "-fx-font-size: 14pt;");
		textArea.setPrefSize(400, 310);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		

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
			
		stage.setScene(new Scene(root, 400, 330));
		stage.show();
	}

	@Override
	public void run() {
		System.out.println("Current Thread: " + Thread.currentThread().getName());
			
		// event listeners
		textArea.addEventHandler(MouseEvent.MOUSE_CLICKED, 
			new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent arg0) {
		            textField.requestFocus();
					
				}
		});
		
        final String keyComb1 = "_CONTROL_C";
        final StringBuilder key = new StringBuilder();
        
		textField.addEventHandler(KeyEvent.KEY_PRESSED, 
				new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER) {
					System.out.println("Current Thread: " + Thread.currentThread().getName());
					String cmd = textField.getText();
					cmds.add(0, cmd);
					if (cmd == null)
						cmd = "";
					String output = vty.write(cmd); // return a string with one or more lines
					textArea.appendText(output+"\n");
					textArea.positionCaret(textArea.getLength());
					textField.setText(null);
					itr = cmds.listIterator();
				} 
				
				if(event.getCode() == KeyCode.UP) {
					if(itr.hasNext())
						textField.setText((String) itr.next());
				} 
				
				if(event.getCode() == KeyCode.DOWN) {
					if(itr.hasPrevious())
						textField.setText((String) itr.previous());
				} 
				
				String codeStr = event.getCode().toString();
                if(!key.toString().endsWith("_"+codeStr)){
                        key.append("_"+codeStr);
                }
				
			};
		});
		
		textField.addEventHandler(KeyEvent.KEY_RELEASED, 
				new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if(key.length()>0) {
					if(key.toString().equals(keyComb1)){
                        System.out.println("Key Combination 1 pressed");
                        textField.setText("");
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
