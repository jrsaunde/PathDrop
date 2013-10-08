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
import com.cisco.onep.element.ElementProperty;
import com.cisco.onep.element.NetworkElement;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The Element PropertyTutorial demonstrates get information about the system
 * properties of a network element.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class ElementPropertyTutorial extends BaseTutorial {

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        ElementPropertyTutorial tutorial = new ElementPropertyTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("ElementPropertyTutorial")) {
                System.exit(1);
            }
            NetworkElement networkElement = tutorial.getNetworkElement();

            /*
             * Get a ElementProperty instance from the network element.
             */
            // START SNIPPET: get_ne_prop
            ElementProperty elementProperty = networkElement.getProperty();
            // END SNIPPET: get_ne_prop
            tutorial.getElementPropertyAttributes(elementProperty);
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }

    /**
     * Prints all the attributes of Property of a Network element.
     *
     * @param elementProperty
     * @throws OnepException
     */
    public void getElementPropertyAttributes(ElementProperty elementProperty) throws OnepException {
        /*
         * Get the processor of the network element.
         */
        // START SNIPPET: get_np_processor
        String processor = elementProperty.getProcessor();
        // END SNIPPET: get_np_processor
        getLogger().info("Processor - " + processor);

        /*
         * Get the product ID of the network element.
         */
        // START SNIPPET: get_np_pid
        String productId = elementProperty.getProductID();
        // END SNIPPET: get_np_pid
        getLogger().info("Product Id - " + productId);

        /*
         * Get the serial number of the network element.
         */
        // START SNIPPET: get_np_sn
        String serialNumber = elementProperty.getSerialNo();
        // END SNIPPET: get_np_sn
        getLogger().info("Serial Number - " + serialNumber);

        /*
         * Get the system description of the network element.
         */
        // START SNIPPET: get_np_desc
        String sysDesc = elementProperty.getSysDescr();
        // END SNIPPET: get_np_desc
        getLogger().info("System Description - " + sysDesc);

        /*
         * Get the system name of the network element.
         */
        // START SNIPPET: get_np_sysnm
        String systemName = elementProperty.getSysName();
        // END SNIPPET: get_np_sysnm
        getLogger().info("System Name - " + systemName);

        /*
         * Get the system uptime of the network element.
         */
        // START SNIPPET: get_np_sysuptime
        long sysUpTime = elementProperty.getSysUpTime();
        // END SNIPPET: get_np_sysuptime
        getLogger().info("System Up time - " + sysUpTime);
    }
}
