/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.logging.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import com.ibm.wsspi.zos.command.processing.CommandHandler;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 * An implementation of an MVS console command handler that allows a
 * system operator to change the trace and logging configuration.
 */
@Component(name = "com.ibm.ws.zos.command.processing.logging.LoggingCommandHandler",
           configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { "modify.filter.regex=((?i)(logging).*)",
                       "service.vendor=IBM" })
public class LoggingCommandHandler implements CommandHandler {

    /**
     * A human readable name for this handler.
     */
    final static String NAME = "Logging Command Handler";

    /**
     * Help text.
     */
    final static List<String> HELP_TEXT = buildHelpText();

    /**
     * The persistent identifier for the logging configuration object.
     */
    final static String LOGGING_PID = "com.ibm.ws.logging";

    /**
     * The configuration key for the trace specification.
     */
    final static String TRACE_SPEC_KEY = "traceSpecification";

    /**
     * The injected reference to the OSGi configuration admin service.
     */
    protected ConfigurationAdmin configAdmin;

    /**
     * The initial configured trace specification.
     */
    protected String configuredTraceSpec = null;

    /**
     * DS method to inject the ConfigurationAdmin and capture the initial trace specification
     */
    @Reference
    protected void setConfigAdmin(ConfigurationAdmin configAdmin) {
        this.configAdmin = configAdmin;
        this.setConfiguredTraceSpec();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getHelp() {
        return HELP_TEXT;
    }

    /**
     * Get the name of this console command handler.
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public void handleModify(String command, ModifyResults results) {
        List<String> responses = null;

        String[] loggingCmd = command.split("=", 2);
        if (loggingCmd.length > 1 && loggingCmd[1] != null) {
            try {
                //Assumption is the command is entered with mixed case enclosed in single quotes
                String traceSpec = loggingCmd[1];
                traceSpec = this.trimQuotes(traceSpec);

                if (traceSpec.isEmpty()) {
                    results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
                } else {
                    Configuration config = configAdmin.getConfiguration(LOGGING_PID, null);
                    Dictionary<String, Object> props = config.getProperties();
                    if (traceSpec.equalsIgnoreCase("reset")) {
                        if (configuredTraceSpec != null) {
                            props.put(TRACE_SPEC_KEY, configuredTraceSpec);
                            config.update(props);
                        } else {
                            //The configuredTraceSpec should never be null. If a traceSpecification
                            //was not explicitly set the runtime sets a default value of '*=info=enabled'.
                            //This is just to catch the case where something unexpected has occurred.
                            responses = new ArrayList<String>();
                            responses.add("Unexpected null found for configuredTraceSpec");
                            results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
                            results.setResponsesContainMSGIDs(false);
                            results.setResponses(responses);
                            return;
                        }
                    } else {
                        props.put(TRACE_SPEC_KEY, traceSpec);
                        config.update(props);
                    }
                    results.setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
                }
            } catch (IOException ioe) {
                results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
                ioe = null; // Avoid FindBugs DLS
            }
        } else {
            results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
        }

        results.setResponsesContainMSGIDs(false);
        results.setResponses(responses);

    }

    /**
     * Strip off leading and trailing single quotes, if string is null or any quotes are missing then
     * return an empty string.
     * 
     * @param aString
     * @return aString
     */
    String trimQuotes(String aString) {
        if (aString == null) {
            return "";
        }

        if (!aString.startsWith("\'") && !aString.endsWith("\'")) {
            return aString;
        }

        if (aString.startsWith("\'")) {
            aString = aString.substring(1, aString.length());
        } else {
            return "";
        }

        if (aString.endsWith("\'")) {
            aString = aString.substring(0, aString.length() - 1);
        } else {
            return "";
        }

        return aString;
    }

    private static List<String> buildHelpText() {
        List<String> responses = new ArrayList<String>();

        responses.add("Issue \"MODIFY <jobname.>identifier,logging=\'tracespec\'\"");
        responses.add("  to change the trace specification of the server");
        responses.add("  The tracespec value is a valid trace specification or the value reset");
        responses.add("  The tracespec is case sensitive and must be enclosed in single quotes");
        responses.add("  Specifying the value reset will return the server to the settings specified in the configuration");

        return responses;
    }

    /**
     * Capture the configured trace specification
     */
    protected void setConfiguredTraceSpec() {

        if (configuredTraceSpec != null)
            return;

        try {
            Configuration config = configAdmin.getConfiguration(LOGGING_PID, null);
            Dictionary<String, Object> props = config.getProperties();
            configuredTraceSpec = (String) props.get(TRACE_SPEC_KEY);
        } catch (IOException e) {
            configuredTraceSpec = null;
            e = null; //satisfy findbugs
        }
    }

}
