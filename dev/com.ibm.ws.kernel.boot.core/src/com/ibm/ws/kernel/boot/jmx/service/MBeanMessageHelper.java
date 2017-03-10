/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot.jmx.service;

import java.text.MessageFormat;

import com.ibm.ws.kernel.boot.internal.BootstrapConstants;

/**
 * Helper for accessing generic messages for mbeans
 */
public class MBeanMessageHelper {

    /**
     * Provides a message for when the mbean is unable to perform an operation. Takes an Object[]
     * which should contain 4 strings:<br>
     * {0} server name<br>
     * {1} host name<br>
     * {2} logs location<br>
     * {3} unique request id<br>
     * 
     * @param inserts Substitution text for the message
     * @return Formated "mbean is unable to perform an operation" string
     */
    public static String getUnableToPerformOperationMessage(String libertyServerName, String hostName, String logsLocation, String requestId) {
        Object[] inserts = { libertyServerName, hostName, logsLocation, requestId };
        String msg = BootstrapConstants.messages.getString("error.mbean.operation.failure");
        return inserts == null || inserts.length == 0 ? msg : MessageFormat.format(msg, inserts);

    }

}
