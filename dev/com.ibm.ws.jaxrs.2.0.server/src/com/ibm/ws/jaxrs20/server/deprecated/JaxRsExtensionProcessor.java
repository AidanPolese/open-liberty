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
package com.ibm.ws.jaxrs20.server.deprecated;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.wsspi.webcontainer.osgi.extension.WebExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/** 
 *
 */
@Deprecated
public class JaxRsExtensionProcessor extends WebExtensionProcessor {

    /**
     * @param context
     */
    public JaxRsExtensionProcessor(IServletContext context) {
        super(context);
    }

    @Override
    public void handleRequest(ServletRequest req, ServletResponse res) throws Exception {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */

}
