package vty;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.cisco.onep.core.exception.OnepConnectionException;
import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.exception.OnepIllegalArgumentException;
import com.cisco.onep.core.exception.OnepInvalidSettingsException;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.idl.ExceptionIDL;
import com.cisco.onep.vty.VtyService;

public class VTYSession {

	private InetAddress address;
	private String username;
	private String password;
	
	public VtyService vtyService;
	
	public VTYSession(String ip, String user, String pass) {
		// TODO Auto-generated constructor stub
		
		try {
			this.address = InetAddress.getByName(ip);
			this.username = user;
			this.password = pass;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
	        //Get inital prompt
			this.write("whoami");
			prompt = this.vtyService.getParserState().getPrompt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return prompt;

	}

	public String[] write(String command) {
//		if (command.contains("multiple"))
//			return command+"\n"+command+"\n"+command;
//		else 
//			return command;
		String result="";
		String prompt="";
		try {
			result = this.vtyService.write(command);
			prompt = this.vtyService.getParserState().getPrompt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new String[]{prompt,result};
		
	}
	
	public void close(){
		try {
			this.vtyService.close();
	        this.vtyService.destroy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return;
	}
}
