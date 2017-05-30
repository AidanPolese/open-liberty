//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

// Change History
// Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson

package com.ibm.ws.jsp.inmemory.resource;

import javax.servlet.jsp.tagext.TagFileInfo;

import com.ibm.wsspi.jsp.context.JspCoreContext;
import com.ibm.wsspi.jsp.context.translation.JspTranslationEnvironment;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.translation.JspResources;
import com.ibm.wsspi.jsp.resource.translation.JspResourcesFactory;
import com.ibm.wsspi.jsp.resource.translation.TagFileResources;

public class InMemoryJspResourceFactory implements JspResourcesFactory {
    private JspCoreContext context = null; 
    private JspTranslationEnvironment env = null;

    public InMemoryJspResourceFactory(JspCoreContext context, JspTranslationEnvironment env) {
        this.context = context;
        this.env = env;
    }
    
    public JspResources createJspResources(JspInputSource jspInputSource) {
        return new InMemoryJspResources(jspInputSource, context, env);
    }

    public TagFileResources createTagFileResources(JspInputSource tagFileInputSource, TagFileInfo tagFileInfo) {
        return new InMemoryTagFileResources(tagFileInputSource, tagFileInfo, context, env);
    }
}
