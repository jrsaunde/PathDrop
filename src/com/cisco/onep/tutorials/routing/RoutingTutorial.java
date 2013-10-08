/*
 * Copyright (c) 2010-2013, Cisco Systems, Inc.
 *
 * THIS SAMPLE CODE IS PROVIDED "AS IS" WITHOUT ANY EXPRESS OR IMPLIED WARRANTY
 * BY CISCO SOLELY FOR THE PURPOSE of PROVIDING PROGRAMMING EXAMPLES.
 * CISCO SHALL NOT BE HELD LIABLE FOR ANY USE OF THE SAMPLE CODE IN ANY
 * APPLICATION.
 *
 * Redistribution and use of the sample code, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * Redistributions of source code must retain the above disclaimer.
 *
 */
package com.cisco.onep.tutorials.routing;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cisco.onep.core.exception.OnepConnectionException;
import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.exception.OnepIllegalArgumentException;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.interfaces.NetworkPrefix;
import com.cisco.onep.routing.ARTRouteStateEvent;
import com.cisco.onep.routing.ARTRouteStateListener;
import com.cisco.onep.routing.AppRouteTable;
import com.cisco.onep.routing.L3UnicastNextHop;
import com.cisco.onep.routing.L3UnicastRIBFilter;
import com.cisco.onep.routing.L3UnicastRoute;
import com.cisco.onep.routing.L3UnicastRouteOperation;
import com.cisco.onep.routing.L3UnicastRouteRange;
import com.cisco.onep.routing.L3UnicastScope;
import com.cisco.onep.routing.L3UnicastScope.AFIType;
import com.cisco.onep.routing.L3UnicastScope.SAFIType;
import com.cisco.onep.routing.RIB;
import com.cisco.onep.routing.RIB.RouteStateListenerFlag;
import com.cisco.onep.routing.RIBRouteStateEvent;
import com.cisco.onep.routing.RIBRouteStateListener;
import com.cisco.onep.routing.Route;
import com.cisco.onep.routing.RouteOperation;
import com.cisco.onep.routing.RouteOperation.RouteOperationType;
import com.cisco.onep.routing.RouteRange;
import com.cisco.onep.routing.Routing;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial shows how to create Routing Service Set object, how to read RIB routes,
 * update application routes, add listeners for route up and down events in the RIB,
 * and add listeners for application routes.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class RoutingTutorial extends BaseTutorial {

    /**
     * Invokes the test via the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        RoutingTutorial tutorial = new RoutingTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("RoutingTutorial")) {
                System.exit(1);
            }

            // Create an Application Routing Table.
            AppRouteTable appRouteTable = tutorial.getAppRouteTable();

            // Grab the Routing Information Base
            RIB rib = tutorial.getRIB();

            // Add an ART listener to listen for changes in the ART.
            int aARTEventHandler = tutorial.addARTRouteListener(appRouteTable);

            // Add a RIB listener to listen for changes in the RIB.
            int aRIBEventHandler = tutorial.addRIBRouteListener(rib);

            if ((aARTEventHandler == -1) || (aRIBEventHandler == -1)) {
                tutorial.getLogger().error("Could not add one or more listeners.");
            } else {

                // Add custom application routes.
                tutorial.addRoutes(appRouteTable);

                // Removes the ART Listener.
                tutorial.removeARTRouteListener(appRouteTable, aARTEventHandler);

                // Removes the RIB Listener.
                tutorial.removeRIBRouteListener(rib, aRIBEventHandler);
            }
        } catch (UnknownHostException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }

    /**
     ** RIBRouteStateListener implemented as inner class.
     **/
    // START SNIPPET: ExampleRIBRouteListener
    public class ExampleRIBRouteListener implements RIBRouteStateListener {
        @Override
        public void handleEvent(RIBRouteStateEvent event, Object clientData) {
            getLogger().info("RIBRouteStateEvent received...");
            getLogger().info("Scope: " + event.getScope());
            getLogger().info("Route: " + event.getRoute());
        }
    }

    // END SNIPPET: ExampleRIBRouteListener

    /**
     ** ARTRouteStateListener implemented as inner class.
     **/
    // START SNIPPET: ExampleARTRouteListener
    public class ExampleARTRouteListener implements ARTRouteStateListener {
        @Override
        public void handleEvent(ARTRouteStateEvent event, Object clientData) {
            getLogger().info("ARTRouteStateEvent received...");
            getLogger().info("Scope: " + event.getScope());
            getLogger().info("Route: " + event.getRoute());
        }
    }

    // END SNIPPET: ExampleARTRouteListener

    /**
     * Gets the Application Route Table generated by this application.
     *
     * @return AppRouteTable for this application
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     */
    public AppRouteTable getAppRouteTable() throws OnepConnectionException {
        // START SNIPPET: getAppRouteTable
        // Create a Routing object for the network element.
        Routing routing = Routing.getInstance(getNetworkElement());

        // Get the instance of application route table.
        AppRouteTable appRouteTable = routing.getAppRouteTable();

        return appRouteTable;
        // END SNIPPET: getAppRouteTable
    }

    /**
     * Gets the RIB table for the connected network element.
     *
     * @return RIB table for the connected network element.
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     */
    public RIB getRIB() throws OnepConnectionException {
        // START SNIPPET: getRIB
        // Create a Routing object for the network element.
        Routing routing = Routing.getInstance(getNetworkElement());

        // Get the instance of the RIB table.
        RIB rib = routing.getRib();

        return rib;
        // END SNIPPET: getRIB
    }

    /**
     * Adds custom application routes to the network element.
     *
     * @param appRouteTable
     *            application route table to be updated
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     * @throws OnepIllegalArgumentException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     * @throws UnknownHostException
     *             as determined by the presentation layer semantics.
     */
    public void addRoutes(AppRouteTable appRouteTable) throws OnepConnectionException, OnepIllegalArgumentException,
            OnepRemoteProcedureException, OnepException, UnknownHostException {
        // START SNIPPET: addRoutes
        // Create a Routing object for the network element.
        Routing routing = Routing.getInstance(getNetworkElement());
        // Specify scope, filter and range;
        L3UnicastScope aL3UnicastScope = new L3UnicastScope("", AFIType.IPV4, SAFIType.UNICAST, "base");
        NetworkPrefix networkPrefix = new NetworkPrefix(InetAddress.getByName("10.0.0.0"), 32);
        L3UnicastRIBFilter filter = new L3UnicastRIBFilter();

        // Get the instance of RIB information.
        RIB rib = routing.getRib();

        L3UnicastRouteRange range = new L3UnicastRouteRange(networkPrefix, RouteRange.RangeType.EQUAL_OR_LARGER, 10);

        try {

            // Get all routes from RIB.
            List<Route> routeList = rib.getRouteList(aL3UnicastScope, filter, range);

            // Print the route in the list if it is a layer 3 unicast route.
            for (Route route : routeList) {
                if (route instanceof L3UnicastRoute) {
                    L3UnicastRoute l3uRoute = (L3UnicastRoute) route;
                    getLogger().info("Route: " + l3uRoute);
                }
            }
        } catch (OnepException e) {
            getLogger().error(e.getLocalizedMessage(), e);
        }

        // Create a new route and change its administrative distance
        // to make it more trusted. This operation will have the same effect
        // as the adding/replacing static route using the following IOS
        // config command:
        //
        // ip route 10.1.1.0 255.255.255.0 10.15.1.7
        //
        NetworkPrefix destNetwork = new NetworkPrefix(InetAddress.getByName("10.1.1.0"), 24);
        NetworkInterface networkInterface = getInterface();

        L3UnicastNextHop aL3UnicastNextHop = new L3UnicastNextHop(
                networkInterface, InetAddress.getByName("10.15.1.7"),
                new L3UnicastScope("", AFIType.IPV4, SAFIType.UNICAST, ""));
        Set<L3UnicastNextHop> aL3UnicastNextHopList = new HashSet<L3UnicastNextHop>();
        aL3UnicastNextHopList.add(aL3UnicastNextHop);
        L3UnicastRoute aRoute = new L3UnicastRoute(destNetwork, aL3UnicastNextHopList);
        aRoute.setAdminDistance(1);

        // Now update the app route table with this route.
        RouteOperation routeOperation = new L3UnicastRouteOperation(RouteOperationType.REPLACE, aRoute);
        List<RouteOperation> routeOperationList = new ArrayList<RouteOperation>();
        routeOperationList.add(routeOperation);
        appRouteTable.updateRoutes(aL3UnicastScope, routeOperationList);
        // END SNIPPET: addRoutes
    }

    /**
     * Adds an Application Routing Table Route Listener.
     *
     * @return Event handler identifier for the listener.
     * @param appRouteTable
     *            Application routing table to be added with an listener
     * @throws OnepException
     *             If there is an error.
     */
    public int addARTRouteListener(AppRouteTable appRouteTable) throws OnepException {
        L3UnicastScope aL3UnicastScope = new L3UnicastScope("", AFIType.IPV4, SAFIType.UNICAST, "base");
        // START SNIPPET: addARTRouteListener
        // Add a listener to receive route state change events.
        // When events arrive, listener.handleEvent() will be invoked.
        ExampleARTRouteListener exampleARTRouteListener1 = new ExampleARTRouteListener();
        getLogger().info("adding ART listener...");
        int exampleARTRouteListenerEventHandle = appRouteTable.addRouteStateListener(exampleARTRouteListener1,
                aL3UnicastScope, null);
        // END SNIPPET: addARTRouteListener
        return exampleARTRouteListenerEventHandle;
    }

    /**
     * Removes the Application Routing Table listener.
     *
     * @param appRouteTable
     *            ART associated with the listener.
     * @param eventHandler
     *            associated with the ART to be removed.
     * @throws OnepException
     *             If there is an error.
     */
    public void removeARTRouteListener(AppRouteTable appRouteTable, int eventHandler) throws OnepException {
        // START SNIPPET: removeARTRouteListener
        // Remove Application Route listener.
        getLogger().info("removing ART listener...");
        appRouteTable.removeRouteStateListener(eventHandler);
        // END SNIPPET: removeARTRouteListener
    }

    /**
     * Adds a RIB Route listener.
     *
     * @return Event handler identifier for the listener.
     * @param rib
     *            Routing Information Base to be applied
     * @throws OnepException
     *             If there is an error.
     */
    public int addRIBRouteListener(RIB rib) throws OnepException {
        // START SNIPPET: addRIBRouteListener
        L3UnicastScope aL3UnicastScope = new L3UnicastScope("", AFIType.IPV4, SAFIType.UNICAST, "base");
        L3UnicastRIBFilter filter = new L3UnicastRIBFilter();

        // Add a listener to receive route state change events.
        // When events arrive, listener.handleEvent() will be invoked.
        ExampleRIBRouteListener exampleRIBRouteListener = new ExampleRIBRouteListener();
        getLogger().info("adding RIB listener...");
        int exampleRIBRouteListenerEventHandle = rib.addRouteStateListener(exampleRIBRouteListener, aL3UnicastScope,
                filter, RouteStateListenerFlag.TRIGER_INITIAL_WALK, null);
        // END SNIPPET: addRIBRouteListener
        return exampleRIBRouteListenerEventHandle;
    }

    /**
     * Remove the RIB listener.
     *
     * @param rib
     *            RIB associated with listener to be removed
     * @param eventHandler
     *            associated with the RIB to be removed
     * @throws OnepIllegalArgumentException
     * @throws OnepRemoteProcedureException
     * @throws OnepConnectionException
     */
    public void removeRIBRouteListener(RIB rib, int eventHandler) throws OnepIllegalArgumentException,
            OnepRemoteProcedureException, OnepConnectionException {
        // START SNIPPET: removeRIBRouteListener
        // Remove RIB listener.
        getLogger().info("removing RIB listener...");
        rib.removeRouteStateListener(eventHandler);
        // END SNIPPET: removeRIBRouteListener
    }

}
