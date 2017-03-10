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
package com.ibm.wsspi.kernel.feature;

import java.util.Collection;
import java.util.Locale;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * 
 * 
 */
public interface LibertyFeature {

    public Version getVersion();

    public String getFeatureName();

    public String getSymbolicName();

    public Collection<Bundle> getBundles();

    public String getHeader(String header);

    public String getHeader(String header, Locale locale);
}
