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

import com.cisco.onep.core.exception.OnepConnectionException;
import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.core.exception.OnepIllegalArgumentException;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.location.Location;
import com.cisco.onep.location.LocationChangeEvent;
import com.cisco.onep.location.LocationChangeEventChanger;
import com.cisco.onep.location.LocationChangeFilter;
import com.cisco.onep.location.LocationChangeListener;
import com.cisco.onep.location.LocationSubtype;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial shows how to get Location data for Network Elements, how to create and add a LocationChangeListener and
 * sets the Location to generate events.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */

public class LocationEventsTutorial extends BaseTutorial {

    /**
     * Invokes the tutorial via the command line.
     *
     * @param args
     */
    public static void main(String[] args) {
        LocationEventsTutorial tutorial = new LocationEventsTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("LocationEventsTutorial")) {
                System.exit(1);
            }
            NetworkElement networkElement = tutorial.getNetworkElement();
            Location networkElementLocation = networkElement.getLocation();

            // Add a Location Change Listener to the Location.
            int geoEventHandler = tutorial.addListener(networkElementLocation, tutorial.getGeoLocationChangeFilter());
            int customEventHandler = tutorial.addListener(networkElementLocation,
                    tutorial.getCustomLocationChangeFilter());
            int civicEventHandler = tutorial.addListener(networkElementLocation,
                    tutorial.getCivicLocationChangeFilter());
            if (geoEventHandler == -1
                    || customEventHandler == -1
                    || civicEventHandler == -1) {
                tutorial.getLogger().error("Could get add one or more event listeners.");
            } else {
                // Adds Location to the Network Element.
                LocationGetSetTutorial locationSetters = new LocationGetSetTutorial();
                locationSetters.setNetworkElementLocation(networkElement);

                // Remove Listeners from the Network Element.
                tutorial.removeListener(networkElementLocation, geoEventHandler);
                tutorial.removeListener(networkElementLocation, customEventHandler);
                tutorial.removeListener(networkElementLocation, civicEventHandler);

                // Remove Location from the Network Element.
                locationSetters.removeNetworkElementLocation(networkElement);
            }
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }

    /**
     * Custom ChangeListener to be implemented as an inner class.
     */
    // START SNIPPET: ExampleLocationChangeListener
    public class ExampleLocationChangeListener implements LocationChangeListener {
        /**
         * This will handle the events that was caught by the listener.
         *
         * @param event
         *            Event that was caught by the listener
         * @param clientData
         *            optional client data to be passed in.
         */
        public void handleEvent(LocationChangeEvent event, Object clientData) {

            // Check what has been changed.
            boolean geoChanged = event.hasSubtype(LocationSubtype.LOCATION_TYPE_GEO);
            boolean civicChanged = event.hasSubtype(LocationSubtype.LOCATION_TYPE_CIVIC);
            boolean customChanged = event.hasSubtype(LocationSubtype.LOCATION_TYPE_CUSTOM);

            // Check the cause of the change.
            LocationChangeEventChanger changer = event.getChanger();

            // Print the information
            StringBuilder output = new StringBuilder("Watched location (");
            if (geoChanged) {
                output.append("Geo ");
            }
            if (civicChanged) {
                output.append("Civic ");
            }
            if (customChanged) {
                output.append("Custom ");
            }
            output.append(") was changed via ");
            output.append(changer);
            getLogger().info(output.toString());
        }
    }

    // END SNIPPET: ExampleLocationChangeListener

    /**
     * Get the LocationChangeFilter that will filter for GeoLocation.
     *
     * @return LocationChangeFilter that will filter for GeoLocation.
     */
    public LocationChangeFilter getGeoLocationChangeFilter() {
        // START SNIPPET: getGeoLocationChangeFilter
        // Creating a filter for change events
        LocationChangeFilter filter = new LocationChangeFilter();
        filter.addSubtype(LocationSubtype.LOCATION_TYPE_GEO);
        return filter;
        // END SNIPPET: getGeoLocationChangeFilter
    }

    /**
     * Get the LocationChangeFilter that will filter for CustomLocation.
     *
     * @return LocationChangeFilter that will filter for CustomLocation.
     */
    public LocationChangeFilter getCustomLocationChangeFilter() {
        // Creating a filter for change events
        LocationChangeFilter filter = new LocationChangeFilter();
        filter.addSubtype(LocationSubtype.LOCATION_TYPE_CUSTOM);
        return filter;
    }

    /**
     * Get the LocationChangeFilter that will filter for CivicLocation.
     *
     * @return LocationChangeFilter that will filter for CivicLocation.
     */
    public LocationChangeFilter getCivicLocationChangeFilter() {
        // Creating a filter for change events
        LocationChangeFilter filter = new LocationChangeFilter();
        filter.addSubtype(LocationSubtype.LOCATION_TYPE_CIVIC);
        return filter;
    }

    /**
     * Adds an LocationChangeFilter to the specific location.
     *
     * @param location
     *            location to be listening to
     * @param filter
     *            LocationChangeFilter to be applied
     * @return eventHandler in the form of an integer
     * @throws OnepException
     *             as determined by the presentation layer semantics.
     */
    public int addListener(Location location, LocationChangeFilter filter) throws OnepException {
        // START SNIPPET: addListener
        // Add a listener for location change events
        ExampleLocationChangeListener listener = new ExampleLocationChangeListener();
        int eventHandler = location.addChangeListener(listener, filter, null);

        return eventHandler;
        // END SNIPPET: addListener
    }

    /**
     * Removes the listener from a specific location.
     *
     * @param location
     *            to have its listener be removed
     * @param eventHandler
     *            associated with the location
     * @throws OnepIllegalArgumentException
     *             as determined by the presentation layer semantics.
     * @throws OnepRemoteProcedureException
     *             as determined by the presentation layer semantics.
     * @throws OnepConnectionException
     *             as determined by the presentation layer semantics.
     */
    public void removeListener(Location location, int eventHandler) throws OnepIllegalArgumentException,
            OnepRemoteProcedureException, OnepConnectionException {
        // START SNIPPET: removeListener
        // Removing event listener
        location.removeChangeListener(eventHandler);
        // END SNIPPET: removeListener
    }
}
