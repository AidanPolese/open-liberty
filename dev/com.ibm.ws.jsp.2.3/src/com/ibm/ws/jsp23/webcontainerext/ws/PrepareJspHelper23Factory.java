/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jsp23.webcontainerext.ws;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.webcontainerext.AbstractJSPExtensionProcessor;
import com.ibm.ws.jsp.webcontainerext.ws.PrepareJspHelper;
import com.ibm.ws.jsp.webcontainerext.ws.PrepareJspHelperFactory;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 *
 */
@Component(property = { "service.vendor=IBM" })
public class PrepareJspHelper23Factory implements PrepareJspHelperFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.webcontainer.osgi.webapp.WebAppFactory#createWebApp(com.ibm.ws.webcontainer.osgi.webapp.WebAppConfiguration, java.lang.ClassLoader,
     * com.ibm.wsspi.injectionengine.ReferenceContext, com.ibm.ws.container.service.metadata.MetaDataService, com.ibm.websphere.csi.J2EENameFactory)
     */
    @Override
    public PrepareJspHelper createPrepareJspHelper(AbstractJSPExtensionProcessor s, IServletContext webapp, JspOptions options) {
        return new PrepareJspHelper23(s, webapp, options);
    }

}
