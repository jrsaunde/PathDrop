/**
 * Copyright (c) 2010-2013, Cisco Systems, Inc.
 * 
 * THIS SAMPLE CODE IS PROVIDED "AS IS" WITHOUT ANY EXPRESS OR IMPLIED WARRANTY BY CISCO SOLELY FOR THE PURPOSE of
 * PROVIDING PROGRAMMING EXAMPLES. CISCO SHALL NOT BE HELD LIABLE FOR ANY USE OF THE SAMPLE CODE IN ANY APPLICATION.
 * 
 * Redistribution and use of the sample code, with or without modification, are permitted provided that the following
 * conditions are met: Redistributions of source code must retain the above disclaimer.
 * 
 */

package com.cisco.onep.tutorials.applmgmt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.cisco.onep.applmgmt.ApplicationCLI;
import com.cisco.onep.applmgmt.ApplicationCLIData;
import com.cisco.onep.applmgmt.ApplicationConfigCLIListener;
import com.cisco.onep.applmgmt.ApplicationExecCLIListener;
import com.cisco.onep.core.exception.OnepRemoteProcedureException;

import com.cisco.onep.tutorials.BaseTutorial;

/**
 * This tutorial demonstrates how to use custom application managed data.
 * 
 * @author The onePK Team (onepk-feedback@cisco.com)
 */

public class ApplicationManagementTutorial extends BaseTutorial {
    
    private static final long SLEEP = 300000L;
    private static String app_name;
    private static String domain;
    private static String instance;
    private static String version;
    
    /**
     * Invokes the test via the command line.
     *
     * @param args
     */
    public static void main(String args[]) {
        ApplicationManagementTutorial tutorial = new ApplicationManagementTutorial();
        
        tutorial.parseOptions(args);
        
        try {
            // START SNIPPET: appName
            /**
             *  Note that the application name has to match the name in xml file.
             *  In this case, in cli_ext_tutorial_app.xml, its name 'cli_tutorial_app'.
             */
            
            if (!tutorial.connect(app_name)) {
                System.exit(1);
            }
            // END SNIPPET: appName
            
            /* Create ApplicationCLI instance */
            ApplicationCLI applicationCLI = new ApplicationCLI(
                    tutorial.getNetworkElement(),
                    version,
                    instance,
                    domain);
             
             // START SNIPPET: getConfig
             /*Retrieve config value for App config data 'app-category' */
            String configData = applicationCLI.getConfig("app_category_cmd");
            // END SNIPPET: getConfig
            if (configData != null) {
                tutorial.getLogger().info("Config data = " + configData);
            }            
            
            // START SNIPPET: registerListener
            /* Add execListener and configListener */
            applicationCLI.setExecListener(new ExampleExecListener(), null);
            applicationCLI.setConfigListener(new ExampleConfigListener(), null);
            // END SNIPPET: registerListener
            
            /**
             * Issue config command on Network Element to receive notification 
             * about application defined config data.
             */
            tutorial.getLogger().info(
                    "\n on NE, issue the command in : " + "conf t \n" + 
                    "onep applications domain1 \n" + 
                    "cli_tutorial_app v0.2 instance1 \n" + 
                    "app-category name routing \n"+ "end\n");
            
             /* Issue show command for application defined show data */
            tutorial.getLogger().info("\n on NE, issue command: " + 
                    "show onep application cli_tutorial_app v0.2 domain1 instance1 app-category");
            try {
                /*Wait for response*/
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        } catch (OnepRemoteProcedureException e) {
                        tutorial.disconnect();
                        tutorial.getLogger().error(
                                "Make sure the cli_ext_tutorial_app.xsd" +
                                " is installed properly on the router\n" +
                                "Run this command on the router: \n" +
                                "\"show onep cli-extensions applications\" " +
                                " and check if \"cli_tutorial_app\" is installed\n"
                                );
        }catch (Exception e) {
            tutorial.disconnect();
            tutorial.getLogger().error(e.getLocalizedMessage(), e);
        }
        tutorial.disconnect();
        System.exit(0);
    }
    
    /**
     * ExampleConfigListener implemented as inner class.
     */
    // START SNIPPET: ExampleConfigListener
    static class ExampleConfigListener implements ApplicationConfigCLIListener {
        @Override
        public void handleEvent(ApplicationCLIData cliData, Object clientData) {
            String cliDataName = cliData.getDataName();
            String cliDataValue = cliData.getDataValue();
            System.out.println("\tdataName = " + cliDataName + ", dataValue = " + cliDataValue
                    + ", clientData = " + clientData);
        }
    }
    // END SNIPPET: ExampleConfigListener
    
    /**
     * ExampleShowListener implemented as inner class.
     */
    // START SNIPPET: ExampleExecListener
     static class ExampleExecListener implements ApplicationExecCLIListener {
        @Override
        public String handleEvent(ApplicationCLIData cliData, Object clientData) {
            String cliDataName = cliData.getDataName();
            System.out.println("\tdataName = " + cliDataName + ", clientData = " + clientData);
            return "JavaClient data";
        }
    }
    // END SNIPPET: ExampleExecListener
     
    /**
     * @see com.cisco.onep.tutorials.BaseTutorial#parseOptions()
     */
    @Override
    public void parseOptions(String[] args) {
        super.parseOptions(args);

        if (args.length > 0) {
            for (int i = 0; i + 1 < args.length; i += 2) {
                if (args[i].equals("-n") ||
                    args[i].equals("--app_name")) {
                    app_name = args[i + 1];
                } else if (args[i].equals("-c") ||
                           args[i].equals("--domain")) {
                    domain = args[i + 1];
                } else if (args[i].equals("-i") ||
                           args[i].equals("--instance")) {
                    instance = args[i + 1];
                } else if (args[i].equals("-v") ||
                           args[i].equals("--version")) {
                    version = args[i + 1];
                }
            }
        } else {
            Properties properties = getProperties();
            if (properties == null) {
                InputStream inputStream = null;
                try {
                    inputStream = this.getClass().getClassLoader().getResourceAsStream("tutorial.properties");
                    if (inputStream != null) {
                        properties = new Properties();
                        properties.load(inputStream);
                        setProperties(properties);
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
                app_name = properties.getProperty("appName");
                domain = properties.getProperty("domain");
                instance = properties.getProperty("instance");
                version = properties.getProperty("version");
            }
        }

        if (app_name == null || domain == null
            || instance == null || version == null) {
            getLogger().info(getUsageString());
            System.exit(1);
        }
    }

    /**
     * @see com.cisco.onep.tutorials.BaseTutorial#getUsageRequiredOptions()
     */
    @Override
    public String getUsageRequiredOptions() {
        return super.getUsageRequiredOptions()
            + " -n <app name>"
            + " -c <config domain>"
            + " -i <instance>"
            + " -v <version>";
    }

}
