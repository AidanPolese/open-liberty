/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.Transaction;

/**
 * An interface that allows the cdi TransactionContext type to register itself
 * with TransactionImpl, for notifying when the TransactionImpl reaches the
 * afterComplete stage. At this point, TransactionImpl will call the destroy()
 * method.
 *
 * This is a slightly roundabout way to avoid having a circular dependency on
 * the project containing TransactionContext.
 */
public interface TransactionScopeDestroyer {
    public void destroy();
}
