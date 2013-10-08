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

package com.cisco.onep.tutorials.policy;

import java.net.InetAddress;
import java.util.List;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants.OnepAddressFamilyType;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.policy.Acl;
import com.cisco.onep.policy.L3Ace;
import com.cisco.onep.policy.L3Acl;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial show how to create an L3 ACL, and create an L3 ACE with TCP protocol.
 * The ACE will then be added to the ACL which will be applied an interface.
 * In this example the first interface connected to the network element will be used.
 * Results could be verified on the network element using the following commands:
 *  #show ip access-list dynamic
 *  #show ip interface <interface_name> | i access list
 */

public class ACLTutorial extends BaseTutorial {

    private static final long TEN_SECONDS = 10000L;
    private static final int SEQUENCE_NUMBER = 10;

    /**
     * Invokes the test via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        ACLTutorial tutorial = new ACLTutorial();

        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("ACLTutorial")) {
                System.exit(1);
            }

            // Create a L3 IPv4 Access Control List
            L3Acl accessControlList = new L3Acl(tutorial.getNetworkElement(), OnepAddressFamilyType.ONEP_AF_INET);

            // Create Access Control Element
            L3Ace accessControlElement = tutorial.createAccessControlElement();

            // START SNIPPET: addACEtoACL
            // Add Access Control Element to Access Control List
            accessControlList.addAce(accessControlElement);
            // END SNIPPET: addACEtoACL

            // Apply Access Control Element to Interface
            List<NetworkInterface> networkInterfaces = tutorial.getAllInterfaces();
            List<InetAddress> addressList = null;
            boolean applicationInterface = false;
            NetworkInterface testNetworkInterface = null;
            int i = 0;

            // Loop over all interfaces
            if (networkInterfaces != null) {
                for (NetworkInterface networkInterface : networkInterfaces) {
                    addressList = networkInterface.getAddressList();
                    // Loop over all addresses for an interface
                    if (addressList != null) {
                        for (InetAddress inetAddress : addressList) {
                            // Check that this is not the interface that the NetworkApplication is connected to. If it is then move
                            // onto the next interface.
                            if (inetAddress.equals(tutorial.getElementInetAddress())) {
                                applicationInterface = true;
                                break;
                            }
                        }
                    }
                    if (applicationInterface) {
                        applicationInterface = false;
                        continue;
                    }
                    testNetworkInterface = networkInterface;
                    if (i > 0) {
                        break;
                    }
                    i++;
                }
            }
            if (testNetworkInterface != null) {
                // START SNIPPET: applyAcl
                // Apply ACL to the interface
                accessControlList.applyToInterface(testNetworkInterface,Acl.Direction.ONEP_DIRECTION_BOTH);
                // END SNIPPET: applyAcl

                tutorial.getLogger().info("Applied ACL to interface = " + testNetworkInterface.getName());
                tutorial.getLogger().info("Verify on network element using:" );
                tutorial.getLogger().info("#show ip access-list dynamic");
                tutorial.getLogger().info("#show ip interface " + testNetworkInterface.getName() + " | i access list");

                Thread.sleep(TEN_SECONDS);

                // Clear the Access Control List match count.
                accessControlList.clearMatch();
                // Remove Access Control Element from ACL.
                accessControlList.removeAce(accessControlElement);
                // Remove ACL from interface.
                // START SNIPPET: removeAcl
                accessControlList.removeFromInterface(testNetworkInterface,    Acl.Direction.ONEP_DIRECTION_BOTH);
                // END SNIPPET: removeAcl
            } else {
                tutorial.getLogger().info("No Ethernet interface found.");
                tutorial.getLogger().info("Couldn't apply Access Control Element to Interface.");
            }
        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        }
        finally {
            tutorial.disconnect();
        }
        System.exit(0);
    }

    /**
     * Creates a Layer 3 access control element with specific options.
     *
     * @return L3Ace containing options applied
     * @throws OnepException
     *             if an exception is thrown
     */
    public L3Ace createAccessControlElement() throws OnepException {
        // START SNIPPET: createAccessControlElement
        // Creates a new Access Control Element w/ Sequence Number 10 and this
        // will "deny" the following conditions.
        L3Ace accessControlElement = new L3Ace(SEQUENCE_NUMBER, false);

        // Set the protocol to TCP protocol.
        accessControlElement.setProtocol(Acl.AclProtocol.TCP);

        // Permit any source prefix.
        accessControlElement.setSrcPrefixAny();

        // Permit any destination prefix.
        accessControlElement.setDstPrefixAny();

        // Set source port to 0 and everything greater
        accessControlElement.setSrcPort((short) 0, L3Ace.PortOperator.ONEP_OPER_GT);

        // Set destination port to 0 and everything greater
        accessControlElement.setDstPort((short) 0, L3Ace.PortOperator.ONEP_OPER_GT);

        // Log the Access Control Element.
        accessControlElement.setLogFlag(L3Ace.LogFlag.ONEP_ACL_LOG_NORMAL);

        return accessControlElement;
        // END SNIPPET: createAccessControlElement
    }
}
