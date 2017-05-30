//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.resource.translation;

import javax.servlet.jsp.tagext.TagFileInfo;

import com.ibm.wsspi.jsp.resource.JspInputSource;

/**
 * Used by the JSP Container to create jsp and tagfile resource objects
 */
public interface JspResourcesFactory {
    /**
     * Returns a JspResouces object for the given JspInputSource.
     * 
     * @param jspInputSource
     * @return JspResources
     */
    JspResources createJspResources(JspInputSource jspInputSource);
    
    /**
     * Returns a TagFileResouces object for the given TagFile Input Source and 
     * TagFileInfo object representing the tagfile.
     * 
     * @param tagFileInputSource
     * @param tagFileInfo
     * @return TagFileResources
     */
    TagFileResources createTagFileResources(JspInputSource tagFileInputSource, TagFileInfo tagFileInfo);
}
