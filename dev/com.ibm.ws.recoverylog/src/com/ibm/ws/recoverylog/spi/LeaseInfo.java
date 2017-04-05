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
package com.ibm.ws.recoverylog.spi;

/**
 *
 */
public class LeaseInfo {
    private String leaseDetail = null;

    /**
     * @return the leaseDetail
     */
    public String getLeaseDetail() {
        return leaseDetail;
    }

    /**
     * @param leaseDetail the leaseDetail to set
     */
    public void setLeaseDetail(String leaseDetail) {
        this.leaseDetail = leaseDetail;
    }

}
