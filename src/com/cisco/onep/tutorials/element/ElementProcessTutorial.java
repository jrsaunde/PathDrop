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
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.system.ElementProcess;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The Element Process Tutorial demonstrates how to retrieve information about
 * the processes running on a network element.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class ElementProcessTutorial extends BaseTutorial {

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        ElementProcessTutorial tutorial = new ElementProcessTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("ElementProcessTutorial")) {
                System.exit(1);
            }
            NetworkElement networkElement = tutorial.getNetworkElement();

            /*
             * Get a list of processes running on a network element.
             */
            // START SNIPPET: get_ne_process
            List<ElementProcess> processList = networkElement.getProcessList();
            // END SNIPPET: get_ne_process
            tutorial.getElementProcessAttributes(processList);
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }

    /**
     * Prints all the attributes of Process while iterating over the Process List of the network element.
     *
     * @param processList
     */
    public void getElementProcessAttributes(List<ElementProcess> processList) {
        for (ElementProcess elementProcess : processList) {

            /*
             * Get allocated memory of the Element Process of a Network Element
             */
            // START SNIPPET: get_ne_process_alloc_mem
            long allocatedMemory = elementProcess.getAllocatedMemory();
            // END SNIPPET: get_ne_process_alloc_mem
            getLogger().info("Allocated Memory - " + allocatedMemory);

            /*
             * Get CPU utilization of the Element Process of a Network Element
             */
            // START SNIPPET: get_ne_process_cpuutil
            double cpuUtilization = elementProcess.getCPUUtilization();
            // END SNIPPET: get_ne_process_cpuutil
            getLogger().info("CPU utilization - " + cpuUtilization);

            /*
             * Get freed memory of the Element Process of a Network Element
             */
            // START SNIPPET: get_ne_process_freemem
            long freedMemory = elementProcess.getFreedMemory();
            // END SNIPPET: get_ne_process_freemem
            getLogger().info("Freed Memory - " + freedMemory);

            /*
             * Get Process ID of the Element Process of a Network Element
             */
            // START SNIPPET: get_ne_process_pid
            int processId = elementProcess.getProcessID();
            // END SNIPPET: get_ne_process_pid
            getLogger().info("Process ID - " + processId);

            /*
             * Get Process Name of the Element Process of a Network Element
             */
            // START SNIPPET: get_ne_process_pname
            String processName = elementProcess.getProcessName();
            // END SNIPPET: get_ne_process_pname
            getLogger().info("Process Name - " + processName);

            /*
             * Get used memory of the Element Process of a Network Element
             */
            // START SNIPPET: get_ne_process_usedmem
            long usedMemory = elementProcess.getUsedMemory();
            // END SNIPPET: get_ne_process_usedmem
            getLogger().info("Used Memory - " + usedMemory);
        }
    }
}
