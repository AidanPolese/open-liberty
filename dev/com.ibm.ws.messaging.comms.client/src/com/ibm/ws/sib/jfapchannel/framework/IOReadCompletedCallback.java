/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/framework/IOReadCompletedCallback.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:04:21 [4/12/12 22:14:18]
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
 * A callback interface used to provide completion notification for requests to 
 * read data from the network.  Typically a user of this package would obtain
 * a IOReadRequestContext implementation (via a IOConnectionContext, via a
 * NetworkConnection) and invoke the read method supplying an instance of this
 * interface.  When the read operation completes the appropriate notification 
 * event is delivered to the implementation of this interface.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.IOReadRequestContext
 */
public interface IOReadCompletedCallback
{
   /**
    * Notification that the network read operation completed succesfully. 
    * @param networkConnection the network connection that data was read
    * from.
    * @param readContext the context object used to request the read
    * operation.
    */
   public void complete(NetworkConnection networkConnection, 
                        IOReadRequestContext readContext);
   
   /**
    * Notification that the network read operation completed but was
    * not successful.
    * @param networkConnection the network connection for which the
    * read operation was attempted.
    * @param readContext the context object used to request the read
    * operation.
    * @param ioException an exception which provides information 
    * about why the operation did not complete successfully.
    */
   public void error(NetworkConnection networkConnection, 
                     IOReadRequestContext readContext,
                     IOException ioException);
}
