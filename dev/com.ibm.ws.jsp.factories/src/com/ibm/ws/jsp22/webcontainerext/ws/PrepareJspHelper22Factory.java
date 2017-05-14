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
package com.ibm.ws.jsp22.webcontainerext.ws;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.webcontainerext.AbstractJSPExtensionProcessor;
import com.ibm.ws.jsp.webcontainerext.ws.PrepareJspHelper;
import com.ibm.ws.jsp.webcontainerext.ws.PrepareJspHelperFactory;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 * This factory creates instances of the PrepareJspHelper class that are
 * specific to the JSP 2.2 spec.
 */
@Component(property = { "service.vendor=IBM" })
public class PrepareJspHelper22Factory implements PrepareJspHelperFactory {

    /* (non-Javadoc)
     * @see com.ibm.ws.jsp.webcontainerext.ws.PrepareJspHelperFactory#createPrepareJspHelper(com.ibm.ws.jsp.webcontainerext.AbstractJSPExtensionProcessor, com.ibm.wsspi.webcontainer.servlet.IServletContext, com.ibm.ws.jsp.JspOptions)
     */
    @Override
    public PrepareJspHelper createPrepareJspHelper(AbstractJSPExtensionProcessor s, IServletContext webapp, JspOptions options) {
        return new PrepareJspHelper(s, webapp, options);
    }

}
