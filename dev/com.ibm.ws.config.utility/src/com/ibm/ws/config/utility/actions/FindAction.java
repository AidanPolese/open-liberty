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
import java.util.Collections;
import java.util.List;

import com.ibm.ws.config.utility.ConfigUtilityAction;
import com.ibm.ws.config.utility.TaskErrorException;
import com.ibm.ws.config.utility.utils.CommandUtils;
import com.ibm.ws.config.utility.utils.ConsoleWrapper;
import com.ibm.ws.config.utility.utils.RepositoryAccessUtility;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.repository.exceptions.RepositoryException;

/**
 * This class is intended to find and list the configuration snippets found in the Repository.
 */
public class FindAction implements ConfigUtilityAction {

    static final String NL = System.getProperty("line.separator");

    @Override
    public void handleAction(ConsoleWrapper stdin, PrintStream stdout, PrintStream stderr, String[] args) throws TaskErrorException, RepositoryException, InstallException {
        List<String> configSnippetList;
        if (args.length == 1) {
            stdout.println(CommandUtils.getMessage("getListOfAllSnippets"));
            configSnippetList = RepositoryAccessUtility.getConfigSnippetList();
            Collections.sort(configSnippetList);
            for (String snippet : configSnippetList) {
                stdout.println(NL + snippet);
            }
        }
        else if (args.length >= 2) {
            String find_snippet = args[1];
            stdout.println(CommandUtils.getMessage("findSnippet", find_snippet));
            configSnippetList = RepositoryAccessUtility.getConfigSnippetList();
            boolean found = false;
            if (find_snippet != null) {
                for (String snippet : configSnippetList) {
                    String desc = RepositoryAccessUtility.getConfigSnippetDescription(snippet);
                    if (snippet.toLowerCase().contains(find_snippet.toLowerCase()) ||
                        desc.toLowerCase().contains(find_snippet.toLowerCase())) {
                        stdout.println(NL + snippet);
                        found = true;
                    }
                }
            }
            if (!found) {
                stdout.println(CommandUtils.getMessage("snippetNotFound", find_snippet));
            }
        }
    }

    @Override
    public String getActionName() {
        return "find";
    }

}
