// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer.servlet;

import javax.servlet.ServletRequest;

/**
 * 
 * 
 * This interface maybe be used by websphere components in situations where
 * they would like to do a parallel dispatch. In order to do this, they would have
 * to clone the request, and pass on the cloned copy to the new thread which 
 * does a dispatch to a resource.
 * @ibm-private-in-use
 */
public interface IServletRequest extends ServletRequest, Cloneable 
{
	/**
	 * Clones this request
	 * @return
	 * @throws CloneNotSupportedException
	 */
    public Object clone() throws CloneNotSupportedException;
}
