package guiFX;

/**

 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.

 * All rights reserved. Use is subject to license terms.

 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Iterator;

import topo.NetworkDiscovery;
import vty.VTYSession;

public class GuiFx extends Application {

	private Iterator<Node> i;
	
	private String srcIP;
	private String dstIP;
	private int srcPort;
	private int dstPort;
	private int window;
	private String protocol;
	private String username;
	private String password;

	private TextField srcIPField = new TextField("");
	private TextField dstIPField = new TextField("");
	private TextField srcPortField = new TextField("");
	private TextField dstPortField = new TextField("");
	private TextField windowField = new TextField("");
	private ChoiceBox protocolField = new ChoiceBox(FXCollections.observableArrayList("TCP", "UDP", "DCCP", "SCTP", "RSVP"));
	private TextField usernameField = new TextField("");
	private TextField passwordField = new TextField("");
	private NetworkDiscovery network;
	private Image loaderImage = new Image("img/loader.gif", true);
	
	@Override public void start(Stage stage) throws Exception {
		Group root = new Group();
		Scene scene = new Scene(root, 800, 600);
		HBox body = new HBox();
		root.getChildren().add(body);
		HBox controlPane = new HBox();
		controlPane.setStyle("-fx-background-image: url('img/left_banner.png');");
		controlPane.setPrefSize(260, 600);
		controlPane.setPadding(new Insets(10));
		VBox viewPane = new VBox();
		viewPane.setPrefSize(540, 600);
		body.getChildren().addAll(controlPane, viewPane);
		
		// control pane
		final VBox labels = new VBox(21);
		final VBox fields = new VBox(16);
		labels.setPadding(new Insets(168, 8, 8, 8));
		fields.setPadding(new Insets(166, 6, 6, 6));
		controlPane.getChildren().addAll(labels,fields);
		
		Label srcIPLabel = new Label("Source IP:");
		Label dstIPLabel = new Label("Destination IP:");
		Label srcPortLabel = new Label("Source Port:");
		Label dstPortLabel = new Label("Destination Port:");
		Label windowLabel = new Label("Window Size:");
		Label protocolLabel = new Label("Protocol:");
		Label usernameLabel = new Label("Username:");
		Label passwordLabel = new Label("Password:");
		Button discoverButton = new Button("Discover");
		Button consoleButton = new Button("Connect");
		
		final Button traceButton = new Button("Trace");
		final Button stopButton = new Button("Stop");
		protocolField.getSelectionModel().selectFirst();
		
		labels.getChildren().addAll(srcIPLabel, dstIPLabel,srcPortLabel, dstPortLabel, 
				windowLabel, protocolLabel, usernameLabel, passwordLabel);
		fields.getChildren().addAll(this.srcIPField, this.dstIPField, this.srcPortField, this.dstPortField, 
				this.windowField, this.protocolField, this.usernameField, this.passwordField, discoverButton, consoleButton, traceButton);
		
		// topology pane
		Browser browser = new Browser("src/web/web.html", loaderImage);  
		viewPane.getChildren().add(browser);
		
		// format labels
		i = labels.getChildren().iterator();
		while(i.hasNext()) {
			Node node = i.next();
			if (node instanceof Label) {
				Label label = (Label) node;
				label.setFont(Font.font("Cambria", 14));
			}
		}
		
		// format fields
		i = fields.getChildren().iterator();
		while(i.hasNext()) {
			Node node = i.next();
			if (node instanceof TextField) {
				TextField field = (TextField) node;
				field.setMaxWidth(100);
			}
		}
		
		// Trace Listener
		traceButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (Validator.validateIP(srcIPField))
					srcIP = srcIPField.getText().trim();
				else
					return;
				
				if (Validator.validateIP(dstIPField))
					srcIP = dstIPField.getText().trim();
				else 
					return;
				
				fields.getChildren().remove(traceButton);
				fields.getChildren().add(stopButton);
				stopButton.requestFocus();
				// call  trace object (inputs)
			}
		});
		
		// Stop trace Listener
		stopButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {

				fields.getChildren().remove(stopButton);
				fields.getChildren().add(traceButton);
				traceButton.requestFocus();
				// call stop trace object (inputs)
			}
		});
		
		// Discovery route listener
		discoverButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (Validator.validateIP(srcIPField))
					srcIP = srcIPField.getText().trim();
				else 
					return;
				if (Validator.validateUsername(usernameField))
					srcIP = srcIPField.getText().trim();
				else 
					return;
				if (Validator.validatePassword(passwordField))
					srcIP = srcIPField.getText().trim();
				else 
					return;
				
				// call network discovery object (inputs)
			}

		});
		
		
		// console into a box
		consoleButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				if (Validator.validateIP(srcIPField))
					srcIP = srcIPField.getText().trim();
				else 
					return;
				if (Validator.validateUsername(usernameField))
					srcIP = srcIPField.getText().trim();
				else 
					return;
				if (Validator.validatePassword(passwordField))
					srcIP = srcIPField.getText().trim();
				else 
					return;
				
				new Console(srcIP, username, password);
			}

		});
		
		// core stage
		stage.setTitle("PathDrop - Kickass Network Visualizer & Packet Tracer");
		stage.setScene(scene);
		stage.getIcons().add(new Image("img/cisco_blue.png"));
		stage.show();
	}
	
	public static void main(String[] args) { 
		launch(args); 
	}
}








/*				
if (!srcPortField.getText().isEmpty())
	if (Validator.validatePort(srcPortField.getText().trim()))
		srcPort = Integer.parseInt(srcPortField.getText().trim());
	else
		System.out.println("Invalid source port number");
if (!dstPortField.getText().isEmpty())
	if (Validator.validatePort(dstPortField.getText().trim()))
		dstPort = Integer.parseInt(dstPortField.getText().trim());
	else
		System.out.println("Invalid destination port number");

if (!windowField.getText().isEmpty())
	if (Validator.validateWindow(windowField.getText().trim()))
		window = Integer.parseInt(windowField.getText().trim());
	else
		System.out.println("Invalid window size");

protocol = protocolField.getSelectionModel().getSelectedItem().toString();
*/