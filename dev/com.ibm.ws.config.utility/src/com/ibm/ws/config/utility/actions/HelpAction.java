/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility.actions;

import java.io.PrintStream;

import com.ibm.ws.config.utility.ConfigUtility;
import com.ibm.ws.config.utility.ConfigUtilityAction;
import com.ibm.ws.config.utility.TaskErrorException;
import com.ibm.ws.config.utility.utils.CommandUtils;
import com.ibm.ws.config.utility.utils.ConsoleWrapper;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.repository.exceptions.RepositoryException;

/**
 * This class gets the script usage and help options for the configUtility.
 */
public class HelpAction implements ConfigUtilityAction {

    static final String NL = System.getProperty("line.separator");

    public static String getScriptUsage() {
        StringBuffer scriptUsage = new StringBuffer(NL);
        scriptUsage.append(CommandUtils.getMessage("usage", ConfigUtility.SCRIPT_NAME));
        scriptUsage.append(NL);
        return scriptUsage.toString();
    }

    public static String getHelpOptions() {
        StringBuffer helpOptions = new StringBuffer(NL);
        helpOptions.append(getScriptUsage());
        helpOptions.append(CommandUtils.getOption("config.options"));
        return helpOptions.toString();
    }

    @Override
    public String getActionName() {
        return "help";
    }

    /** {@inheritDoc} */
    @Override
    public void handleAction(ConsoleWrapper stdin, PrintStream stdout, PrintStream stderr, String[] args) throws TaskErrorException, RepositoryException, InstallException {
        stdout.println(HelpAction.getHelpOptions());
    }
}
