package guiFX;



import javafx.application.Application;
import javafx.stage.Stage;

public class ConsoleTest extends Application {

	
	@Override public void start(Stage stage) throws Exception {
		Console console = new Console("1.1.1.1", "cisco", "cisco");
		Thread thread = new Thread(console);
		thread.start();
	}
	
	// tests
	public static void main(String[] args) {
		launch(args); 
	}
}
