/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.jmx.connector.server.rest.helpers;

import java.io.IOException;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.ws.jmx.connector.server.rest.APIConstants;

/**
 *
 */
public class FileUtilsHelper {

    /**
     * @param filePath
     * @return
     */
    public static String getParentDir(String filePath) {
        String parentDir = filePath.substring(0, filePath.lastIndexOf("/"));

        if (!parentDir.contains("/")) {
            //catch cases where filePath is something like C:/temp.zip or /home.zip
            parentDir = parentDir + "/";
        }

        return parentDir;
    }

    /**
     * @param file
     * @return
     */
    public static String removeTrailingSlash(String file) {
        if (file.charAt(file.length() - 1) == '/') {
            file = file.substring(0, file.length() - 1);
        }
        return file;
    }

    /**
     * @param directory
     * @param filename
     * @return
     */
    public static String appendFilename(String directory, String filename) {
        if (directory.endsWith("/")) {
            //There's already a slash separating the two, so just append
            return directory + filename;
        }

        return directory + "/" + filename;
    }

    /**
     * Get the filename from a given path
     *
     * @param path
     * @return
     */
    public static String getFilename(String path) {
        //The filename is after the last slash
        final int index = path != null ? path.lastIndexOf("/") : -1;
        if (index == -1) {
            IOException ioe = new IOException(TraceNLS.getFormattedMessage(FileUtilsHelper.class,
                                                                           APIConstants.TRACE_BUNDLE_FILE_TRANSFER,
                                                                           "PATH_NOT_VALID",
                                                                           new String[] { path },
                                                                           "CWWKX0127E: The path " + path + " is not valid."));
            throw ErrorHelper.createRESTHandlerJsonException(ioe, null, APIConstants.STATUS_BAD_REQUEST);
        }

        return path.substring(index + 1);
    }

}
