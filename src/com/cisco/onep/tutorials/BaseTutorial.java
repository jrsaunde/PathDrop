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
package com.cisco.onep.tutorials;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.onep.core.exception.OnepConnectionException;
import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.core.util.OnepConstants;
import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionConfig;
import com.cisco.onep.element.SessionConfig.SessionTransportMode;
import com.cisco.onep.element.SessionHandle;
import com.cisco.onep.interfaces.InterfaceFilter;
import com.cisco.onep.interfaces.NetworkInterface;

/**
 * The goal of the BaseTutorial is to show how to create an application and connect to a network element to obtain a
 * session handle. The BaseTutorial provides base class functions for reading test properties, parsing command line
 * properties, and network element connectivity.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 */
public class BaseTutorial {

    private static final String DEFAULT_IP = "10.10.10.110";

    // START SNIPPET: java_variables
    private String elementAddr;
    private String username;
    private String password;
    private String transportType = "tls";
    private NetworkElement networkElement;
    private SessionHandle sessionHandle;
    private SessionConfig config;
    // END SNIPPET: java_variables

    private Logger logger;
    private Properties properties;

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        BaseTutorial tutorial = new BaseTutorial();
        tutorial.parseOptions(args);
        try {
            if (!tutorial.connect("BaseTutorial")) {
                System.exit(1);
            }
        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }

    /**
     * Instantiates the Logger instance using the classname of the implementing class.
     */
    public BaseTutorial() {
        // START SNIPPET: createLogger
        logger = LoggerFactory.getLogger(this.getClass());
        // END SNIPPET: createLogger
    }

    /**
     * Obtains a NetworkApplication instance, sets the name to applicationName, gets a network element for the IP
     * address in the command line arguments or tutorial.properties file - both via the string format and an InetAddress
     * formed from the IP address string - and then tries to connect to the Network Element with the username and
     * password supplied, or from the tutorial.properties file.
     *
     * @param applicationName
     *            The NetworkApplication name is set to this value.
     * @return True if the connection succeeded without exception, else false.
     * @throws OnepException
     *             If there is an error.
     */
    public boolean connect(String applicationName) throws OnepException {

        // START SNIPPET: init_myapp
        NetworkApplication networkApplication = NetworkApplication.getInstance();
        // END SNIPPET: init_myapp

        // START SNIPPET: name_myapp
        networkApplication.setName(applicationName);
        // END SNIPPET: name_myapp

        try {
        // START SNIPPET: get_ne
            InetAddress ipAddress = InetAddress.getByName(elementAddr);
            networkElement = networkApplication.getNetworkElement(ipAddress);
        // END SNIPPET: get_ne
        } catch (UnknownHostException e) {
            getLogger().error(e.getLocalizedMessage(), e);
            return false;
        } catch (OnepException e) {
            getLogger().error(e.getLocalizedMessage(), e);
            return false;
        }
        if (networkElement == null) {
            getLogger().error("Failed to get network element");
            return false;
        }
        getLogger().info("We have a NetworkElement - " + networkElement);

        /* Pick a transport mode to connect to with network element with. */
        if (transportType.equalsIgnoreCase("tcp")
            || transportType.equals("1")) {
            config = new SessionConfig(SessionTransportMode.SOCKET);
            config.setPort(OnepConstants.ONEP_PORT);
        } else {
            config = new SessionConfig(SessionTransportMode.TLS);
            config.setPort(OnepConstants.ONEP_TLS_PORT);
        }

        try {
            // START SNIPPET: connect
            sessionHandle = networkElement.connect(username, password, config);
            // END SNIPPET: connect
        } catch (OnepConnectionException e) {
            getLogger().error(e.getMessage());
            return false;
        } catch (OnepException e) {
            getLogger().error(e.getLocalizedMessage(), e);
            return false;
        }
        if (sessionHandle == null) {
            // START SNIPPET: loggerError
            getLogger().error("Failed to connect to NetworkElement - " + networkElement);
            // END SNIPPET: loggerError
            return false;
        }
        getLogger().info("Successful connection to NetworkElement - " + networkElement);
        return true;
    }

    /**
     * Disconnects the application from the Network Element.
     *
     * @return True if the disconnect succeeded without an exception, else false if the application failed to disconnect
     *         from the Network Element.
     */
    public boolean disconnect() {
        try {
            networkElement.disconnect();
        } catch (Exception e) {
            getLogger().error("Failed to disconnect from Network Element");
            return false;
        }
        return true;
    }

    /**
     * Gets the IP address or hostname of the Network Element set during instantiation.
     *
     * @return The elementAddr.
     */
    public String getElementAddr() {
        return elementAddr;
    }

    /**
     * Gets the elementAddr, the Network Element's address or hostname, as an InetAddress.
     *
     * @return The elementAddr as an InetAddress.
     * @throws UnknownHostException
     *             If the IP address of the NetworkElement cannot be resolved to a host.
     */
    public InetAddress getElementInetAddress() throws UnknownHostException {
        return InetAddress.getByName(getElementAddr());
    }

    /**
     * Gets the username on whose behalf the connection will be made. The username is specified via the command line or
     * in the tutorial.properties file.
     *
     * @return The username that is specified via the command line or in the tutorial.properties file.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password for the user on whose behalf a connection will be made. The password is specified either via
     * the command line or in the tutorial.properties file.
     *
     * @return The password that is specified via the command line or in the tutorial.properties file.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the transport type used to communicate with the network element.
     *
     * @return The transport type as a string.
     */
    public String getTransportType() {
        return transportType;
    }

    /**
     * Gets the NetworkElement that is being connected to by the application.
     *
     * @return The NetworkElement that is being connected to by the application.
     */
    public NetworkElement getNetworkElement() {
        return networkElement;
    }

    /**
     * Sets the NetworkElement to be used.
     *
     * @param networkElement
     *            The networkElement to set.
     */
    public void setNetworkElement(NetworkElement networkElement) {
        this.networkElement = networkElement;
    }

    /**
     * Implements the logger, which sends all enabled log messages.
     *
     * @return The logger.
     */
    // START SNIPPET: getLogger
    public Logger getLogger() {
        return logger;
    }

    // END SNIPPET: getLogger

    /**
     * Gets the Properties instance that contains options for this application.
     *
     * @return The Properties instance
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the Properties instance that contains options for this application.
     *
     * @param properties
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Obtains all interfaces on a NetworkElement.
     *
     * @return List of NetworkInterface instances.
     */
    public List<NetworkInterface> getAllInterfaces() {
        List<NetworkInterface> interfaceList = null;
        try {
            NetworkElement networkElement = getNetworkElement();
            interfaceList = networkElement.getInterfaceList(new InterfaceFilter());
        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage(), e);
        }
        return interfaceList;
    }

    /**
     * This method changes the status, i.e. shuts down or brings up, of all interfaces for the NetworkElement the
     * NetworkApplication is connected to, except the interface that the NetworkApplication itself is connected to.
     * <p>
     * The method will first check to see if the interface address matches the interface that the NetworkApplication is
     * connected to. If it matches then the interface will not be acted upon. Otherwise shutdown(state) is call on the
     * NetworkInterface.
     * <p>
     * Call simulateShutdown(false) to turn ON all of the interfaces or simulateShutdown(true) to turn OFF all of the
     * interfaces (except the interface the NetworkApplication is connected to, of course).
     *
     * @param state
     *            Interface goes down if state = true, or up if state = false.
     * @throws OnepException
     *             When there is an error.
     * @throws UnknownHostException
     *             If the IP address of the NetworkElement cannot be resolved.
     */
    // START SNIPPET: simulateShutdown
    public void simulateShutdown(boolean state) throws OnepException, UnknownHostException {
        List<NetworkInterface> networkInterfaces = getAllInterfaces();
        List<InetAddress> addressList = null;
        boolean applicationInterface = false;

        // Loop over all interfaces
        for (NetworkInterface networkInterface : networkInterfaces) {
            getLogger().info("Processing NetworkInterface " + networkInterface.getName());
            addressList = networkInterface.getAddressList();
            // Loop over all addresses for an interface
            for (InetAddress inetAddress : addressList) {
                getLogger().info("NetworkInterface " + networkInterface.getName() + " has InetAddress " + inetAddress.toString());
                // Check that this is not the interface that the NetworkApplication is connected to. If it is then move
                // onto the next interface.
                if (inetAddress.equals(getElementInetAddress())) {
                    applicationInterface = true;
                    getLogger().info(
                            "Skipping NetworkInterface " + networkInterface.getName() + " with address " + inetAddress.toString()
                                    + " as this is the interface on the NetworkElement"
                                    + " that this application is connected to.\n\n");
                    break;
                }
            }

            if (applicationInterface) {
                applicationInterface = false;
                continue;
            }

            getLogger().info("Calling shutdown(" + state + ") on " + networkInterface + "\n\n");
            // Call shutdown with the boolean value passed in.
            networkInterface.shutdown(state);
        }
    }
    // END SNIPPET: simulateShutdown

    /**
     * Returns the first interface from the interface list.
     *
     * @param networkElement
     * @return
     * @throws OnepException
     * @throws OnepRemoteProcedureException
     * @throws OnepConnectionException
     */
    public NetworkInterface getInterface()
            throws OnepException, OnepRemoteProcedureException,
            OnepConnectionException, UnknownHostException {
        boolean isHostInterface = false;
        List<NetworkInterface> ifList = getAllInterfaces();
        if (ifList != null) {
            List<InetAddress> addressList = null;
            for (NetworkInterface networkInterface : ifList) {
                addressList = networkInterface.getAddressList();
                // Loop over all addresses for an interface
                if (addressList != null && !addressList.isEmpty()) {
                    for (InetAddress inetAddress : addressList) {
                        // Check that this is not the interface that the
                        // NetworkApplication is connected to.
                        // If it is then move on to the next interface.
                        if (inetAddress.equals(getElementInetAddress())) {
                            isHostInterface = true;
                            break;
                        }
                    }
                }
                if (!isHostInterface) {
                    return networkInterface;
                }
            }
        }
        return null;
    }

    /**
     * Parse options from the command line arguments, or if none are supplied,
     * from the properties file "tutorial.properties".
     *
     * If any required options are missing, the application will exit.
     *
     * @param args  The command line arguments passed to the main(...) method.
     */
    public void parseOptions(String[] args) {
        if (args.length > 0) {
            for (int i = 0; i + 1 < args.length; i += 2) {
                if ((args[i].equals("-a")) || (args[i].equals("--addr"))) {
                    elementAddr = args[i + 1];
                } else if ((args[i].equals("-u")) || (args[i].equals("--user"))) {
                    username = args[i + 1];
                } else if ((args[i].equals("-p")) || (args[i].equals("--pass"))) {
                    password = args[i + 1];
                } else if ((args[i].equals("-t")) || (args[i].equals("--transport"))) {
                    transportType = args[i + 1];
                }
            }
        } else {
            if (properties == null) {
                InputStream inputStream = null;
                try {
                    inputStream = this.getClass().getClassLoader().getResourceAsStream("tutorial.properties");
                    if (inputStream != null) {
                        properties = new Properties();
                        properties.load(inputStream);
                    }
                } catch (Exception e) {
                    getLogger().error(e.getLocalizedMessage(), e);
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ioe) {
                            getLogger().error("Error in closing the tutorial.properties file: " + ioe.getMessage());
                        }
                    }
                }
            }

            if (properties != null) {
                elementAddr = properties.getProperty("elementAddress", DEFAULT_IP);
                username = properties.getProperty("userName");
                password = properties.getProperty("password");
                transportType = properties.getProperty("transportType", "tls");
            }
        }

        if (elementAddr == null || username == null || password == null) {
            getLogger().info(getUsageString());
            System.exit(1);
        }
    }

    /**
     * Gets a string that shows the command to run the application.
     *
     * @return The usage string.
     */
    public String getUsageString() {
        return "Usage: java " + getClass().getCanonicalName() +
            " " + getUsageRequiredOptions() +
            " " + getUsageOptionalOptions();
    }

    /**
     * Gets a string that shows the options that are required to run the
     * application.
     *
     * @return The required options.
     */
    public String getUsageRequiredOptions() {
        return "-a <element addr> -u <username> -p <password>";
    }

    /**
     * Gets a string that shows the options that are not required to run the
     * application.
     *
     * @return The optional options.
     */
    public String getUsageOptionalOptions() {
        return "[-t <0=tls | 1=tcp>]";
    }

}
