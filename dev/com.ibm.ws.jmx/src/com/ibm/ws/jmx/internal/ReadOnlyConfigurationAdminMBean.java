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
package com.ibm.ws.jmx.internal;

import java.io.IOException;

import javax.management.openmbean.TabularData;

/**
 *
 */
public interface ReadOnlyConfigurationAdminMBean {

    public String getBundleLocation(String pid) throws IOException;

    public String[][] getConfigurations(String filter) throws IOException;

    public String getFactoryPid(String pid) throws IOException;

    public String getFactoryPidForLocation(String pid, String location) throws IOException;

    public TabularData getProperties(String pid) throws IOException;

    public TabularData getPropertiesForLocation(String pid, String location) throws IOException;
}
