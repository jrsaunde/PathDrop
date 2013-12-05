package vty;

import guiFX.LogBox;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.vty.VtyService;

public class VTYSession {

	private InetAddress address;
	private String username;
	private String password;
	
	public VtyService vtyService;
	LogBox logBox;
	
	public VTYSession(String ip, String user, String pass, LogBox _logBox) {
		// TODO Auto-generated constructor stub
		this.logBox = _logBox;
		try {
			this.address = InetAddress.getByName(ip);
			this.username = user;
			this.password = pass;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			LogBox.println(e.toString());
		}       
        	}

	public String open() {
		// TODO Auto-generated method stub
		NetworkApplication networkApplication = NetworkApplication.getInstance();
		NetworkElement networkElement;
		String prompt = "";
		try {
			networkElement = networkApplication.getNetworkElement(this.address);
			networkElement.connect(this.username, this.password);
			this.vtyService = new VtyService(networkElement);
	        this.vtyService.open();
		
	        //Get initial prompt
			this.write("whoami");
			prompt = this.vtyService.getParserState().getPrompt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogBox.println(e.toString());
		}
		
		return prompt;

	}

	public String[] write(String command) {

		String result="";
		String prompt="";
		try {
			result = this.vtyService.write(command);
			prompt = this.vtyService.getParserState().getPrompt();
		} catch (Exception e) {
			LogBox.println(e.toString());
		}
		
		return new String[]{prompt,result};
		
	}
	
	public void close(){
		try {
			this.vtyService.close();
	        this.vtyService.destroy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogBox.println(e.toString());
		}
		return;
	}
}
