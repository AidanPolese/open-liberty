/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client/src/com/ibm/ws/sib/jfapchannel/ConnectionInterface.java, SIB.comms, WASX.SIB, uu1215.01 07/08/13 10:25:10 [4/12/12 22:18:15]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2007
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
 * PK48027         070720 ajw      Creation
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel;

public interface ConnectionInterface
{
  /*
   * Actually invalidates this connection.  For every day operation this should
   * never be called.  When invoked, the implementation should:
   * <ul>
   * <li>Attempt to notify it's peer (depending on the argument)</li>
   * <li>Purge the connection from any tracker with which it is
   *     registered.</li>
   * <li>Wake up any outstanding exchanges with an exception.</li>
   * <li>Send an exception to each conversation receive listener.</li>
   * <li>Mark all the conversations that use the connection as
   *     invalid.</li>
   * <li>Close the underlying physical socket.</li>
   * </ul>
   * @param notifyPeer When set to true, an attempt is made to notify
   * our peer that we are about to close the socket.
   * @param throwable The exception to link to the "JFapConnectionBrokenException"
   * passed to outstanding exchanges and conversation receive listeners.
   * @param debugReason The reason for the invalidate to be called
   */
  void invalidate(boolean notifyPeer, Throwable throwable, String debugReason);
  
  /*
   * This method will just check that the state of the connection is not in 
   * open state. Any other state is defined as closed by this method.
   */
  boolean isClosed();
}
