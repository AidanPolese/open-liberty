// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer.util;

import com.ibm.ws.webcontainer.core.RequestMapper;
import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.servlet.IExtendedRequest;


public class URIMapper extends com.ibm.ws.util.URIMapper implements RequestMapper
{
	public URIMapper()
	{
		matcher = new URIMatcher();
	}
	
	public URIMapper(boolean scalable)
	{
	    matcher = new URIMatcher(scalable);
	}

	/**
	 * @see com.ibm.ws.core.RequestMapper#map(String)
	 */
	public RequestProcessor map(String reqURI)
	{
		RequestProcessor r = (RequestProcessor) matcher.match(reqURI);
		return r;
	}
	
	public Object replaceMapping(String path, Object target) throws Exception
	{
		return matcher.replace(path, target);
	}

	/**
	 * @see com.ibm.ws.core.RequestMapper#map(IWCCRequest)
	 */
	public RequestProcessor map(IExtendedRequest req)
	{
		RequestProcessor r = (RequestProcessor)((URIMatcher)matcher).match(req);
		return r;
	}

	/* (non-Javadoc)
	 * @see com.ibm.ws.webcontainer.core.RequestMapper#exists(java.lang.String)
	 */
	public boolean exists(String path)
	{
		return matcher.exists(path);
	}

}
