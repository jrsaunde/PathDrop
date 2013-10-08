/**
* InterfaceAddressTutorial.java
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

import java.net.InetAddress;
import java.util.List;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants.OnepAddressScopeType;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.interfaces.NetworkPrefix;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial shows how to access and manipulate the interfaces addresses of a Network Element.
 * As a example to show how setAddress works, this tutorial gets all interfaces and sets the interface
 * address to the first address in the address list for the interface.
 * Note that it is not recommended to set the address to an arbitrary value,
 * as that may lead to loss of connectivity.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class InterfaceAddressTutorial extends BaseTutorial {

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        InterfaceAddressTutorial tutorial = new InterfaceAddressTutorial();

        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("InterfaceAddressTutorial")) {
                System.exit(1);
            }
            tutorial.logInterfaceAddresses();
            tutorial.logInterfacePrefixes();
            tutorial.setInterfaceAddress();
        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
            System.exit(0);
        }
    }

    /**
     * Gets all addresses for all interfaces and logs them.
     *
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: interfaceAddresses
    public void logInterfaceAddresses() throws OnepException {
        List<NetworkInterface> networkInterfaces = getAllInterfaces();
        List<InetAddress> addresses = null;
        if (networkInterfaces != null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                addresses = networkInterface.getAddressList();
                for (InetAddress address : addresses) {
                    if (address != null) {
                        getLogger().info("Interface - " + networkInterface + "\tAddress\t: " + address);
                    }
                }
            }
        }
    }
    // END SNIPPET: interfaceAddresses

    /**
     * Gets all prefixes for all interfaces and logs them.
     *
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: interfacePrefixes
    public void logInterfacePrefixes() throws OnepException {
        List<NetworkInterface> networkInterfaces = getAllInterfaces();
        List<NetworkPrefix> prefixes = null;
        if (networkInterfaces != null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                prefixes = networkInterface.getPrefixList();
                for (NetworkPrefix prefix : prefixes) {
                    if (prefix != null) {
                        getLogger().info("Interface - " + networkInterface + "\tPrefix\t: " + prefix.getPrefixLength());
                    }
                }
            }
        }
    }
    // END SNIPPET: interfacePrefixes

    /**
     * Gets all interfaces and sets the interface address to the first address in the address list for the interface.
     * Note that it is not recommended to set the address to an arbitrary value, as that may lead to loss of
     * connectivity.
     * In this example setAddress is shown for IPv4 with prefix length 24.
     * @throws OnepException
     *             If there is an error.
     */
    // START SNIPPET: setInterfaceAddress
    public void setInterfaceAddress() throws OnepException {
        List<NetworkInterface> networkInterfaces = getAllInterfaces();
        List<NetworkPrefix> prefixes = null;
        InetAddress inetAddress = null;
        int prefixLen = 24;
        if (networkInterfaces != null) {
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> addressList = networkInterface.getAddressList();
                prefixes = networkInterface.getPrefixList();
                if (addressList.size() > 0) {
                    inetAddress = addressList.get(0);
                } else {
                    continue;
                }
                for (NetworkPrefix prefix : prefixes) {
                    if (prefix != null && inetAddress != null) {
                        prefixLen = prefix.getPrefixLength();
                        networkInterface.setAddress(true, OnepAddressScopeType.ONEP_ADDRESS_IPv4_PRIMARY, inetAddress, prefixLen);
                        getLogger().info("Successfully reset " + networkInterface.getName() + "'s address to " + inetAddress);
                    }
                }
            }
        }
    }
    // END SNIPPET: setInterfaceAddress
}
