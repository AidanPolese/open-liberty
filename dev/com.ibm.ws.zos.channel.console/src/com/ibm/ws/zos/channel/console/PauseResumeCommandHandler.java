/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.zos.channel.console;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.kernel.launch.service.PauseableComponentController;
import com.ibm.ws.zos.channel.console.internal.PauseResumeConsoleSupport;
import com.ibm.wsspi.kernel.service.location.WsLocationAdmin;
import com.ibm.wsspi.zos.command.processing.CommandHandler;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 * An implementation of an MVS console command handler that allows a
 * system operator to pause or resume the server.
 */
@Component(name = "com.ibm.ws.zos.channel.console.PauseResumeCommandHandler", service = { CommandHandler.class }, immediate = true, configurationPolicy = ConfigurationPolicy.IGNORE, property = {
                                                                                                                                                                                                   "modify.filter.regex=((?i)(pause|resume|status).*)",
                                                                                                                                                                                                   "display.command.help=TRUE",
                                                                                                                                                                                                   "service.vendor=IBM" })
public class PauseResumeCommandHandler implements CommandHandler {
    /**
     * trace variable
     */
    private static final TraceComponent tc = Tr.register(PauseResumeCommandHandler.class);

    /**
     * A reference to the PauseableComponentController service. It handles delivering the pause/resume requests to
     * interested server components.
     */
    private PauseableComponentController m_pauseableComponentController;

    /**
     * A reference to obtain the server's name.
     */
    private WsLocationAdmin m_locationAdmin;

    /**
     * A human readable name for this handler.
     */
    final static String NAME = "Pause/Resume Command Handler";

    /**
     * Help text.
     */
    final static List<String> HELP_TEXT = buildHelpText();

    private static final String PAUSE_COMMAND = "pause";
    private static final String PAUSE_COMMAND_WITH_TARGET = "pause,target=";
    private static final int PAUSE_COMMAND_WITH_TARGET_LENGTH = PAUSE_COMMAND_WITH_TARGET.length();

    private static final String RESUME_COMMAND = "resume";
    private static final String RESUME_COMMAND_WITH_TARGET = "resume,target=";
    private static final int RESUME_COMMAND_WITH_TARGET_LENGTH = RESUME_COMMAND_WITH_TARGET.length();

    private static final String STATUS_COMMAND = "status";
    private static final String STATUS_COMMAND_WITH_TARGET = "status,target=";
    private static final int STATUS_COMMAND_WITH_TARGET_LENGTH = STATUS_COMMAND_WITH_TARGET.length();
    private static final String STATUS_COMMAND_WITH_DETAILS = "status,details";
    private static final int STATUS_COMMAND_WITH_DETAILS_LENGTH = STATUS_COMMAND_WITH_DETAILS.length();

    /**
     * DS method to activate this component.
     */
    protected void activate() {}

    /**
     * DS method to deactivate this component.
     */
    protected void deactivate() {}

    /**
     * Set the PauseableComponentController reference.
     *
     * @param m_pauseableComponentController the PauseableComponentController to set
     *
     *            Note: We want to this CommandHandler to be activated even if the PauseableComponentController is not
     *            around to perform all the functions. The reason is that we want to be able to provide the z/OS Command
     *            help. If we are not registered as a CommandHandler then a "f <server>,help" will not be able to obtain
     *            out help information (getHelp()).
     */
    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
    protected void setPauseableComponentController(PauseableComponentController pauseableComponentController) {
        this.m_pauseableComponentController = pauseableComponentController;

        PauseResumeConsoleSupport.INSTANCE.setPauseableComponentController(this.m_pauseableComponentController);
    }

    /**
     * Unset the PauseableComponentController reference.
     *
     * @param PauseableComponentController the PauseableComponentController to unset
     */
    protected void unsetPauseableComponentController(PauseableComponentController pauseableComponentController) {
        if (this.m_pauseableComponentController == pauseableComponentController) {
            this.m_pauseableComponentController = null;

            PauseResumeConsoleSupport.INSTANCE.setPauseableComponentController(this.m_pauseableComponentController);
        }
    }

    /**
     * Sets the WsLocationAdmin reference.
     *
     * Note: We want to this CommandHandler to be activated even if the PauseableComponentController is not
     * around to perform all the functions. The reason is that we want to be able to provide the z/OS Command
     * help. If we are not registered as a CommandHandler then a "f <server>,help" will not be able to obtain
     * out help information (getHelp()).
     */
    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
    protected void setLocationAdmin(WsLocationAdmin locationAdmin) {
        this.m_locationAdmin = locationAdmin;
    }

    /**
     * Clears the WsLocationAdmin reference.
     */
    protected void unsetLocationAdmin(WsLocationAdmin locationAdmin) {
        this.m_locationAdmin = null;
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
     */
    @Override
    public void handleModify(String command, ModifyResults results) {
        List<String> responses = new ArrayList<String>();

        if (command.toLowerCase().startsWith("pause")) {
            pause(command, responses, results);
        } else if (command.toLowerCase().startsWith("resume")) {
            resume(command, responses, results);
        } else if (command.toLowerCase().startsWith("status")) {
            serverStatus(command, responses, results);
        } else {
            results.setCompletionStatus(ModifyResults.UNKNOWN_COMMAND);
        }

        results.setResponsesContainMSGIDs(false);
        results.setResponses(responses);
    }

    /**
     * Process "f <server>,pause[,target=<PauseableComponent name>]". Deliver the pause request to all registered
     * PauseableComponents.
     *
     * @param command Command string.
     * @param responses Command response strings.
     * @param results processing results (ie. sort of success, error, ...)
     */
    private void pause(String command, List<String> responses, ModifyResults results) {
        int result = ModifyResults.PROCESSED_COMMAND;

        String targetIds = null;
        if (!!!command.equalsIgnoreCase(PAUSE_COMMAND)) {
            if (command.toLowerCase().startsWith(PAUSE_COMMAND_WITH_TARGET)) {
                targetIds = getTargetIds(command.substring(PAUSE_COMMAND_WITH_TARGET_LENGTH));

                if (targetIds == null || targetIds.isEmpty()) {
                    // Error: invalid target= syntax.  f bbgzsrv,pause,target=''
                    responses.add("Could not parse target= parameter: \"" + command + "\"");
                    results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
                    return;
                }
            } else {
                // Invalid pause syntax like:  f bbgzsrv,pauseit or ...pause,  or ...pause,target
                responses.add("Could not parse command: \"" + command + "\"");
                results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
                return;
            }
        }

        if (PauseResumeConsoleSupport.INSTANCE.pause(targetIds) > 0) {
            result = ModifyResults.ERROR_PROCESSING_COMMAND;
        }

        results.setCompletionStatus(result);
    }

    /**
     * Process "f <server>,resume[,target=<PauseableComponent name>]". Deliver the resume request to all registered
     * PauseableComponents.
     *
     * @param command Command string.
     * @param responses Command response strings.
     * @param results processing results (ie. sort of success, error, ...)
     */
    private void resume(String command, List<String> responses, ModifyResults results) {
        int result = ModifyResults.PROCESSED_COMMAND;

        String targetIds = null;
        if (!!!command.equalsIgnoreCase(RESUME_COMMAND)) {
            if (command.toLowerCase().startsWith(RESUME_COMMAND_WITH_TARGET)) {
                targetIds = getTargetIds(command.substring(RESUME_COMMAND_WITH_TARGET_LENGTH));

                if (targetIds == null || targetIds.isEmpty()) {
                    // Error: invalid target= syntax.  f bbgzsrv,resume,target=''
                    responses.add("Could not parse target= parameter: \"" + command + "\"");
                    results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
                    return;
                }
            } else {
                // invalid resume syntax:  f bbgzsrv,resumeit or ...resume,  or ...resume,target
                responses.add("Could not parse command: \"" + command + "\"");
                results.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
                return;
            }
        }

        if (PauseResumeConsoleSupport.INSTANCE.resume(targetIds) > 0) {
            result = ModifyResults.ERROR_PROCESSING_COMMAND;
        }

        results.setCompletionStatus(result);
    }

    /**
     * Process "f <server>,status[,target='id,...'|details]" command. Deliver the display information on all registered
     * PauseableComponents.
     *
     * @param command Command string.
     * @param responses Command response strings.
     * @param results processing results (ie. sort of success, error, ...)
     */
    private void serverStatus(String command, List<String> responses, ModifyResults results) {
        int result = ModifyResults.PROCESSED_COMMAND;

        String targetIds = null;
        if (!!!command.equalsIgnoreCase(STATUS_COMMAND)) {
            if (command.toLowerCase().startsWith(STATUS_COMMAND_WITH_TARGET)) {
                targetIds = getTargetIds(command.substring(STATUS_COMMAND_WITH_TARGET_LENGTH));

                if (targetIds == null || targetIds.isEmpty()) {
                    // Error: invalid target= syntax.  f bbgzsrv,status,target=''
                    responses.add("Could not parse target= parameter: \"" + command + "\"");
                    result = ModifyResults.ERROR_PROCESSING_COMMAND;
                } else {
                    if (PauseResumeConsoleSupport.INSTANCE.statusTarget(responses, targetIds) > 0) {
                        result = ModifyResults.ERROR_PROCESSING_COMMAND;
                    }
                }
            } else if (command.toLowerCase().startsWith(STATUS_COMMAND_WITH_DETAILS)) {
                // "f <server>,status,details" -- displays information on all currently registered PauseableComponents.
                if (PauseResumeConsoleSupport.INSTANCE.details(responses) > 0) {
                    result = ModifyResults.ERROR_PROCESSING_COMMAND;
                }
            } else {
                // invalid resume syntax:  f bbgzsrv,statusit or ...status,  or ...status,target
                responses.add("Could not parse command options: \"" + command + "\"");
                result = ModifyResults.ERROR_PROCESSING_COMMAND;
            }
        } else {
            // "f <server>,status" -- displays information on all currently registered PauseableComponents.
            int pauseRC = PauseResumeConsoleSupport.INSTANCE.status();

            String serverName = (this.m_locationAdmin != null) ? this.m_locationAdmin.getServerName() : "unknown";

            if (pauseRC == 0) {
                responses.add("server " + serverName + " is paused.");
            } else if (pauseRC == 4) {
                responses.add("server " + serverName + " is active.");
            } else {
                result = ModifyResults.ERROR_PROCESSING_COMMAND;
            }
        }

        results.setCompletionStatus(result);
    }

    private static List<String> buildHelpText() {
        List<String> responses = new ArrayList<String>();

        responses.add("Issue \"MODIFY <jobname.>identifier,pause<,target='target1,...'>\"");
        responses.add("  to pause server components accepting work");
        responses.add("Issue \"MODIFY <jobname.>identifier,resume<,target='target1,...'>\"");
        responses.add("  to resume server components to accept work");
        responses.add("Issue \"MODIFY <jobname.>identifier,status<,target='target1,...'>\"");
        responses.add("  to display the server status or targeted status");
        responses.add("Issue \"MODIFY <jobname.>identifier,status,details\"");
        responses.add("  to display information on the pause capable components");
        return responses;
    }

    /**
     * Parses the "target=" parameter.
     *
     * @param targetIds the "target=" parameter string
     * @return String of target ids for the targeted request, or null if the command
     *         string contains a syntax error.
     */
    private String getTargetIds(String targetIds) {

        if (targetIds == null) {
            return null;
        }

        // Remove leading and/or trailing single quote.
        String ids = targetIds.replaceAll("^\'|\'$", "");

        return ids;
    }
}
