/*
 * @start_prolog@
 * Version: @(#) 1.7 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/impl/NonReadAheadSessionProxyQueueImpl.java, SIB.comms, WASX.SIB, uu1215.01 08/04/16 21:36:40 [4/12/12 22:14:07]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2005, 2008
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
 * D249069         050129 prestona Fix proxy queue synchronization
 * 487006          080414 vaughton Refactor, tidyup & fix locking
 * 513500          080417 sibcopyr Automatic update of trace guards 
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client.proxyqueue.impl;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.utils.ras.SibTr;

/*
 * A proxy queue implementation that can be used to deliver messages to a non-read ahead, non-ordered consumer session
 */
public final class NonReadAheadSessionProxyQueueImpl extends AsynchConsumerProxyQueueImpl {
  private static final TraceComponent tc = SibTr.register(NonReadAheadSessionProxyQueueImpl.class, CommsConstants.MSG_GROUP, CommsConstants.MSG_BUNDLE);

  //@start_class_string_prolog@
  public static final String $sccsid = "@(#) 1.7 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/impl/NonReadAheadSessionProxyQueueImpl.java, SIB.comms, WASX.SIB, uu1215.01 08/04/16 21:36:40 [4/12/12 22:14:07]";
  //@end_class_string_prolog@

  static {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source Info: " + $sccsid);
  }

  /*
   * Creates a non-read ahead, non-ordered proxy queue
   * @param group The proxy queue group to which the new proxy queue will belong
   * @param id The proxy queue id to use for the new proxy queue
   * @param conversation The JFAP Conversation that the proxy queue will converse over
   */
  public NonReadAheadSessionProxyQueueImpl (final ProxyQueueConversationGroupImpl group, final short id, final Conversation conversation) {
    super(group, id, conversation);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", "group="+group+", id="+id+", conversation="+conversation);
    setType(ASYNCH);
    setQueue(obtainQueue(ASYNCH, null, null));
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
  }

  /*
   * Constructor for unit test only
   */
  public NonReadAheadSessionProxyQueueImpl (final ProxyQueueConversationGroupImpl group, final short id, final ConversationHelper convHelper) {
    super(group, id, null);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", "group="+group+", id="+id+", convHelper="+convHelper);
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "test form of constructor invoked");
    setType(ASYNCH);
    setConversationHelper(convHelper);
    setQueue(obtainQueue(ASYNCH, null, null));
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
  }
}
