package guiFX;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//private Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
public class FlowBuffer {
	private static Map<Integer,List<String>> synchronizedMap = Collections.synchronizedMap(new HashMap<Integer, List<String>>());
	
	
	public static void addToBuffer(int ID, List<String> value){
		synchronized(synchronizedMap){
			if(synchronizedMap.containsKey(ID)){
				//Do we care?
			}else if((value.get(0).equals("Router1")) && (value.get(1).equals("GigabitEthernet0/0")) ){
				System.out.println("packet: " + ID + " is leaving the network from " + value.get(0) + "[" + value.get(1) + "]");
			}else{
			System.out.println("Added " + ID + " from " + value.get(0) + "[" + value.get(1) + "]");
			synchronizedMap.put(ID, value);
			}
		}
		return;
	}
	public static void removeFrombuffer(int ID, String name){
		synchronized(synchronizedMap){
			if(synchronizedMap.containsKey(ID)){
				System.out.println("Removed " + ID + " from " + name);
				synchronizedMap.remove(ID);
			}
			return;
		}
	}
	public static int getSize(){
		return synchronizedMap.size();
	}
	public static void printBuffer(){
		System.out.println(synchronizedMap.toString());
		return;	
	}
}
