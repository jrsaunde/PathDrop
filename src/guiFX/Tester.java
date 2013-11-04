package guiFX;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.web.WebView;

public class Tester {
	public Tester() {
		
	}
	public static void main(String args[]) {
		System.out.println(Validator.validateIPLogic("123.123.123.123"));
		Image image2 = new Image("web/topology.html", 100, 150, false, false);
		new Tester();
	}
	
}
