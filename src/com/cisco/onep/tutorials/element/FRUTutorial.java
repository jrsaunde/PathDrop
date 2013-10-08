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
import com.cisco.onep.element.FRU;
import com.cisco.onep.element.NetworkElement;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The FRU Tutorial demonstrates how to retrieve information about the field-
 * removable units on a network element.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class FRUTutorial extends BaseTutorial {

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        FRUTutorial tutorial = new FRUTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("FRUTutorial")) {
                System.exit(1);
            }
            NetworkElement networkElement = tutorial.getNetworkElement();

            /*
             * Get a list of the field-removable units on the network element.
             */
            // START SNIPPET: get_ne_fru_list
            List<FRU> fruList = networkElement.getFRUList();
            // END SNIPPET: get_ne_fru_list
            tutorial.getFRUAttributes(fruList);
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }

    /**
     * Prints all the attributes of FRU while iterating over the FRU List of the network element.
     *
     * @param fruList
     */
    public void getFRUAttributes(List<FRU> fruList) {
        for (FRU fru : fruList) {

            /* Get the summary of the FRU in the string format. */
            getLogger().info("FRU Details " + fru.toString());

            /* Get the alarm type of the FRU. */
            // START SNIPPET: get_fru_alarm
            String alarmType = fru.getAlarmType();
            // END SNIPPET: get_fru_alarm
            getLogger().info("FRU alarm type - " + alarmType);

            /* Get the firmware version of the FRU. */
            // START SNIPPET: get_fru_fw
            String firmwareVersion = fru.getFWVersion();
            // END SNIPPET: get_fru_fw
            getLogger().info("FRU Firmware Version - " + firmwareVersion);

            /* Get the hardware version of the FRU. */
            // START SNIPPET: get_fru_hw
            String hardwareVersion = fru.getHWVersion();
            // END SNIPPET: get_fru_hw
            getLogger().info("FRU Hardware Version - " + hardwareVersion);

            /* Get the part number of the FRU. */
            // START SNIPPET: get_fru_pn
            String partNumber = fru.getPartNo();
            // END SNIPPET: get_fru_pn
            getLogger().info("FRU Part Number - " + partNumber);

            /* Get the product ID of the FRU. */
            // START SNIPPET: get_fru_pid
            String productId = fru.getProductID();
            // END SNIPPET: get_fru_pid
            getLogger().info("FRU product Identifier (PID) - " + productId);

            /* Get the serial number of the FRU. */
            // START SNIPPET: get_fru_sn
            String serialNumber = fru.getSerialNo();
            // END SNIPPET: get_fru_sn
            getLogger().info("FRU Serial Number (SN) - " + serialNumber);

            /* Get the slot number of the FRU. */
            // START SNIPPET: get_fru_slot
            int slotNumber = fru.getSlot();
            // END SNIPPET: get_fru_slot
            getLogger().info("FRU slot number - " + slotNumber);

            /* Get the software version of the FRU. */
            // START SNIPPET: get_fru_sw
            String swVersion = fru.getSWVersion();
            // END SNIPPET: get_fru_sw
            getLogger().info("FRU Software Version - " + swVersion);
        }
    }
}
