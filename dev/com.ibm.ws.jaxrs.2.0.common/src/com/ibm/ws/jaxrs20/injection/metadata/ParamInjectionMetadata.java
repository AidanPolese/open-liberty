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
package com.ibm.ws.jaxrs20.injection.metadata;

import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;

/**
 *
 */
public class ParamInjectionMetadata {

    OperationResourceInfo operationResourceInfo;
    Message inMessage;

    public ParamInjectionMetadata(OperationResourceInfo operationResourceInfo, Message inMessage) {
        this.operationResourceInfo = operationResourceInfo;
        this.inMessage = inMessage;
    }

    /**
     * @return the operationResourceInfo
     */
    public OperationResourceInfo getOperationResourceInfo() {
        return operationResourceInfo;
    }

    /**
     * @param operationResourceInfo the operationResourceInfo to set
     */
    public void setOperationResourceInfo(OperationResourceInfo operationResourceInfo) {
        this.operationResourceInfo = operationResourceInfo;
    }

    /**
     * @return the inMessage
     */
    public Message getInMessage() {
        return inMessage;
    }

    /**
     * @param inMessage the inMessage to set
     */
    public void setInMessage(Message inMessage) {
        this.inMessage = inMessage;
    }

}
