/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.j2c;

public class PmiProps {
    private int type;
    private String factoryId;
    private String providerId;
    private String pmiName;

    PmiProps() {
        type = 0;
        factoryId = "";
        providerId = "";
        pmiName = "";
    }

    public String getFactoryId() {
        return factoryId;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getPmiName() {
        return pmiName;
    }

    public int getType() {
        return type;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setPmiName(String pmiName) {
        this.pmiName = pmiName;
    }

    public void setType(int type) {
        this.type = type;
    }
}
