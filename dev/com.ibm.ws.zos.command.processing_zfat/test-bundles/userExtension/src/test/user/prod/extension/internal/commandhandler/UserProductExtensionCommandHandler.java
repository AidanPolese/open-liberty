/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package test.user.prod.extension.internal.commandhandler;

import java.util.ArrayList;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.wsspi.zos.command.processing.CommandHandler;
import com.ibm.wsspi.zos.command.processing.ModifyResults;

/**
 * User extension command handler
 */
@Component(configurationPid = "user.prod.extension.commandHandler",
                configurationPolicy = ConfigurationPolicy.IGNORE,
                property = { "modify.filter.regex=((?i)(test.command).*)",
		                     "display.command.help=TRUE",
		                     "service.vendor=IBM" })
public class UserProductExtensionCommandHandler implements CommandHandler {

    protected String filterExpresson = null;
    protected boolean getHelp = true;

    private ArrayList<String> helpText = null;

    private final String name = "CommandHandlerTest";

    /**
     * This is the declarative services activate method. This gets driven
     * when the runtime decides it actually needs our service (as opposed to
     * when the bundle is activated). The metatype will be provided to us
     * in the config parameter.
     * 
     * @param cc The OSGi component context.
     * @param config The configuration (metatype)
     */
    protected void activate(ComponentContext cc, java.util.Map<String, Object> config) {
        filterExpresson = (String) config.get(MODIFY_FILTER);
        getHelp = Boolean.parseBoolean((String) config.get(DISPLAY_HELP));
    }

    /**
     * This is the declarative services deactivate method.
     * 
     * @param cc
     */
    protected void deactivate(ComponentContext cc) {
        filterExpresson = null;
        getHelp = false;
    }

    /**
     * This is the declarative services modified method. When the
     * configuration changes dynamically, this method is driven to
     * give us the updated configuration.
     * 
     * @param config The configuration (metatype).
     */
    protected void modified(java.util.Map<String, Object> config) {
        filterExpresson = (String) config.get(MODIFY_FILTER);
        getHelp = Boolean.parseBoolean((String) config.get(DISPLAY_HELP));
    }

    @Override
    public ArrayList<String> getHelp() {

        if (helpText == null) {
            helpText = new ArrayList<String>();
            helpText.add("Test Command Handler has no help");
        }
        return helpText;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void handleModify(String handle, ModifyResults mod) {
        if (handle.equals("test.command")) {
            ArrayList<String> response = new ArrayList<String>();
            response.add("UserProductExtensionCommandHandler_handleModify_respondingToModifyCommand: " + handle);
            mod.setResponses(response);
            mod.setCompletionStatus(ModifyResults.PROCESSED_COMMAND);
            mod.setResponsesContainMSGIDs(false);
        } else {
            ArrayList<String> response = new ArrayList<String>();
            response.add("Test Failed command " + handle + " not valid");
            mod.setResponses(response);
            mod.setCompletionStatus(ModifyResults.ERROR_PROCESSING_COMMAND);
            mod.setResponsesContainMSGIDs(false);
        }

    }
}
