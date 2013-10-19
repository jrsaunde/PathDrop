package demo.path;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.cisco.onep.core.exception.OnepConnectionException;
import com.cisco.onep.core.exception.OnepDuplicateElementException;
import com.cisco.onep.core.exception.OnepIllegalArgumentException;
import com.cisco.onep.core.exception.OnepInvalidSettingsException;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionHandle;
import com.cisco.onep.interfaces.NetworkPrefix;
import com.cisco.onep.routing.AppRouteTable;
import com.cisco.onep.routing.L3UnicastRIBFilter;
import com.cisco.onep.routing.L3UnicastRoute;
import com.cisco.onep.routing.L3UnicastRouteRange;
import com.cisco.onep.routing.L3UnicastScope;
import com.cisco.onep.routing.L3UnicastScope.AFIType;
import com.cisco.onep.routing.L3UnicastScope.SAFIType;
import com.cisco.onep.routing.RIB;
import com.cisco.onep.routing.Route;
import com.cisco.onep.routing.RouteRange.RangeType;
import com.cisco.onep.routing.Routing;

public class RoutingTest {

	public NetworkApplication routingApplication = NetworkApplication.getInstance();

	
	public static void main(String[] args){
		/*Parse arguments*/
		String start = args[0];
		String dest = args[1];
		String username = args[2];
		String password = args[3];
		
		try{
			System.out.println("Routing Path from " 
								+ start + " to "
								+ dest  + " with "
								+ username + " " + password);
			
			InetAddress startNode = InetAddress.getByName(start);
			InetAddress destNode  = InetAddress.getByName(dest);
			try {
				RoutingTest routeTest = new RoutingTest(startNode, destNode, username, password);
			} catch (OnepIllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OnepDuplicateElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OnepInvalidSettingsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (UnknownHostException e){
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public RoutingTest(InetAddress startAddress, InetAddress destAddress, String username, String password) throws OnepIllegalArgumentException, OnepDuplicateElementException, OnepInvalidSettingsException{
		
		try {

			NetworkElement startNode = routingApplication.getNetworkElement(startAddress);
			SessionHandle nodeSession = startNode.connect(username, password);
			
			getRouteTable(startNode);
			
		} catch (OnepConnectionException | OnepRemoteProcedureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}
	
	public void getRouteTable(NetworkElement node) throws OnepConnectionException, OnepRemoteProcedureException, OnepIllegalArgumentException {
		
		Routing routing = Routing.getInstance(node);
		
		RIB rib = routing.getRib();
		L3UnicastScope aL3UnicastScope = new L3UnicastScope("", AFIType.IPV4, SAFIType.UNICAST, "base");
		L3UnicastRIBFilter filter = new L3UnicastRIBFilter();
		NetworkPrefix prefix = new NetworkPrefix(node.getAddress(),16);
		L3UnicastRouteRange range = new L3UnicastRouteRange(prefix, RangeType.EQUAL_OR_LARGER, 20);
		
		
		List <Route> routes = rib.getRouteList(aL3UnicastScope,filter, range);
		
		for(Route route: routes){
			System.out.println("Route: " + route );
		}
		return;
	}
}
