// 1.3, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.servlet.cache;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

/**
 * Implement this interface in your Servlet or JSP to
 * have a callback invoked during cache hits.  This
 * will allow Dynamic Cache to imbed your dynamic content 
 * within a cached fragment.
 * @ibm-api 
 */
public interface DynamicContentProvider {

	/**
	  * This method generates and writes the dynamic content to the OutputStream.
	  * It is called on a cache hit or miss to generate the dynamic content of the cacheable servlet.
	  * @param request      The HttpServletRequest to determin what dynamic content to create.
	  * @param streamWriter The OutputStream that this method will write the dynamic content to.
	  * 
      * @ibm-api 
	  */
	public void provideDynamicContent(HttpServletRequest request, OutputStream streamWriter) throws IOException;

	/**
	 * This method generates and writes the dynamic content to the Writer.
	 * It is called on a cache hit or miss to generate the dynamic content of the cacheable servlet.
	 * @param request      The HttpServletRequest to determin what dynamic content to create.
	 * @param streamWriter The Writer that this method will write the dynamic content to.
	 * 
     * @ibm-api 
	 */
	public void provideDynamicContent(HttpServletRequest request, Writer streamWriter) throws IOException;
}
