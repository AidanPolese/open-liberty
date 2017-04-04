/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.webservices.handler;

/**
 * Components that want to register webServices handlers to all the webServics end points should implement
 * the Handler interface and register that implementation in the service registry.
 * <p>
 * The properties associated with the registered service specify the engine type, Flow Type, Client or server side
 * the handlers will take effect. Valid service properties are listed
 * as HandlerConstants with descriptive javadoc.
 * 
 * @see com.ibm.wsspi.webservices.handler.HandlerConstants
 */

public interface Handler {

    /**
     * The handleFault method is invoked for fault message processing.
     * 
     * @param GlobalHandlerMessageContext - the message context.
     */
    public void handleFault(GlobalHandlerMessageContext context);

    /**
     * The handleMessage method is invoked for normal processing of inbound and outbound messages.
     * 
     * @param: GlobalHandlerMessageContext - the message context.
     * @throws Exception: Causes the runtime to cease remaining global handler processing and switch to fault message processing.
     */
    public void handleMessage(GlobalHandlerMessageContext context) throws Exception;

}
