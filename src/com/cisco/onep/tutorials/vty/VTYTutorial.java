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
package com.cisco.onep.tutorials.vty;

import java.util.List;

import com.cisco.onep.core.exception.OnepRemoteProcedureException;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.vty.VtyService;
import com.cisco.onep.vty.VtyService.CmdResults;
import com.cisco.onep.vty.VtyService.OnepVtyState;
import com.cisco.onep.vty.VtyService.ParserState;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial shows how to create VTY Service on a ONEP Application
 * and demonstrates the capability to communicate with a Network Element via virtual terminal. .
 *
 * @author The onePK Team (onepk-feedback@cisco.com)
 *
 */
public class VTYTutorial extends BaseTutorial {

    /**
     * Invokes the test via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        VTYTutorial tutorial = new VTYTutorial();
        tutorial.parseOptions(args);

        try {
            if (!tutorial.connect("VTYTutorial")) {
                System.exit(1);
            }

            NetworkElement networkElement = tutorial.getNetworkElement();
            // START SNIPPET: create_vty
            /*
             * Get a VTYService instance for the network element.
             */
            VtyService vtyService = new VtyService(networkElement);
            // END SNIPPET: create_vty

            // START SNIPPET: open_vty
            /*
             * Open the VTY with the default command interpreter.
             */
            vtyService.open();
            // END SNIPPET: open_vty

            // START SNIPPET: get_vty_timeout
            /*
             * Get the timeout value of the VTY.
             */
            int timeOut = vtyService.getTimeout();
            tutorial.getLogger().info("VTY Time Out - " + timeOut);
            // END SNIPPET: get_vty_timeout

            // START SNIPPET: write_vty_command
            /*
             * Write a command to the VTY.
             */
            String showOnepStatusCmd = "show onep status";
            String cliResult = vtyService.write(showOnepStatusCmd);
            tutorial.getLogger().info("CLI Result (" + showOnepStatusCmd + ") - " + cliResult);
            // END SNIPPET: write_vty_command


            /*
             * Get the parser state of the VTY.
             */
            try {
                // START SNIPPET: get_vty_parser_state
                ParserState parserState = vtyService.getParserState();
                // END SNIPPET: get_vty_parser_state
                /*
                 * Show the parser state attributes
                 */
                tutorial.showParserStateAttributes(parserState);
            } catch (OnepRemoteProcedureException e) {
                tutorial.getLogger().error(e.getLocalizedMessage(), e);
            }

            // START SNIPPET: get_vty_state
            /*
             * Get the VTY state.
             */
            OnepVtyState state = vtyService.getState();
            tutorial.getLogger().info("State - " + state);
            // END SNIPPET: get_vty_state

            // START SNIPPET: set_vty_max_response
            /*
             * Set the max response length.
             */
            int maxResponseLength = 110;
            vtyService.setMaxResponse(maxResponseLength);
            tutorial.getLogger().info("MaxResponse - " + vtyService.getMaxResponse());
            // END SNIPPET: set_vty_max_response

            /*
             * Write a command to the VTY and check the response for its length.
             */
            String showVersionCmd = "show version";
            cliResult = vtyService.write(showVersionCmd);
            tutorial.getLogger().info("CLI Result (" + showVersionCmd + ") with maxresponse set to "
                                      + maxResponseLength + " - " + cliResult);

            // START SNIPPET: get_vty_max_response
            /*
             * Get the VTY's current max response length.
             */
            tutorial.getLogger().info("MaxResponse - " + vtyService.getMaxResponse());
            // END SNIPPET: get_vty_max_response

            /*
             * Reset the max response length.
             */
            maxResponseLength = 0;
            vtyService.setMaxResponse(maxResponseLength);

            cliResult = vtyService.write(showVersionCmd);
            tutorial.getLogger().info("CLI Result (" + showVersionCmd + ") with maxresponse set to "
                                      + maxResponseLength + " - " + cliResult);
            try {
                ParserState parserState = vtyService.getParserState();

                /*
                 * Show the parser state attributes.
                 */
                tutorial.showParserStateAttributes(parserState);
            } catch (OnepRemoteProcedureException e) {
                tutorial.getLogger().error(e.getLocalizedMessage(), e);
            }

            // START SNIPPET: vty_cancel_cmd
            /*
             * Cancel the command execution.
             */
            vtyService.cancel();
            // END SNIPPET: vty_cancel_cmd

            // START SNIPPET: vty_close
            /*
             * Close the VTY connection.
             */
            vtyService.close();
            // END SNIPPET: vty_close

            /*
             * Check if the VTY is still open
             */
            tutorial.getLogger().info("Is Open - " + vtyService.isOpen());

            // START SNIPPET: vty_destroy
            /*
             * Destroy the VTY service.
             */
            vtyService.destroy();
            // END SNIPPET: vty_destroy
        } catch (Exception e) {
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        } finally {
            tutorial.disconnect();
        }
    }


    /**
     * This method prints the attributes of the ParserState and iterates over the command results
     *
     * @param parserState
     */
    public void showParserStateAttributes(ParserState parserState) {
        // START SNIPPET: vty_parser_state_attribs
        getLogger().info("ParserState prompt - " + parserState.getPrompt());
        getLogger().info("ParserState overallrc - " + parserState.getOverallRC());
        List<CmdResults> cmdResults = parserState.getResults();
        for (CmdResults cmdResult : cmdResults) {
            getLogger().info("ParserState::cmdresult:inputline - " + cmdResult.getInputLine());
            getLogger().info("ParserState::cmdresult:parsereturncode - " + cmdResult.getParseReturnCode());
            getLogger().info("ParserState::cmdresult:errorlocation - " + cmdResult.getErrorLocaiton());
        }
        // END SNIPPET: vty_parser_state_attribs
    }
}
