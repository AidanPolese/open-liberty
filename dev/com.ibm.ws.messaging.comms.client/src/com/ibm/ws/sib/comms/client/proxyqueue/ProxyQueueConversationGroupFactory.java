/*
 * @start_prolog@
 * Version: @(#) 1.8 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/ProxyQueueConversationGroupFactory.java, SIB.comms, WASX.SIB, uu1215.01 05/02/04 07:51:04 [4/12/12 22:14:05]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-I63, 5724-H88, 5655-N01, 5733-W60          (C) Copyright IBM Corp. 2004, 2005
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
 * Creation        030612 prestona Original
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client.proxyqueue;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.client.proxyqueue.impl.ProxyQueueConversationGroupFactoryImpl;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * A factory for proxy queue conversation groups.
 * @see com.ibm.ws.sib.comms.client.proxyqueue.ProxyQueueConversationGroup
 */
public abstract class ProxyQueueConversationGroupFactory
{
   private static final TraceComponent tc = SibTr.register(ProxyQueueConversationGroupFactory.class, CommsConstants.MSG_GROUP, CommsConstants.MSG_BUNDLE);

   private static ProxyQueueConversationGroupFactory instance = null;
   
   static
   {
      if (tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#) SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/ProxyQueueConversationGroupFactory.java, SIB.comms, WASX.SIB, uu1215.01 1.8");
      instance = new ProxyQueueConversationGroupFactoryImpl();
   }
   
   public static ProxyQueueConversationGroupFactory getRef()
   {
      return instance;
   }
   
   /**
    * Creates a new conversation group for the specified conversation.
    * @param conversation The conversation to create the group for.
    * @return ProxyQueueConversationGroup The group created.
    * @throws IllegalArgumentException Thrown if an group already
    * exists for the specified conversation.
    */
   public abstract ProxyQueueConversationGroup create(Conversation conversation)
   throws IllegalArgumentException;

}
