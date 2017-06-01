//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.compiler;

import java.util.Collection;
import java.util.List;

import com.ibm.wsspi.jsp.resource.translation.JspResources;

/**
 * The JspCompiler interface provides support for a pluggable Java Compiler.
 */
public interface JspCompiler {
    /**
     * The compile method is called by the JSP Container to compile a generated JSP servlet.
     * 
     * @param jspResources An array JspResources object containing the paths to the source files
     * @param jspResources An array JspResources object containing the dependencies
     * @param jspLineIds A collection of JspLineId objects that can be used to lookup JSP source line numbers 
     * @param compilerOptions  A List of String objects to be passed on the java compiler command-line  
     * @return JspCompilerResult - Contains details of any compile failure and relevant messages
     */
    public JspCompilerResult compile(JspResources[] jspResources, JspResources[] dependencyResources, Collection jspLineIds, List compilerOptions); 
    /**
     * The compile method is called by the JSP Container to compile a generated JSP servlet.
     * 
     * @param sourcePath A string containing the path to the source file
     * @param jspLineIds A collection of JspLineId objects that can be used to lookup JSP source line numbers 
     * @param compilerOptions  A List of String objects to be passed on the java compiler command-line  
     * @return JspCompilerResult - Contains details of any compile failure and relevant messages
     * 
     */
    public JspCompilerResult compile(String sourcePath, Collection jspLineIds, List compilerOptions); 
}
