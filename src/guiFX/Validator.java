package guiFX;
import javafx.scene.control.TextField;

public class Validator {
	
	public static void setFalse(TextField textField) {
		System.out.println("Please provide valid source IP address");
		textField.requestFocus();
		textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
	}
	
	public static void setTrue(TextField textField) {
		textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
	}
	
	public static boolean validateIP(TextField textField) {
		if (textField.getText().isEmpty()) {
			System.out.println("Please provide valid source IP address");
			textField.requestFocus();
			textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
			return false;
		} else {
			if (!Validator.validateIPLogic(textField.getText().trim())) {
				System.out.println("Please provide valid source IP address");
				textField.requestFocus();
				textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
				return false;
			} else {
				textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
				return true;
			}
		}
	}
	
	public static boolean validateUsername(TextField textField) {
		if (textField.getText().isEmpty()) {
			System.out.println("Please provide valid username");
			textField.requestFocus();
			textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
			return false;
		} else { 
			textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
			return true;
		}
	}
	
	public static boolean validatePassword(TextField textField) {
		if (textField.getText().isEmpty()) {
			System.out.println("Please provide valid password");
			textField.requestFocus();
			textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
			return false;
		} else {
			textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
			return true;
		}
	}
	
	
	
	public static boolean validateWindowLogic(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean validatePort(TextField textField) {
		if (textField.getText().isEmpty()) {
			System.out.println("Please provide valid Port");
			textField.requestFocus();
			textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
			return false;
		} else {
			if (!Validator.validatePortLogic(textField.getText().trim())) {
				System.out.println("Please provide valid port");
				textField.requestFocus();
				textField.setStyle("-fx-background-color: red, -fx-text-box-border, -fx-control-inner-background;");
				return false;
			} else {
				textField.setStyle("-fx-text-box-border, -fx-control-inner-background;");
				return true;
			}
		}
	}
	
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
	
	public static boolean validateUsernameLogic(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static boolean validatePasswordLogic(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
}
