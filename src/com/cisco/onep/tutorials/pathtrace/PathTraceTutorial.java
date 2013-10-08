/*
 * Copyright (c) 2010-2013 by Cisco Systems, Inc.
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
 */
package com.cisco.onep.tutorials.pathtrace;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.pathtrace.CPUProfile;
import com.cisco.onep.pathtrace.PathSpecifier;
import com.cisco.onep.pathtrace.PathTrace;
import com.cisco.onep.pathtrace.PathTrace.ProtocolType;
import com.cisco.onep.pathtrace.PathTraceNode;
import com.cisco.onep.pathtrace.Profile.ProfileType;
import com.cisco.onep.pathtrace.Route;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * PathTrace Tutorial
 * 
 * Goal
 * 
 * PathTrace is a way of inspecting a group of onePK-enabled devices along the
 * route, or "path", between a pair of network devices. This tutorial is
 * intended for developers who wish to use the PathTrace service set.
 * 
 * Overview
 * 
 * To use PathTrace, a source and a destination node must first be specified,
 * which corresponds to a pair of PathTrace-enabled "initiator" and "responder"
 * devices, respectively. Nodes along the route connecting the two end nodes may
 * then be queried for a predefined profile and the responses are sent back to
 * the calling application.
 * 
 * Device Setup
 * 
 * In order to use the PathTrace service set, the following commands must be
 * issued on the end nodes. In this example, NE100 is the initiator and NE101 is
 * the responder.
 * 
 * <pre>
 * {@code
 * NE100>enable
 * NE100#configure terminal
 * Enter configuration commands, one per line.  End with CNTL/Z.
 * NE100(config)#onep
 * NE100(config-onep)#service set pathtrace 
 * NE100(config-onep)#exit
 * NE100(config)#mediatrace initiator source-ip 10.10.10.1
 * NE100(config)#end
 * }
 * </pre>
 * 
 * <pre>
 * {@code
 * NE101>enable
 * NE101#configure terminal
 * Enter configuration commands, one per line.  End with CNTL/Z.
 * NE101(config)#mediatrace responder 
 * NE101(config)#end
 * }
 * </pre>
 * 
 * You are now ready to run the tutorial.
 * 
 * @author The onePK Team (onepk-feedback@cisco.com)
 * 
 */
public class PathTraceTutorial extends BaseTutorial {
    
    private static final int TIMEOUT = 10;
    private String sourceAddr;
    private String destAddr;
    
    /**
     * Invokes the tutorial via the command line.
     * 
     * @param args
     */
    public static void main(String args[]) {
        PathTraceTutorial tutorial = new PathTraceTutorial();
        tutorial.parseOptions(args);
        try {
            if (!tutorial.connect("PathTraceTutorial")) {
                System.exit(1);
            }
            PathSpecifier pathSpecifier = tutorial.createPathSpecifier();
            Route route = tutorial.profileCPU(pathSpecifier);
            tutorial.displayRouteInformation(route);
            tutorial.displayAttributes(route);
           
        } catch (Exception e) {
            tutorial.disconnect();
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
        	tutorial.disconnect();
        	System.exit(0);
        }
    }
    
    /**
     * Creates a PathSpecifier based on the given source node address and
     * destination node address.
     * 
     * @return PathSpecifier object based on the source and destination.
     * @throws UnknownHostException
     *             thrown if an address is unknown or invalid.
     */
    public PathSpecifier createPathSpecifier() throws UnknownHostException {
        // START SNIPPET: createPathSpecifier
        InetAddress source = InetAddress.getByName(sourceAddr);
        InetAddress destination = InetAddress.getByName(destAddr);

        /* Construct a PathSpecifier object at the specified node endpoints.
         * An endpoint is defined by an address and a port number, with 0
         * denoting the default port number.
         */
        PathSpecifier pathSpecifier = new PathSpecifier(source, 0,
                                                        destination, 0,
                                                        ProtocolType.TCP);
        
        return pathSpecifier;
        // END SNIPPET: createPathSpecifier
    }
    
    /**
     * Will profile the CPU utilization for each hop along the PathSpecifier.
     * 
     * @param  pathSpecifier the PathSpecifier to be profiled.
     * @return               route that was profiled for CPU utilization.
     * @throws OnepException if any exception is thrown.
     */
    public Route profileCPU(PathSpecifier pathSpecifier) throws OnepException {
        // START SNIPPET: profileCPU
        PathTrace pathTrace = new PathTrace(ProfileType.CPU, pathSpecifier);
        Route route = pathTrace.executeRequest(getNetworkElement(), TIMEOUT);
        
        return route;
        // END SNIPPET: profileCPU
    }
    
    /**
     * Display the route information for the given route.
     * 
     * @param route obtained from profiling.
     */
    public void displayRouteInformation(Route route) {
        // START SNIPPET: displayRouteInformation
        // Get the Route object using the graph returned from the request.
        getLogger().info("Route status: " + route.getStatus());
        getLogger().info("Route index: " + route.getRouteIndex());
        getLogger().info("Profile type: " + route.getProfileType());
        getLogger().info("Number of completed nodes: " +
                         route.getNumOfNodesSuccessful());
        // END SNIPPET: displayRouteInformation
    }
    
    /**
     * Display the attributes of the given route.
     * 
     * @param route obtained from the profiling.
     */
    public void displayAttributes(Route route) {
        // START SNIPPET: displayAttributes
        for (PathTraceNode node : route.getNodeList()) {
            getLogger().info("Node hostname: " + node.getName());
            ProfileType profileType = node.getProfileType();
            getLogger().info("  Type: " + profileType);
            if (profileType == ProfileType.CPU) {
                CPUProfile cpuProfile = (CPUProfile) node.getProfile();
                getLogger().info("  One minute CPU utlization: " +
                                 cpuProfile.getOneMinuteCPUUtilization());
                getLogger().info("  Collection time stamp: " +
                                 cpuProfile.getCollectionTimeStamp());
            }
        }
        // END SNIPPET: displayAttributes
    }

    /**
     * @see com.cisco.onep.tutorials.BaseTutorial#parseOptions()
     */
    @Override
    public void parseOptions(String[] args) {
        super.parseOptions(args);

        if (args.length > 0) {
            for (int i = 0; i + 1 < args.length; i += 2) {
                if (args[i].equals("-s") ||
                           args[i].equals("--source")) {
                    sourceAddr = args[i + 1];
                } else if (args[i].equals("-d") ||
                           args[i].equals("--dest")) {
                    destAddr = args[i + 1];
                }
            }
        } else {
            Properties properties = getProperties();
            if (properties == null) {
                InputStream inputStream = null;
                try {
                    inputStream = this.getClass().getClassLoader().getResourceAsStream("tutorial.properties");
                    if (inputStream != null) {
                        properties = new Properties();
                        properties.load(inputStream);
                        setProperties(properties);
                    }
                } catch (Exception e) {
                    getLogger().error(e.getLocalizedMessage(), e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ioe) {
                            getLogger().error("Error in closing the tutorial.properties file: " + ioe.getMessage());
                        }
                    }
                }
            }

            if (properties != null) {
                sourceAddr = properties.getProperty("sourceAddress");
                destAddr = properties.getProperty("destinationAddress");
            }
        }

        if (sourceAddr == null || destAddr == null) {
            getLogger().info(getUsageString());
            System.exit(1);
        }
    }

    /**
     * @see com.cisco.onep.tutorials.BaseTutorial#getUsageRequiredOptions()
     */
    @Override
    public String getUsageRequiredOptions() {
        return super.getUsageRequiredOptions()
            + " -s <source addr>"
            + " -d <dest addr>";
    }

}
