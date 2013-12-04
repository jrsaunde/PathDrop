package tests;



import vty.VTYSession;

public class VTYtest {
	public static void main(String args[]){
		String start = "10.192.10.110";
		String username = "cisco";
		String password = "cisco";
		try{
			System.out.println("Connecting to " + start);
		
			VTYSession vty = new VTYSession(start, username, password);
			vty.open();
			System.out.println(vty.write("sh ip int br | i 0/0"));
			
			System.out.println(vty.write("sh ip int br"));
			
			vty.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}
}