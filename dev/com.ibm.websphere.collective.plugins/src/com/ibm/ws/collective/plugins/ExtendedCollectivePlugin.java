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
package com.ibm.ws.collective.plugins;

import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.util.List;
import java.util.Properties;

import com.ibm.wsspi.collective.plugins.CollectivePlugin;
import com.ibm.wsspi.collective.plugins.RemoteAccessWrapper;

/**
 *
 */
public interface ExtendedCollectivePlugin extends CollectivePlugin {

    public static final String KEY_PATH = "path";
    public static final String KEY_IS_DIRECTORY = "isDirectory";

    /**
     * List of files under the given directory, or the file itself if not a directory.
     * 
     * @param remoteAccess the remote access object to be used
     * @param remoteFile the file that will be checked
     * @param recursive if we should list files recursively
     * @return an array of files under the given directory
     * @throws FileNotFoundException
     * @throws ConnectException
     */
    List<Properties> listFiles(RemoteAccessWrapper remoteAccess, String remoteFile, boolean recursive) throws ConnectException, FileNotFoundException;

}
