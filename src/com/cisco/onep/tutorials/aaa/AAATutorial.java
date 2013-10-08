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
package com.cisco.onep.tutorials.aaa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.cisco.onep.aaa.Attribute;
import com.cisco.onep.aaa.OnepAAAAttributeType;
import com.cisco.onep.aaa.Server;
import com.cisco.onep.aaa.StringAttribute;
import com.cisco.onep.aaa.User;
import com.cisco.onep.aaa.User.OnepAAAAcctAction;
import com.cisco.onep.core.exception.OnepException;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * AAA Tutorial
 *
 * This tutorial demonstrates the onePK AAA (Authentication, Authorization,
 * and Accounting) Service Set. The AAA Service Set provides AAA client
 * functionality to onePK applications.
 *
 * You will learn how to authenticate a user, how to read the user's
 * authorization attributes configured on the AAA server, and how to send
 * accounting requests to the AAA server.
 */
public final class AAATutorial extends BaseTutorial {

    /**
     * Main method.
     * @param args Command line arguments:
     *             {@code -a <element address>}
     *             {@code -u <username>}
     *             {@code -p <password>}
     *             {@code -t <transport type>}
     */
    public static void main(String[] args) {
        AAATutorial tutorial = new AAATutorial();

        /* Parse arguments from command line or properties file. */
        tutorial.parseOptions(args);

        tutorial.getLogger().info("\n******* CONNECT *******");

        /* In order for a user to be granted application-specific authorization,
         * the application name specified here must match the application name
         * in the attributes configured on the AAA server. It is important to
         * use a name that uniquely identifies the application. AAA service set
         * APIs require an established connection to the network element.
         */
        try {
// START SNIPPET: connect_network_element
            if (!tutorial.connect("com.cisco.onep.tutorials.aaa.AAATutorial")) {
                tutorial.getLogger().error("Failed to connect to element.");
                System.exit(1);
            }
// END SNIPPET: connect_network_element
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
            System.exit(1);
        }

        /* Instantiate a User that holds a AAA user's attributes. The AAA user
         * is associated with one network element, and the association can be
         * changed using setNetworkElement(). The AAA user must authenticate
         * to the AAA server prior to using other AAA services. During
         * authentication, the application can supply an optional list of
         * attributes if required by the AAA server.
         */
        tutorial.getLogger().info("\n******* AUTHENTICATE *******");

        User aaaUser = null;
        try {
// START SNIPPET: onep_aaa_user_new
            aaaUser = new User(tutorial.getNetworkElement(),
                               tutorial.getUsername(),
                               tutorial.getPassword());
// END SNIPPET: onep_aaa_user_new
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
            tutorial.disconnect();
            System.exit(1);
        }

        List<Attribute> aaaAttributeList = null;
        try {
// START SNIPPET: onep_aaa_user_authenticate
            /* Authenticate without supplying any additional attributes. */
            aaaAttributeList = aaaUser.authenticate(null);
// END SNIPPET: onep_aaa_user_authenticate
        } catch (OnepException e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
            tutorial.disconnect();
            System.exit(1);
        }

        tutorial.getLogger().info("Authentication successful: " + aaaUser.getUsername());

        tutorial.getLogger().info("\n******* GET AAA SERVER INFO *******");

        /* We can get information about the AAA server that served the last
         * request using this API.
         */
// START SNIPPET: onep_aaa_user_get_last_used_server
        Server server = aaaUser.getLastUsedServer();
// END SNIPPET: onep_aaa_user_get_last_used_server
        tutorial.getLogger().info("IP address: " + server.getAddress().toString() + "\n"
                + "Protocol: " + server.getProtocol().name());

        tutorial.getLogger().info("\n******* GET AUTHORIZATION PROFILE *******");

        if (aaaAttributeList.size() > 0) {

        /* Accounting records may be sent manually unless auto-accounting is
         * enabled for the user.
         */
// START SNIPPET: aaa_accounting_sta
            if (aaaUser.isAutoAcctEnabled()) {
                tutorial.getLogger().info("Auto-accounting is enabled.");
            } else {
                tutorial.getLogger().info("Auto-accounting is not enabled.");
                try {
                    aaaUser.sendAccountingRecord(
                            User.OnepAAAAcctAction.ONEP_AAA_ACCT_ACTION_START,
                            null);
                } catch (OnepException e) {
                    tutorial.getLogger().error(e.getLocalizedMessage(), e);
                }
            }
// END SNIPPET: aaa_accounting_sta

        }
        /* Get an action from the user. */
        tutorial.getLogger().info("Possible actions are: [ walk | run | custom ]");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String nextLine = "";
        while (!nextLine.equalsIgnoreCase("quit") && !nextLine.equalsIgnoreCase("exit")) {
            tutorial.getLogger().info("\nEnter an action, or \"quit\" to end the application: ");
            try {
                nextLine = reader.readLine();
                if (null == nextLine) {
                    break;
                }
            } catch (IOException e) {
                break;
            }

            /* The only action allowed by default is "walk". All others require
             * authorization.
             */
            if (!nextLine.equals("walk")) {
                try {
                    /* Allowed actions are returned separate from the authorization
                     * profile. Here, we check if the user is allowed to perform the
                     * requested action.
                     */
// START SNIPPET: onep_aaa_user_is_action_authorized
                    if (!aaaUser.isActionAuthorized(nextLine)) {
                        tutorial.getLogger().info("You are not allowed to perform this action.");
                        continue;
                    }
// END SNIPPET: onep_aaa_user_is_action_authorized
                } catch (OnepException e) {
                    tutorial.getLogger().error(e.getLocalizedMessage(), e);
                    break;
                }
                /* Authorization passed. */

                /* If the AAA server is configured with app-specific attributes,
                 * we can get them from the authorization profile. Here, we read
                 * the app attribute "custom" from the attribute list.
                 */
// START SNIPPET: onep_aaa_attr_list_get_app_attr_string
                if (nextLine.equals("custom")) {
                    for (Attribute attribute : aaaAttributeList) {
                        if (OnepAAAAttributeType.ONEP_AAA_AT_APP_ATTR == attribute.getType()
                            && attribute.getName().equals("aaa-tutorial-custom")) {
                            /* Print the message for the custom action. */
                            tutorial.getLogger().info("You " +
                                    ((StringAttribute)attribute).getString());
                            break;
                        }
                    }
                    tutorial.getLogger().info("Custom action not found.");
                    continue;
                }
// END SNIPPET: onep_aaa_attr_list_get_app_attr_string
            }

            /* Print the message for a non-custom action. */
            tutorial.getLogger().info("You " + nextLine);
        }
        tutorial.getLogger().info("Goodbye!");

        tutorial.getLogger().info("\n******* DISCONNECT AND CLEAN UP *******\n\n");

        /* When auto-accounting is enabled, accounting stops when the application
         * disconnects from the network element. Otherwise, the application can
         * manually send a "stop" request.
         */
        if (aaaAttributeList.size() > 0 && !aaaUser.isAutoAcctEnabled()) {
            try {
// START SNIPPET: aaa_accounting_stop
                aaaUser.sendAccountingRecord(OnepAAAAcctAction.ONEP_AAA_ACCT_ACTION_STOP, null);
// END SNIPPET: aaa_accounting_stop
            } catch (OnepException e) {
                tutorial.getLogger().error(e.getLocalizedMessage(), e);
            }
        }

        tutorial.disconnect();
    }

}
