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
package com.ibm.ws.artifact.url;

import java.net.URL;

import org.osgi.framework.Bundle;

/**
 * Service for converting urls from a Bundle into a form that can survive round tripping via String
 * across frameworks.
 */
public interface URLService {
    /**
     * Convert a URL from the owningBundle into a form that can be round tripped via String across frameworks.
     * 
     * @param urlToConvert the url that should be converted
     * @param owningBundle the bundle the url came from (used to manage the lifecycle of the returned url)
     * @return new URL that can be safely used across frameworks.
     * @throws IllegalStateException if owningBundle is uninstalled.
     */
    public URL convertURL(URL urlToConvert, Bundle owningBundle) throws IllegalStateException;
}
