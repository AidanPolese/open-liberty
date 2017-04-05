// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.ws.jsf.extprocessor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

//import com.ibm.wsspi.webcontainer.extension.WebExtensionProcessor;
import com.ibm.ws.webcontainer.extension.WebExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public class JSFExtensionProcessor extends WebExtensionProcessor {

    public JSFExtensionProcessor(IServletContext webapp) {
        super(webapp);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.webcontainer.RequestProcessor#handleRequest(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    public void handleRequest(ServletRequest req, ServletResponse res) throws Exception {
        throw new IllegalStateException("JSFExtensionProcessor.handleRequest(ServletRequest, ServletResponse) is not implemented by this extension processor");
    }

}