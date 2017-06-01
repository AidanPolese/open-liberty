//IBM Confidential OCO Source Material
//5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997-2004
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.


package com.ibm.ws.jsp.runtime;

/**
 * Interface for retrieving information about the classfile.
 */

public interface JspClassInformation {

   /**
    * Returns a list of files names that the current page has a source
    * dependency on for the purpose of compiling out of date pages.  This is used for
    * 1) files that are included by include directives
    * 2) files that are included by include-prelude and include-coda in jsp:config
    * 3) files that are tag files and referenced
    * 4) TLDs referenced
    */
    public String[] getDependants();
    /**
     * Returns the WebSphere version on which the JSP classfile was generated
     */
    public String getVersionInformation();
 
	// begin 228118: JSP container should recompile if debug enabled and jsp was not compiled in debug.
    /**
     * Returns whether the JSP was compiled with debug enabled.
     */
    public boolean isDebugClassFile ();
	// end 228118: JSP container should recompile if debug enabled and jsp was not compiled in debug.
    
}