//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//
//PK72039      Add ability to continue to compile the rest of the JSPs during a batch compile failure  2008/10/21  Jay Sartoris

package com.ibm.wsspi.jsp.compiler;

/**
 * The JspCompilerResult interface provides a repository for the results of a JSP servlet compile
 */
public interface JspCompilerResult {
    /**
     * Returns the return code for the java compile
     * @return int java compile return code. Non-zero is treated as a compile failure
     */
    int getCompilerReturnValue();
    
    /**
     * Returns any messages that the java compiler produced. When a compile fails this method is called
     * to provide the error message that is passed back to the caller of the JSP.
     * @return
     */
    String getCompilerMessage();

    //PK72039
    /**
     * Returns a List of JSPs which failed to compile in a directory during batch compilation.
     * @return List
     */
    java.util.List getCompilerFailureFileNames();
}
