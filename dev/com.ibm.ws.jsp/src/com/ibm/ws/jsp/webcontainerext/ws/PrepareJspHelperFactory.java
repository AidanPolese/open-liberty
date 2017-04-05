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
package com.ibm.ws.jsp.webcontainerext.ws;

import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.webcontainerext.AbstractJSPExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 * Factory for creating instances of PrepareJspHelper classes that are
 * specific to the spec version.
 */
public interface PrepareJspHelperFactory {

    public PrepareJspHelper createPrepareJspHelper(AbstractJSPExtensionProcessor s, IServletContext webapp, JspOptions options);
}
