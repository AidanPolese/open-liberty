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
package com.ibm.ws.jaxrs20.support;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.extension.Extension;
import org.apache.cxf.transport.http.HTTPTransportFactory;

import com.ibm.ws.jaxrs20.api.ExtensionProvider;

/**
 * This class will provider LibertyHTTPTransportFactory extension, which will override the default HTTPTransportFactory extension
 * provided by CXF
 */
public class LibertyHTTPTransportFactoryProvider implements ExtensionProvider {

    @Override
    public Extension getExtension(Bus bus) {
        return new Extension(LibertyHTTPTransportFactory.class, HTTPTransportFactory.class);
    }

}
