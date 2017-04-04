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
package com.ibm.ws.container.service.config;

import com.ibm.ws.javaee.dd.web.WebFragment;
import com.ibm.wsspi.adaptable.module.Container;

/**
 * Information about a web fragment
 */
public interface WebFragmentInfo {
    /**
     * Returns the library URI
     * 
     * @return
     */
    public String getLibraryURI();

    /**
     * Returns the container associated with the web fragment
     * 
     * @return
     */
    public Container getFragmentContainer();

    /**
     * Returns the web fragment
     * 
     * @return
     */
    public WebFragment getWebFragment();

    public boolean isSeedFragment();

    public boolean isPartialFragment();
}
