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
import java.net.UnknownHostException;
import java.util.List;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants.OnepBandwidthUnits;
import com.cisco.onep.core.util.OnepConstants.OnepDscp;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.policy.ActionClassQueue;
import com.cisco.onep.policy.ActionMark;
import com.cisco.onep.policy.ActionShape;
import com.cisco.onep.policy.ClassMap;
import com.cisco.onep.policy.FilterDscp;
import com.cisco.onep.policy.FilterPktLen;
import com.cisco.onep.policy.QosPolicy;
import com.cisco.onep.policy.Target;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial show how a onePK application creates a QoS Policy on a Network Element.
 * The QoS Policy object is a top-level object in a hierarchy of objects that define class,
 * filter and action objects that are used to apply QoS policy to interface targets.
 * The Policy object is associated with a target (or interface).
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */

public class QoSPolicyTutorial extends BaseTutorial {

    // START SNIPPET: QOS_VARIABLES
    private static final long TEN_SECONDS = 10000L;
    private static final int SEQUENCE_NUMBER = 10;
    private static final int PACKET_LENGTH = 100;
    private static final int MARKING_VALUE = 33;
    private static final int COMMITED_INFORMATION_RATE = 8000;
    private static final int COMMITED_BURST_SIZE = 20000;
    private static final int EXCESS_BURST_SIZE = 20000;
    private static final int BANDWIDTH = 1000;
    // END SNIPPET: QOS_VARIABLES

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        QoSPolicyTutorial tutorial = new QoSPolicyTutorial();

        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("QoSPolicyTutorial")) {
                System.exit(1);
            }
            // Creates a new Quality of Service Policy object.
            QosPolicy qosPolicy = new QosPolicy(tutorial.getNetworkElement());

            // START SNIPPET: applyQos
            /* Create a Class on the Network Element. As these functions are
             * invoked the Class is created on the Network Element. Create
             * Filters and add them to the Class. As these functions are invoked
             * the Filter is created on the Network Element.In this example, the
             * Class has a DSCP filter and a Packet Length filter
             */
            ClassMap classMap = tutorial.createClassMap();

            // Adds a DSCP Filter to the class.
            classMap.addFilter(tutorial.createFilterDscp());

            // Adds a Packet Length filter to the class.
            classMap.addFilter(tutorial.createFilterPktLen());

            //Adds the ClassMap with our configuration to the QosPolicy object.
            qosPolicy.addClass(classMap, SEQUENCE_NUMBER);

            /* Create Actions and then add the Actions to the Policy.
             * This example the Class has a Packet Marking action,
             * a traffic Shaping Action and a Class-based Queue Action
            */
            qosPolicy.addAction(classMap, tutorial.createActionMark());
            qosPolicy.addAction(classMap, tutorial.createActionShape());
            qosPolicy.addAction(classMap, tutorial.createActionClassQueue());

            /*Create a Target and then apply the QOS Policy to the Target.
             * A Target is defined as an Interface and a location within the
             * feature processing chain for that Interface.
             */
            Target target = tutorial.createTarget();
            if (target!=null)
                qosPolicy.applyToTarget(target);
            // END SNIPPET: applyQos

            // Prints out the information within the ClassMap and QoS Policy object
            tutorial.getLogger().info("ClassMap and QoS Policy info..");
            tutorial.getLogger().info(classMap.toString());
            tutorial.getLogger().info(qosPolicy.toString());
            tutorial.getLogger().info("Verify the info on the device with command - show policy-map\n");

            // To check on the NE100 IOL Instance, use command on IOL / device:
            // show policy-map
            Thread.sleep(TEN_SECONDS);

            tutorial.getLogger().info("Removing the QoS policy...");
            // START SNIPPET: removeQos
            // Remove the QoS Policy from the Network Element
            qosPolicy.removeFromTarget(target);
            // END SNIPPET: removeQos
        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
            System.exit(0);
        }
    }

    /**
     * Creates an ClassMap based on the current Network Element and matches any filters that will be applied to this
     * ClassMap.
     *
     * @return ClassMap object with given Network Element and options.
     * @throws OnepException
     *             if an exception is thrown
     */
     // START SNIPPET: createClassMap
    public ClassMap createClassMap() throws OnepException {
        ClassMap classMap = new ClassMap(getNetworkElement(), ClassMap.ClassOper.CLASS_OPER_OR);
        return classMap;
        // END SNIPPET: createClassMap
    }

    /**
     * Creates a DSCP Filter with AF11 and AF13 applied.
     *
     * @return FilterDscp object with its options applied.
     * @throws OnepException
     *             if an exception is thrown.
     */
        // START SNIPPET: createFilterDscp
    public FilterDscp createFilterDscp() throws OnepException {
        FilterDscp filterDscp = new FilterDscp(OnepDscp.ONEP_DSCP_AF11);
        filterDscp.addDscp(OnepDscp.ONEP_DSCP_AF13);
        return filterDscp;
        // END SNIPPET: createFilterDscp
    }

    /**
     * Create a Packet Length Filter with minimum packet length of 100.
     *
     * @return FilterPktLen object with its options applied.
     * @throws OnepException
     *             if an exception is thrown
     */
        // START SNIPPET: createFilterPktLen
    public FilterPktLen createFilterPktLen() throws OnepException {
        FilterPktLen filterPacketLength = new FilterPktLen(PACKET_LENGTH);
        return filterPacketLength;
        // END SNIPPET: createFilterPktLen
    }

    /**
     * Creates a new ActionMark that will mark the IP Dscp packets with a value of 33.
     *
     * @return ActionMark object with its options applied.
     */
         // START SNIPPET: createActionMark
    public ActionMark createActionMark() {
        ActionMark actionMark = new ActionMark(ActionMark.MarkType.ONEP_MARK_IP_DSCP, MARKING_VALUE);
        return actionMark;
        // END SNIPPET: createActionMark
    }

    /**
     * Creates a new Traffic Shaping Action.
     *
     * @return ActionShape object with its options applied.
     */
         // START SNIPPET: createActionShape
    public ActionShape createActionShape() {
        ActionShape actionShape = new ActionShape(COMMITED_INFORMATION_RATE, COMMITED_BURST_SIZE,
        EXCESS_BURST_SIZE, ActionShape.ShapeType.ONEP_SHAPE_AVG,
        ActionShape.RateUnits.ONEP_RATE_UNITS_BPS,
        ActionShape.BurstUnits.ONEP_BURST_UNITS_BITS);
        return actionShape;
    }
       // END SNIPPET: createActionShape

    /**
     * Creates a new Class-based Queue Action of 1000 Kbps.
     *
     * @return ActionClassQueue object with its options applied.
     */
         // START SNIPPET: createActionClassQueue
    public ActionClassQueue createActionClassQueue() {
        ActionClassQueue actionClassQueue = new ActionClassQueue(BANDWIDTH, OnepBandwidthUnits.ONEP_BW_UNITS_KBPS);
        return actionClassQueue;
        // END SNIPPET: createActionClassQueue
    }

    /**
     * Creates a new target that will be applied to the current Network Element interface "Ethernet 0/1".
     *
     * @return Target applied to Ethernet 0/1
     * @throws OnepException
     *             if an exception is thrown
     * @throws UnknownHostException
     */
    public Target createTarget() throws OnepException, UnknownHostException {

        Target target = null;
        //Get first available interface on the Network Element
        List<NetworkInterface> networkInterfaces = getAllInterfaces();
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
                        // Check that this is not the interface that the NetworkApplication is connected to.
                        // If it is then move on to the next interface.
                        if (inetAddress.equals(getElementInetAddress())) {
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
        // START SNIPPET: createTarget
        if (testNetworkInterface != null) {
            target = new Target(testNetworkInterface,
                Target.Location.ONEP_TARGET_LOCATION_HARDWARE_DEFINED_OUTPUT);
            getLogger().info("QoS policies and actions will be applied to interface " + testNetworkInterface.getName());
        }
        return target;
        // END SNIPPET: createTarget
    }
}
