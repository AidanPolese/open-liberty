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

package com.ibm.jbatch.container.ws;

public class WSPurgeResponse {
    
    long instanceId = -1;
    PurgeStatus purgeStatus = null;
    String message = null;
    String redirectURL = null;
    
    public WSPurgeResponse() {}
    
    public WSPurgeResponse(long instanceId, PurgeStatus purgeStatus, String message, String redirectURL) {
        this.instanceId = instanceId;
        this.purgeStatus = purgeStatus;
        this.message = message;
        this.redirectURL = redirectURL;
    }

    public PurgeStatus getPurgeStatus() {
        return purgeStatus;
    }

    public void setPurgeStatus(PurgeStatus purgeStatus) {
        this.purgeStatus = purgeStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(long instanceId) {
        this.instanceId = instanceId;
    }
    
    
}
