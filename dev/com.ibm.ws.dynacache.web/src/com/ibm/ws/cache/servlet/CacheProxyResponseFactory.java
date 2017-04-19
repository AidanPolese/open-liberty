/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.cache.servlet;

import javax.servlet.http.HttpServletResponse;

public interface CacheProxyResponseFactory {
    CacheProxyResponse createCacheProxyResponse(HttpServletResponse response);
}
