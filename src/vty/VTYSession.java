package vty;

public class VTYSession {

	public VTYSession() {
		// TODO Auto-generated constructor stub
	}

	public void openVTY(String ip, String username, String password) {
		// TODO Auto-generated method stub
		
	}

	public String write(String command) {
		if (command.contains("multiple"))
			return command+"\n"+command+"\n"+command;
		else 
			return command;
	}
}
