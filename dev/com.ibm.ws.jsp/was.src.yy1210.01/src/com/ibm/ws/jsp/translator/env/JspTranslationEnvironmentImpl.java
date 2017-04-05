//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.env;

import com.ibm.ws.jsp.translator.utils.NameMangler;
import com.ibm.wsspi.jsp.compiler.JspCompilerFactory;
import com.ibm.wsspi.jsp.context.JspClassloaderContext;
import com.ibm.wsspi.jsp.context.translation.JspTranslationEnvironment;
import com.ibm.wsspi.jsp.resource.JspInputSourceFactory;
import com.ibm.wsspi.jsp.resource.translation.JspResourcesFactory;

public class JspTranslationEnvironmentImpl implements JspTranslationEnvironment {
    private String outputDir = null;
    private String contextRoot = null;
    private JspInputSourceFactory jspInputSourceFactory = null;
    private JspResourcesFactory jspResourcesFactory = null;
    private JspClassloaderContext jspClassloaderContext = null;
    private JspCompilerFactory jspCompilerFactory = null;
    
    public JspTranslationEnvironmentImpl(String outputDir, 
                                         String contextRoot, 
                                         JspInputSourceFactory jspInputSourceFactory,
                                         JspResourcesFactory jspResourcesFactory, 
                                         JspClassloaderContext jspClassloaderContext,
                                         JspCompilerFactory jspCompilerFactory) {
        this.outputDir = outputDir;
        this.contextRoot = contextRoot;
        this.jspInputSourceFactory = jspInputSourceFactory;
        this.jspResourcesFactory = jspResourcesFactory; 
        this.jspClassloaderContext = jspClassloaderContext;
        this.jspCompilerFactory = jspCompilerFactory;    
    }
    
    public String mangleClassName(String jspFileName) {
        return (NameMangler.mangleClassName(jspFileName));
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getContextRoot() {
        return contextRoot;
    }
    
    public JspInputSourceFactory getDefaultJspInputSourceFactory() {
        return jspInputSourceFactory; 
    }

    public JspResourcesFactory getDefaultJspResourcesFactory() {
        return jspResourcesFactory;
    }
    
    public JspClassloaderContext getDefaultJspClassloaderContext() {
        return jspClassloaderContext;
    }

    public JspCompilerFactory getDefaultJspCompilerFactory() {
        return jspCompilerFactory;
    }
}
