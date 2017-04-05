/*
 * @start_prolog@
 * Version: @(#) 1.12 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/impl/ProxyQueueConversationGroupFactoryImpl.java, SIB.comms, WASX.SIB, uu1215.01 07/07/03 06:33:35 [4/12/12 22:14:07]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2007
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
 * Creation        030702 prestona Original
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D434395         070424 prestona FINBUGS: fix findbug warnings in sib.comms.client.impl
 * ============================================================================
 */

package com.ibm.ws.sib.comms.client.proxyqueue.impl;

import java.util.HashMap;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.client.proxyqueue.ProxyQueueConversationGroup;
import com.ibm.ws.sib.comms.client.proxyqueue.ProxyQueueConversationGroupFactory;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * Implementation of the proxy queue conversation group factory.
 * A factory for proxy queue conversation groups.
 */
public class ProxyQueueConversationGroupFactoryImpl extends ProxyQueueConversationGroupFactory
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = ProxyQueueConversationGroupFactoryImpl.class.getName();
   
   /** Trace */
   private static final TraceComponent tc = SibTr.register(ProxyQueueConversationGroupFactory.class, 
                                                           CommsConstants.MSG_GROUP, 
                                                           CommsConstants.MSG_BUNDLE);
                                                           
   /** NLS handle */
   private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);

   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#) SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/impl/ProxyQueueConversationGroupFactoryImpl.java, SIB.comms, WASX.SIB, uu1215.01 1.12");
   }
   
   // Maps conversations to proxy queue groups.
   private HashMap<Conversation, ProxyQueueConversationGroup> convToGroupMap;
   
   /** Constructor */
   public ProxyQueueConversationGroupFactoryImpl()
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "<init>");
      convToGroupMap = new HashMap<Conversation, ProxyQueueConversationGroup>();
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "<init>");
   }
   
   /**
    * Creates a proxy queue conversation group.
    * @see com.ibm.ws.sib.comms.client.proxyqueue.ProxyQueueConversationGroupFactory#create(com.ibm.ws.sib.jfapchannel.Conversation)
    */
   public synchronized ProxyQueueConversationGroup create(Conversation conversation)
      throws IllegalArgumentException
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "create", conversation);
      
      if (convToGroupMap.containsKey(conversation))
      {
         // A proxy queue conversation group is associated one-to-one with a conversation. In this
         // case someone has tried to create 2 for the same conversation.
         SIErrorException e = new SIErrorException(
            nls.getFormattedMessage("PQGROUP_ALREADY_CREATED_SICO1054", null, null)
         );
            
         FFDCFilter.processException(e, CLASS_NAME + ".create",
                                     CommsConstants.PQCONVGRPFACTIMPL_CREATE_01, this);
         
         throw e;
      }
      
      ProxyQueueConversationGroup group =
         new ProxyQueueConversationGroupImpl(conversation, this);
      
      convToGroupMap.put(conversation, group);
      
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "create", group);
      return group;
   }

   /**
    * Called by a proxy queue conversation group to notify this
    * factory of its closure.
    * @param conversation
    * @param group
    */
   protected synchronized void groupCloseNotification(Conversation conversation, 
                                                      ProxyQueueConversationGroup group)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "groupCloseNotification", new Object[] {conversation, group});
      if (convToGroupMap.remove(conversation) == null)
      {
         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "group unknown!");
      }
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "groupCloseNotification");
   }
}
