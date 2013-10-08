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

import java.net.InetAddress;

import com.cisco.onep.cdp.CDPEvent;
import com.cisco.onep.cdp.CDPEvent.CDPEventNotifyType;
import com.cisco.onep.cdp.CDPFilter;
import com.cisco.onep.cdp.CDPListener;
import com.cisco.onep.core.exception.OnepException;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.interfaces.InterfaceFilter;
import com.cisco.onep.interfaces.NetworkInterface;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * The goal of this tutorial is to add a CDP (Cisco Discovery Protocol) listener and to process
 * the CDP event occurred on a NetworkElement object.
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class CDPEventTutorial extends BaseTutorial{

  private static final long SLEEP = 10000L;

  /**
   * Invokes the tutorial via the command line.
   *
   * @param args
   */
  public static void main(String args[]) {
     CDPEventTutorial tutorial = new CDPEventTutorial();
     tutorial.parseOptions(args);

     try {
         if (!tutorial.connect("CDPEventTutorial")) {
             System.exit(1);
         }

         /*
          * A handle is returned upon sucessfully adding an event listener.
          * Otherwise, -1 is returned.
          */
         int eventHdl = tutorial.addCDPListenerOnNe();
         if (eventHdl == -1) {
             tutorial.getLogger().error("Could not add CDP listener.");
         } else {
             try {
                 Thread.sleep(SLEEP);
             } catch (InterruptedException e) {
                 tutorial.getLogger().info("Sleep interrupted.");
             }
             tutorial.getLogger().info("Removing CDP listener.");
           // START SNIPPET: removeCDPListenerOnNe
             tutorial.getNetworkElement().removeCDPListener(eventHdl);
          // END SNIPPET: removeCDPListenerOnNe
         }
     } catch (OnepException e) {
         tutorial.getLogger().error(e.getLocalizedMessage(), e);
     } finally {
         tutorial.disconnect();
     }
  }

  /**
   * Implements an CDPListener that logs the String representation of the CDPEvent received.
   * The listener is constructed with a name so that different listener instances can
   * differentiate their logged output.
   */
  // START SNIPPET: ExampleNeCDPListener
  class ExampleNeCDPListener implements CDPListener {
      private String name;
      public ExampleNeCDPListener(String name) {
          this.name = name;
      }

      /**
       * Invoked when an event is received from a network element.
       *
       * @param event
       *       An event object that indicates that an event has occurred in a network element.
       * @param clientData
       *       The clientData is an object that is passed in when the application calls an API to add/register
       *        the event listener. The application is responsible for casting the input clientData to the
       *        appropriate class before using it.
       */

     public void handleEvent(CDPEvent event, Object clientData) {
         getLogger().info(name + " has received event on - " + event.getInterface().getName());
         getLogger().info("Capabilities = " + event.getCapabilities());
         getLogger().info("Device id = " + event.getDeviceID());
         getLogger().info("Hold time = " + event.getHoldTime());
         getLogger().info("Management Domain = " + event.getManagementDomain());
         getLogger().info("Notify Type = " + event.getNotifyType().name());
         getLogger().info("Platform = " + event.getPlatform());
         getLogger().info("Version = " + event.getVersion());

         for (InetAddress addr : event.getAddresses()) {
             getLogger().info("Address = " + addr);
         }
      }
  }
  // END SNIPPET: ExampleNeCDPListener

  /**
   * Creates an CDPListener and adds that as a listener for any CDP event
   * on the Network Element.
   *
   * @return eventHdl event handle id
   * @throws OnepException
   *             If there is an error.
   */
  // START SNIPPET: addCDPListenerOnNe
  public int addCDPListenerOnNe() throws OnepException {

    NetworkElement ne = getNetworkElement();
    int eventHdl;

    ExampleNeCDPListener cdpListener = new ExampleNeCDPListener( "NeworkElement CDP listener");

    /* Constructs a CDPFilter object without specifying criteria. and expression */
    CDPFilter cdpfilter = new CDPFilter();
    /* Sets the type of notification to be monitored. */
    cdpfilter.setNotifyType(CDPEventNotifyType.ONEP_CDP_ALL);

    /* Gives the empty interface filter that will match all the interfaces */
    InterfaceFilter intfFilter = new InterfaceFilter();
    /* Sets the type of interface */
    intfFilter.setType(NetworkInterface.Type.ONEP_IF_TYPE_ANY);

    /* Add a CDP listener on a network element */
    eventHdl = ne.addCDPListener(cdpListener, intfFilter, cdpfilter, null);

    getLogger().info("Added CDP listener on the Network Element.");

    return eventHdl;
  }
  // END SNIPPET: addCDPListenerOnNe
}

