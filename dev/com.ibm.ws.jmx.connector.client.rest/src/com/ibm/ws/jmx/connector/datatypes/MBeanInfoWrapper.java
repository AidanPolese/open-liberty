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

import java.util.Map;

import javax.management.MBeanInfo;

/**
 *
 */
public final class MBeanInfoWrapper {
    public MBeanInfo mbeanInfo;
    public String attributesURL;
    public Map<String, String> attributeURLs, operationURLs;
}
