//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.context.translation;


import com.ibm.wsspi.jsp.compiler.JspCompilerFactory;
import com.ibm.wsspi.jsp.context.JspCoreContext;
import com.ibm.wsspi.jsp.resource.translation.JspResourcesFactory;

/**
 * Implementations of this interface are used by the JSP Container to
 * provide access to external resources and also additional Factory implements for
 * Resource management and Compiler management. It extends the JspCoreContext 
 * interface to provide function specific to translating JSP's in addtion to those
 * found in JspCoreContext.
 */
public interface JspTranslationContext extends JspCoreContext {
    /**
     * Returns the JspResourcesFactory object that the JSP Container will 
     * use to create JSP Resource objects
     * 
     * @return JspResourcesFactory
     */
    JspResourcesFactory getJspResourcesFactory();
    
    /**
     * Returns the JspCompilerFactory object that the JSP Container will 
     * use to create JspCompiler objects.
     * 
     * @return JspCompilerFactory
     */
    JspCompilerFactory getJspCompilerFactory();
    
    /**
     * This method is called by the JSP Container to provide a JSP environment
     * object that can be used to obtain default version of the factories and other
     * useful utility functions.
     * 
     * @param jspEnvironment
     */
    void setJspTranslationEnviroment(JspTranslationEnvironment jspEnvironment);
}
