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
import javafx.scene.control.TextField;

/**
 * This class is used for error checking and validation of the input fields in the GUI
 *
 */
public class Validator {
	
	/**
	 * This will highlight the input TextField to warn the user there was a problem
	 * @param textField
	 */
	public static void setFalse(TextField textField) {
		LogBox.println("Please provide valid source IP address");
		textField.requestFocus();
		textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
	}
	
	/**
	 * This will remove the highlight from the TextField to clear the warning
	 * @param textField
	 */
	public static void setTrue(TextField textField) {
		textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
	}
	
	/**
	 * This will validate the IP address is a valid IP address
	 * @param textField
	 * @return True if the IP address is valid
	 */
	public static boolean validateIP(TextField textField) {
		if (textField.getText().isEmpty()) {
			LogBox.println("Please provide valid source IP address");
			textField.requestFocus();
			textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
			return false;
		} else {
			if (!Validator.validateIPLogic(textField.getText().trim())) {
				LogBox.println("Please provide valid source IP address");
				textField.requestFocus();
				textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
				return false;
			} else {
				textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
				return true;
			}
		}
	}
	
	/**
	 * This with validate the username from the GUI
	 * @param textField
	 * @return True if the username is valid
	 */
	public static boolean validateUsername(TextField textField) {
		if (textField.getText().isEmpty()) {
			LogBox.println("Please provide valid username");
			textField.requestFocus();
			textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
			return false;
		} else { 
			textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
			return true;
		}
	}
	
	/**
	 * This will validate the password from a TextField
	 * @param textField
	 * @return True if the password is valid
	 */
	public static boolean validatePassword(TextField textField) {
		if (textField.getText().isEmpty()) {
			LogBox.println("Please provide valid password");
			textField.requestFocus();
			textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
			return false;
		} else {
			textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
			return true;
		}
	}
	
	
	/**
	 * This method will validate the window size from the gui TextField
	 * @param str - the window size String from the TextField
	 * @return True if str is a valid number
	 */
	public static boolean validateWindowLogic(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * This method checks the port number from the GUI TextField object. It will print an error message and highlight
	 * the box if it is not a valid port number
	 * @param textField - port number TextField
	 * @return True - the port number is valid
	 */
	public static boolean validatePort(TextField textField) {
		if (textField.getText().isEmpty()) {
			LogBox.println("Please provide valid Port");
			textField.requestFocus();
			textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
			return false;
		} else {
			if (!Validator.validatePortLogic(textField.getText().trim())) {
				LogBox.println("Please provide valid port");
				textField.requestFocus();
				textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
				return false;
			} else {
				textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
				return true;
			}
		}
	}
	
	/**
	 * This method validates the portFields in the GUI
	 * @param str - string of the port number entered
	 * @return True if str is a valid port number
	 */
	public static boolean validatePortLogic(String str){
		
		try {
			int num = Integer.parseInt(str);
			if (num>65535)
				return false;
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method validates that a correctly formatted IP address was entered
	 * @param str - the IP address string from the GUI field
	 * @return True if the String is a valid IP address
	 */
	public static boolean validateIPLogic(String str) {
		String[] srcIPSplit = str.split("\\."); // regular expression
		if (srcIPSplit.length != 4)
			return false;
		for (String s : srcIPSplit) {
			if (s.length()>3)
				return false;
			try {
				int num = Integer.parseInt(s);
				if (num>255)
					return false;
			} catch(NumberFormatException e) {
				return false;
			}
			
		}
		return true;
	}
	/**
	 * This method validates the username field logic
	 * @param str - entered password from the username field
	 * @return True if str contains an Integer
	 */
	public static boolean validateUsernameLogic(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method validates the password field logic
	 * @param str - entered password from the password field
	 * @return True if str contains an Integer
	 */
	public static boolean validatePasswordLogic(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
}
