/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.ibm.ws.config.utility.actions.FindAction;
import com.ibm.ws.config.utility.actions.HelpAction;
import com.ibm.ws.config.utility.actions.InstallAction;
import com.ibm.ws.config.utility.utils.CommandUtils;
import com.ibm.ws.config.utility.utils.ConsoleWrapper;
import com.ibm.ws.config.utility.utils.FileUtility;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.repository.exceptions.RepositoryException;

/**
 *
 */
public class ConfigUtility {

    public static final String SCRIPT_NAME = "configUtility";
    static final String NL = System.getProperty("line.separator");
    private final ConsoleWrapper stdin;
    private final PrintStream stdout;
    private final PrintStream stderr;
    List<ConfigUtilityAction> actions = new ArrayList<ConfigUtilityAction>();

    public ConfigUtility(ConsoleWrapper stdin, PrintStream stdout, PrintStream stderr) {
        this.stdin = stdin;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    /**
     * Register an action into the CollectiveUtility script.
     * 
     * @param task
     */
    void registerAction(ConfigUtilityAction action) {
        actions.add(action);
    }

    /**
     * Given a task name, return the corresponding CollectiveUtilityTask.
     * 
     * @param taskName desired task name
     * @return corresponding CollectiveUtilityTask, or null if
     *         no match is found
     */
    private ConfigUtilityAction getAction(String actionName) {
        ConfigUtilityAction action = null;
        for (ConfigUtilityAction availAction : actions) {
            if (availAction.getActionName().equals(actionName)) {
                action = availAction;
                break;
            }
        }
        return action;
    }

    /**
     * Drive the logic of the program.
     * 
     * @param args
     */
    int runProgram(String[] args) {
        if (stdin == null) {
            stderr.println(CommandUtils.getMessage("error.missingIO", "stdin"));
            return 254;
        }
        if (stdout == null) {
            stderr.println(CommandUtils.getMessage("error.missingIO", "stdout"));
            return 253;
        }
        if (stderr == null) {
            stdout.println(CommandUtils.getMessage("error.missingIO", "stderr"));
            return 252;
        }

        if (args.length == 0) {
            stdout.println(HelpAction.getScriptUsage());
            return 0;
        }

        ConfigUtilityAction action = getAction(args[0]);

        if (action == null) {
            if (args[0].equalsIgnoreCase("--help")) {
                stdout.println(HelpAction.getHelpOptions());
                return 0;
            }
            stderr.println(CommandUtils.getMessage("task.unknown", args[0]));
            stderr.println(HelpAction.getScriptUsage());
            return 0;
        }
        else {
            try {
                action.handleAction(stdin, stdout, stderr, args);
            } catch (IllegalArgumentException e) {
                stderr.println();
                stderr.println(CommandUtils.getMessage("error", e.getMessage()));
                return 20;
            } catch (TaskErrorException e) {
                if (e.getMessage() != null) {
                    stderr.println();
                    stderr.println(CommandUtils.getMessage("error", e.getMessage()));
                }
                return 255;
            } catch (RepositoryException e) {
                if (e.getMessage() != null) {
                    stderr.println();
                    stderr.println(CommandUtils.getMessage("error", e.getMessage()));
                }
                return 251;
            } catch (InstallException e) {
                if (e.getMessage() != null) {
                    stderr.println();
                    stderr.println(e.getLocalizedMessage());
                }
                return 250;
            }

            return 0;
        }
    }

    /**
     * Main method, which wraps the instance logic.
     * 
     * @param args
     */
    public static void main(String[] args) {
        ConsoleWrapper console = new ConsoleWrapper(System.console(), System.err);
        IFileUtility fileUtil = new FileUtility(System.getenv("WLP_INSTALL_DIR"), System.getenv("WLP_USER_DIR"));
        ConfigUtility utility = new ConfigUtility(console, System.out, System.err);

        //Register Actions with configUtility
        utility.registerAction(new FindAction());
        utility.registerAction(new HelpAction());
        utility.registerAction(new InstallAction(SCRIPT_NAME, fileUtil));

        //Kick off Utility
        int rc = utility.runProgram(args);
        System.exit(rc);
    }
}
