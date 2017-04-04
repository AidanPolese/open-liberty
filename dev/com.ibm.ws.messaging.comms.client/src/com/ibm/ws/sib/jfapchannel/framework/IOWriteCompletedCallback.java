/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/IOWriteCompletedCallback.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:24 [4/12/12 22:14:18]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2003, 2006 
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 336594          060109 prestona JFAP channel for thin client
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.framework;

import java.io.IOException;

/**
 * A callback used to provide notification that a write request has completed.
 * Typically, a user of this package will use the write method of a
 * IOWriteRequestContext implementation to schedule a write request.  One of
 * the arguments to this method is an implementation of this interface which 
 * the user must provide.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext 
 */
public interface IOWriteCompletedCallback
{
   /**
    * Notification that the write operation completed successfully
    * (that is to say that no errors were detected - not that the data has
    * been received by the peer).
    * @param networkConnection the network connection for which the write
    * operation was requested.
    * @param writeRequestContext the write context which was used to
    * request the write operation.
    */
   public void complete(NetworkConnection networkConnection, 
                        IOWriteRequestContext writeRequestContext);
   
   /**
    * Notification that the write operation completed - but not
    * successfully.
    * @param networkConnection the network connection for which the
    * write operation was requested.
    * @param writeRequestContext the write context which was used to
    * request the write operation.
    * @param ioException an exception which provides information about
    * why the write operation did not complete successfully.
    */
   public void error(NetworkConnection networkConnection,
                     IOWriteRequestContext writeRequestContext,  
                     IOException ioException);
}
