//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.wsspi.jsp.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.w3c.dom.Document;

/**
 * Implementions of this interface provide access to the jsp input source via URL objects.
 * The input source can also be in the form of an xml document object thus bypassing the
 * standard jsp parsing mechanism.
 */
public interface JspInputSource {
    /**
     * Returns a URL object that represents the absolute path to the jsp.
     * @return URL
     */
    URL getAbsoluteURL();
    
    /**
     * Returns a URL object that represents the context portion of the jsp URL.
     * @return URL
     */
    URL getContextURL();
    
    /**
     * Returns a String the represents the relative portion of the jsp URL
     * @return String 
     */
    String getRelativeURL();
    
    /**
     * Returns an IOStream object typcially obtained from the AbsoluteURL object.
     * @return InputStream
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;
    
    /**
     * Returns the timestamp of the input source if it is available.
     * @return
     */
    long getLastModified();
    
    /**
     * Return an XML document version of the jsp input.
     * @return org.w3c.dom.Document
     */
    Document getDocument();
    
    
    /**
     * Indicated that this JspInputSource contain an xml document version of the 
     * input source.
     * @return boolean
     */
    boolean isXmlDocument();
}
