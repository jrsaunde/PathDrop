/**
 * Copyright (c) 2010-2012, Cisco Systems, Inc.
 *
 * THIS SAMPLE CODE IS PROVIDED "AS IS" WITHOUT ANY EXPRESS OR IMPLIED WARRANTY BY CISCO SOLELY FOR THE PURPOSE of
 * PROVIDING PROGRAMMING EXAMPLES. CISCO SHALL NOT BE HELD LIABLE FOR ANY USE OF THE SAMPLE CODE IN ANY APPLICATION.
 *
 * Redistribution and use of the sample code, with or without modification, are permitted provided that the following
 * conditions are met: Redistributions of source code must retain the above disclaimer.
 *
 */
package com.cisco.onep.tutorials.location;

import java.util.List;

import com.cisco.onep.core.exception.OnepConnectionException;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.location.CivicLocation;
import com.cisco.onep.location.CustomLocation;
import com.cisco.onep.location.GeoLocation;
import com.cisco.onep.location.Location;
import com.cisco.onep.location.LocationSubtype;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial demonstrates the types and sub-types of Locations.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */

public class LocationTypesTutorial extends BaseTutorial {

    /**
     * Invokes the test via the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        LocationTypesTutorial tutorial = new LocationTypesTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("LocationEventsTutorial")) {
                System.exit(1);
            }

            NetworkElement networkElement = tutorial.getNetworkElement();

            /*
             * Sets the predefined location subtypes in LocationGetSetTutorial.
             */
            LocationGetSetTutorial locationGetSetTutorial = new LocationGetSetTutorial();
            locationGetSetTutorial.setNetworkElementLocation(networkElement);

            /*
             * Check for the location subtypes.
             */
            tutorial.checkLocationSubtype(networkElement);

            /*
             * Remove the location from the Network Element.
             */
            locationGetSetTutorial.removeNetworkElementLocation(networkElement);

        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }

    /**
     * Checks if the subtypes are present in a network elements location.
     *
     * @param networkElement
     *            Network Element to have its subtypes checked
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     */
    public void checkLocationSubtype(NetworkElement networkElement) throws OnepConnectionException,
            OnepRemoteProcedureException {
        // START SNIPPET: checkLocationSubtype
        Location location = networkElement.getLocation();

        /*
         * Checking to see if ELIN information is present
         */
        boolean hasSubtype = location.hasSubtype(LocationSubtype.LOCATION_TYPE_ELIN);
        if (hasSubtype) {
            String elin = location.getElinLocation();
            getLogger().info(elin);
        }

        // Checking to see if Civic information is present
        hasSubtype = location.hasSubtype(LocationSubtype.LOCATION_TYPE_CIVIC);
        if (hasSubtype) {
            List<CivicLocation> civicList = location.getCivicLocationList();
            for (CivicLocation civic : civicList) {
                getLogger().info("CAtype " + civic.getCatype() + " has value " + civic.getValue());
            }
        }

        // Checking to see if Custom information is present
        hasSubtype = location.hasSubtype(LocationSubtype.LOCATION_TYPE_CUSTOM);
        if (hasSubtype) {
            List<CustomLocation> customList = location.getCustomLocationList();
            for (CustomLocation custom : customList) {
                getLogger().info("Attribute " + custom.getName() + " has value " + custom.getValue());
            }
        }

        // Checking to see if Geo Location information is present
        hasSubtype = location.hasSubtype(LocationSubtype.LOCATION_TYPE_GEO);
        if (hasSubtype) {
            GeoLocation geoLocation = location.getGeoLocation();
            getLogger().info(geoLocation.toString());
        }
        // END SNIPPET: checkLocationSubtype
    }

}
