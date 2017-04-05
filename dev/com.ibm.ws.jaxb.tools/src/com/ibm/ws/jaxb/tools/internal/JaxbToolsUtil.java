/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxb.tools.internal;

/**
 * The general utility for com.ibm.ws.jaxb.tools projects.
 */
public class JaxbToolsUtil {
    /**
     * get the localized message provided in the message files in the com.ibm.ws.jaxb.tools.
     * 
     * @param msgKey
     * @return
     */
    public static String formatMessage(String msgKey) {
        String msg;
        try {
            msg = JaxbToolsConstants.messages.getString(msgKey);
        } catch (Exception ex) {
            // no FFDC required
            return msgKey;
        }

        return msg;
    }
}
