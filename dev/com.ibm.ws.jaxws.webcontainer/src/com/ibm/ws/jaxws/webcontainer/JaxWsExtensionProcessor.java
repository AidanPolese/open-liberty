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
package com.ibm.ws.jaxws.webcontainer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.wsspi.webcontainer.osgi.extension.WebExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 *
 */
public class JaxWsExtensionProcessor extends WebExtensionProcessor {

    /**
     * @param context
     */
    public JaxWsExtensionProcessor(IServletContext context) {
        super(context);
    }

    /** {@inheritDoc} */
    @Override
    public void handleRequest(ServletRequest req, ServletResponse res) throws Exception {}

}
