//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.resource;

import java.net.URL;

/**
 * This factory is used by the JSP Container to create JspInputSource objects
 */
public interface JspInputSourceFactory {
    /**
     * Returns a JspInputSource object given a relative url
     * @param relativeURL
     * @return JspInputSource
     */
    JspInputSource createJspInputSource(String relativeURL);
    
    /**
     * Returns a JspInputSource object given a relative URL and 
     * an alternative context URL
     * @param contextURL
     * @param relativeURL
     * @return JspInputSource
     */
    JspInputSource createJspInputSource(URL contextURL, String relativeURL);
    
    /**
     * Returns a new JspInputSource object that has it context information obtained
     * from the provided base input source. The relative URL is used to provide the
     * addtional information.
     * 
     * @param base
     * @param relativeURL
     * @return JspInputSource
     */
    JspInputSource copyJspInputSource(JspInputSource base, String relativeURL);
}
