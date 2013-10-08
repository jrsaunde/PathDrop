/**
* InterfaceStatisticsTutorial.java
*
* Copyright (c) 2012-2013, Cisco Systems, Inc.
*
* THIS SAMPLE CODE IS PROVIDED "AS IS" WITHOUT ANY EXPRESS OR IMPLIED WARRANTY
* BY CISCO SOLELY FOR THE PURPOSE of PROVIDING PROGRAMMING EXAMPLES.
* CISCO SHALL NOT BE HELD LIABLE FOR ANY USE OF THE SAMPLE CODE IN ANY
* APPLICATION.
*
* Redistribution and use of the sample code, with or without modification, is subject to the terms and conditions of the
* Cisco onePK Software Development Kit License Agreement (onePK SDK Internal User License).
*
*/

package com.cisco.onep.tutorials.interfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants.OnepOperatorType;
import com.cisco.onep.interfaces.InterfaceStatistics;
import com.cisco.onep.interfaces.InterfaceStatistics.InterfaceStatisticsParameter;
import com.cisco.onep.interfaces.InterfaceStatisticsEvent;
import com.cisco.onep.interfaces.InterfaceStatisticsFilter;
import com.cisco.onep.interfaces.InterfaceStatisticsFilter.InterfaceStatisticsType;
import com.cisco.onep.interfaces.InterfaceStatisticsListener;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial shows how a onePK application can obtain network interface statistics
 * via two different methods: either polling for statistics or
 * by registering a "listener" to retrieve statistics from an interface
 * only when the data crosses a certain threshold.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class InterfaceStatisticsTutorial extends BaseTutorial {

    private static final long TEN_SEC = 10000L;
    private static final long POLL_SLEEP = 600L;
    private static final int POLL_LOOP = 2;

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        InterfaceStatisticsTutorial tutorial = new InterfaceStatisticsTutorial();

        tutorial.parseOptions(args);

        Integer[] interfaceEventHandles = null;

        try {
            if (!tutorial.connect("InterfaceStatisticsTutorial")) {
                System.exit(1);
            }

            /* register the listeners on interfaces */
            interfaceEventHandles = tutorial.registerStatisticsListenerOnInterfaces();
            Thread.sleep(TEN_SEC);
            tutorial.getLogger().info("Poll Statistics");
            tutorial.pollStatistics();

            /* remove the listeners on interfaces */
            if (interfaceEventHandles!=null)
                tutorial.removeStatisticsListenerOnInterfaces(interfaceEventHandles);
        } catch (Exception e) {
            tutorial.disconnect();
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        }
        tutorial.disconnect();
        System.exit(0);
    }

    /**
     * Implements an InterfaceStatisticsListener that logs the String representation of the InterfaceStatisticsEvent
     * received. The listener is constructed with a name so that different listener instances can differentiate their
     * logged output.
     */
    // START SNIPPET: ExampleInterfaceStatisticsListener
    class ExampleInterfaceStatisticsListener implements InterfaceStatisticsListener {

        private String name;

        public ExampleInterfaceStatisticsListener(String name) {
            this.name = name;
        }

        /**
         * Invoked when an event is received from a network element.
         *
         * @param event
         *            An event object that indicates that an event has occurred in a network element.
         * @param clientData
         *            The clientData is an object that is passed in when the application calls an API to add/register
         *            the event listener. The application is responsible for casting the input clientData to the
         *            appropriate class before using it.
         */

        public void handleEvent(InterfaceStatisticsEvent event, Object clientData) {
            getLogger().info(name + " has received event - " + event.getParameter().toString());
        }
    }

    // END SNIPPET: ExampleInterfaceStatisticsListener

    /**
     * Gets statistics from NetworkInterfaces on a periodic basis, that is, using a poll loop.
     *
     * @throws OnepException
     *             If there is a error.
     * @throws InterruptedException
     *             If there is an interruption when sleeping between polls.
     */
    // START SNIPPET: pollStatistics
    public void pollStatistics() throws OnepException, InterruptedException {
        for (int i = 0; i < POLL_LOOP; i++) {
            /* Note that we get the NetworkInterfaces within the loop in case
             * interfaces have come or gone within the polling period.
             */
            List<NetworkInterface> networkInterfaces = getAllInterfaces();
            if (networkInterfaces != null) {
                for (NetworkInterface networkInterface : networkInterfaces) {
                    InterfaceStatistics statistics = networkInterface.getStatistics();
                    getLogger().info("-------- " + networkInterface.getName() + " ----------");
                    getLogger().info("CRC Errors: " + statistics.getInErrorCRC());
                    getLogger().info("Frame Errors:" + statistics.getInErrorFrame());
                    getLogger().info("Receive Rate (in BPS): " + statistics.getReceiveRateBPS());
                    getLogger().info("Received multicast packets: " + statistics.getReceiveMulticast());
                }
            }
            Thread.sleep(POLL_SLEEP);
        }
    }
    // END SNIPPET: pollStatistics

    /**
     * Creates an InterfaceStatisticsListener and adds that as a listener, via the Network Interface, for any statistics
     * change events for all interfaces on the Network Element.
     *
     * @return An array of event handles.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: registerStatisticsListenerOnInterfaces
    public Integer[] registerStatisticsListenerOnInterfaces() throws OnepException {
        ExampleInterfaceStatisticsListener interfaceStatisticsListener = new ExampleInterfaceStatisticsListener(
                "Interface listener");
        List<NetworkInterface> networkInterfaces = getAllInterfaces();
        if (networkInterfaces != null) {
            ArrayList<Integer> eventHandles = new ArrayList<Integer>();
            InterfaceStatisticsFilter filter = new InterfaceStatisticsFilter(
                    InterfaceStatisticsParameter.ONEP_IF_STAT_TX_LOAD,
                    OnepOperatorType.ONEP_OP_GT,
                    0,
                    InterfaceStatisticsType.ONEP_INTERFACE_STATISTICS_TYPE_VALUE);

            for (NetworkInterface networkInterface : networkInterfaces) {
                eventHandles.add(networkInterface.addStatisticsListener(interfaceStatisticsListener, filter, null));
            }
            getLogger().info("Added statistics listener on the Transmit rate of the interface.\n");
            return eventHandles.toArray(new Integer[eventHandles.size()]);
        }
        return null;
    }
    // END SNIPPET: registerStatisticsListenerOnInterfaces

    /**
     * Removes InterfaceStatisticsListener for any statistics events for all interfaces on the Network Element.
     *
     * @param interfaceEventHandles
     *            Array of interface event handles obtained when adding listeners.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: removeStatisticsListenerOnInterfaces
    public void removeStatisticsListenerOnInterfaces(Integer[] interfaceEventHandles) throws OnepException {
        if (getAllInterfaces()!=null) {
            Iterator<NetworkInterface> iterator = getAllInterfaces().iterator();
            while (iterator.hasNext()) {
                for (int i = 0; i < interfaceEventHandles.length; i++) {
                    iterator.next().removeStateListener(interfaceEventHandles[i]);
                }
            }
        }
    }
    // END SNIPPET: removeStatisticsListenerOnInterfaces

}
