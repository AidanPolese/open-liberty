//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.ws.jsp.webcontainerext;

import java.net.MalformedURLException;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import javax.servlet.http.HttpServletRequest;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.configuration.JspConfigurationManager;
import com.ibm.ws.jsp.taglib.TagLibraryCache;
import com.ibm.wsspi.jsp.context.translation.JspTranslationContext;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

public class JSPExtensionServletWrapper extends AbstractJSPExtensionServletWrapper {
    
    public JSPExtensionServletWrapper(IServletContext parent, 
                                      JspOptions options, 
                                      JspConfigurationManager configManager, 
                                      TagLibraryCache tlc,
                                      JspTranslationContext context, 
                                      CodeSource codeSource) throws Exception {
        super(parent, options, configManager, tlc, context, codeSource);
    }

    protected PermissionCollection createPermissionCollection() throws MalformedURLException {
        return Policy.getPolicy().getPermissions(codeSource);
    }
    
    protected void preinvokeCheckForTranslation(HttpServletRequest req) throws JspCoreException {
    }
}
