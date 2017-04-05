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
package com.ibm.ws.jaxws.globalhandler;

import javax.xml.ws.LogicalMessage;

import org.apache.cxf.jaxws.handler.logical.LogicalMessageContextImpl;
import org.apache.cxf.message.Message;

/**
 *
 */
public class GlobalHandlerLogicalMsgCtxt extends LogicalMessageContextImpl {

    /**
     * @param wrapped
     */
    public GlobalHandlerLogicalMsgCtxt(Message wrapped) {
        super(wrapped);
        // TODO Auto-generated constructor stub
    }

    @Override
    public LogicalMessage getMessage() {
        return new GlobalHandlerLogicalMessageImpl(this);
    }

}
