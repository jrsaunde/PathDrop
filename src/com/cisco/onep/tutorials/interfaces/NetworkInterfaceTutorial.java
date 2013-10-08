/**
* NetworkInterfaceTutorial.java
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
import java.util.Collection;
import java.util.List;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.interfaces.InterfaceConfig;
import com.cisco.onep.interfaces.InterfaceFilter;
import com.cisco.onep.interfaces.InterfaceProperty;
import com.cisco.onep.interfaces.InterfaceStatus;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.interfaces.NetworkInterface.Type;
import com.cisco.onep.interfaces.VLAN;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The goal of this tutorial is to show how to access Interfaces(ports/links), and their properties from a
 * onePK-enabled Network Element.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class NetworkInterfaceTutorial extends BaseTutorial {

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        NetworkInterfaceTutorial tutorial = new NetworkInterfaceTutorial();
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();
        List<InterfaceConfig> interfaceConfigList = new ArrayList<InterfaceConfig>();
        List<InterfaceStatus> intfStatus = new ArrayList<InterfaceStatus>();

        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("NetworkInterfaceTutorial")) {
                System.exit(1);
            }

            /* Get all Interfaces */
            networkInterfaces = tutorial.getAllInterfaces();
            tutorial.getLogger().info("Printing all interfaces...");
            if (networkInterfaces != null) {
                for (NetworkInterface networkInterface : networkInterfaces) {
                    tutorial.getLogger().info("\t" + networkInterface.toString());
                }
            }

            /* Get Ethernet Interfaces */
            networkInterfaces = tutorial.getEthernetInterfaces();
            if (networkInterfaces != null) {
                tutorial.getLogger().info("Printing all Ethernet interfaces...");
                for (NetworkInterface networkInterface : networkInterfaces) {
                       tutorial.getLogger().info("\t" + networkInterface.toString());
                }
            }

            /* Get Hardware properties of Ethernet interfaces */
            tutorial.getLogger().info("Printing all interfaces hardware properties...");
            tutorial.getInterfaceHardwareProperties();

            /* Get all interface config data */
            interfaceConfigList = tutorial.getInterfaceConfigurations();
            tutorial.getLogger().info("Printing all interfaces config data...");
            for (InterfaceConfig interfaceConfig : interfaceConfigList) {
                tutorial.getLogger().info("\t" + interfaceConfig.toString());
            }

            /* Get all parent interfaces */
            networkInterfaces = tutorial.getParentInterfaces();
            tutorial.getLogger().info("Printing all Parent interfaces...");
            for (NetworkInterface networkInterface : networkInterfaces) {
                tutorial.getLogger().info("\t" + networkInterface.toString());
            }

            /* Get all sub interfaces */
            networkInterfaces = tutorial.getSubInterfaces();
            tutorial.getLogger().info("Printing all subinterfaces...");
            for (NetworkInterface networkInterface : networkInterfaces) {
                tutorial.getLogger().info("\t" + networkInterface.toString());
            }

            /* Get all interface status */
            intfStatus = tutorial.getInterfaceStatus();
            tutorial.getLogger().info("Printing status of all interfaces...");
            for (InterfaceStatus intfs : intfStatus) {
                tutorial.getLogger().info("\t" + intfs.toString());
            }

            /* Get VLAN interface info */
            tutorial.getVlanInterfaces();

        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
            System.exit(0);
        }
    }

    /**
     * Gets InterfaceStatus elements for all Network Interfaces.
     * The status is a snapshot of the current condition of the NetworkInterface attributes.
     *
     * @return List of InterfaceStatus instances which has status attributes keys and values
     * @throws OnepException
     *             If there is an error.
     *
     */
    // START SNIPPET: getInterfaceStatus
    public List<InterfaceStatus> getInterfaceStatus() throws OnepException {

        // Create a list of InterfaceStatus for the status information we get from the NetworkInterface
        List<InterfaceStatus> statusList = new ArrayList<InterfaceStatus>();

        // Get all NetworkInterfaces on the NetworkElement
        List<NetworkInterface> networkInterfaces = getAllInterfaces();

        // For each NetworkInterface, obtain the status information and put it into the statusList
        if (networkInterfaces!=null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                statusList.add(networkInterface.getStatus());
            }
        }
        return statusList;
    }
    // END SNIPPET: getInterfaceStatus

    /**
     * Obtains the list of Ethernet interfaces on a Network Element.
     *
     * @return List of Network Interface instances.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: getEthernetInterfaces
    public List<NetworkInterface> getEthernetInterfaces() throws OnepException {
        /* First create an InterfaceFilter for Ethernet interfaces */
        InterfaceFilter ethernetFilter = new InterfaceFilter(null, NetworkInterface.Type.ONEP_IF_TYPE_ETHERNET);
        List<NetworkInterface> interfaceList = null;

        /* Then get the interfaces from the NetworkElement using the filter */
        interfaceList = getNetworkElement().getInterfaceList(ethernetFilter);
        return interfaceList;
    }

    // END SNIPPET: getEthernetInterfaces

    /**
     * Gets Interface Properties, that is, hardware properties, for all Network Interfaces.
     *
     * @return List of InterfaceProperty instances.
     * @throws OnepException
     *             If there is an error.
     *
     */
    // START SNIPPET: getInterfaceHardwareProperties
    public void getInterfaceHardwareProperties() throws OnepException {

        /* Get all NetworkInterfaces on the NetworkElement */
        List<NetworkInterface> networkInterfaces = getAllInterfaces();

        /* For each NetworkInterface obtain the properties */
        if (networkInterfaces != null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                InterfaceProperty itp = networkInterface.getProperty();
                getLogger().info(networkInterface.getName()
                        + " Port = " + itp.getPort()
                        + " Slot = " + itp.getSlot());
            }
        }
        return;
    }
    // END SNIPPET: getInterfaceHardwareProperties

   /**
     * Gets InterfaceConfigs, that is, configuration properties, for all Network Interfaces.
     *
     * @return List of InterfaceConfig instances.
     * @throws OnepException
     *             If there is an error.
     *
     */
    // START SNIPPET: getInterfaceConfigurations
    public List<InterfaceConfig> getInterfaceConfigurations() throws OnepException {
        /* Create a list of InterfaceConfig to hold the configuration we get from the NetworkInterface */
        List<InterfaceConfig> configList = new ArrayList<InterfaceConfig>();

        /* Get all NetworkInterfaces on the NetworkElement */
        List<NetworkInterface> networkInterfaces = getAllInterfaces();

        /* For each NetworkInterface obtain the configuration and put it into the configList */
        if (networkInterfaces != null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                InterfaceConfig config = networkInterface.getConfig();
                configList.add(config);
            }
        }
        return configList;
    }
    // END SNIPPET: getInterfaceConfigurations

    /**
     * Gets parent NetworkInterface for all sub-interfaces.
     *
     * @return List of NetworkInterface instances.
     * @throws OnepException
     *             If there is an error.
     *
     */
    // START SNIPPET: getParentInterfaces
    public List<NetworkInterface> getParentInterfaces() throws OnepException {

        /* Create an empty List to hold the parent NetworkInterfaces */
        List<NetworkInterface> parentInterfaces = new ArrayList<NetworkInterface>();
        NetworkInterface parent = null;

        /* Get all NetworkInterfaces on the NetworkElement */
        List<NetworkInterface> networkInterfaces = getAllInterfaces();

        /* Loop over all NetworkInterfaces */
        if (networkInterfaces != null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                /* Get the parent NetworkInterface for a given NetworkInterface in the list */
                parent = networkInterface.getParent();

                /* If there is a parent, then add it to the list of parent NetworkInterfaces */
                if (parent != null) {
                    parentInterfaces.add(parent);
                }
            }
        }
        return parentInterfaces;
    }

    // END SNIPPET: getParentInterfaces

    /**
     * Gets a single list of sub interfaces for all parent interfaces.
     *
     *
     * @return List of NetworkInterface instances.
     * @throws OnepException
     *             If there is an error.
     *
     */
    // START SNIPPET: getSubInterfaces
    public List<NetworkInterface> getSubInterfaces() throws OnepException {

        /* Create an empty list to hold the subInterfaces */
        List<NetworkInterface> subInterfacesAll = new ArrayList<NetworkInterface>();

        /*
         * The collection of all available subinterfaces for this base interface,
         * the collection includes the base interface itself.
         * If no subinterfaces under the interface, the interface itself is returned.
         */
        Collection<NetworkInterface> subInterfaces = null;

        /* Get an list for all parent interfaces */
        List<NetworkInterface> parentInterfaces = getParentInterfaces();

        /* Loop over all parent NetworkInterfaces */
        for (NetworkInterface parentInterface:parentInterfaces) {
            /* Get the list of sub NetworkInterfaces for the parent interface */
            if (parentInterface.getSubInterfaceList()!=null && !parentInterface.getSubInterfaceList().isEmpty())
                subInterfaces = parentInterface.getSubInterfaceList().values();

            /* Loop over the sub NetworkInterfaces and add them to the subInterfaces list */
            if (subInterfaces != null) {
                for (NetworkInterface subInterface:subInterfaces) {
                    subInterfacesAll.add(subInterface);
                }
            }
        }
        return subInterfacesAll;
    }
    // END SNIPPET: getSubInterfaces

    /**
     * Gets the Layer 3 VLAN information of the network interface.
     *
     * @return String
     * @throws OnepException if there is an error.
     */
    // START SNIPPET: getVlanInterfaces
    public void getVlanInterfaces() throws OnepException{
        List<NetworkInterface> networkInterfaces = getAllInterfaces();
        VLAN vlan = null;
        if (networkInterfaces != null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                /* if the interface is of type VLAN, then get VLAN info */
                 if (networkInterface.getType() == Type.ONEP_IF_TYPE_VLAN) {
                    vlan = networkInterface.getVLAN();
                    if (vlan != null) {
                        getLogger().info("" + networkInterface.getName() + " is a VLAN interface");
                        return;
                    }
                }
            }
        }
        getLogger().info("No Vlan Interface found");
    }
    // END SNIPPET: getVlanInterfaces

}
