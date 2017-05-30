//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.compiler;

/**
 * The JspCompilerFactory interface provides a way of creating JspCompiler objects
 */
public interface JspCompilerFactory {
    /**
     * The method is called by the JSP Container when it has translated a JSP and
     * needs to compile the generated servlet.
     * 
     * @return JspCompiler object to be used to compile a generated JSP servlet.
     */
    JspCompiler createJspCompiler();
}
