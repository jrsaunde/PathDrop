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

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.element.ElementLicense;
import com.cisco.onep.element.NetworkElement;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The Element License Tutorial demonstrates how to get the licensing
 * information and other identifying information on a network element.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class ElementLicenseTutorial extends BaseTutorial {

    /**
     * Prints the attributes of the Element License.
     *
     * @param elementLicense
     */
    public void getElementLicenseAttributes(ElementLicense elementLicense) {
        /*
         * Get the product identifier of the network element.
         */
        // START SNIPPET: get_el_pid
        String pid = elementLicense.getPID();
        // END SNIPPET: get_el_pid
        getLogger().info("pid - " + pid);

        /*
         * Get the serial number of the network element.
         */
        // START SNIPPET: get_el_sn
        String serialNumber = elementLicense.getSN();
        // END SNIPPET: get_el_sn
        getLogger().info("Serial Number - " + serialNumber);

        /*
         * Get the unique device identifier of the network element.
         */
        // START SNIPPET: get_el_udi
        String udi = elementLicense.getUDI();
        // END SNIPPET: get_el_udi
        getLogger().info("Unique Device Identifier (UDI)  - " + udi);

        /*
         * Get version ID of the network element.
         */
        // START SNIPPET: get_el_vid
        String vid = elementLicense.getVID();
        // END SNIPPET: get_el_vid
        getLogger().info("version ID (VID) - " + vid);
    }

    /**
     * Prints all the attributes of License of the network element.
     *
     * @param args
     */
    public static void main(String[] args) {
        ElementLicenseTutorial tutorial = new ElementLicenseTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("ElementLicenseTutorial")) {
                System.exit(1);
            }
            NetworkElement networkElement = tutorial.getNetworkElement();

            /*
             * Get a License element of the Network Element.
             */
            // START SNIPPET: get_ne_el
            ElementLicense elementLicense = networkElement.getLicensing();
            // END SNIPPET: get_ne_el
            tutorial.getElementLicenseAttributes(elementLicense);
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }
}
