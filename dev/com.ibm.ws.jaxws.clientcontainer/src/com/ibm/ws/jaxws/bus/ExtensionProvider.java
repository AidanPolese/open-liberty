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
package com.ibm.ws.jaxws.bus;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.Extension;

/**
 * The interface provides Liberty extension to add CXF extension.
 * 
 * In the OSGi environment, CXF will use bundle listener to monitor /META-INF/cxf/bus-extensions.txt files for each bundle,
 * and add those extensions in the ExtensionRegistry, which are added in each created bus.
 * 
 * The typical scenario for this provider is that, it could be used to override any existing extension provided by CXF.
 * 
 * While implementing the interface, getExtension method will be invoked for each created bus
 * 
 */
public interface ExtensionProvider {

    /**
     * Return the extension provided by current provider.
     * 
     * @param bus The instance which the Extension will be added
     * @return return the created Extension instance or null if the current bus instance should be skipped
     */
    public Extension getExtension(Bus bus);
}
