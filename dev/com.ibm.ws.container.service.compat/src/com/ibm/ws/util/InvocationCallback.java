// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.1 SERV1/ws/code/ecutils/src/com/ibm/ws/util/InvocationCallback.java, WAS.ejbcontainer, WASX.SERV1, aa1225.01 5/14/04 09:56:28 
//
// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  InvocationCallback.java
//
// Source File Description:
//
//   short description.  
//
// Change Activity:
//
// Reason       Version   Date      Userid    Change Description
//---------    ---------  --------  --------- -----------------------------------------
// d194342.1.1  ASV60     20040513  kjlaw     : New copyright/prologue 
//---------    ---------  --------  --------- -----------------------------------------

package com.ibm.ws.util;

/**
 * InvocationCallback is an interface that allows non-container components
 * to dynamically enlist for a callback during the execution of a J2EE component
 * and to be called back during various points after the enlistment of the callback.
 * The enlistment is done by a websphere cmvc component looking up one of the container
 * services and using the enlistInvocationCallback method on the container interface
 */
public interface InvocationCallback {
    /**
     * Called by the container after executing a component or method on a
     * component. In the EJB container, the callback occurs after the
     * transaction has completed. Note, the implementation of this callback
     * must should not throw any Throwable objects. If it does, FFDC is logged
     * and the Throwable is thrown away (e.g nothing is thrown to client).
     * 
     * @param cookie is the same object reference that was passed to the
     *            Container.enlistInvocationCallback method.
     */
    public void postInvoke(Object callbackCookie);

}
