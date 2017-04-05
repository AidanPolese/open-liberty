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
package com.ibm.wsspi.collective.plugins;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ibm.wsspi.collective.plugins.helpers.CommandResult;

/**
 * A storage service that is used by the {@link com.ibm.wsspi.collective.plugins.CollectiveExecutor} and
 * can be exploited by other services as well.
 * 
 * @ibm-spi
 */
public interface TaskStorage {

    /**
     * The initial status of a task or a host
     */
    public static final String STATUS_PENDING = "pending";

    /**
     * The successful completion status of a task or a host
     */
    public static final String STATUS_SUCCEEDED = "succeeded";

    /**
     * The completion status of a task or a host is failed
     */
    public static final String STATUS_FAILED = "failed";

    /**
     * The status of a task or host is in progress
     */
    public static final String STATUS_IN_PROGRESS = "inProgress";

    /**
     * The status of a task or host has some successful and failed steps
     */
    public static final String STATUS_PARTIAL_SUCCEEDED = "partialSucceeded";

    /**
     * Transfer credentials is a Map<String,String> with attributes from the
     * CollectiveRegistrationMBean. Only credentials (user name, password, keys)
     * are defined. The host and port attributes are not supported.
     * Value could be {@code null}.
     */
    public static final String KEY_TRANSFER_CREDENTIALS = "transferCredentials";

    /**
     * A Map<String,String> of environment variables to be set into the remote session.
     */
    public static final String KEY_ENV_VARS = "transferEnvVars";

    /**
     * The raw postTransferAction string from the request header.
     */
    public static final String KEY_POST_TRANSFER_ACTION = "postTransferAction";

    /**
     * The preTransferActions array is the array of actions to perform, as specified
     * by the preTransferAction header. The array is the already parsed set of actions
     * to perform. Value will be a non-null array, but may be empty.
     */
    public static final String KEY_PRE_TRANSFER_ACTION_ARRAY = "preTransferActionArray";

    /**
     * The postTransferActions array is the array of actions to perform, as specified
     * by the postTransferAction header. The array is the already parsed set of actions
     * to perform. Value will be a non-null array, but may be empty.
     */
    public static final String KEY_POST_TRANSFER_ACTION_ARRAY = "postTransferActionArray";

    /**
     * The raw postTransferActionOptions string from the request header.
     */
    public static final String KEY_POST_TRANSFER_ACTION_OPTIONS = "postTransferActionOptions";

    /**
     * The preTransferActionOptions array is the array of action options, as specified
     * by the preTransferActionOption header. The array is the already parsed set of action
     * options to perform. If there were no options specified, the value will be {@code null}.
     * If options are specified, the length will be validated.
     */
    public static final String KEY_PRE_TRANSFER_ACTION_OPTIONS_ARRAY = "preTransferActionOptionsArray";

    /**
     * The postTransferActionOptions array is the array of action options, as specified
     * by the postTransferActionOption header. The array is the already parsed set of action
     * options to perform. If there were no options specified, the value will be {@code null}.
     * If options are specified, the length will be validated.
     */
    public static final String KEY_POST_TRANSFER_ACTION_OPTIONS_ARRAY = "postTransferActionOptionsArray";

    /**
     * Timestamp (long) for the creation of a task.
     */
    public static final String KEY_CREATION_TIMESTAMP = "creationTimestamp";

    /**
     * Timestamp (long) for the completion of a task.
     */
    public static final String KEY_COMPLETION_TIMESTAMP = "completionTimestamp";

    /**
     * An absolute location (String) of the source file that will be uploaded.
     */
    public static final String KEY_UPLOAD_FROM_FILE = "uploadFromFile";

    /**
     * An absolute location (String) of the target upload directory.
     */
    public static final String KEY_UPLOAD_TO_DIR = "uploadToDir";

    /**
     * A boolean flag indicating if the upload source needs to be deleted after uploaded.
     */
    public static final String KEY_NEED_TO_DELETE_UPLOAD_SOURCE = "needToDeleteUploadSource";

    /**
     * The name (String) of the uploaded + expanded archive.
     */
    public static final String KEY_UPLOAD_EXPANSION_FILENAME = "uploadExpansionFilename";

    /**
     * The hostname (String) of the Collective Controller.
     */
    public static final String KEY_CONTROLLER_HOST = "controllerHost";

    /**
     * The https port (as a String) of the Collective Controller.
     */
    public static final String KEY_CONTROLLER_PORT = "controllerPort";

    /**
     * The current user (String) of the task.
     */
    public static final String KEY_USER = "user";

    /**
     * The current status (String) of the task.
     */
    public static final String KEY_STATUS = "status";

    /**
     * A boolean flag indicating if the delete operation of the task should be recursive.
     */
    public static final String KEY_RECURSIVE_DELETE = "recursiveDelete";

    /**
     * The absolute location (String) of the file to be deleted.
     */
    public static final String KEY_FILE_TO_DELETE = "fileToDelete";

    /**
     * Kept for backwards compatibility. Use {@link #KEY_NEED_TO_DELETE_UPLOAD_SOURCE}.
     */
    public static final String KEY_DELETE_SOURCE = "deleteSource";

    /**
     * Create a new task in the task storage
     * 
     * @param hostNames An array of host names to run this task on
     * @param properties Properties of the task
     * @return An unique task identifier in String
     */
    public String createTask(String[] hostNames, Map<String, Object> properties);

    /**
     * Get all task identifiers in the task storage
     * 
     * @param filter A set of filter entries, or null for no filtering.
     * @return A set of task identifiers in String
     */
    public Set<String> getTaskTokens(Set<Entry<String, List<String>>> filter);

    /**
     * Get all the property keys of a given task
     * 
     * @param token Task identifier
     * @return A set of keys in String
     */
    public Set<String> getTaskPropertyKeys(String token);

    /**
     * Get all the host names of a given task
     * 
     * @param token Task identifier
     * @return An array of host names to run this task on
     */
    public String[] getTaskHostNames(String token);

    /**
     * Get the property value of a given task and key
     * 
     * @param token Task identifier
     * @param key The key of the property
     * @return The value of the property
     */
    public Object getTaskPropertyValue(String token, String key);

    /**
     * Get the overall status of a task. A task may be run on many hosts. Each host will have its own
     * overall status. The status of a task represents the overall result of a task.
     * 
     * @param token Task identifier
     * @return The status in String
     */
    public String getTaskStatus(String token);

    /**
     * Get the overall status of a host. A task may contain more than one step. The status
     * of a host represents the overall status of all steps involved.
     * 
     * @param token Task identifier
     * @param hostName Host name
     * @return The status in String
     */
    public String getHostStatus(String token, String hostName);

    /**
     * Add a result record to the specified task and host
     * 
     * @param token Task identifier
     * @param hostName Host name
     * @param commandResult The CommandResult object which holds the result
     */
    public void addHostResult(String token, String hostName, CommandResult commandResult);

    /**
     * Get a list of CommandResult object from a given task and host
     * 
     * @param token Task identifier
     * @param hostName Host name
     * @return A list of CommandResult object
     */
    public List<CommandResult> getHostResult(String token, String hostName);

    /**
     * Declare the start of work for a given task and host. This method should be called
     * before adding result for the given task and host. This method will change the status
     * of the host to STATUS_IN_PROGRESS.
     * 
     * @param token Task identifier
     * @param hostName Host name
     */
    public void startWorking(String token, String hostName);

    /**
     * Signal the completion of the work for a given task and host. This method should be
     * called after adding the last result for the given task and host. This method will
     * change the status of the host to STATUS_SUCCEEDED.
     * 
     * @param token Task identifier
     * @param hostName Host name
     */
    public void stopWorking(String token, String hostName);

    /**
     * Signal the completion of the work for a given task and host. This method should be
     * called after adding the last result for the given task and host.
     * 
     * @param token Task identifier
     * @param hostName Host name
     * @param status The completion status of the host. If null is specified, the status
     *            will be changed to STATUS_SUCCEEDED.
     */
    public void stopWorking(String token, String hostName, String status);
}