/**
* InterfaceStateChangeTutorial.java
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

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.interfaces.InterfaceFilter;
import com.cisco.onep.interfaces.InterfaceStateEvent;
import com.cisco.onep.interfaces.InterfaceStateListener;
import com.cisco.onep.interfaces.InterfaceStatus;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The goal of this tutorial is to show how to register for a state change event on the Network element.
 * When the monitored state change happens on the Network element an event is triggered and the state
 * change event handler is called. In this example the simply prints the state change details.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class InterfaceStateChangeTutorial extends BaseTutorial {

    private static final long SLEEP = 10000L;

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        InterfaceStateChangeTutorial tutorial = new InterfaceStateChangeTutorial();
        int elementEventHandle = -1;
        Integer[] interfaceEventHandles = null;

        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("InterfaceStateChangeTutorial")) {
                System.exit(1);
            }
            elementEventHandle = tutorial.registerStateChangeListenerOnElement();
            interfaceEventHandles = tutorial.registerStateChangeListenerOnInterfaces();
            /* Stimulate shutdown to trigger an event */
            tutorial.simulateShutdown(true);
            Thread.sleep(SLEEP);
            tutorial.simulateShutdown(false);
            Thread.sleep(SLEEP);

            /* remove the state change listeners on network element */
            if (elementEventHandle != -1)
                tutorial.getNetworkElement().removeInterfaceStateListener(elementEventHandle);

            /* remove the state change listeners on network interface */
            if (interfaceEventHandles!=null)
                tutorial.removeStateChangeListenerOnInterfaces(interfaceEventHandles);
        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
            System.exit(0);
        }
    }

    /**
     * Implements an InterfaceStateListener that logs the String representation of the InterfaceStateEvents received.
     * The listener is constructed with a name so that different instances can differentiate their logged output.
     *
     */
    // START SNIPPET: ExampleInterfaceStateListener
    class ExampleInterfaceStateListener implements InterfaceStateListener {

        private String name;

        public ExampleInterfaceStateListener(String name) {
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
         *
         *
         */

        public void handleEvent(InterfaceStateEvent event, Object clientData) {
            getLogger().info(name
                    + " has received event - from Event Handler "
                    + event.getStateEventType() + " for Interface "
                    + event.getInterface() + " that has state " + event.getState());
        }
    }

    // END SNIPPET: ExampleInterfaceStateListener

    /**
     * Creates an InterfaceStateListener and adds that as a listener, via the Network Element, for any state change
     * events for all interfaces on the Network Element.
     *
     * @return The event handle.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: registerStateChangeListenerOnElement
    public int registerStateChangeListenerOnElement() throws OnepException {
        InterfaceStateListener interfaceStateListener = new ExampleInterfaceStateListener("Element listener");
        InterfaceFilter filter = new InterfaceFilter();
        int eventHandle = -1;

        NetworkElement networkElement = getNetworkElement();
        eventHandle = networkElement.addInterfaceStateListener(interfaceStateListener, filter,
                InterfaceStatus.InterfaceStateEventType.ONEP_IF_STATE_EVENT_ANY, null);
        return eventHandle;
    }

    // END SNIPPET: registerStateChangeListenerOnElement

    /**
     * Creates an InterfaceStateListener and adds that as a listener, via the Network Interface, for any state change
     * events for all interfaces on the Network Element.
     *
     * @return An array of event handles.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: registerStateChangeListenerOnInterfaces
    public Integer[] registerStateChangeListenerOnInterfaces() throws OnepException {
        InterfaceStateListener interfaceStateListener = new ExampleInterfaceStateListener("Interface listener");
        if (getAllInterfaces() != null) {
            Iterator<NetworkInterface> iterator = getAllInterfaces().iterator();
            ArrayList<Integer> eventHandles = new ArrayList<Integer>();

            while (iterator.hasNext()) {
                eventHandles.add(iterator.next().addStateListener(interfaceStateListener,
                        InterfaceStatus.InterfaceStateEventType.ONEP_IF_STATE_EVENT_ANY, null));
            }
            return eventHandles.toArray(new Integer[eventHandles.size()]);
        }
        return null;
    }

    // END SNIPPET: registerStateChangeListenerOnInterfaces

    /**
     * Removes InterfaceStateListeners for any state change events for all interfaces on the Network Element.
     *
     * @param interfaceEventHandles
     *            Array of interface event handles obtained when adding listeners.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: removeStateChangeListenerOnInterfaces
    public void removeStateChangeListenerOnInterfaces(Integer[] interfaceEventHandles) throws OnepException {
        if (getAllInterfaces()!=null) {
            Iterator<NetworkInterface> iterator = getAllInterfaces().iterator();

             while (iterator.hasNext()) {
                 for (int i = 0; i < interfaceEventHandles.length; i++) {
                     iterator.next().removeStateListener(interfaceEventHandles[i]);
                 }
             }
        }
    }
    // END SNIPPET: removeStateChangeListenerOnInterfaces

}
