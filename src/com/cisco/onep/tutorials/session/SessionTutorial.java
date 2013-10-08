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

package com.cisco.onep.tutorials.session;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.util.OnepConstants;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionConfig;
import com.cisco.onep.element.SessionConfig.SessionTransportMode;
import com.cisco.onep.element.SessionHandle;
import com.cisco.onep.element.SessionProperty;
import com.cisco.onep.element.SessionStatistics;

/* The BaseTutorial provides utilities common to most applications. */
import com.cisco.onep.tutorials.BaseTutorial;

/**
 * Session Tutorial
 *
 * This tutorial is intended for application developers who need to specify configuration parameters for
 * connections made between onePK applications and network elements.
 *
 * In onePK, a session represents an authenticated channel of communication
 * between an application and a network element. This tutorial shows
 * how to configure a session, how to get the properties and statistics of a
 * session, and how to reconnect to a disconnected session.
 *
 */
public final class SessionTutorial extends BaseTutorial {

    private static final long SLEEP = 1000L;

    /**
     * Main method.
     * @param args Command line arguments:
     *             {@code -a <element address>}
     *             {@code -u <username>}
     *             {@code -p <password>}
     *             {@code -t <transport type>}
     */
    public static void main(String[] args) {
        SessionTutorial tutorial = new SessionTutorial();

        /* Parse arguments from command line or properties file. */
        tutorial.parseOptions(args);

        SessionTransportMode transportMode;
        if (tutorial.getTransportType().equalsIgnoreCase("tcp")
            || tutorial.getTransportType().equals("1")) {
            transportMode = SessionTransportMode.SOCKET;
        } else {
            transportMode = SessionTransportMode.TLS;
        }

        /* Create a session configuration with transport mode socket. */
        SessionConfig config = SessionTutorial.createSessionConfig(
                transportMode);

        tutorial.getLogger().info("\n********* INITIALIZE AND CONNECT *******");

        /* Connect to the network element using the given configuration. */
        SessionHandle originalSessionHandle = null;
        try {
            originalSessionHandle = tutorial.connectWithConfig(
                    "Session Tutorial", /* Application name         */
                    config);            /* Session configuration    */
        } catch (UnknownHostException e) {
            tutorial.getLogger().error("Failed to resolve host.", e);
            return;
        } catch (OnepException e) {
            tutorial.getLogger().error("Failed to connect to element.", e);
            return;
        }

        /* Upon a successful connection, a session is established and a handle
         * is returned in the form of a SessionHandle. When a session is in the
         * connected state, its configuration cannot be modified. The session
         * handle may be used to query information about the session. Here, we
         * use it to get the session's ID, which will be needed when we want to
         * reconnect to the session.
         */
// START SNIPPET: onep_session_handle_get_id
        int sessionID = originalSessionHandle.getID();
// END SNIPPET: onep_session_handle_get_id
        tutorial.getLogger().info("Connected to network element " +
                "with session ID: " + sessionID);

        tutorial.getLogger().info(
                "\n********* PRINT SESSION PROPERTIES *******");
        tutorial.printSessionProperties(originalSessionHandle);

        tutorial.getLogger().info(
                "\n********* PRINT SESSION STATISTICS *******");
        tutorial.printSessionStatistics(originalSessionHandle);

        tutorial.getLogger().info("\n********* DISCONNECT *******");
        tutorial.getNetworkElement().disconnect();
        tutorial.getLogger().info("Disconnected from element, " +
                "waiting to reconnect...");

        /* Wait until the session is disconnected. */
        while (tutorial.getNetworkElement().isConnected()) {
           try {
              Thread.sleep(SLEEP);
           } catch (InterruptedException e) {
              break;
           }
        }

        tutorial.getLogger().info("\n********* RECONNECT *******");

        /* If the session becomes disconnected, the application can attempt to
         * reconnect to the network element and continue using the same session
         * so long as it can provide the session's ID and the reconnect timer of
         * that session has not yet expired.
         */
        SessionHandle reconnectedSessionHandle = null;
        try {
// START SNIPPET: onep_element_reconnect
            reconnectedSessionHandle = tutorial.getNetworkElement().reconnect(
                    tutorial.getUsername(),    /* Authentication username    */
                    tutorial.getPassword(),    /* Authentication password    */
                    sessionID,                 /* Existing session ID        */
                    config);                   /* Session configuration      */
// END SNIPPET: onep_element_reconnect
        } catch (OnepException e) {
            tutorial.getLogger().error("Failed to reconnect to element", e);
            return;
        }

        /* Get the session ID associated with the reconnected session's
         * handle.
         */
        sessionID = reconnectedSessionHandle.getID();
        tutorial.getLogger().info("Reconnected to network element " +
                "with session ID: " + sessionID);

        tutorial.getLogger().info(
                "\n********* DISCONNECT AND CLEAN UP *******\n\n");
        tutorial.disconnect();
    }

    /**
     * Creates an instance of SessionConfig with the given transport mode and
     * sets the reconnect timer to one minute. All other attributes are set to
     * their default values.
     *
     * When connecting to a network element, the caller may optionally provide a
     * SessionConfig that contains the desired configuration for the resulting
     * session. When creating the SessionConfig, the only required attribute is
     * the transport mode. TLS is the recommended transport mode for production
     * environments. The SOCKET transport mode should only be used for testing purposes.
     * All other attributes are optional, and will take on their default values
     * if not explicitly set. To demonstrate reconnecting to the session, the
     * reconnect timer will be set to one minute.
     *
     * @param mode The transport mode used by the connection.
     * @return a SessionConfig instance.
     */
    private static SessionConfig createSessionConfig(SessionTransportMode mode) {
// START SNIPPET: create_session_config
        /* Construct a SessionConfig instance with the given transport mode. */
        SessionConfig config = new SessionConfig(mode);

        /* Set the reconnect timer to one minute. */
        config.setReconnectTimer(60);

        /* The session attributes below this point are set to their default
         * values.
         */

        /* Set the port to connect to on the network element.
         * The default ports are: SOCKET   15001
         *                        TLS      15002
         */
        switch (mode) {
        case SOCKET:
            config.setPort(OnepConstants.ONEP_PORT);
            break;
        case TLS:
            config.setPort(OnepConstants.ONEP_TLS_PORT);
            break;
        default:
            break;
        }

        /* Set the event queue size of the session. */
        config.setEventQueueSize(SessionConfig.DEFAULT_EVENT_QUEUE_SIZE);

        /* Set the event thread pool size of the session. */
        config.setEventThreadPool(SessionConfig.DEFAULT_THREADPOOL_SIZE);

        /* Set the event drop mode of the session. */
        config.setEventDropMode(SessionConfig.DEFAULT_EVENT_DROP_MODE);

        /* Set the keepalive attributes of the session. */
        /* Idle time in seconds */
        config.setKeepAliveIdleTime(SessionConfig.DEFAULT_KEEPALIVE_IDLE_TIME);
        /* Interval between keepalives in seconds */
        config.setKeepAliveInterval(SessionConfig.DEFAULT_KEEPALIVE_INTERVAL);
        /* Number of keepalives */
        config.setKeepAliveRetryCount(
            SessionConfig.DEFAULT_KEEPALIVE_RETRY_COUNT);
// END SNIPPET: create_session_config

        return config;
    }

    /**
     * Initializes the network application. Then, gets the network element and
     * connects to it with the given session configuration.
     *
     * @param applicationName       The unique name of this application.
     * @param config                Configuration options instance.
     * @return SessionHandle        The handle of the connected session, or
     *                              null if there was an error.
     * @throws OnepException        If there was an error in executing a onePK
     *                              call.
     * @throws UnknownHostException If the address of the network element could
     *                              not be resolved.
     */
    private SessionHandle connectWithConfig(String applicationName,
                                            SessionConfig config)
        throws OnepException, UnknownHostException {
        /* Get the NetworkApplication instance. */
        NetworkApplication networkApplication =
                NetworkApplication.getInstance();

        /* Set the name of the application to applicationName. */
        networkApplication.setName(applicationName);

        /* Get the network element's address passed in from the command line
         * or the properties file.
         */
        String elementAddress = this.getElementAddr();

        /* Get the NetworkElement instance with the address. */
        NetworkElement networkElement =
            networkApplication.getNetworkElement(
                    InetAddress.getByName(elementAddress));
        getLogger().info("Got a NetworkElement - " + networkElement);

        /* Set the network element for this tutorial. */
        this.setNetworkElement(networkElement);

        /* Connect to the element using the given session configuration. If no
         * configuration is specified by the caller (i.e. null is used), the
         * session will take on the default values.
         */
// START SNIPPET: onep_element_connect
        SessionHandle handle = networkElement.connect(
                this.getUsername(),
                this.getPassword(),
                config);            /* SessionHandle */
// END SNIPPET: onep_element_connect
        getLogger().info("Successfully connected to NetworkElement - " +
                this.getNetworkElement());

        return handle;
    }

    /**
     * Prints the session properties to the logger.
     * @param handle  The handle to the session to print the properties
     *                for.
     */
    private void printSessionProperties(SessionHandle handle) {
// START SNIPPET: print_session_properties
        /* Get the property instance for this session using the
         * session handle.
         */
        SessionProperty property = handle.getSessionProperty();

        /* Get the port number the session is connected on. */
        getLogger().info("Port: " + property.getPort());

        /* Get the event queue size of the session. */
        getLogger().info("EventQueueSize: " + property.getEventQueueSize());

        /* Get the event thread pool size of the session. */
        getLogger().info("EventThreadPool: " + property.getEventThreadPool());

        /* Get the event drop mode of the session. */
        getLogger().info("EventDropMode: " +
                         property.getEventDropMode().name());

        /* Get the reconnect timer of the session in seconds. */
        getLogger().info("ReconnectTimer: " + property.getReconnectTimer()
                         + " s");
// END SNIPPET: print_session_properties
    }

    private void printSessionStatistics(SessionHandle handle) {
// START SNIPPET: print_session_statistics
        /* Get the statistics instance for this session using the
         * session handle.
         */
        SessionStatistics statistics = handle.getSessionStatistics();

        /* Get the count of events received and dropped. */
        getLogger().info("EventsReceived: " + statistics.getEventRecvCount()
                + "\nEventsDropped: " + statistics.getEventDropCount());
// END SNIPPET: print_session_statistics
    }
}
