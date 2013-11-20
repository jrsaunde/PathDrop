package topo;

import java.net.InetAddress;
import java.util.List;

import com.cisco.onep.element.NetworkElement;

public class JNI {

	void getCFunctions(List<NetworkElement> nodes, InetAddress sourceIP, InetAddress destIP){
		//not sure if this is all we need for input args
		//also not sure if we need multiple methods with 1 C call each or
		//1 method with multiple C calls
	}
}
