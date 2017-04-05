// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//
package com.ibm.wsspi.webcontainer.servlet;

import javax.servlet.ServletRequest;
/**
 * 
 * Simple interface to allowing retrieving of a wrapped request object
 * without all the extra methods specified in the Servlet Specification
 * @ibm-private-in-use
 */
public interface IServletRequestWrapper {
	public ServletRequest getWrappedRequest();
}
