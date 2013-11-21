package guiFX;

import java.io.File;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.web.WebView;

public class Tester implements Runnable {
	public void threadMonitor(ArrayList<Thread>threads) {
		for(Thread thread : threads)
    		System.out.println(thread.isAlive());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
