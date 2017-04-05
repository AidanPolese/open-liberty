/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.server.rest.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;
import com.ibm.json.java.OrderedJSONObject;
import com.ibm.websphere.jmx.connector.rest.ConnectorSettings;
import com.ibm.websphere.jsonsupport.JSON;
import com.ibm.websphere.jsonsupport.JSONFactory;
import com.ibm.websphere.jsonsupport.JSONMarshallException;
import com.ibm.websphere.jsonsupport.JSONSettings;
import com.ibm.websphere.jsonsupport.JSONSettings.Include;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.jmx.connector.server.rest.APIConstants;
import com.ibm.ws.jmx.connector.server.rest.handlers.FileStatusTaskStatusHandler;
import com.ibm.ws.rest.handler.helper.ServletRESTRequestWithParams;
import com.ibm.wsspi.collective.plugins.CollectiveExecutor;
import com.ibm.wsspi.collective.plugins.CollectivePlugin;
import com.ibm.wsspi.collective.plugins.TaskStorage;
import com.ibm.wsspi.collective.plugins.helpers.CommandResult;
import com.ibm.wsspi.collective.plugins.helpers.PasswordUtils;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.kernel.service.utils.FileUtils;
import com.ibm.wsspi.rest.handler.RESTRequest;

/**
 * This helper specializes in scenarios where we're routing to multiple targets
 */
@Component(service = { MultipleRoutingHelper.class },
           configurationPolicy = ConfigurationPolicy.IGNORE,
           immediate = true,
           property = { "service.vendor=IBM" })
public class MultipleRoutingHelper {

    private static final TraceComponent tc = Tr.register(MultipleRoutingHelper.class, APIConstants.TRACE_GROUP, APIConstants.TRACE_BUNDLE_FILE_TRANSFER);

    public MultipleRoutingHelper() {

    }

    //FileTransferHelper
    private final String KEY_FILE_TRANSFER_HELPER = "fileTransferHelper";
    private final AtomicServiceReference<FileTransferHelper> fileTransferHelperRef = new AtomicServiceReference<FileTransferHelper>(KEY_FILE_TRANSFER_HELPER);

    //TaskStorage
    private final String KEY_TASK_STORAGE = "taskStorage";
    private final AtomicServiceReference<TaskStorage> taskStorageRef = new AtomicServiceReference<TaskStorage>(KEY_TASK_STORAGE);

    //CollectivePlugin
    private final String KEY_COLLECTIVE_PLUGIN = "collectivePlugin";
    private final AtomicServiceReference<CollectivePlugin> collectivePluginRef = new AtomicServiceReference<CollectivePlugin>(KEY_COLLECTIVE_PLUGIN);

    //CollectiveExecutor
    private final String KEY_COLLECTIVE_EXECUTOR = "collectiveExecutor";
    private final AtomicServiceReference<CollectiveExecutor> collectiveExecutorRef = new AtomicServiceReference<CollectiveExecutor>(KEY_COLLECTIVE_EXECUTOR);

    @Activate
    protected void activate(ComponentContext cc) {
        fileTransferHelperRef.activate(cc);
        taskStorageRef.activate(cc);
        collectivePluginRef.activate(cc);
        collectiveExecutorRef.activate(cc);
    }

    @Deactivate
    protected void deactivate(ComponentContext cc) {
        fileTransferHelperRef.deactivate(cc);
        taskStorageRef.deactivate(cc);
        collectivePluginRef.deactivate(cc);
        collectiveExecutorRef.deactivate(cc);
    }

    //FileTransferHelper service
    @Reference(name = KEY_FILE_TRANSFER_HELPER, service = FileTransferHelper.class)
    protected void setFileTransferHelper(ServiceReference<FileTransferHelper> ref) {
        fileTransferHelperRef.setReference(ref);
    }

    protected void unsetFileTransferHelper(ServiceReference<FileTransferHelper> ref) {
        fileTransferHelperRef.unsetReference(ref);
    }

    protected FileTransferHelper getFileTransferHelper() {
        FileTransferHelper fileTransferHelper = fileTransferHelperRef.getService();

        if (fileTransferHelper == null) {
            IOException ioe = new IOException(TraceNLS.getFormattedMessage(this.getClass(),
                                                                           APIConstants.TRACE_BUNDLE_FILE_TRANSFER,
                                                                           "OSGI_SERVICE_ERROR",
                                                                           new Object[] { "FileTransferHelper" },
                                                                           "CWWKX0122E: OSGi service is not available."));
            throw ErrorHelper.createRESTHandlerJsonException(ioe, null, APIConstants.STATUS_INTERNAL_SERVER_ERROR);
        }
        return fileTransferHelper;
    }

    //TaskStorage service
    @Reference(name = KEY_TASK_STORAGE, service = TaskStorage.class)
    protected void setTaskStorageRef(ServiceReference<TaskStorage> ref) {
        taskStorageRef.setReference(ref);
    }

    protected void unsetTaskStorageRef(ServiceReference<TaskStorage> ref) {
        taskStorageRef.unsetReference(ref);
    }

    protected TaskStorage getTaskStorage() {
        TaskStorage taskStorage = taskStorageRef.getService();

        if (taskStorage == null) {
            IOException ioe = new IOException(TraceNLS.getFormattedMessage(this.getClass(),
                                                                           APIConstants.TRACE_BUNDLE_FILE_TRANSFER,
                                                                           "OSGI_SERVICE_ERROR",
                                                                           new Object[] { "TaskStorage" },
                                                                           "CWWKX0122E: OSGi service is not available."));
            throw ErrorHelper.createRESTHandlerJsonException(ioe, null, APIConstants.STATUS_INTERNAL_SERVER_ERROR);
        }
        return taskStorage;
    }

    //CollectivePlugin service
    @Reference(name = KEY_COLLECTIVE_PLUGIN, service = CollectivePlugin.class)
    protected void setCollectivePlugin(ServiceReference<CollectivePlugin> ref) {
        collectivePluginRef.setReference(ref);
    }

    protected void unsetCollectivePlugin(ServiceReference<CollectivePlugin> ref) {
        collectivePluginRef.unsetReference(ref);
    }

    protected CollectivePlugin getCollectivePlugin() {
        CollectivePlugin collectivePlugin = collectivePluginRef.getService();

        if (collectivePlugin == null) {
            IOException ioe = new IOException(TraceNLS.getFormattedMessage(this.getClass(),
                                                                           APIConstants.TRACE_BUNDLE_FILE_TRANSFER,
                                                                           "OSGI_SERVICE_ERROR",
                                                                           new Object[] { "CollectivePlugin" },
                                                                           "CWWKX0122E: OSGi service is not available."));
            throw ErrorHelper.createRESTHandlerJsonException(ioe, null, APIConstants.STATUS_INTERNAL_SERVER_ERROR);
        }
        return collectivePlugin;
    }

    //CollectiveExecutor service
    @Reference(name = KEY_COLLECTIVE_EXECUTOR, service = CollectiveExecutor.class)
    protected void setCollectiveExecutor(ServiceReference<CollectiveExecutor> ref) {
        collectiveExecutorRef.setReference(ref);
    }

    protected void unsetCollectiveExecutor(ServiceReference<CollectiveExecutor> ref) {
        collectiveExecutorRef.unsetReference(ref);
    }

    protected CollectiveExecutor getCollectiveExecutor() {
        CollectiveExecutor collectiveExecutor = collectiveExecutorRef.getService();

        if (collectiveExecutor == null) {
            IOException ioe = new IOException(TraceNLS.getFormattedMessage(this.getClass(),
                                                                           APIConstants.TRACE_BUNDLE_FILE_TRANSFER,
                                                                           "OSGI_SERVICE_ERROR",
                                                                           new Object[] { "CollectiveExecutor" },
                                                                           "CWWKX0122E: OSGi service is not available."));
            throw ErrorHelper.createRESTHandlerJsonException(ioe, null, APIConstants.STATUS_INTERNAL_SERVER_ERROR);
        }
        return collectiveExecutor;
    }

    /**
     * Extract the env variables from the inbound JSON map.
     */
    Map<String, String> processEnvVars(final String headerEnvVars) throws IOException {
        if (headerEnvVars != null && !headerEnvVars.isEmpty()) {
            Map<String, String> map = new HashMap<String, String>();

            JSONObject obj = JSONObject.parse(headerEnvVars);
            for (Object key : obj.keySet()) {
                try {
                    String k = (String) key;
                    map.put(k, String.valueOf(obj.get(k)));
                } catch (Exception e) {
                    if (tc.isDebugEnabled()) {
                        Tr.debug(tc, "Exception while processing env vars map: " + e.getMessage(), e);
                    }
                }
            }
            return map;
        } else {
            return null;
        }
    }

    /**
     * Extract the credentials from the inbound JSON map.
     *
     * @param headerTransferCredentials
     * @return
     */
    @Sensitive
    Map<String, String> processTransferCredentials(@Sensitive final String headerTransferCredentials) throws IOException {
        if (headerTransferCredentials != null && !headerTransferCredentials.isEmpty()) {
            Map<String, String> map = new HashMap<String, String>();

            JSONObject obj = JSONObject.parse(headerTransferCredentials);
            for (Object key : obj.keySet()) {
                try {
                    String k = (String) key;
                    if (k.equals("rpcUser") ||
                        k.equals("rpcUserPassword") ||
                        k.equals("sshPrivateKey") ||
                        k.equals("sshPrivateKeyPassword") ||
                        k.equals("useSudo") ||
                        k.equals("sudoUser") ||
                        k.equals("sudoUserPassword")) {
                        map.put(k, String.valueOf(obj.get(k)));
                    }
                } catch (Exception e) {
                    if (tc.isDebugEnabled()) {
                        Tr.debug(tc, "Exception while processing credentials map: " + e.getMessage(), e);
                    }
                }
            }

            return map;
        } else {
            return null;
        }
    }

    public String multipleDeleteInternal(RESTRequest request, String targetPath, boolean recursive) throws IOException {
        //Fetch relevant headers
        String headerHosts = request.getHeader(ConnectorSettings.COLLECTIVE_HOST_NAMES);
        String headerAsync = request.getHeader(ConnectorSettings.ASYNC_EXECUTION);
        String headerTransferCredentials = request.getHeader(ConnectorSettings.TRANSFER_CREDENTIALS);
        String headerEnvVars = request.getHeader(ConnectorSettings.TRANSFER_ENV_VARS);
        String headerAction = request.getHeader(ConnectorSettings.PRE_TRANSFER_ACTION);
        String headerActionOptions = request.getHeader(ConnectorSettings.PRE_TRANSFER_ACTION_OPTIONS);

        //We wouldn't have come in this method if the hosts header was null
        assert (headerHosts != null);

        //Process JSON maps
        Map<String, String> transferCredentials = processTransferCredentials(headerTransferCredentials);
        Map<String, String> envVars = processEnvVars(headerEnvVars);

        // Perform up-front validation. There's no point in running any operations if the API was invoked with bad data.
        String[] actions = {};
        String[] actionOptions = null;
        if (headerAction != null && !headerAction.isEmpty()) {
            actions = headerAction.split(",");
            actionOptions = (headerActionOptions == null || headerActionOptions.isEmpty()) ? null : headerActionOptions.split(",");
            //If the action options header is present it must match exactly the same length as the action header.
            if (actionOptions != null && actions.length != actionOptions.length) {
                throw new IllegalArgumentException(Tr.formatMessage(tc, request.getLocales(), "INVALID_ACTION_OPTIONS",
                                                                    new Object[] { actionOptions }));
            }
        }

        //Host list uses comma as delimiter
        final String[] hosts = headerHosts.split(",");

        //Trim whitespaces
        for (int i = 0; i < hosts.length; i++) {
            hosts[i] = hosts[i].trim();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "Processing a list of " + hosts.length + " hosts.");
        }

        //Fetch the async value (will be false if it was not specified)
        final boolean async = Boolean.valueOf(headerAsync);

        //Light processing of path
        targetPath = FileTransferHelper.processRoutingPathLight(targetPath);

        //Store info
        TaskStorage taskStorage = getTaskStorage();
        Map<String, Object> map = new HashMap<String, Object>();

        map.put(TaskStorage.KEY_TRANSFER_CREDENTIALS, transferCredentials);
        map.put(TaskStorage.KEY_ENV_VARS, envVars);

        map.put(TaskStorage.KEY_PRE_TRANSFER_ACTION_ARRAY, actions);
        map.put(TaskStorage.KEY_PRE_TRANSFER_ACTION_OPTIONS_ARRAY, actionOptions);

        map.put(TaskStorage.KEY_CONTROLLER_HOST, getCollectivePlugin().getControllerHost());
        map.put(TaskStorage.KEY_CONTROLLER_PORT, getCollectivePlugin().getControllerPort());

        map.put(TaskStorage.KEY_RECURSIVE_DELETE, recursive);

        map.put(TaskStorage.KEY_FILE_TO_DELETE, targetPath);

        //Grab username
        Principal principal = request.getUserPrincipal();
        map.put(TaskStorage.KEY_USER, principal == null ? null : principal.getName());

        //Get a task ID
        String taskID = taskStorage.createTask(hosts, map);

        //Dispatch task to collective executor
        getCollectiveExecutor().deleteFile(taskID, async);

        //If we're doing async, we just return the taskID and taskURL.
        //If we're not doing async, we return the taskStatus, the taskID and the taskURL.
        OrderedJSONObject obj = new OrderedJSONObject();
        if (!async) {
            obj.put("taskStatus", taskStorage.getTaskStatus(taskID));
        }
        obj.put("taskID", taskID);
        obj.put("taskURL", FileStatusTaskStatusHandler.ROOT_URL + "/" + taskID);

        return serializeJSON(obj);
    }

    public String multipleUploadInternal(RESTRequest request, String targetPath, boolean expand, boolean local) throws IOException {
        //Fetch relevant headers
        String headerHosts = request.getHeader(ConnectorSettings.COLLECTIVE_HOST_NAMES);
        String headerAsync = request.getHeader(ConnectorSettings.ASYNC_EXECUTION);
        String headerTransferCredentials = request.getHeader(ConnectorSettings.TRANSFER_CREDENTIALS);
        String headerAction = request.getHeader(ConnectorSettings.POST_TRANSFER_ACTION);
        String headerActionOptions = request.getHeader(ConnectorSettings.POST_TRANSFER_ACTION_OPTIONS);
        String headerEnvVars = request.getHeader(ConnectorSettings.TRANSFER_ENV_VARS);

        ServletRESTRequestWithParams req = null;
        if (request instanceof ServletRESTRequestWithParams) {
            req = (ServletRESTRequestWithParams) request;
            if (req.getAdditionalParamaMap().containsKey("deployService")) {
                headerHosts = req.getParam(ConnectorSettings.COLLECTIVE_HOST_NAMES);
                headerAction = req.getParam(ConnectorSettings.POST_TRANSFER_ACTION);
                headerActionOptions = req.getParam(ConnectorSettings.POST_TRANSFER_ACTION_OPTIONS);
            }
        }

        //We wouldn't have come in this method if the hosts header was null
        assert (headerHosts != null);

        //Process JSON maps
        Map<String, String> transferCredentials = processTransferCredentials(headerTransferCredentials);
        Map<String, String> envVars = processEnvVars(headerEnvVars);

        // Perform up-front validation. There's no point in running any operations if the API was invoked with bad data.
        String[] actions = {};
        String[] actionOptions = null;
        if (headerAction != null && !headerAction.isEmpty()) {
            actions = headerAction.split(",");
            actionOptions = (headerActionOptions == null || headerActionOptions.isEmpty()) ? null : headerActionOptions.split(",");
            //If the action options header is present it must match exactly the same length as the action header.
            if (actionOptions != null && actions.length != actionOptions.length) {
                throw new IllegalArgumentException(Tr.formatMessage(tc, request.getLocales(), "INVALID_ACTION_OPTIONS",
                                                                    new Object[] { actionOptions }));
            }
        }

        //Host list uses comma as delimiter
        final String[] hosts = headerHosts.split(",");

        //Trim whitespaces
        for (int i = 0; i < hosts.length; i++) {
            hosts[i] = hosts[i].trim();
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "Processing a list of " + hosts.length + " hosts.");
        }

        //Fetch the async value (will be false if it was not specified)
        final boolean async = Boolean.valueOf(headerAsync);

        //Light processing of path
        targetPath = FileTransferHelper.processRoutingPathLight(targetPath);

        //Get the filename portion from the path
        final String fileName = FileTransferHelper.getFilename(targetPath);

        //This variable will hold the directory where the file is in our controller
        String uploadFrom = null;

        //Fetch target directory (on the target host)
        final String uploadToDir = FileTransferHelper.getParentDir(targetPath);

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "Processed targetPath: " + targetPath + " | FileName: " + fileName + " | uploadToDir: " + uploadToDir);
        }

        boolean createdNewFile = false;

        //Now we must either download the file locally, or check the local reference that was passed in
        if (local) {
            //File is already in the system. The body payload will have the location (could have variables, relative to this controller)
            Reader reader = null;
            StringBuilder sb = new StringBuilder();
            try {
                //Get the incoming stream
                //String actionHeader = request.getHeader(ConnectorSettings.POST_TRANSFER_ACTION);
                if (ConnectorSettings.POST_TRANSFER_ACTION_FIND_SERVER_NAME.equals(headerAction)) {

                    // this is where the pacake is located on the controller
                    String originPath = req.getParam("originPackagePath");
                    originPath = URLDecoder.decode(originPath, "UTF8");
                    if (tc.isDebugEnabled()) {
                        Tr.debug(this, tc, "Origin path: " + originPath);
                    }
                    sb = new StringBuilder(originPath);
                } else {
                    reader = new InputStreamReader(request.getInputStream(), "UTF-8");
                    char[] buffer = new char[1024];
                    int count = 0;
                    while ((count = reader.read(buffer)) != -1) {
                        sb.append(buffer, 0, count);
                    }
                }
            } finally {
                FileUtils.tryToClose(reader);
            }

            //Get the local file
            String absLocalFile = sb.toString();

            //Must process the localFile to resolve symbols, relative to THIS controller
            absLocalFile = getFileTransferHelper().getWsLocationAdmin().resolveString(absLocalFile);

            //absLocalFile is now an absolute file location

            if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
                Tr.event(this, tc, "Local file: " + absLocalFile);
            }

            //If the filename of the local archive is exactly the same as the target filename, then we must make a copy of the file and change its
            //name slightly, otherwise we won't be able to expand into the target folder name
            if (expand && fileName.equals(FileTransferHelper.getFilename(absLocalFile))) {
                createdNewFile = true;

                //The renamed/copied file will go into the writable location
                String localTarget = getFileTransferHelper().getWritableLocation() + FileTransferHelper.getTempArchiveName(request, fileName);

                // check the folder(s)
                FileUtils.ensureDirExists(new File(FileTransferHelper.getParentDir(localTarget)));

                //Transfer contents
                FileInputStream fis = null;
                FileOutputStream fos = null;
                FileChannel fromChannel = null;
                FileChannel toChannel = null;
                try {
                    fis = new FileInputStream(absLocalFile);
                    fos = new FileOutputStream(localTarget);
                    fromChannel = fis.getChannel();
                    toChannel = fos.getChannel();
                    fromChannel.transferTo(0, fromChannel.size(), toChannel);
                } finally {
                    FileUtils.tryToClose(fis);
                    FileUtils.tryToClose(fos);
                    FileUtils.tryToClose(fromChannel);
                    FileUtils.tryToClose(toChannel);
                }

                //Update our "uploadFrom" field to point to our newly renamed/copied file
                uploadFrom = localTarget;
            } else {
                //Not an archive, or different name. So just use the local file path as-is.
                uploadFrom = absLocalFile;
            }

        } else {
            //File is coming from stream.
            createdNewFile = true;

            //Get a writable location for our incoming file
            //Change the name slightly if we're going to be expanding the archive.
            uploadFrom = getFileTransferHelper().getWritableLocation() + (expand ? FileTransferHelper.getTempArchiveName(request, fileName) : fileName);

            //Read contents of stream into file
            getFileTransferHelper().readRequestIntoFile(uploadFrom, request, false);
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(this, tc, "uploadFrom: " + uploadFrom);
        }

        //At this point the file is in the controller (either through the stream, or was already there)
        //We're ready to push the request information into a TaskStorage and send the task for execution

        //Store info
        TaskStorage taskStorage = getTaskStorage();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(TaskStorage.KEY_UPLOAD_FROM_FILE, uploadFrom);
        map.put(TaskStorage.KEY_NEED_TO_DELETE_UPLOAD_SOURCE, createdNewFile);
        map.put(TaskStorage.KEY_UPLOAD_TO_DIR, uploadToDir);
        if (expand) {
            map.put(TaskStorage.KEY_UPLOAD_EXPANSION_FILENAME, fileName);
        }
        map.put(TaskStorage.KEY_TRANSFER_CREDENTIALS, transferCredentials);
        map.put(TaskStorage.KEY_ENV_VARS, envVars);
        // Need to set both String and Array for backwards compatibility
        map.put(TaskStorage.KEY_POST_TRANSFER_ACTION, headerAction);
        map.put(TaskStorage.KEY_POST_TRANSFER_ACTION_ARRAY, actions);
        map.put(TaskStorage.KEY_POST_TRANSFER_ACTION_OPTIONS, headerActionOptions);
        map.put(TaskStorage.KEY_POST_TRANSFER_ACTION_OPTIONS_ARRAY, actionOptions);
        map.put(TaskStorage.KEY_CONTROLLER_HOST, getCollectivePlugin().getControllerHost());
        map.put(TaskStorage.KEY_CONTROLLER_PORT, getCollectivePlugin().getControllerPort());

        //Grab username
        Principal principal = request.getUserPrincipal();
        map.put(TaskStorage.KEY_USER, principal == null ? null : principal.getName());

        //Get a task ID
        String taskID = taskStorage.createTask(hosts, map);

        //Dispatch task to collective executor
        getCollectiveExecutor().deployArchive(taskID, async);

        //If we're doing async, we just return the taskID and taskURL.
        //If we're not doing async, we return the taskStatus, the taskID and the taskURL.
        OrderedJSONObject obj = new OrderedJSONObject();

        if (headerAction != null && headerAction.equals(ConnectorSettings.POST_TRANSFER_ACTION_FIND_SERVER_NAME)) {
            // use the taskurl and host name to call the method getHostDetails(taskID, host);
            // In case there are multiple hosts we can use any host sice the list of server names to deploy will be always the same.
            JSON jsonService;
            boolean foundSuccessfulHostTransfer = false;
            String serverNames = "";
            try {
                jsonService = getJSONService();

                for (int i = 0; i < hosts.length; i++) {
                    String hostResults = getHostDetails(taskID, hosts[0]);
                    CommandResultHelper[] results = jsonService.parse(hostResults, CommandResultHelper[].class);

                    //check the status of the last entry in the command result array
                    String status = results[results.length - 1].getStatus();
                    if (TaskStorage.STATUS_SUCCEEDED.equals(status)) {
                        serverNames = results[results.length - 1].getStdOut();
                        foundSuccessfulHostTransfer = true;
                        break;
                    }
                }

            } catch (JSONMarshallException e) {
                if (tc.isDebugEnabled()) {
                    Tr.debug(this, tc, "JSONMarshallException was caught, parsing was unsuccsessul:  " + e.getMessage());
                }
                throw new IOException(e);
            }

            obj.put("serverNames", serverNames);

            if (tc.isDebugEnabled()) {
                Tr.debug(this, tc, "ServerNames from MultiRoutingHelper : " + serverNames);
            }

        } else {
            if (!async) {
                obj.put("taskStatus", taskStorage.getTaskStatus(taskID));
            }
            obj.put("taskID", taskID);
            obj.put("taskURL", FileStatusTaskStatusHandler.ROOT_URL + "/" + taskID);
        }

        return serializeJSON(obj);
    }

    private void ensureTaskIDExists(TaskStorage storage, String taskID) {
        if (storage.getTaskStatus(taskID) == null) {
            throw ErrorHelper.createRESTHandlerJsonException(new IllegalArgumentException(TraceNLS.getFormattedMessage(this.getClass(),
                                                                                                                       APIConstants.TRACE_BUNDLE_FILE_TRANSFER,
                                                                                                                       "UNKNOWN_TASK_ID_ERROR",
                                                                                                                       new Object[] { taskID },
                                                                                                                       "CWWKX0131E: A task with id ''{0}'' was not found.")),
                                                             null,
                                                             APIConstants.STATUS_NOT_FOUND);
        }
    }

    public String getTaskProperty(String taskID, String property) {
        TaskStorage storage = getTaskStorage();
        ensureTaskIDExists(storage, taskID);

        Object value = storage.getTaskPropertyValue(taskID, property);

        // if value is not null,
        // 1) if the property key contains password, change the value to ********
        // 2) if the value contains something like --password=, mask the passwords in the value
        String valueString = null;
        if (value != null) {
            valueString = PasswordUtils.maskPasswords(property, value).toString();
            valueString = PasswordUtils.maskPasswords(valueString);
        }
        return valueString;
    }

    public String getTaskProperties(String taskID) {
        TaskStorage storage = getTaskStorage();
        ensureTaskIDExists(storage, taskID);

        Set<String> keys = storage.getTaskPropertyKeys(taskID);
        JSONArray array = new JSONArray();

        if (keys != null) {
            String basePropertyURL = FileStatusTaskStatusHandler.ROOT_URL + "/" + taskID + "/properties/";
            for (String key : keys) {
                OrderedJSONObject obj = new OrderedJSONObject();
                obj.put("key", key);
                obj.put("keyURL", basePropertyURL + key);
                array.add(obj);
            }
        }

        return serializeJSON(array);
    }

    //[ { "taskID" : "123", "taskStatus" : "status" , "taskURL" : "url" } ]
    public String getAllStatus(Set<Entry<String, List<String>>> filter) {
        TaskStorage storage = getTaskStorage();
        JSONArray array = new JSONArray();
        Set<String> ids = storage.getTaskTokens(filter);

        if (ids != null) {
            for (String taskID : ids) {
                OrderedJSONObject obj = new OrderedJSONObject();
                obj.put("taskID", taskID);
                obj.put("taskStatus", storage.getTaskStatus(taskID));
                obj.put("taskURL", FileStatusTaskStatusHandler.ROOT_URL + "/" + taskID);
                array.add(obj);
            }
        }

        return serializeJSON(array);
    }

    //{ "taskStatus" : "completed", "hostsURL" : "url" }
    public String getStatus(String taskID) {
        TaskStorage storage = getTaskStorage();
        ensureTaskIDExists(storage, taskID);

        OrderedJSONObject obj = new OrderedJSONObject();
        obj.put("taskStatus", storage.getTaskStatus(taskID));
        obj.put("propertiesURL", FileStatusTaskStatusHandler.ROOT_URL + "/" + taskID + "/properties");
        obj.put("hostsURL", FileStatusTaskStatusHandler.ROOT_URL + "/" + taskID + "/hosts");

        return serializeJSON(obj);
    }

    // [ {"hostName" : "host", "hostStatus" : "status", "hostURL" : "url"}* ]
    public String getHosts(String taskID) {
        TaskStorage storage = getTaskStorage();
        ensureTaskIDExists(storage, taskID);

        JSONArray array = new JSONArray();
        String[] hosts = storage.getTaskHostNames(taskID);

        if (hosts != null) {
            for (String host : hosts) {
                OrderedJSONObject obj = new OrderedJSONObject();
                obj.put("hostName", host);
                obj.put("hostStatus", storage.getHostStatus(taskID, host));
                obj.put("hostURL", FileStatusTaskStatusHandler.ROOT_URL + "/" + taskID + "/hosts/" + host);
                array.add(obj);
            }
        }

        return serializeJSON(array);
    }

    // [ { "timestamp" : String,"status" : String, "description" : String, "returnCode" : int, "stdOut" : String, "stdErr" : String }*]
    public String getHostDetails(String taskID, String host) {
        TaskStorage storage = getTaskStorage();
        ensureTaskIDExists(storage, taskID);

        JSONArray array = new JSONArray();

        List<CommandResult> results = storage.getHostResult(taskID, host);

        if (results != null) {
            for (CommandResult result : results) {
                OrderedJSONObject obj = new OrderedJSONObject();
                obj.put("timestamp", result.getTimestamp());
                obj.put("status", result.getStatus());
                obj.put("description", result.getDescription());
                obj.put("returnCode", result.getReturnCode());
                obj.put("stdOut", result.getStdout());
                obj.put("stdErr", result.getStderr());

                array.add(obj);
            }
        }
        return serializeJSON(array);
    }

    private String serializeJSON(JSONArtifact artifact) {
        try {
            //Our JSON library escapes forward slashes, but there's no need to escape them in this case (they are just URLs)
            return artifact.serialize().replace("\\/", "/");
        } catch (IOException ioe) {
            throw ErrorHelper.createRESTHandlerJsonException(ioe, null, APIConstants.STATUS_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Utility that returns a JSON object from a factory
     *
     * @return the JSON object providing POJO-JSON serialization and deserialization
     * @throws JSONMarshallException if there are problems configuring serialization inclusion
     */
    protected JSON getJSONService() throws JSONMarshallException {
        JSONSettings settings = new JSONSettings(Include.NON_NULL);
        return JSONFactory.newInstance(settings);

    }

}
