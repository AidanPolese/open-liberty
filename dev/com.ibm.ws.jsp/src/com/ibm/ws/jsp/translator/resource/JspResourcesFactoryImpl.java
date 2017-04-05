//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.resource;

import javax.servlet.jsp.tagext.TagFileInfo;

import com.ibm.ws.jsp.JspOptions;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.jsp.context.JspCoreContext;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.translation.JspResources;
import com.ibm.wsspi.jsp.resource.translation.JspResourcesFactory;
import com.ibm.wsspi.jsp.resource.translation.TagFileResources;

public class JspResourcesFactoryImpl implements JspResourcesFactory {
    protected JspOptions jspOptions = null;
    protected JspCoreContext context = null;
    protected Container container;
    
    public JspResourcesFactoryImpl(JspOptions jspOptions, JspCoreContext context, Container container) {
        this.jspOptions = jspOptions;
        this.context = context;    
        this.container = container;
    }
    
    public JspResources createJspResources(JspInputSource inputSource) {
        if (container!=null) {
            return new JspResourcesContainerImpl(inputSource, jspOptions, context);
        } else {
            return new JspResourcesImpl(inputSource, jspOptions, context);
        }
    }

    public TagFileResources createTagFileResources(JspInputSource inputSource, TagFileInfo tfi) {
        return new TagFileResourcesImpl(inputSource, tfi, jspOptions, context);
    }
}
