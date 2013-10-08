/**
 * Copyright (c) 2010-2013, Cisco Systems, Inc.
 *
 * THIS SAMPLE CODE IS PROVIDED "AS IS" WITHOUT ANY EXPRESS OR IMPLIED WARRANTY BY CISCO SOLELY FOR THE PURPOSE of
 * PROVIDING PROGRAMMING EXAMPLES. CISCO SHALL NOT BE HELD LIABLE FOR ANY USE OF THE SAMPLE CODE IN ANY APPLICATION.
 *
 * Redistribution and use of the sample code, with or without modification, are permitted provided that the following
 * conditions are met: Redistributions of source code must retain the above disclaimer.
 *
 */
package com.cisco.onep.tutorials.discovery;

import java.util.List;
import java.util.Set;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.discovery.DiscoveryEvent;
import com.cisco.onep.discovery.DiscoveryFilter;
import com.cisco.onep.discovery.DiscoveryListener;
import com.cisco.onep.discovery.ServiceSetDescription;
import com.cisco.onep.discovery.ServiceSetDescription.ServiceSetName;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial gets and displays the list of onePK Service Sets
 * available on the target network device.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */

public class DiscoveringOnePKServiceSetsTutorial extends BaseTutorial {

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */

    public static void main(String args[]) {
        DiscoveringOnePKServiceSetsTutorial tutorial =
                                    new DiscoveringOnePKServiceSetsTutorial();

        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("ServiceDiscoveryTutorial")) {
                System.exit(1);
            }
            tutorial.getServiceSetDiscoveryList();
            /**
             * Add service discovery listener
             */
            // START SNIPPET: add_disc_filter
            DiscoveryFilter filter = new DiscoveryFilter();
            filter.setName(ServiceSetDescription.ServiceSetName.ONEP_BASE_SERVICE_SET);
            filter.setState(DiscoveryFilter.ServiceSetState.ONEP_SERVICE_SET_ENABLED);
            filter.setType(DiscoveryFilter.DiscoveryType.ONEP_DISCOVERY_ALL);
            // END SNIPPET: add_disc_filter
            System.out.println("Discovery filter:\n " +
                                "name = " + filter.getName() +
                                "\n state = " + filter.getState() +
                                "\n type = " + filter.getType());


            // START SNIPPET: add_disc_listener
            MyDiscoveryListener discoveryListener = new MyDiscoveryListener();
            tutorial.getNetworkElement().addDiscoveryListener(
                    discoveryListener,
                    filter,
                    null);
            // END SNIPPET: add_disc_listener
        } catch (Exception e) {
            tutorial.disconnect();
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
            System.exit(0);
        }
    }

    /**
     * Obtain and log the Service Sets.
     *
     * @throws OnepException
     *             If there is an error.
     */

    public void getServiceSetDiscoveryList() throws OnepException {
        // START SNIPPET: getServiceSetDiscoveryList
        List<ServiceSetDescription> serviceSetDescriptions = getNetworkElement().discoverServiceSetList();
        if (serviceSetDescriptions == null || serviceSetDescriptions.size() == 0) {
            getLogger().info("Empty Service Set Description list");
        } else {
            for (ServiceSetDescription description : serviceSetDescriptions) {
                getLogger().info(
                        "NetworkElement IPAddress " +
                        description.getNetworkElement().getDescription().getAddress());
                Set<ServiceSetName> serviceSetNames = description.getServiceSetList().keySet();
                if (serviceSetNames != null) {
                    for (ServiceSetName serviceName : serviceSetNames) {
                        getLogger().info("Service Name: " + serviceName);
                        String version = description.getServiceSetList().get(serviceName);
                        getLogger().info("Versions: " + version + "\n\n");
                    }
                }
            }
        }
        // END SNIPPET: getServiceSetDiscoveryList
    }

    /*
     * Discovery Listener
     */
    // START SNIPPET: disc_event_handler
    static class MyDiscoveryListener implements DiscoveryListener {
        public void handleEvent(DiscoveryEvent event, Object clientData) {
            System.out.println("handleEvent:" +" DiscoveryEvent " +
                                "name = " + event.getName() +
                                "state = "  +    event.getState() +
                                "type = " +    event.getType() +
                                "version = " + event.getVersion() +
                                "element = " + event.getNetworkElement().
                                                getDescription().getAddress());
        }

    }
    // END SNIPPET: disc_event_handler

}

