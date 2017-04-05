// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer.servlet;

/**
 *
 * A listner that is associated with a single instance of a ServletWrapper (and
 * hence a servlet), and listens for invalidation events.
 * 
 * @ibm-private-in-use
 */
public interface ServletReferenceListener
{
	/**
	 * Signals that the servlet that this listener is associated with has
	 * been invalidated
	 *
	 */
	public void invalidate();
}
