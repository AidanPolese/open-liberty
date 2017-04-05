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
package com.ibm.wsspi.stackManager;

import java.util.Map;

/**
 * Callback interface for StackManager deployments.
 */
public interface DeployStackCallBack {

    public final static int FAIL_BAD_STACK_PARAMETER = 1;
    public final static int FAIL_COULD_NOT_OPEN_ARCHIVE_FILE = 2;
    public final static int FAIL_IO_ERROR_ON_TRANSFER = 3;
    public final static int FAIL_OTHER_ERROR_ON_TRANSFER = 4;

    public final static String FAIL_MAP_FAILURE_CODE = "failureCode";
    public final static String FAIL_MAP_STACK_NAME = "stackName";
    public final static String FAIL_MAP_MESSAGE = "failureMessage";

    public final static String COLLECTIVE_DOCKER_CONTAINER_TYPE = "docker";

    /**
     * Notification of a successful deployment.
     * 
     * @param stackName
     * @param hostName that received the deployment
     * @param wlpUsrDir on the deployed hostname
     * @param serverName on the deployed hostname
     */
    public void success(String stackName, String hostName, String wlpUsrDir, String serverName);

    /**
     * Notification of a failed deployment.
     * 
     * @param stackName that failed
     * @param hostName receiving the deployment that failed
     * @param failInfoMap failure details including failureCode (as defined by the FAIL_* constants in this class), stackName, and failureMessage entries
     * @param throwable encountered with the failure
     */
    public void failure(String stackName, String hostName, Map<String, Object> failInfoMap, Throwable throwable);

    /**
     * Notification of a successful deployment.
     * 
     * @param stackName
     * @param hostName that received the deployment
     * @param wlpUsrDir on the deployed hostname
     * @param serverName on the deployed hostname
     * @param containerType on the deployed hostname
     */
    public void inProgress(String stackName, String hostName, String wlpUsrDir, String serverName, String containerType);
}
