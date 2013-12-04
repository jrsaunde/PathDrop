package guiFX;



import javafx.application.Application;
import javafx.stage.Stage;

public class ConsoleTest extends Application {

	
	@Override public void start(Stage stage) throws Exception {
		Console console = new Console("10.192.10.110", "cisco", "cisco");
		Thread thread = new Thread(console);
		thread.start();
	}
	
	// tests
	public static void main(String[] args) {
		launch(args); 
	}
}


/*AffineTransform affinetransform = new AffineTransform();     
FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
Font font = new Font("Tahoma", Font.PLAIN, 12);
int textwidth = (int)(font.getStringBounds(text, frc).getWidth());
int textheight = (int)(font.getStringBounds(text, frc).getHeight());

 myComponent.setStyle("-fx-text-fill: white;"+
    "-fx-background-color: black;"+
    "-fx-font: Courier New;"+
    "-fx-font-family: Courier New;"+
    "-fx-font-weight: bold;"+
    "-fx-font-size: 30;");
    
   */
