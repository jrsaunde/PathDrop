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
package com.cisco.onep.tutorials.element;

import java.util.List;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The Network Element Tutorial demonstrates some of the API pertaining to the
 * network element, such as getting the network elements the application is
 * conected to and querying usage statistics from them.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class NetworkElementTutorial extends BaseTutorial {

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        NetworkElementTutorial tutorial = new NetworkElementTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("NetworkElementTutorial")) {
                System.exit(1);
            }
            NetworkElement networkElement = tutorial.getNetworkElement();

            /*
             * Get the parent network application that instantiated this network element.
             */
            // START SNIPPET: get_ne_parent
            NetworkApplication networkApplication = networkElement.getParent();
            // END SNIPPET: get_ne_parent

            /*
             * Get Network Element Count in a Network Application
             */
            // START SNIPPET: get_ne_count
            int numberOfElements = networkApplication.getConnectedElementCount();
            tutorial.getLogger().info("Number of Network Elements for this Network Application - " + numberOfElements);
            // END SNIPPET: get_ne_count

            /*
             * Get Network Element List in a Network Application
             */
            // START SNIPPET: get_ne_list
            List<NetworkElement> elementList = networkApplication.getElementList();
            // END SNIPPET: get_ne_list
            for (NetworkElement tNetworkElement : elementList) {
                // START SNIPPET: get_ne_tostring
                tutorial.getLogger().info(tNetworkElement.toString());
                // END SNIPPET: get_ne_tostring
            }

            /*
             * Get Network Element Address
             */
            // START SNIPPET: get_ne_address
            tutorial.getLogger().info("NetworkElement Address - " + networkElement.getAddress());
            // END SNIPPET: get_ne_address

            /*
             * Check if Network Element is connected
             */
            // START SNIPPET: ne_isconnected
            tutorial.getLogger().info("NetworkElement is connected - " + networkElement.isConnected());
            // END SNIPPET: ne_isconnected

            /*
             * Get Free System Memory of a Network Element
             */
            // START SNIPPET: get_ne_fsm
            tutorial.getLogger().info("Free System Memory - " + networkElement.getFreeSystemMemory());
            // END SNIPPET: get_ne_fsm

            /*
             * Get Total System Memory of a Network Element
             */
            // START SNIPPET: get_ne_tsm
            tutorial.getLogger().info("Total System Memory - " + networkElement.getTotalSystemMemory());
            // END SNIPPET: get_ne_tsm

            /*
             * Get Total CPU Utilization of a Network Element
             */
            // START SNIPPET: get_ne_cpuutil
            tutorial.getLogger().info("Total System CPU Utilization - " + networkElement.getSystemCPUUtilization());
            // END SNIPPET: get_ne_cpuutil
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            // START SNIPPET: disconnect_ne
            tutorial.disconnect();
            // END SNIPPET: disconnect_ne
        }
    }
}
