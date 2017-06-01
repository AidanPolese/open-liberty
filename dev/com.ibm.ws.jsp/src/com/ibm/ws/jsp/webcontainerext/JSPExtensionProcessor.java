//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
// Changes
//APAR PK81387   Possible Security exposure  Jay Sartoris 2009/03/04

package com.ibm.ws.jsp.webcontainerext;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.ws.jsp.configuration.JspXmlExtConfig;
import com.ibm.ws.jsp.taglib.GlobalTagLibraryCache;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;
import com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;

public class JSPExtensionProcessor extends AbstractJSPExtensionProcessor {
    public JSPExtensionProcessor(IServletContext webapp, 
                                 JspXmlExtConfig webAppConfig, 
                                 GlobalTagLibraryCache globalTagLibraryCache,
                                 JspClassloaderContext jspClassloaderContext) throws Exception {
        super(webapp, webAppConfig, globalTagLibraryCache, jspClassloaderContext);
    }

    //PK81387 - added checkWEBINF param
    protected boolean processZOSCaseCheck(String path, boolean checkWEBINF) throws IOException {
        return true;
    }

	public WebComponentMetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}
}
