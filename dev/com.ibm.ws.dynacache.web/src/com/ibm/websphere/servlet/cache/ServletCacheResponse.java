// 1.5, 10/18/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.websphere.servlet.cache;


import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * This interface is a proxy for the WebSphere response object.
 * It has features added to enable caching.
 * @ibm-api 
 */
public interface ServletCacheResponse extends HttpServletResponse
{
	/**
	 * This adds a Dynamic Content Provider that will
	 * generate dynamic content without executing its JSP.
	 *
	 * @param dynamicContentProvider The DynamicContentProvider.
         * @ibm-api 
	 */
	public void addDynamicContentProvider(DynamicContentProvider dynamicContentProvider) throws IOException;

        /**
         * This sets the page to not be consumed by its parents
         *      
         * @param value True if the page is to be set as do-not-consume
	 * @ibm-api 
         */
	public void setDoNotConsume(boolean doNotConsume);
 
}
