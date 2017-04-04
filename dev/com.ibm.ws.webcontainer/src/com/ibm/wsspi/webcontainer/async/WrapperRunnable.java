// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.wsspi.webcontainer.async;

/**
 * 
 * Class that wraps the Runnable an application passes to start(Runnable).  Most of the heavy lifting has been
 * moved to the Impl for our uses.
 * @ibm-private-in-use
 */
public interface WrapperRunnable extends Runnable {

	public void run();

	public boolean getAndSetRunning(boolean b);
}
