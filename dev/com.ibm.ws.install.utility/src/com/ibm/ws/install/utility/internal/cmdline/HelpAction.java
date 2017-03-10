/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.utility.internal.cmdline;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ibm.ws.kernel.boot.cmdline.ActionHandler;
import com.ibm.ws.kernel.boot.cmdline.Arguments;
import com.ibm.ws.kernel.feature.internal.cmdline.ReturnCode;

public class HelpAction implements ActionHandler {
    public static final String NL = System.getProperty("line.separator");
    public static final String COMMAND = "installUtility";
    public static final ResourceBundle featureToolOptions = com.ibm.ws.install.internal.cmdline.ExeHelpAction.getOptions();
    public static final ResourceBundle installUtilityToolOptions = ResourceBundle.getBundle("com.ibm.ws.install.utility.internal.resources.InstallUtilityToolOptions");
    private static final ResourceBundle licenseOptions = ResourceBundle.getBundle("wlp.lib.extract.SelfExtractMessages");
    private static final Map<String, String> LICENSE_KEY_MAPPING = new HashMap<String, String>();

    static {
        LICENSE_KEY_MAPPING.put("deploy.option-desc.--acceptLicense", "helpAcceptLicense");
        LICENSE_KEY_MAPPING.put("deploy.option-desc.--viewLicenseAgreement", "helpAgreement");
        LICENSE_KEY_MAPPING.put("deploy.option-desc.--viewLicenseInfo", "helpInformation");
        LICENSE_KEY_MAPPING.put("download.option-desc.--acceptLicense", "helpAcceptLicense");
        LICENSE_KEY_MAPPING.put("download.option-desc.--viewLicenseAgreement", "helpAgreement");
        LICENSE_KEY_MAPPING.put("download.option-desc.--viewLicenseInfo", "helpInformation");
        LICENSE_KEY_MAPPING.put("install.option-desc.--acceptLicense", "helpAcceptLicense");
        LICENSE_KEY_MAPPING.put("install.option-desc.--viewLicenseAgreement", "helpAgreement");
        LICENSE_KEY_MAPPING.put("install.option-desc.--viewLicenseInfo", "helpInformation");
    }

    /**
     * {@inheritDoc} Prints the usage statement. The format is:
     * <pre>
     * Usage: {tasks|...} [arguments]
     * </pre>
     * 
     */
    public String getScriptUsage() {
        StringBuffer scriptUsage = new StringBuffer(NL);
        scriptUsage.append(getHelpPart("usage", COMMAND));
        scriptUsage.append(" {");
        Action[] tasks = Action.values();
        for (int i = 0; i < tasks.length; i++) {
            Action task = tasks[i];
            scriptUsage.append(task.toString());
            if (i != (tasks.length - 1)) {
                scriptUsage.append("|");
            }
        }
        scriptUsage.append("} [");
        scriptUsage.append(getHelpPart("global.options.lower"));
        scriptUsage.append("]");
        scriptUsage.append(NL);
        return scriptUsage.toString();
    }

    /**
     * Constructs a string to represent the usage for a particular task.
     * 
     * @param task
     * @return
     */
    public String getTaskUsage(Action task) {
        StringBuilder taskUsage = new StringBuilder(NL);

        // print a empty line
        taskUsage.append(getHelpPart("global.usage"));
        taskUsage.append(NL);
        taskUsage.append('\t');
        taskUsage.append(COMMAND);
        taskUsage.append(' ');
        taskUsage.append(task);
        if (task.showOptions()) {
            taskUsage.append(" [");
            taskUsage.append(getHelpPart("global.options.lower"));
            taskUsage.append("]");
        }
        List<String> options = task.getCommandOptions();
        for (String option : options) {
            if (option.charAt(0) != '-') {
                taskUsage.append(' ');
                taskUsage.append(option);
            }
        }

        taskUsage.append(NL);
        taskUsage.append(NL);
        taskUsage.append(getHelpPart("global.description"));
        taskUsage.append(NL);
        taskUsage.append(getDescription(task));
        taskUsage.append(NL);
        if (options.size() > 0) {
            taskUsage.append(NL);
            taskUsage.append(getHelpPart("global.options"));

            for (String option : task.getCommandOptions()) {
                taskUsage.append(NL);
                String optionKey;
                try {
                    optionKey = getHelpPart(task + ".option-key." + option);
                } catch (MissingResourceException e) {
                    // This happens because we don't have a message for the key for the 
                    // license arguments but these are not translated and don't need any
                    // arguments after them so can just use the option
                    optionKey = "    " + option;
                }
                taskUsage.append(optionKey);
                taskUsage.append(NL);
                taskUsage.append(getHelpPart(task + ".option-desc." + option));
                taskUsage.append(NL);
            }
        }
        taskUsage.append(NL);
        return taskUsage.toString();
    }

    /**
     * Constructs a string to represent the verbose help, which lists the
     * usage for all tasks.
     * 
     * @return String for the verbose help message
     */
    private StringBuilder verboseHelp() {
        StringBuilder verboseHelp = new StringBuilder(getScriptUsage());
        verboseHelp.append(NL);
        verboseHelp.append(getHelpPart("global.actions"));
        verboseHelp.append(NL);
        for (Action action : Action.values()) {
            verboseHelp.append(NL);
            verboseHelp.append("    ");
            verboseHelp.append(action.toString());
            verboseHelp.append(NL);
            verboseHelp.append(getDescription(action));
            verboseHelp.append(NL);
        }
        verboseHelp.append(NL);
        verboseHelp.append(getHelpPart("global.options"));
        verboseHelp.append(NL);
        verboseHelp.append(getHelpPart("global.options.statement"));
        verboseHelp.append(NL);
        verboseHelp.append(NL);
        return verboseHelp;
    }

    /**
     * @param action
     * @return
     */
    private String getDescription(Action action) {
        return getHelpPart(action + ".desc");
    }

    /** {@inheritDoc} */
    @Override
    public ReturnCode handleTask(PrintStream stdout, PrintStream stderr, Arguments args) {
        String actionName = args.getAction();
        if (actionName == null) {
            stdout.println(getScriptUsage());
        } else if (args.getPositionalArguments().isEmpty()) {
            stdout.println(verboseHelp());
        } else {
            try {
                Action task = Action.valueOf(args.getPositionalArguments().get(0));
                stdout.println(getTaskUsage(task));
            } catch (IllegalArgumentException e) {
                stderr.println();
                stderr.println(getHelpPart("task.unknown", args.getPositionalArguments().get(0)));
                stdout.println(getScriptUsage());
            }
        }

        return ReturnCode.OK;
    }

    public static String getHelpPart(String key, Object... args) {
        // We get the license information from the self extractor and want everything
        // to be identical, to ensure this for the help we also use the messages from
        // there but this means we need to special case those keys.
        String option;
        if (LICENSE_KEY_MAPPING.containsKey(key)) {
            key = LICENSE_KEY_MAPPING.get(key);
            option = MessageFormat.format(licenseOptions.getString(key), new Object[0]);
            if (!option.isEmpty() && !Character.isWhitespace(option.charAt(0))) {
                // The translated string isn't indented, so prefix with a tab,
                // and ensure all line separators have a tab.
                option = "\t" + option.replaceAll("[\r\n]+", "$0\t");
            }
        } else {
            option = installUtilityToolOptions.getString(key);
        }
        return args.length == 0 ? option : MessageFormat.format(option, args);
    }
}