package demo.path;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.cisco.onep.core.exception.OnepConnectionException;
import com.cisco.onep.core.exception.OnepDuplicateElementException;
import com.cisco.onep.core.exception.OnepIllegalArgumentException;
import com.cisco.onep.core.exception.OnepInvalidSettingsException;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionHandle;
import com.cisco.onep.interfaces.InterfaceFilter;
import com.cisco.onep.interfaces.InterfaceStatus;
import com.cisco.onep.interfaces.InterfaceStatus.InterfaceState;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.interfaces.NetworkPrefix;
import com.cisco.onep.routing.AppRouteTable;
import com.cisco.onep.routing.L3UnicastNextHop;
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
	public L3UnicastScope scope = new L3UnicastScope("", AFIType.IPV4, SAFIType.UNICAST, "base");
	public L3UnicastRIBFilter filter = new L3UnicastRIBFilter();

	
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
			
			getPaths(startNode, destAddress);
			
		} catch (Exception e) {
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
			System.out.println("Route: " + route);
		}
		return;
	}
	
	public void getPaths(NetworkElement node, InetAddress destAddress) throws Exception{
		
		//Print out all interfaces that are UP
		List<NetworkInterface> interfaces = getInterfaces(node);

		
		//Look at routing table for destination node
		Routing routing = Routing.getInstance(node);
		
		//Setup range for RIB table
		NetworkPrefix prefix = new NetworkPrefix(node.getAddress(), 16);
		L3UnicastRouteRange range = new L3UnicastRouteRange(prefix, RangeType.EQUAL_OR_LARGER, 20);
		
		
		//Get Routing table
		List <Route> routes = routing.getRib().getRouteList(this.scope, this.filter, range);
		
		//Print out the Routing table and next hops
		printRouteTable(routes);
	}
	
	public List <NetworkInterface> getInterfaces(NetworkElement node) throws Exception{
		InterfaceFilter ifFilter = new InterfaceFilter();		//TODO:Find a way to filter based on up interfaces
		
		List<NetworkInterface> allInterfaceList = node.getInterfaceList(ifFilter);
		List<NetworkInterface> interfaceList = new ArrayList<NetworkInterface>();
		for (NetworkInterface netInterface: allInterfaceList){
			InterfaceStatus status = netInterface.getStatus();
			if(status.getLineProtoState() == InterfaceState.ONEP_IF_STATE_OPER_UP){
				interfaceList.add(netInterface);
			}
		}
		return interfaceList;
		
	}
	
	public void printRouteTable(List<Route> routes) throws Exception{
		for(Route route: routes){
			//Cast the route to be L3UnicastRoute so we don't have to parse it and we can use it
			L3UnicastRoute route2 = (L3UnicastRoute) route;
			
			//Get NextHopList from Route
			Set<L3UnicastNextHop> nextHopList = route2.getNextHopList();
			
			//Print out Route information
			System.out.println("Network: " + route2.getPrefix().getAddress() +
					   		   " AD: " + route2.getAdminDistance() +
							   " Metric: " + route2.getMetric() +
							   " Type: " + route2.getOwnerType().toString() +
							   " NextHop: ");
			//Print out Next Hop information
			for(L3UnicastNextHop hop: nextHopList){
				List<InetAddress>address = hop.getNetworkInterface().getAddressList();
				List<NetworkPrefix> prefix = hop.getNetworkInterface().getPrefixList();
				checkIP(address.get(0), prefix.get(0));
				System.out.println(hop.getNetworkInterface().getName() + " IP: " + address.get(0) + " Mask: "  + prefix.get(0).getPrefixLength());
			}
		}
	}
	
	public boolean checkIP(InetAddress nodeAddress, NetworkPrefix network){
		
		System.out.println("Network Address is " + network.getAddress() + " and mask is " + Integer.toString(network.getPrefixLength()));
		return false;
	}
	
}
