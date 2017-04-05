/*
 * @start_prolog@
 * Version: @(#) 1.9 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/AcceptListener.java, SIB.comms, WASX.SIB, uu1215.01 05/02/04 09:48:00 [4/12/12 22:14:11]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2003, 2005
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel
 * D199145         040812 prestona Fix Javadoc 
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel;

/**
 * A listener which is notified when new incoming conversations are
 * accpeted by a listening socket.  The implementor of this interface should
 * provide logic to deal with the new conversations.
 * <p>
 * By the time this listener is notified, the new conversation will have been
 * established but no initial data flows will have been sent.
 * 
 * @author prestona
 */
public interface AcceptListener
{
   /**
    * Notified when a new conversation is accepted by a listening socket.
    * <strong>Note:</strong> since this code is executed on the thread we
    * use for "receiving" data please don't go and invoked methods on the
    * connection which wait for a reply (e.g. exchange or some forms of
    * send) - you will block the receive thread waiting for itself to do
    * something.  Not a good idea.   
    * @param conversation The new conversation.
    * @return The conversation receive listener to use for asynchronous receive
    *          notification for this whole conversation.
    */
   ConversationReceiveListener acceptConnection(Conversation conversation);
}
