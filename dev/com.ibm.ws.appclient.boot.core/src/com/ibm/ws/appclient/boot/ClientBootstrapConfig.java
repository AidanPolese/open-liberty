/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.appclient.boot;

import java.net.URL;
import java.util.List;

import com.ibm.ws.kernel.boot.BootstrapConfig;
import com.ibm.ws.kernel.boot.ReturnCode;
import com.ibm.ws.kernel.boot.internal.BootstrapConstants;
import com.ibm.ws.kernel.boot.internal.KernelUtils;

/**
 *
 */
public class ClientBootstrapConfig extends BootstrapConfig {

    /**
     * Return the root directory name of the processes.
     * 
     * @return BootstrapConstants.LOC_AREA_NAME_CLIENTS
     */
    @Override
    protected String getProcessesSubdirectory() {
        return BootstrapConstants.LOC_AREA_NAME_CLIENTS;
    }

    /**
     * Return the output directory name value set in the environment variable WLP_CLIENT_OUTPUT_DIR.
     * 
     * @return BootstrapConstants.ENV_WLP_CLIENT_OUTPUT_DIR
     */
    @Override
    protected String getOutputDirectoryEnvName() {
        return BootstrapConstants.ENV_WLP_CLIENT_OUTPUT_DIR;
    }

    @Override
    protected String getDefaultProcessName() {
        return BootstrapConstants.DEFAULT_CLIENT_NAME;
    }

    @Override
    protected String getProcessXMLFilename() {
        return BootstrapConstants.CLIENT_XML;
    }

    @Override
    protected String getProcessXMLResourcePath() {
        return "/OSGI-OPT/websphere/client/client.xml";
    }

    @Override
    protected String getErrorCreatingNewProcessMessageKey() {
        return "error.creatingNewClient";
    }

    @Override
    protected String getErrorCreatingNewProcessMkDirFailMessageKey() {
        return "error.creatingNewClientMkDirFail";
    }

    @Override
    protected String getErrorCreatingNewProcessExistsMessageKey() {
        return "error.creatingNewClientExists";
    }

    @Override
    protected String getErrorNoExistingProcessMessageKey() {
        return "error.noExistingClient";
    }

    @Override
    protected String getErrorProcessDirExistsMessageKey() {
        return "error.clientDirExists";
    }

    @Override
    protected String getErrorProcessNameCharacterMessageKey() {
        return "error.clientNameCharacter";
    }

    @Override
    protected String getInfoNewProcessCreatedMessageKey() {
        return "info.newClientCreated";
    }

    @Override
    protected String getProcessesTemplateDir() {
        return "templates/clients/";
    }

    @Override
    public String getProcessType() {
        return BootstrapConstants.LOC_PROCESS_TYPE_CLIENT;
    }

    @Override
    public void addBootstrapJarURLs(List<URL> urlList) {
        urlList.add(KernelUtils.getLocationFromClass(ClientBootstrapConfig.class));
        super.addBootstrapJarURLs(urlList);
    }

    /**
     * Disabling the PermGen is only necessary in server processes. There is not need to
     * generate this file in client processes.
     */
    @Override
    protected ReturnCode disablePermGenIfNecessary() {
        return ReturnCode.OK;
    }
}
