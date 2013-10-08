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

import java.net.UnknownHostException;

import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.element.SyslogEvent;
import com.cisco.onep.element.SyslogFilter;
import com.cisco.onep.element.SyslogListener;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The SyslogEventsTutorial creates an example SyslogListener that logs the String representation of the SyslogEvents
 * received from the Network Element. The listener is constructed with a name so that different listener instances can differentiate their logged
 * output.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class SyslogEventTutorial extends BaseTutorial {

  private static final long SLEEP = 10000L;

  /**
   * Invokes the tutorial via the command line.
   *
   * @param args
   */
  public static void main(String args[]) {
     SyslogEventTutorial tutorial = new SyslogEventTutorial();
     tutorial.parseOptions(args);

     try {
         if (!tutorial.connect("SyslogEventTutorial")) {
             System.exit(1);
         }

         int eventHandleUpDown = tutorial.addSyslogListenerUpDown();
         int eventHandleAll = tutorial.addSyslogListenerAll();
         if ((eventHandleUpDown == -1) || (eventHandleAll == -1)) {
             tutorial.getLogger().error("Could not add one or more syslog listeners.");
         } else {
             tutorial.simulateShutdown(true);
             try {
                 Thread.sleep(SLEEP);
             } catch (InterruptedException e) {
                 tutorial.getLogger().info("Sleep interrupted.");
             }
             tutorial.simulateShutdown(false);
             try {
                 Thread.sleep(SLEEP);
             } catch (InterruptedException e) {
                 tutorial.getLogger().info("Sleep interrupted.");
             }
             tutorial.getNetworkElement().removeSyslogListener(eventHandleUpDown);
             tutorial.getNetworkElement().removeSyslogListener(eventHandleAll);
         }
     } catch (UnknownHostException e) {
         tutorial.getLogger().error(e.getLocalizedMessage(), e);
     } catch (OnepException e) {
         tutorial.getLogger().error(e.getLocalizedMessage(), e);
     } finally {
         tutorial.disconnect();
     }
  }

  /**
   * This is an example Syslog Listener that will printout.
   */
  // START SNIPPET: ExampleSyslogListener
  public class ExampleSyslogListener implements SyslogListener {

     private String name;

     /**
      * Creates an ExampleSyslogListener.
      *
      * @param name
      *            Identifies the instance.
      */
     public ExampleSyslogListener(String name) {
         this.name = name;
     }

     /**
      * Invoked when an event is received from a network element.
      *
      * @param event
      *         An event object that indicates that an event occurred in a network element.
      * @param clientData
      *         The ClientData is an object that was passed in when the application read an API to register/add
      *         the event listener. The application is responsible for casting the input clientData to the
      *        appropriate class before using it.
      */

     public void handleEvent(SyslogEvent event, Object clientData) {
         getLogger().info(name + " has received event - " + event.getMessage());
     }
  }

  // END SNIPPET: ExampleSyslogListener

  /**
   * Adds a SyslogListener with a SyslogFilter for "UPDOWN" events on interfaces to the Network Element.
   *
   * @return The event handle for the added listener.
   */
  // START SNIPPET: addSyslogListener
  public int addSyslogListenerUpDown() {
     /* Constructs a SyslogFilter object with specified regular expression pattern.*/
     SyslogFilter syslogFilter = new SyslogFilter("UPDOWN");
     /* messages that are at the specified logging priority level or lower, are filtered */
     syslogFilter.setPriority(5);
     ExampleSyslogListener exampleSyslogListener = new ExampleSyslogListener("Syslog UPDOWN");

     try {
         return getNetworkElement().addSyslogListener(exampleSyslogListener, syslogFilter, null);
     } catch (Exception e) {
         getLogger().error(e.getLocalizedMessage(), e);
     }
     return -1;

  }

  // END SNIPPET: addSyslogListener

  /**
   * Adds a SyslogListener with a SyslogFilter for all, '.*', syslog events to the Network Element.
   *
   * @return The event handle for the added listener.
   */
  // START SNIPPET: addSyslogListenerAll
  public int addSyslogListenerAll() {
      SyslogFilter syslogFilter = new SyslogFilter(".*");
      syslogFilter.setPriority(5);
      ExampleSyslogListener exampleSyslogListener = new ExampleSyslogListener("Syslog All");

      try {
          return getNetworkElement().addSyslogListener(exampleSyslogListener, syslogFilter, null);
      } catch (Exception e) {
          getLogger().error(e.getLocalizedMessage(), e);
      }
      return -1;

  }
  // END SNIPPET: addSyslogListenerAll
}
