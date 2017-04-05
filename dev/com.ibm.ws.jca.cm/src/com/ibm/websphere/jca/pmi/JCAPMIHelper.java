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
package com.ibm.websphere.jca.pmi;

/**
 *
 */
public interface JCAPMIHelper {

    //The Below two  methods are added to support PMI data for connection pools.This can be avoid if we expose com.ibm.ejs.jca,but currently as per JIM
    //it should not be done as j2c code is partial implementation only for JDBC and JMS.In future when j2c code is fully implemented its better to
    //remove the interface JCAPMIHelper and implemented methods and update ConnectionPoolMonitor.java to use the exposed j2c code.
    public String getUniqueId();

    public String getJNDIName();

    public boolean getParkedValue();
}
