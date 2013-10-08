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
package com.cisco.onep.tutorials.location;

import java.util.List;

import com.cisco.onep.core.exception.OnepConnectionException;
import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.exception.OnepIllegalArgumentException;
import com.cisco.onep.core.exception.OnepNotSupportedException;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.interfaces.NetworkInterface;
import com.cisco.onep.location.AltType;
import com.cisco.onep.location.CivicLocation;
import com.cisco.onep.location.CivicType;
import com.cisco.onep.location.CustomLocation;
import com.cisco.onep.location.GeoLocation;
import com.cisco.onep.location.Location;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial shows how to get Location data for Network Elements and Interfaces,
 * how to create and add a LocationChangeListener and sets the Location to generate events.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */

public class LocationGetSetTutorial extends BaseTutorial {

    private static final double NETWORK_ELEMENT_LATITUDE = 32.3;
    private static final double NETWORK_ELEMENT_LONGITUDE = 122.5;
    private static final double NETWORK_INTERFACE_LATITUDE = 35.3;
    private static final double NETWORK_INTERFACE_LONGITUDE = 125.5;

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        LocationGetSetTutorial tutorial = new LocationGetSetTutorial();

        tutorial.parseOptions(args);

        try {
            /* Connect to the network element */
            if (!tutorial.connect("LocationGetSetTutorial")) {
                System.exit(1);
            }

            NetworkElement networkElement = tutorial.getNetworkElement();

            /* Network Element Location APIs */
            /* Get the current Location from the Network Element. */
            tutorial.getLogger().info("Get Current NetworkElement Location");
            Location networkElementLocation = tutorial.getNetworkElementLocation(networkElement);

            /* Sets the Locations for the Network Element. */
            tutorial.getLogger().info("Setting NetworkElement Location");
            tutorial.setNetworkElementLocation(networkElement);

            /* Get the updated Location from the Network Element. */
            tutorial.getLogger().info("Get updated NetworkElement Location");
            networkElementLocation = tutorial.getNetworkElementLocation(networkElement);
            tutorial.getLogger().info("NetworkElement Location : " + networkElementLocation.toString());


            /* Removes the Location from the Network Element */
            tutorial.getLogger().info("Remove NetworkElement Location");
            tutorial.removeNetworkElementLocation(networkElement);


            /* Network Interface Location APIs */
            /* Get the Current Location for the Network Interface. */
            tutorial.getLogger().info("Get NetworkInterface Location");
            Location networkInterfaceLocation = tutorial.getNetworkInterfaceLocation(networkElement);

            /* Sets the Locations for the Network Interface. */
            tutorial.getLogger().info("Setting NetworkInterface Location");
            tutorial.setNetworkInterfaceLocation(networkElement);

            /* Get the Updated Location for the Network Interface. */
            networkInterfaceLocation = tutorial.getNetworkInterfaceLocation(networkElement);
            tutorial.getLogger().info("NetworkInterface Location : " + networkInterfaceLocation.toString());

            /* Removes the Location for the Network Interface */
            tutorial.getLogger().info("Remove NetworkInterface Location");
            tutorial.removeNetworkInterfaceLocation(networkElement);

        } catch (OnepNotSupportedException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }

    /**
     * Gets the Location of an Network Element.
     *
     * @param networkElement
     *            Network Element to get the Location from
     * @return Location of the Network Element
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     */
    public Location getNetworkElementLocation(NetworkElement networkElement) throws OnepConnectionException,
            OnepRemoteProcedureException {

        // START SNIPPET: getNetworkElementLocation
        Location networkElementLocation = networkElement.getLocation();
        // END SNIPPET: getNetworkElementLocation

        /*
         * Get the Civic Location information from the Network Element Location.
         */
        // START SNIPPET: getCivicLocation
        getLogger().info("Civic Location : " + networkElementLocation.getCivicLocation(CivicType.LOCATION_CIVIC_BUILDING));
        // END SNIPPET: getCivicLocation

        /*
         * Get the Custom Location information from the Network Element Location.
         */
        // START SNIPPET: getCustomLocation
        getLogger().info("Custom Location : " + networkElementLocation.getCustomLocation("department"));
        // END SNIPPET: getCustomLocation

        /*
         * Get the Geo Location information from the Network Element Location.
         */
        // START SNIPPET: getGeoLocation
        getLogger().info("Geo Location : " + networkElementLocation.getGeoLocation());
        // END SNIPPET: getGeoLocation

        /*
         * Get the Elin  ("Equipment Line Item Number" or "Emergency Location Identification Number")
         * Location information from the Network Element Location.
         */
        // START SNIPPET: getElinLocation
        getLogger().info("Elin Location : " + networkElementLocation.getElinLocation());
        // END SNIPPET: getElinLocation

        /*
         * Get the Civic Location List information from the Network Element Location.
         */
        // START SNIPPET: getCivicLocationList
        List<CivicLocation> civicLocationList = networkElementLocation.getCivicLocationList();
        for (CivicLocation civicLocation : civicLocationList) {
            getLogger().info("Civic Location string : " + civicLocation.toString());
        }
        // END SNIPPET: getCivicLocationList

        /*
         * Get the Custom Location List information from the Network Element Location.
         */
        // START SNIPPET: getCustomLocationList
        List<CustomLocation> customLocationList = networkElementLocation.getCustomLocationList();
        for (CustomLocation customLocation : customLocationList) {
            getLogger().info("Custom Location string : " + customLocation.toString());
        }
        // END SNIPPET: getCustomLocationList

        return networkElementLocation;
    }

    /**
     * Gets the Location of an Network Interface.
     *
     * @param networkElement
     *            Network Element to obtain the Network Interface
     * @return Location of the Network Interface.
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     * @throws OnepIllegalArgumentException
     *             as determined by the presentation layer semantics.
     */
    public Location getNetworkInterfaceLocation(NetworkElement networkElement) throws OnepConnectionException,
            OnepRemoteProcedureException, OnepIllegalArgumentException {
        // START SNIPPET: getNetworkInterfaceLocation
        NetworkInterface networkInterface = networkElement.getInterfaceByName("e0/0");
        Location networkInterfaceLocation = networkInterface.getLocation();
        // END SNIPPET: getNetworkInterfaceLocation

        /*
         * Get the Civic Location information from the Network Interface Location.
         */
        // START SNIPPET: getInterfaceCivicLocation
        getLogger().info("Civic Location : " + networkInterfaceLocation.getCivicLocation(CivicType.LOCATION_CIVIC_BUILDING));
        // END SNIPPET: getInterfaceCivicLocation

        /*
         * Get the Custom Location information from the Network Interface Location.
         */
        // START SNIPPET: getInterfaceCustomLocation
        getLogger().info("Custom Location : " + networkInterfaceLocation.getCustomLocation("department"));
        // END SNIPPET: getInterfaceCustomLocation

        /*
         * Get the Geo Location information from the Network Interface Location.
         */
        // START SNIPPET: getInterfaceGeoLocation
        getLogger().info("Geo Location : " + networkInterfaceLocation.getGeoLocation());
        // END SNIPPET: getInterfaceGeoLocation

        /*
         * Get the Elin Location information from the Network Interface Location.
         */
        // START SNIPPET: getInterfaceElinLocation
        getLogger().info("Elin Location : " + networkInterfaceLocation.getElinLocation());
        // END SNIPPET: getInterfaceElinLocation

        /*
         * Get the Civic Location List information from the Network Interface Location.
         */
        // START SNIPPET: getInterfaceCivicLocationList
        List<CivicLocation> civicLocationList = networkInterfaceLocation.getCivicLocationList();
        for (CivicLocation civicLocation : civicLocationList) {
            getLogger().info("Civic Location string : " + civicLocation.toString());
        }
        // END SNIPPET: getInterfaceCivicLocationList

        /*
         * Get the Custom Location List information from the Network Interface Location.
         */
        // START SNIPPET: getInterfaceCustomLocationList
        List<CustomLocation> customLocationList = networkInterfaceLocation.getCustomLocationList();
        for (CustomLocation customLocation : customLocationList) {
            getLogger().info("Custom Location string : " + customLocation.toString());
        }
        // END SNIPPET: getInterfaceCustomLocationList

        return networkInterfaceLocation;
    }

    /**
     * Sets the location of the specified Network Element. This method shows how to set different types of locations
     *
     * @param networkElement
     *            Network Element to have location be set.
     * @throws OnepIllegalArgumentException
     *             as determined by the presentation layer semantics.
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     */
    public void setNetworkElementLocation(NetworkElement networkElement) throws OnepIllegalArgumentException,
            OnepConnectionException, OnepRemoteProcedureException {

        Location networkElementLocation = networkElement.getLocation();

        /*
         * Set the Civic Location information to the Network Element Location.
         */
        // START SNIPPET: setCivicLocation
        networkElementLocation.addCivicLocation(CivicType.LOCATION_CIVIC_BUILDING, "24");
        // END SNIPPET: setCivicLocation

        /*
         * Set the Custom Location information to the Network Element Location.
         */
        // START SNIPPET: setCustomLocation
        networkElementLocation.addCustomLocation("Company", "Cisco");
        // END SNIPPET: setCustomLocation

        /*
         * Set the Geo Location information to the Network Element Location.
         */
        // START SNIPPET: setGeoLocation
        GeoLocation geoLocation = new GeoLocation(NETWORK_ELEMENT_LATITUDE, //NETWORK_ELEMENT_LATITUDE = 32.3;
                0,
                NETWORK_ELEMENT_LONGITUDE, //NETWORK_ELEMENT_LONGITUDE = 122.5;
                0, 0, 0,
                AltType.LOCATION_ALT_TYPE_METERS);
        networkElementLocation.setGeoLocation(geoLocation);
        // END SNIPPET: setGeoLocation

        /*
         * Set location information to the Location object that was obtained from the Network Element.
         */
        // START SNIPPET: setNetworkElementLocation
        networkElement.setLocation(networkElementLocation);
        // END SNIPPET: setNetworkElementLocation
    }

    /**
     * Sets the Location of the Network Interface.
     *
     * @param networkElement
     *            Network Element whose interface location is going to be set
     * @throws OnepIllegalArgumentException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     */
    public void setNetworkInterfaceLocation(NetworkElement networkElement) throws OnepIllegalArgumentException,
            OnepRemoteProcedureException, OnepConnectionException {
        // START SNIPPET: setNetworkInterfaceLocation
        NetworkInterface networkInterface = networkElement.getInterfaceByName("e0/0");
        Location networkInterfaceLocation = networkInterface.getLocation();

        /*
         * Set the Civic Location information to the Network Element Location.
         */
        // START SNIPPET: setInterfaceCivicLocation
        try {
            networkInterfaceLocation.addCivicLocation(CivicType.LOCATION_CIVIC_CITY, "23");
        } catch (OnepIllegalArgumentException e) {
            getLogger().info(e.getMessage());
        }
        // END SNIPPET: setInterfaceCivicLocation

        /*
         * Set the Custom Location information to the Network Element Location.
         */
        // START SNIPPET: setInterfaceCustomLocation
        try {
            networkInterfaceLocation.addCustomLocation("lab", "server");
        } catch (OnepIllegalArgumentException e) {
            getLogger().info(e.getMessage());
        }

        // Adding Geo Location information
        GeoLocation geoLocation = new GeoLocation(NETWORK_INTERFACE_LATITUDE,
                0,
                NETWORK_INTERFACE_LONGITUDE,
                0, 0, 0,
                AltType.LOCATION_ALT_TYPE_METERS);

        // Adds Geo Location information to the network interface
        networkInterfaceLocation.setGeoLocation(geoLocation);

        // Saves the location information to the network interface.
        networkInterface.setLocation(networkInterfaceLocation);
        // END SNIPPET: setNetworkInterfaceLocation
    }

    /**
     * Removes the location information from the Network Element.
     *
     * @param networkElement
     *            to have its location removed
     * @throws OnepIllegalArgumentException
     *             as determined by the presentation layer semantics.
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     */
    public void removeNetworkElementLocation(NetworkElement networkElement) throws OnepIllegalArgumentException,
            OnepConnectionException, OnepRemoteProcedureException {
        // START SNIPPET: removeNetworkElementLocation
        Location networkElementLocation = networkElement.getLocation();

        /*
         * Removing one Civic information element
         */
        networkElementLocation.removeCivicLocation(CivicType.LOCATION_CIVIC_BUILDING);

        /*
         * Removing all Civic information elements
         */
        networkElementLocation.removeCivicLocationList();

        /*
         * Removing one Custom information element
         */
        networkElementLocation.removeCustomLocation("department");

        /*
         * Removing all Custom information elements
         */
        networkElementLocation.removeCustomLocationList();

        /*
         * Removing Geo Location information
         */
        networkElementLocation.removeGeoLocation();

        /*
         * Saves the changes to the network element.
         */
        networkElement.setLocation(networkElementLocation);
        // END SNIPPET: removeNetworkElementLocation
    }

    /**
     * Removes location information on the network interface.
     *
     * @param networkElement
     *            to have the network interface locations be removed
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     * @throws OnepIllegalArgumentException
     *             as determined by the presentation layer semantics.
     */
    public void removeNetworkInterfaceLocation(NetworkElement networkElement) throws OnepConnectionException,
            OnepRemoteProcedureException, OnepIllegalArgumentException {
        // START SNIPPET: removeNetworkInterfaceLocation
        NetworkInterface networkInterface = networkElement.getInterfaceByName("e0/0");
        Location networkInterfaceLocation = networkInterface.getLocation();

        /*
         * Removing one Civic information element
         */
        networkInterfaceLocation.removeCivicLocation(CivicType.LOCATION_CIVIC_CITY);

        /*
         * Removing all Civic information elements
         */
        networkInterfaceLocation.removeCivicLocationList();

        /*
         * Removing one Custom information element
         */
        networkInterfaceLocation.removeCustomLocation("lab");

        /*
         * Removing all Custom information elements
         */
        networkInterfaceLocation.removeCustomLocationList();

        /*
         * Removing Geo Location information
         */
        networkInterfaceLocation.removeGeoLocation();

        /*
         * Saves the changes to the network interface.
         */
        networkInterface.setLocation(networkInterfaceLocation);
        // END SNIPPET: removeNetworkInterfaceLocation
    }

}
