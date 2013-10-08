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
package com.cisco.onep.tutorials.topology;

import java.util.ArrayList;
import java.util.List;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.topology.Edge;
import com.cisco.onep.topology.Graph;
import com.cisco.onep.topology.Node;
import com.cisco.onep.topology.NodeConnector;
import com.cisco.onep.topology.Topology;
import com.cisco.onep.topology.Topology.TopologyType;
import com.cisco.onep.topology.TopologyEvent;
import com.cisco.onep.topology.TopologyEvent.TopologyEventType;
import com.cisco.onep.topology.TopologyFilter;
import com.cisco.onep.topology.TopologyListener;
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The goal of this tutorial is to show how to get the Network element's
 * topology graph and neighbor network element's information.
 * This tutorial will also show how to register a CDP listener and
 * handle the event when there's a topology change in the neighbors
 * of the Network Element.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */

public class TopologyTutorial extends BaseTutorial {

    private static final long SLEEP = 20000L;

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        TopologyTutorial tutorial = new TopologyTutorial();

        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("TopologyTutorial")) {
                System.exit(1);
            }
            Topology topology = tutorial.discoverCDPTopology();
            tutorial.addTopologyListener(topology);
            /* Shut the interfaces down to see TopologyEventType.EDGES_DELETE */
            tutorial.simulateShutdown(true);
            Thread.sleep(SLEEP);
            /* And bring them back up again to see TopologyEventType.EDGES_ADD */
            tutorial.simulateShutdown(false);
            Thread.sleep(SLEEP);
        } catch (Exception e) {
            tutorial.disconnect();
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        }
        tutorial.disconnect();
        System.exit(0);
    }

    /**
     * An example TopologyListener.
     */
    // START SNIPPET: ExampleTopologyListener
    public class ExampleTopologyListener implements TopologyListener {
        /**
         * Handles TopologyEvents and will notify the user if edges have been added or deleted from the current Network
         * Element.
         *
         * @param event
         *            The TopologyEvent.
         * @param clientData
         *            Data passed in when the listener was registered.
         *
         * @see com.cisco.onep.topology.TopologyListener#handleEvent(com.cisco.onep .topology.TopologyEvent,
         *      java.lang.Object)
         */
        public void handleEvent(TopologyEvent event, Object clientData) {
            getLogger().info("Received TopologyEvent.");

            if (event.getType().contains(TopologyEventType.EDGES_ADD)) {
                getLogger().info("Some edges have been added.");
            }

            if (event.getType().contains(TopologyEventType.EDGES_DELETE)) {
                getLogger().info("Some edges have been deleted");
            }

            getLogger().info("The number of changed edges in the TopologyEvent is " + event.getEdgeList().size());
            getLogger().info("The number of changed nodes in the TopologyEvent is " + event.getNodeList().size());
        }
    }

    // END SNIPPET: ExampleTopologyListener

    /**
     * Add an ExampleTopologyListener to the Topology passed in.
     *
     * @param topology
     *            A Topology to which the ExampleTopologyListener is added.
     * @throws OnepException
     *             If there is an error.
     */
    public void addTopologyListener(Topology topology) throws OnepException {

        /* The Event Filter object allows the application to listen to specific
         * topology change events corresponding to a topology object.
         * When a topology change event matches with the specified filter
         * criteria the application is notified through the registered callback.
         */
        // START SNIPPET: addTopologyListener
        List<TopologyEventType> eventType = new ArrayList<TopologyEventType>();
        eventType.add(TopologyEventType.EDGES_ADD);
        eventType.add(TopologyEventType.EDGES_DELETE);
        TopologyFilter filter = new TopologyFilter(eventType);
        topology.addTopologyListener(new ExampleTopologyListener(), filter, null);
        // END SNIPPET: addTopologyListener
    }

    /**
     * Obtain a Topology instance from the NetworkElement for TopologyType.CDP
     * and print the Topology graph information.
     *
     * @return A Topology instance.
     * @throws OnepException
     *             If there is an error.
     */
    public Topology discoverCDPTopology() throws OnepException {
        // START SNIPPET: discoverCDPTopology
        /* Create a topology object */
        Topology topology = new Topology(getNetworkElement(), TopologyType.CDP);

        /* Get the graph object */
        Graph graph = topology.getGraph();

        /* Get the list of edges from the Graph object. */
        List<Edge> edgeList = graph.getEdgeList(Edge.EdgeType.UNDIRECTED);

        /* Get the Node and Node connectors an Edge is part of, checking
         * that there are any first.
         */
        NodeConnector headNodeConnector = null;
        NodeConnector tailNodeConnector = null;
        Node headNode = null;
        Node tailNode = null;

        if (edgeList.size() > 0) {
            for (Edge edge : edgeList) {

                headNodeConnector = edge.getHeadNodeConnector();
                tailNodeConnector = edge.getTailNodeConnector();
                headNode = headNodeConnector.getNode();
                tailNode = tailNodeConnector.getNode();

                /* Get the Node name and Connector Name from Node and
                 * NodeConnector objects respectively(for both head and tail nodes).
                */
                getLogger().info("\nEdge is " + edge.toString());
                getLogger().info("Local Host is " + headNode.getName());
                getLogger().info("Local interface is " + headNodeConnector.getName());
                getLogger().info("Remote Host is " + tailNode.getName());
                getLogger().info("Remote interface is " + tailNodeConnector.getName());
            }
        } else {
            getLogger().info("There are no edges.");
        }
        return topology;
        // END SNIPPET: discoverCDPTopology
    }
}
