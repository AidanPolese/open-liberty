/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012-2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.datatypes;

/**
 *
 */
public final class JMXServerInfo {
    public int version;
    public String mbeansURL, createMBeanURL, mbeanCountURL, defaultDomainURL, domainsURL, notificationsURL, instanceOfURL, fileTransferURL, apiURL, graphURL;
}
