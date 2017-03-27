/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.datatypes;

import javax.management.ObjectName;

public final class CreateMBean {
    public ObjectName objectName, loaderName;
    public String className, signature[];
    public Object params[];
    public boolean useLoader, useSignature;
}
