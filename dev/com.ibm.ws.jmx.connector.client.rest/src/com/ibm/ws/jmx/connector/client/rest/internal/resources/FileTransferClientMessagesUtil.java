/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.client.rest.internal.resources;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 */
public class FileTransferClientMessagesUtil {

    private static final ResourceBundle logMessages = ResourceBundle.getBundle("com.ibm.ws.jmx.connector.client.rest.internal.resources.FileTransferClientMessages");

    // Error messages
    public static final String CLIENT_ERROR = "filetransfer.client.error";
    public static final String SERVER_ERROR = "filetransfer.server.error";
    public static final String BAD_CREDENTIALS = "filetransfer.client.bad.credentials";
    public static final String RESPONSE_CODE_ERROR = "filetransfer.response.code.error";
    public static final String UNSUPPORTED_OPERATION = "filetransfer.unsupported.operation";

    // Info messages
    public static final String CLIENT_INIT = "filetransfer.client.init";
    public static final String DOWNLOAD_TO_FILE = "filetransfer.download.file";
    public static final String UPLOAD_FROM_FILE = "filetransfer.upload.file";
    public static final String DELETE_FILE = "filetransfer.delete.file";
    public static final String DELETE_ALL = "filetransfer.delete.all";

    public static String getMessage(String messageName, Object... arguments) {
        if (arguments.length > 0)
            return MessageFormat.format(logMessages.getString(messageName), arguments);
        else
            return logMessages.getString(messageName);
    }

    public static String getObjID(Object obj) {
        return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
    }
}
