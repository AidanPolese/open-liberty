//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.resource.translation;

/**
 * Implementions of this interface are use by the JSP Container to handle Tag File Resources 
 * such as the tag file input source, the generated source file and the class name used for the
 * generated servlet. It is also used by the JSP container to check if a tag file is outdated and
 * syncronize the resources if a translation occurs. 
 */
public interface TagFileResources extends JspResources {
    /**
     * Called by the JSP Container to synchronize the tag file generated source files.
     */
    void syncGeneratedSource();
}
