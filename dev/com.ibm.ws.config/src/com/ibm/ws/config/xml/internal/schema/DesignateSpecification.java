/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.xml.internal.schema;

/**
 *
 */
class DesignateSpecification {

    private String pid;
    private boolean isFactory;
    private String ocdId;

    /**
     * @param pid
     * @param isFactory
     * @param ocdref
     */
    public DesignateSpecification() {}

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setOcdId(String ocdId) {
        this.ocdId = ocdId;
    }

    public void setIsFactory(boolean isFactory) {
        this.isFactory = isFactory;
    }

    /**
     * @return the pid
     */
    public String getPid() {
        return pid;
    }

    /**
     * @return the isFactory
     */
    public boolean isFactory() {
        return isFactory;
    }

    /**
     * @return the ocdId
     */
    public String getOcdId() {
        return ocdId;
    }

    /** debug info */
    @Override
    public String toString() {
        return "DesignateSpecification [pid=" + pid + ", isFactory=" + isFactory + ", ocdId=" + ocdId + "]";
    }

}
