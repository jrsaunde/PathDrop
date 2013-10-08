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
import com.cisco.onep.element.OIREvent;
import com.cisco.onep.element.OIRFilter;
import com.cisco.onep.element.OIRListener;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * Online Insertion and Removal (OIR) event tutorial demonstrate how to add an online insertion and removal (OIR)
 * event listener on a network element.
 * It also demonstrates how to handle an OIR event.
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class OirEventTutorial extends BaseTutorial {

  private static final long SLEEP = 10000L;

  /**
   * Invokes the tutorial via the command line.
   *
   * @param args
   */
  public static void main(String args[]) {
     OirEventTutorial tutorial = new OirEventTutorial();
     tutorial.parseOptions(args);

     try {
       if (!tutorial.connect("OirEventTutorial")) {
          System.exit(1);
       }

      /*
       * A handle is returned upon sucessfully adding an event
       * listener. Otherwise, -1 is returned.
       */
      int eventHandle = tutorial.addOirEventListener();
      if (eventHandle == -1) {
          tutorial.getLogger().error("Could not add OIR event listener.");
      } else {
          tutorial.getLogger().info("Added OIR listener on the Network Element.");
          tutorial.getLogger().info("To verify, insert a new card in any slot.");
          /* oir slot <slot_number> insert command to insert the card using the software.*/
          try {
              Thread.sleep(SLEEP);
          } catch (InterruptedException e) {
              tutorial.getLogger().info("Sleep interrupted.");
          }
          // START SNIPPET: removeOirEventListener
          tutorial.getLogger().info("Removing OIR listener.");
          tutorial.getNetworkElement().removeOIRListener(eventHandle);
          // END SNIPPET: removeOirEventListener
      }
    } catch (OnepException e) {
         tutorial.getLogger().error(e.getLocalizedMessage(), e);
    } finally {
        tutorial.disconnect();
    }
  }

  /**
   * This is an example OIR event Listener that will handle the OIR event.
   */
  // START SNIPPET: ExampleOirListener
  public class ExampleOirListener implements OIRListener {

     private String name;

     /**
      * Creates an Example CLI Event Listener.
      *
      * @param name
      *            Identifies the instance.
      */
     public ExampleOirListener(String name) {
         this.name = name;
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
     public void handleEvent(OIREvent event, Object clientData) {
         getLogger().info( name + " has received OIR event on slot - " + event.getSlot());
        /* prints the OIR type such as ONEP_OIR_INSERT, ONEP_OIR_REMOVE or ONEP_OIR_ALL */
         getLogger().info( "OIR type = " + event.getOIRType());
    }
  }

  // END SNIPPET: ExampleOirListener

  /**
   * Adds a OirEventListener
   *
   * @return The event handle for the added listener.
   */
  // START SNIPPET: addOirEventListener
  public int addOirEventListener() {
     /* The default constructor to receive notifications for all OIR events. */
     OIRFilter oirFilter = new OIRFilter();
     /* set OIR event type to insert card */
     oirFilter.setOIRType(OIRFilter.OIRType.ONEP_OIR_INSERT);

     ExampleOirListener exampleOirEventListener = new ExampleOirListener("OIR Event");

     try {
         return getNetworkElement().addOIRListener(exampleOirEventListener, oirFilter, null);
     } catch (Exception e) {
         getLogger().error(e.getLocalizedMessage(), e);
     }
     return -1;
  }
  // END SNIPPET: addOirEventListener
}

