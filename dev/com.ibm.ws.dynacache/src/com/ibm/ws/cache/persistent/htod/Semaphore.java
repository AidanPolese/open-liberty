// 1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.persistent.htod;

/******************************************************************************
*	Get and release a lock.
******************************************************************************/
public class Semaphore {

	private Thread activeThread = null;
	
/******************************************************************************
*	Get the lock.  Caller will wait until the lock is available.  This initial
*	implementation oes not control which thread gets the lock when it is released.  
*   This could cause starvation if not used carefully.
******************************************************************************/
	public synchronized void p() 
	{
		while (activeThread != null) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		activeThread = Thread.currentThread();
	}
	
/******************************************************************************
*	Release the lock.
******************************************************************************/
	public synchronized void v() 
	{
        activeThread = null;
        notify();
	}
	
}
