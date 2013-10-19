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
import com.cisco.onep.pathtrace.CPUProfile;
import com.cisco.onep.pathtrace.EchoProfile;
import com.cisco.onep.pathtrace.PathSpecifier;
import com.cisco.onep.pathtrace.PathTrace;
import com.cisco.onep.pathtrace.PathTraceNode;
import com.cisco.onep.pathtrace.PathTrace.ProtocolType;
import com.cisco.onep.pathtrace.Profile.ProfileType;
import com.cisco.onep.pathtrace.Route;
import com.cisco.onep.routing.L3UnicastRIBFilter;
import com.cisco.onep.routing.L3UnicastRouteRange;
import com.cisco.onep.routing.L3UnicastScope;
import com.cisco.onep.routing.RIB;
import com.cisco.onep.routing.Routing;
import com.cisco.onep.routing.L3UnicastScope.AFIType;
import com.cisco.onep.routing.L3UnicastScope.SAFIType;
import com.cisco.onep.routing.RouteRange.RangeType;

public class PathTraceTest {

	public NetworkApplication pathApplication = NetworkApplication.getInstance();
	
	public static void main(String[] args){
		/*Parse arguments*/
		String start = args[0];
		String dest = args[1];
		String username = args[2];
		String password = args[3];
		
		try{
			System.out.println("Tracing Path from " 
								+ start + " to "
								+ dest  + " with "
								+ username + " " + password);
			
			InetAddress startNode = InetAddress.getByName(start);
			InetAddress destNode  = InetAddress.getByName(dest);
			PathTraceTest pathTrace = new PathTraceTest(startNode, destNode, username, password);
			
		} catch (UnknownHostException | OnepIllegalArgumentException | OnepInvalidSettingsException | OnepConnectionException | OnepDuplicateElementException | OnepRemoteProcedureException e){
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public PathTraceTest(InetAddress startAddress, InetAddress destAddress, String username, String password) throws OnepIllegalArgumentException, OnepInvalidSettingsException, OnepConnectionException, OnepDuplicateElementException, OnepRemoteProcedureException{
		
		NetworkElement startNode = pathApplication.getNetworkElement(startAddress);
		SessionHandle nodeSession2 = startNode.connect(username, password);
		
		PathSpecifier pathSpecifier = new PathSpecifier(startAddress, 0, destAddress, 0, ProtocolType.TCP);
		
		PathTrace pathTrace = new PathTrace(ProfileType.ECHO, pathSpecifier);
		
		Route route = pathTrace.executeRequest(pathApplication.getNetworkElement(startAddress), 10);
		
		// Get the Route object using the graph returned from the request.
		System.out.println("Route status: " + route.getStatus());
		System.out.println("Route index: " + route.getRouteIndex());
		System.out.println("Profile type: " + route.getProfileType());
		System.out.println("Number of completed nodes: " +
		                 route.getNumOfNodesSuccessful());
		
        for (PathTraceNode node : route.getNodeList()) {
            System.out.println("Node hostname: " + node.getName());
            //if(node.getAddressList().size() > 0){
            //	getRoutes(node.getAddressList().get(0), username, password);
            //}
            ProfileType profileType = node.getProfileType();
            System.out.println("  Type: " + profileType);
            if (profileType == ProfileType.ECHO) {
            	EchoProfile echoProfile = (EchoProfile) node.getProfile();
            	System.out.println("IP address: " +
            						echoProfile.getReachabilityAddress().toString());
            	System.out.println("Input Int: " + 
            						echoProfile.getIngressInterfaceName());
            	System.out.println("Output Int: " +
            						echoProfile.getEgressInterfaceName().toString());
            }
        }
		return;
	}
	
	public void getRoutes(InetAddress nodeAdd, String username, String password){
		try {
			NetworkElement node = pathApplication.getNetworkElement(nodeAdd);
			SessionHandle nodeSession = node.connect(username, password);
			
			Routing routing = Routing.getInstance(node);
			
			RIB rib = routing.getRib();
			L3UnicastScope aL3UnicastScope = new L3UnicastScope("", AFIType.IPV4, SAFIType.UNICAST, "base");
			L3UnicastRIBFilter filter = new L3UnicastRIBFilter();
			NetworkPrefix prefix = new NetworkPrefix(node.getAddress(),16);
			L3UnicastRouteRange range = new L3UnicastRouteRange(prefix, RangeType.EQUAL_OR_LARGER, 20);
			
			
			List<com.cisco.onep.routing.Route> routes = rib.getRouteList(aL3UnicastScope,filter, range);
			
			for(com.cisco.onep.routing.Route route: routes){
				System.out.println("Route: " + route );
			}
			return;
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
