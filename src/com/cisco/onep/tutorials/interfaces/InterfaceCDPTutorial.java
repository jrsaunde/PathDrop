/**
* InterfaceCDPTutorial.java
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

import com.cisco.onep.cdp.CDPEvent;
import com.cisco.onep.cdp.CDPEvent.CDPEventNotifyType;
import com.cisco.onep.cdp.CDPFilter;
import com.cisco.onep.cdp.CDPListener;
import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The goal of this tutorial is to add a CDP (Cisco Discovery Protocol)
 * listener on a Network interface. As a CDP-configured network interface
 * sends periodic messages to a multicast address, they are sent to the registered onePK
 * application through a callback mechanism.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class InterfaceCDPTutorial extends BaseTutorial{

    private static final long TEN_SEC = 10000L;

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        InterfaceCDPTutorial tutorial = new InterfaceCDPTutorial();

        tutorial.parseOptions(args);

        //Array of interface event handles obtained when adding listeners.
        Integer[] interfaceEventHandles = null;

        try {
            if (!tutorial.connect("InterfaceCDPTutorial")) {
                System.exit(1);
            }
            interfaceEventHandles = tutorial.registerCDPListenerOnInterfaces();
            Thread.sleep(TEN_SEC);
            tutorial.removeCDPListenerOnInterfaces(interfaceEventHandles);
        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
            System.exit(0);
        }
    }

    /**
     * Implements an CDPListener that logs the String representation of the CDPEvent received.
     * The listener is constructed with a name so that different listener instances can
     * differentiate their logged output.
     * To trigger an event execute 'cdp enable' on one of the interfaces.
     */
    // START SNIPPET: ExampleInterfaceCDPListener
    class ExampleInterfaceCDPListener implements CDPListener {
        private String name;
        public ExampleInterfaceCDPListener(String name) {
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

        public void handleEvent(CDPEvent event, Object clientData) {
            getLogger().info(name + " has received event - " + event.getInterface().getName());
        }
    }

    // END SNIPPET: ExampleInterfaceCDPListener

    /**
     * Creates an CDPListener and adds that as a listener, via the Network Interface, for any CDP
     * change events for all interfaces on the Network Element.
     *
     * @return An array of event handles.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: registerCDPListenerOnInterfaces
    public Integer[] registerCDPListenerOnInterfaces() throws OnepException {
        ExampleInterfaceCDPListener cdpListener = new ExampleInterfaceCDPListener(
                "Interface CDP listener");
        List<NetworkInterface> networkInterfaces = getAllInterfaces();
        ArrayList<Integer> eventHandles = new ArrayList<Integer>();
        CDPFilter filter = new CDPFilter();
        filter.setNotifyType(CDPEventNotifyType.ONEP_CDP_ALL);
        if (networkInterfaces != null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                eventHandles.add(networkInterface.addCDPListener(cdpListener, filter, null));
            }
        }
        getLogger().info("CDP change listener is added on all interfaces.\n");
        return eventHandles.toArray(new Integer[eventHandles.size()]);
    }
    // END SNIPPET: registerCDPListenerOnInterfaces

    /**
     * Removes CDPListener events for all interfaces on the Network Element.
     *
     * @param interfaceEventHandles
     *            Array of interface event handles obtained when adding listeners.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: removeCDPListenerOnInterfaces
    public void removeCDPListenerOnInterfaces(Integer[] interfaceEventHandles) throws OnepException {
        getLogger().info("Removing CDP change listener.\n");
        if (getAllInterfaces() != null) {
            Iterator<NetworkInterface> iterator = getAllInterfaces().iterator();
            while (iterator.hasNext()) {
                for (int i = 0; i < interfaceEventHandles.length; i++) {
                    iterator.next().removeStateListener(interfaceEventHandles[i]);
                }
            }
        }
    }
    // END SNIPPET: removeCDPListenerOnInterfaces
}
