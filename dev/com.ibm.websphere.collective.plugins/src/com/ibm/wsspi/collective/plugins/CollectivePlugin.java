/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012,2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.collective.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.management.AttributeList;
import javax.net.ssl.SSLContext;

import com.ibm.websphere.ssl.SSLException;
import com.ibm.wsspi.collective.plugins.helpers.CommandResult;

/**
 * Defines the bridge to exploit collective-based features and services.
 * This plug point must be implemented to provide collective-based services such as routing.
 * 
 * @ibm-spi
 */
public interface CollectivePlugin {

    /**
     * Computes the appropriate repository server path for the desired node, and then retrieves the value.
     * 
     * @param hostName the name of the host in the path
     * @param userDir the user directory in the path
     * @param serverName the name of the server in the path
     * @param node the leaf node in the path
     * @return the value at the calculated path
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     */
    Object getServerNode(String hostName, String userDir, String serverName, String node) throws IllegalArgumentException, IOException;

    /**
     * Gets the value for the given node.
     * 
     * @param nodePath the full path of the node
     * @return the value of that node
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     */
    Object getNodeValue(String nodePath) throws IllegalArgumentException, IOException;

    /**
     * Computes the appropriate repository server path for the desired node, and then retrieves the value.
     * The target path is considered to be private, and not exposed externally.
     * 
     * @param hostName the name of the host in the path
     * @param userDir the user directory in the path
     * @param serverName the name of the server in the path
     * @param node the leaf node in the path
     * @return the value at the calculated path
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     */
    Object getPrivateServerNode(String hostName, String userDir, String serverName, String node) throws IllegalArgumentException, IOException;

    /**
     * 
     * Gets the value for the given node.
     * The target path is considered to be private, and not exposed externally.
     * 
     * @param nodePath the full path of the node
     * @return the value of that node
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     * @throws NoSuchElementException
     */
    Object getPrivateNodeValue(String nodePath) throws IllegalArgumentException, IOException, NoSuchElementException;

    /**
     * Convenience method to retrieve the SSLContext for the given alias.
     * 
     * @param sslAlias name of the SSL alias we're trying to retrieve
     * @return the corresponding SSLContext that matches the alias.
     * 
     * @throws SSLException
     * @throws IOException
     */
    SSLContext getSSLContent(String sslAlias) throws SSLException, IOException;

    /**
     * Get the MBean attributes for the given Object name.
     * 
     * @param hostName the target host name
     * @param userDir the target user directory
     * @param serverName the target server name
     * @param objectName the String representation of the MBean's ObjectName
     * @return an AttributeList with all the attributes for that MBean.
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     */
    AttributeList getAttributes(String hostName, String userDir, String serverName, String objectName) throws IllegalArgumentException, IOException;

    /**
     * Get the MBean attribute value for the given Object name and attribute.
     * 
     * @param hostName the target host name
     * @param userDir the target user directory
     * @param serverName the target server name
     * @param objectName the String representation of the MBean's ObjectName
     * @param attributeName the name of a specific attribute
     * @return the value of that attribute.
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     */
    Object getAttribute(String hostName, String userDir, String serverName, String objectName, String attributeName) throws IllegalArgumentException, IOException;

    /**
     * Create a new remote access connection to the specified target. The returning object can be passed into various methods of this interface, and must
     * have its session explicitly ended (see {@link RemoteAccessWrapper#endSession()}).
     * 
     * @param hostName the target host name
     * @param userDir the target user directory
     * @param serverName the target server name
     * @param credentials the map of credentials to use to upload the file.
     *            If the map is null, the default credentials for the target will be used.
     *            See CollectiveRegistrationMBean for details on the credentials map.
     * @param envVariables the map of environment variables to be set for this remote access connection
     * @return a RemoteAccessWrapper object that can be used as the remote connection
     * @throws IOException
     */
    RemoteAccessWrapper createRemoteAccess(String hostName, String userDir, String serverName, Map<String, String> credentials, Map<String, String> envVariables) throws IOException;

    /**
     * Download a remote file. This method will create a new remote connection and end its session before existing.
     * 
     * @param hostName the target host name
     * @param userDir the target user directory
     * @param serverName the target server name
     * @param remoteFile the absolute path of the remote file
     * @param localDir the local directory where the file will be downloaded to
     * 
     * @throws ConnectException
     * @throws IOException
     */
    void receiveFile(String hostName, String userDir, String serverName, String remoteFile, File localDir) throws ConnectException, IOException;

    /**
     * Download a remote file.
     * 
     * @param remoteAccess the remote access object to be used
     * @param remoteFile the absolute path of the remote file
     * @param localDir the local directory where the file will be downloaded to
     * 
     * @throws ConnectException
     * @throws IOException
     */
    void receiveFile(RemoteAccessWrapper remoteAccess, String remoteFile, File localDir) throws ConnectException, IOException;

    /**
     * Upload a file to a remote location. This method will create a new remote connection and end its session before existing.
     * 
     * @param hostName the target host name
     * @param userDir the target user directory
     * @param serverName the target server name
     * @param localFile the absolute local file location
     * @param remoteDir the target remote directory that will receive the uploaded file
     * 
     * @throws ConnectException
     * @throws IOException
     */
    void sendFile(String hostName, String userDir, String serverName, File localFile, String remoteDir) throws ConnectException, IOException;

    /**
     * Upload a file to a remote location.
     * 
     * @param remoteAccess the remote access object to be used
     * @param localFile the absolute local file location
     * @param remoteDir the target remote directory that will receive the uploaded file
     * 
     * @throws ConnectException
     * @throws IOException
     */
    void sendFile(RemoteAccessWrapper remoteAccess, File localFile, String remoteDir) throws ConnectException, IOException;

    /**
     * Delete a remote file. This method will create a new remote connection and end its session before existing.
     * 
     * @param hostName the target host name
     * @param userDir the target user directory
     * @param serverName the target server name
     * @param remoteFile the absolute remote file path
     * 
     * @throws ConnectException
     * @throws IOException
     */
    void deleteFile(String hostName, String userDir, String serverName, String remoteFile) throws ConnectException, IOException;

    /**
     * Delete a remote file.
     * 
     * @param remoteAccess the remote access object to be used
     * @param remoteFile the absolute remote file path
     * @param recursiveDelete a boolean that toggles recursive deletion of directories.
     * 
     * @throws ConnectException
     * @throws IOException
     */
    void deleteFile(RemoteAccessWrapper remoteAccess, String remoteFile, boolean recursiveDelete) throws ConnectException, IOException;

    /**
     * Expand a remote archive. This method will create a new remote connection and end its session before existing.
     * 
     * @param hostName the target host name
     * @param userDir the target user directory
     * @param serverName the target server name
     * @param cmdArgs the commands for the archive expansion. Cell 0 should specific the absolute source file location, and cell 1 should specific the absolute target file location
     * @param targetDir the directory from where the file expansion action will be executed
     * @param mustBeLiberty if true, then the archive will be enforced to be a proper Liberty archive. If false, the archive could be a Liberty archive or another archive.
     * 
     * @throws ConnectException
     * @throws IOException
     */
    void expandArchive(String hostName, String userDir, String serverName, String[] cmdArgs, String targetDir, boolean mustBeLiberty) throws ConnectException, IOException;

    /**
     * Expand a remote archive.
     * 
     * @param remoteAccess the remote access object to be used
     * @param cmdArgs the commands for the archive expansion. Cell 0 should specific the absolute source file location, and cell 1 should specific the absolute target file location
     * @param targetDir the directory from where the file expansion action will be executed
     * @param mustBeLiberty if true, then the archive will be enforced to be a proper Liberty archive. If false, the archive could be a Liberty archive or another archive.
     * 
     * @throws ConnectException
     * @throws IOException
     */
    void expandArchive(RemoteAccessWrapper remoteAccess, String[] cmdArgs, String targetDir, boolean mustBeLiberty) throws ConnectException, IOException;

    /**
     * Perform an action after a file has been transferred remotely.
     * 
     * @param hostName the target host name
     * @param targetDir the directory from where the action will be performed
     * @param action the action to be performed
     * @param actionOptions options that will be passed to the action
     * @return the result of the action
     * 
     * @throws ConnectException
     * @throws IOException
     */
    CommandResult postTransferAction(String hostName, String targetDir, String action, String actionOptions) throws ConnectException, IOException;

    /**
     * Perform an action after a file has been transferred remotely.
     * 
     * @param remoteAccess the remote access object to be used
     * @param targetDir the directory from where the action will be performed
     * @param action the action to be performed
     * @param actionOptions options that will be passed to the action
     * @return the result of the action
     * 
     * @throws ConnectException
     * @throws IOException
     */
    CommandResult postTransferAction(RemoteAccessWrapper remoteAccess, String targetDir, String action, String actionOptions) throws ConnectException, IOException;

    /**
     * Fetch the controller's host name
     * 
     * @return the controller's host name
     * @throws IOException
     */
    String getControllerHost() throws IOException;

    /**
     * Fetch the controllers secure (https) port.
     * 
     * @return the controller's secure port
     * @throws IOException
     */
    String getControllerPort() throws IOException;

    /**
     * Perform an action before a file is deleted remotely.
     * 
     * @param remoteAccess the remote access object to be used
     * @param executableDir the directory from where the action will be performed
     * @param action the action to be performed
     * @param actionOptions options that will be passed to the action
     * @return the result of the action
     * 
     * @throws ConnectException
     * @throws IOException
     */
    CommandResult preTransferAction(RemoteAccessWrapper remoteAccess, String executableDir, String action, String actionOption) throws ConnectException, IOException;

    /**
     * Check if a remote file is read-only
     * 
     * @param remoteAccess the remote access object to be used
     * @param remoteFile the file that will be checked
     * @return a boolean that is true is the file is read-only, false otherwise
     * @throws FileNotFoundException
     * @throws ConnectException
     */
    boolean isReadOnly(RemoteAccessWrapper remoteAccess, String remoteFile) throws ConnectException, FileNotFoundException;

    /**
     * Retrieve the java home on the remote machine
     * 
     * @param remoteAccess the remote access object to be used
     * @param hostName the name of the remote machine
     * @return the java home, surrounded by quotes
     * @throws IOException
     */
    String getJavaCommand(RemoteAccessWrapper remoteAccess, String hostName) throws IOException;

    /**
     * Retrieves a Map that contains the read and write lists of the remote host.
     * 
     * @param hostName the name of the remote machine
     * @param failIfNull when true, this flag will cause the method to throw an exception if the host maps are not found
     * @return a map that contains the read and write paths of the host
     * @throws IOException
     */
    Map<String, Object> getHostPaths(String hostName, boolean failIfNull) throws IOException;

    /**
     * Run a command remotely. The keys of the returning Map are: returnCode(int), systemOut(String), systemErr(String) and isTimeoutExpired(boolean).
     * 
     * @param remoteAccess the remote access object to be used
     * @param cmd the command that will be executed
     * @param targetDir is the remote directory where the command will be executed
     * @return a map with the command results
     * @throws ConnectException
     * @throws IOException
     */
    Map<String, Object> runCommand(RemoteAccessWrapper remoteAccess, String cmd, String targetDir) throws ConnectException, IOException;

    /**
     * Creates a directory tree in the remote target.
     * 
     * @param remoteAccess the remote access object to be used
     * @param remoteDirectory the remote directory to be created, including any parent directories
     * @throws ConnectException
     * @throws FileNotFoundException
     * @throws IOException
     */
    void makeRemoteDirectories(RemoteAccessWrapper remoteAccess, String remoteDirectory) throws ConnectException, FileNotFoundException, IOException;

    /**
     * @param hostName the host name of the remote target
     * @param userDir the user directory of the remote target
     * @param serverName the server name of the remote target
     * @param path the remote file path that is being checked for access
     * @param readOnly whether or not we're checking for read-only access
     * @return a boolean that indicates if the given remote file path is within the appropriate white list
     * @throws IOException
     */
    boolean checkServerLevelAccess(String hostName, String userDir, String serverName, String path, boolean readOnly) throws IOException;

    /**
     * @param nodePath the collective repository path to check
     * @return true if the path exists in the collective repository, false otherwise.
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    boolean existsInRepository(String nodePath) throws IllegalArgumentException, IllegalStateException, IOException;

    /**
     * @param remoteAccess the remote access object to be used
     * @param filePath the remote path to check
     * @return true if the path exists in the remote file system, false otherwise.
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    boolean existsInRemoteFileSystem(RemoteAccessWrapper remoteAccess, String filePath) throws IllegalArgumentException, IllegalStateException, IOException;

}
