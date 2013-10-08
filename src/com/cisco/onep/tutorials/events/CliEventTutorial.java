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
package com.cisco.onep.tutorials.events;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.element.CLIEvent;
import com.cisco.onep.element.CLIFilter;
import com.cisco.onep.element.CLIListener;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The CLI event tutorial demonstrates how to add a CLI event listener on a network element.
 *
 * In this example CLI event is set on 'show location' command.
 * When 'show location' command is run on the connected device, first time the user will
 * see its original output, but the second time the user will see the new output as
 * set by the CLI event handler.
 * It also demonstrates how to handle a CLI event and alter its default behavior.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class CliEventTutorial extends BaseTutorial {

  private static final long SLEEP = 20000L;

  /**
   * Invokes the tutorial via the command line.
   *
   * @param args
   */
  public static void main(String args[]) {
     CliEventTutorial tutorial = new CliEventTutorial();
     tutorial.parseOptions(args);

     try {
         if (!tutorial.connect("CliEventTutorial")) {
             System.exit(1);
         }

        /*
         * A handle is returned upon sucessfully adding an event
         * listener. Otherwise, -1 is returned.
         */
        int eventHandle = tutorial.addCliEventListener();
        if (eventHandle == -1) {
           tutorial.getLogger().error("Could not add CLI event listener.");
        } else {
           tutorial.getLogger().info("Added CLI event listener on the Network Element.");
           tutorial.getLogger().info("To verify enter 'show location' command 2 times on the device.");
           /* invoke 'show location' CLI on the connected network element */
           try {
              Thread.sleep(SLEEP);
           } catch (InterruptedException e) {
              tutorial.getLogger().info("Sleep interrupted.");
           }
           tutorial.getLogger().info("Removing CLI listener.");
           /* Remove the CliEventListener from the Network Element. */
          // START SNIPPET: removeCliEventListener
          tutorial.getNetworkElement().removeCLIListener(eventHandle);
          // END SNIPPET: removeCliEventListener
       }
    } catch (OnepException e) {
       tutorial.getLogger().error(e.getLocalizedMessage(), e);
    } finally {
       tutorial.disconnect();
    }
  }

  /**
   * This is an example CLI event Listener that will handle the CLI event.
   */
  // START SNIPPET: ExampleCliListener
  public class ExampleCliListener implements CLIListener {

    private String name;

    /**
     * Creates an Example CLI Event Listener.
     *
     * @param name
     *            Identifies the instance.
     */
     public ExampleCliListener(String name) {
        this.name = name;
     }

    /**
     * Sync indicates if the return code from the listener will
     * control the execution of the CLI on the network element.             *
     * @see com.cisco.onep.element.CLIListener#getSyncReply()
     */
    @Override
    public String getSyncReply() {
        return "-Cisco onePK test reply";
    }

    /**
     * Invoked when a sync is set on CLI event.
     * This event handler will display CLI command's return
     * value as the string that's returned by the
     * overridden CLIListener#getSyncReply() function.
     *
     * @param event
     *        An event object that indicates that an event occurred in a network element.
     * @param clientData
     *        The ClientData is an object that was passed in when the application read an API to register/add
     *        the event listener. The application is responsible for casting the input clientData to the
     *            appropriate class before using it.
     */
     @Override
     public int handleSyncEvent(CLIEvent event, Object clientData) {
         getLogger().info(name + " has received sync event. Sync reply = " + getSyncReply());
         return 0;
     }

    /**
     * Invoked when an event is received from a network element.
     *
     * @param event
     *        An event object that indicates that an event occurred in a network element.
     * @param clientData
     *        The ClientData is an object that was passed in when the application read an API to register/add
     *        the event listener. The application is responsible for casting the input clientData to the
     *        appropriate class before using it.
     */
     public void handleEvent(CLIEvent event, Object clientData) {
         getLogger().info( name + " has received event - " + event.getMessage());
         System.out.println("tty = " + event.getTTY());
     }
  }

   // END SNIPPET: ExampleCliListener

  /**
   * Adds a CliEventListener with a CliFilter for "show location" CLI command
   *
   * @return The event handle for the added listener.
   */
  // START SNIPPET: addCliEventListener
  public int addCliEventListener() {
     /* Constructs a CLIFilter object with specified regular expression pattern,
      * using a pattern string filter to perform CLI message pattern matching.
      */
     CLIFilter cliFilter = new CLIFilter("location");

     /* Sets the occurs parameter used by the filter. It specifies
      * the number of occurrences before the event is raised.
      * 1 is default value. The range is between 1-32 occurrences.
      */
     cliFilter.setOccurs(2);

     /*
      * Sets the sync flag.
      * Any value other than zero indicates that default command
      * will be run synchronously with the listener event handler.
      * Note: if skip is set to true, sync cannot also be true.
      */
     cliFilter.setSync(1);

     ExampleCliListener exampleCliEventListener = new ExampleCliListener("CLI Location Event");

     try {
         return getNetworkElement().addCLIListener(exampleCliEventListener, cliFilter, null);
     } catch (OnepException e) {
         getLogger().error(e.getLocalizedMessage(), e);
     }
     return -1;
  }
    // END SNIPPET: addCliEventListener
}
