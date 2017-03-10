/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.osgi;

/**
 * Interface for passing config updates through the listener service to the msg router configurator.
 */
public interface MessageRouterConfigListener {

    /**
     * Pass updated list of message IDs associated with a log handler to the msg router configurator.
     * 
     * @param msgIds
     * @param handlerId
     */
    void updateMessageListForHandler(String msgIds, String handlerId);

}
