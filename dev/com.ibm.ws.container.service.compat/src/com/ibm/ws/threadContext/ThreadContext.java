// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.5 SERV1/ws/code/utils/src/com/ibm/ws/threadContext/ThreadContext.java, WAS.utils, WAS80.SERV1, h1116.09 5/20/10 09:51:15
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997 - 2010
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  ThreadContext.java
//
// Source File Description:
//
//     Interface for extending ThreadLocal to be a stack.  
//
// Change Activity:
//
// Reason    Version    Date     Userid    Change Description
// --------- ---------  -------- --------- -----------------------------------------
// d133207.2 ASV50      20020613 kjlaw    : performance improvement.
// 206479    WASX       20040601 cheng1   : provide getContextIndex() method 
// d646139.1 WAS80      20100519 bkail    : Generify
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.threadContext;

public interface ThreadContext<T> {

    /**
     * beginContext returns the Object currently associated with the thread.
     * It associates the new Object passed parameter with the thread.
     */
    T beginContext(T object);

    /**
     * endContext disassociates the object currently associated with the thread
     * and returns it.
     */
    T endContext();

    /**
     * getContext returns the Object currently associated thread
     */
    T getContext();

    /**
     * getContext returns the index of the Object currently associated thread
     */
    int getContextIndex();

}
