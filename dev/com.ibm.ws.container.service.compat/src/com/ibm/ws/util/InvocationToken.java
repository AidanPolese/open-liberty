// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.3 SERV1/ws/code/ecutils/src/com/ibm/ws/util/InvocationToken.java, WAS.ejbcontainer, WASX.SERV1, aa1225.01 4/28/05 10:09:34 
//
// IBM Confidential OCO Source Material
// 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  InvocationToken.java
//
// Source File Description:
//
//     An abstract token which represents an instance of a request 
//     upon a J2EE component. 
//
// Change Activity:
//
// Reason     Version   Date     Userid    Change Description
// ---------  --------- -------- --------- -----------------------------------------
// d194342.1.1 ASV60    20040513 kjlaw     : New copyright/prologue 
// d206479     WASX     20040603 cheng1    : Design changed
// d270126     WAS602   20050428 hthomann  : Added ASYNCHBEANS type of 3
// ---------  --------- -------- --------- -----------------------------------------

package com.ibm.ws.util;

import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.MethodMetaData;

/**
 * InvocationToken is an interface which represents an instance
 * of a "request" upon a J2EE component. For example, for a servlet, the
 * token represents the HTTP request that causes the servlet to be executed.
 * For EJB, the token represents the method invocation on a EJB. The token is
 * only valid while the request instance is on the "stack"; containers may
 * resuse a InvocationToken object on subsequent frame stacks. Users of the
 * InvocationToken should relinquish them when the request that generated
 * the Token completes. With this interface, users can determine if they're
 * in the same scope by comparing tokens.
 */
public interface InvocationToken {
    /**
     * Constants for return value of the getContainerType method in this interface.
     */
    public static final int WEB_CONTAINER = 1;
    public static final int EJB_CONTAINER = 2;
    public static final int ASYNCHBEANS = 3;

    /**
     * Called to determine if the container that returned the token was WEB or EJB.
     * 
     * @return one of the return value constants defined in this interface that
     *         indicates the container that is managing the request represented
     *         by this token.
     * 
     */
    public int getContainerType();

    /**
     * Return ComponentMetaData associated with the token.
     * 
     * @return a non-null reference to the ComponentMetaData associated with the token.
     * 
     * 
     */
    public ComponentMetaData getComponentMetaData();

    /**
     * Return MethodtMetaData associated with the token.
     * 
     * @return the MethodMetaData associated with the token or null. A null reference
     *         is returned if the container type does not support MethodMetaData.
     * 
     */
    public MethodMetaData getMethodMetaData();

    /**
     * Called by non-container components to dynamically enlist for a callback after
     * the execution of the current J2EE component. The callback will occur whether
     * the J2EE component exits successfully or due to an exception. If there is no
     * current active J2EE component (for example, on a new thread spun on the server
     * (!), then the container will throw an IllegalStateException.
     * 
     * @param callback is a non-null instance of InvocationCallback implemented by
     *            the caller. The container will call back upon this reference upon
     *            exit from the current J2EE component.
     * 
     * @param cookie is an object that will be passed back by the container when
     *            the call to postInvoke is made.
     * 
     */
    public void enlistInvocationCallback(InvocationCallback callback, Object cookie);

}
