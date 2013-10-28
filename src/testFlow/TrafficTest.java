package testFlow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TrafficTest {

	public TrafficTest(String host, int port){
		try {
		    Socket echoSocket = new Socket(host, port);
		    PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
}
