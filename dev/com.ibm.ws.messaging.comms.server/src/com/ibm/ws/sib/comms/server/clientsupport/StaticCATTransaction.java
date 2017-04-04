/*
 * @start_prolog@
 * Version: @(#) 1.50 SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATTransaction.java, SIB.comms, WASX.SIB, aa1225.01 09/04/01 07:23:46 [7/2/12 05:59:01]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2004, 2007
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
 * Creation        030604 clarkep  Original
 * d170527         030625 mattheg  Tidy and change to SibTr
 * d170639         030627 mattheg  NLS all the messages
 * f169897.2       030708 mattheg  Convert to Core API 0.6
 * f171400         030711 mattheg  Implement Core API 0.6
 * f172297         030724 mattheg  Complete Core API 0.6 implementation
 * F174602         030820 prestona Switch to using SICommsException
 * f174317         030827 mattheg  Add local transaction support (complete overhaul)
 * F183828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * f181007         031211 mattheg  Add boolean 'exchange' flag
 * d186970         040116 mattheg  Overhaul the way we send exceptions to client
 * d187347         040119 mattheg  Send back the first exception when transaction marked as error
 * F188491         040128 prestona Migrate to M6 CF + TCP Channel
 * d175222         040219 mattheg  Ensure SICommsException is reported correctly and not sent to client
 * d192293         040308 mattheg  NLS file changes
 * D202636         040511 mattheg  Modify transaction creation semantics
 * D217372         040719 mattheg  Move JFap constants -> JFapChannelConstants (not change-flagged)
 * D218040         040726 mattheg  Ensure that when a transaction fails to be created the error is logged
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D199177         040816 mattheg  JavaDoc
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D254870         050214 mattheg  Optimize connection close
 * D275383         050516 mattheg  Ensure linked exception is passed back on error
 * D297060         050821 prestona IdToTransactionTable memory leak
 * D307265         050922 prestona Support for optimized transactions
 * D313337.1       051027 prestona overload createUncoordinatedTransaction method
 * D321471         051109 prestona Optimized transaction related problems
 * D341593         060130 mattheg  Remove un-used locals
 * D350111.1       060302 mattheg  Move to FAP 5
 * D354565         060320 prestona ClassCastException thrown during failover
 * D377648         060719 mattheg  Use CommsByteBuffer
 * D378229         060808 prestona Avoid synchronizing on ME-ME send()
 * D441183         072307 mleming  Don't FFDC when calling terminated ME
 * D441898         070730 mleming  Fix failures found by unit tests
 * 471664          071003 vaughton Findbugs tidy up
 * PK83641         310309 ajw      reset LinkLevelState when returning from pool;
 * ============================================================================
 */
package com.ibm.ws.sib.comms.server.clientsupport;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.comms.common.CommsByteBuffer;
import com.ibm.ws.sib.comms.common.CommsByteBufferPool;
import com.ibm.ws.sib.comms.server.ConversationState;
import com.ibm.ws.sib.comms.server.IdToTransactionTable;
import com.ibm.ws.sib.comms.server.ServerLinkLevelState;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.Conversation.ThrottlingPolicy;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.core.SIUncoordinatedTransaction;
import com.ibm.wsspi.sib.core.exception.SIRollbackException;

/**
 * This class takes care of all operations relating to transactions.
 *
 * @author Gareth Matthews
 */
public class StaticCATTransaction
{
   /** Class name for FFDC's */
   private static String CLASS_NAME = StaticCATTransaction.class.getName();

   /** The buffer pool manager */
   private static CommsByteBufferPool poolManager = CommsByteBufferPool.getInstance();

   /** The trace */
   private static final TraceComponent tc = SibTr.register(StaticCATTransaction.class,
                                                           CommsConstants.MSG_GROUP,
                                                           CommsConstants.MSG_BUNDLE);

   /** The NLS stuff */
   private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);

   /** Log class info on static load */
   static
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source info: @(#)SIB/ws/code/sib.comms.server.impl/src/com/ibm/ws/sib/comms/server/clientsupport/StaticCATTransaction.java, SIB.comms, WASX.SIB, aa1225.01 1.50");
   }

   /**
    * Create an SIUncoordinatedTransaction.
    *
    * Mandatory Fields:
    * BIT16    SICoreConnectionObjectId
    * BIT32    Client transaction Id
    *
    * @param request
    * @param conversation
    * @param requestNumber
    * @param allocatedFromBufferPool
    * @param partOfExchange
    */
   static void rcvCreateUCTransaction(CommsByteBuffer request, Conversation conversation,
                                      int requestNumber, boolean allocatedFromBufferPool,
                                      boolean partOfExchange)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "rcvCreateUCTransaction",
                                           new Object[]
                                           {
                                              request,
                                              conversation,
                                              ""+requestNumber,
                                              ""+allocatedFromBufferPool
                                            });

      ConversationState convState = (ConversationState) conversation.getAttachment();

      short connectionObjectId = request.getShort();  // BIT16 ConnectionObjectId
      int clientTransactionId = request.getInt();     // BIT32 Client transaction Id

      // By default subordinates are allowed - however, a FAP version 5 client may specifically
      // request that a local transaction does not support them.
      // see the ConnectionProxy.createUncoordinateTransaction(boolean) method javadoc comment
      // for more details.
      boolean allowSubordinates = true;
      if (conversation.getHandshakeProperties().getFapLevel() >= JFapChannelConstants.FAP_VERSION_5)
      {
         allowSubordinates = (request.get() & 0x01) == 0x01;
      }

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      {
         SibTr.debug(tc, "SICoreConnection Id:", connectionObjectId);
         SibTr.debug(tc, "Client transaction Id:", clientTransactionId);
      }

      CATConnection catConn = (CATConnection) convState.getObject(connectionObjectId);
      SICoreConnection connection = catConn.getSICoreConnection();

      ServerLinkLevelState linkState = (ServerLinkLevelState) conversation.getLinkLevelAttachment();
      SIUncoordinatedTransaction ucTran = null;
      try
      {
         ucTran = connection.createUncoordinatedTransaction(allowSubordinates);

      }
      // Don't bother with all the different types seeing as we don't throw them back
      catch (SIException e)
      {
         //No FFDC code needed
         //Only FFDC if we haven't received a meTerminated event.
         if(!convState.hasMETerminated())
         {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvCreateUCTransaction",
                                        CommsConstants.STATICCATTRANSACTION_CREATE_01);
         }

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Failed to create the transaction", e);

         // We have to mark this transaction as error now - so as to indicate to everyone that
         // it should not be used
         linkState.getTransactionTable().addLocalTran(clientTransactionId, conversation.getId(), IdToTransactionTable.INVALID_TRANSACTION);
         linkState.getTransactionTable().markAsRollbackOnly(clientTransactionId, e);
      }

      // If transaction creation succeeded then add it to the link level state table of
      // transactions.
      if (ucTran != null)
      {
         linkState.getTransactionTable().addLocalTran(clientTransactionId,
                                                      conversation.getId(),
                                                      ucTran);
      }

      request.release(allocatedFromBufferPool);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "rcvCreateUCTransaction");
   }

   /**
    * Commit the transaction provided by the client.
    *
    * This method uses only required fields.  All mandatory fields have a fixed order and size.
    *
    * Mandatory Fields:
    * BIT16    SIMPConnectionObjectId
    * BIT16    SIMPTransactionObjectId
    *
    * @param request
    * @param conversation
    * @param requestNumber
    * @param allocatedFromBufferPool
    * @param partOfExchange
    */
   static void rcvCommitTransaction(CommsByteBuffer request, Conversation conversation,
                                    int requestNumber, boolean allocatedFromBufferPool,
                                    boolean partOfExchange)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "rcvCommitTransaction",
                                           new Object[]
                                           {
                                              request,
                                              conversation,
                                              ""+requestNumber,
                                              ""+allocatedFromBufferPool
                                            });

      short connectionObjectId = request.getShort();  // BIT16 ConnectionObjectId
      int clientTransactionId = request.getInt();     // BIT32 Client transaction Id

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      {
         SibTr.debug(tc, "SICoreConnection Id:", connectionObjectId);
         SibTr.debug(tc, "Client transaction Id:", clientTransactionId);
      }

      try
      {
         // Get the transaction out of the table
         ServerLinkLevelState linkState = (ServerLinkLevelState) conversation.getLinkLevelAttachment();
         SITransaction tran = linkState.getTransactionTable().get(clientTransactionId);

         // An earlier failure may mean that this transaction is rollback only
         boolean tranIsRollbackOnly =
            (tran == IdToTransactionTable.INVALID_TRANSACTION) ||
            linkState.getTransactionTable().isLocalTransactionRollbackOnly(clientTransactionId);

         if (tranIsRollbackOnly)
         {
            Throwable t = linkState.getTransactionTable().getExceptionForRollbackOnlyLocalTransaction(clientTransactionId);

            // At this point here the transaction will either be committed or rolled back.
            // Therefore, any subsequent operations will be invalid and will be thrown out
            // by the client. As such, remove it from the table.
            linkState.getTransactionTable().removeLocalTransaction(clientTransactionId);

            SIUncoordinatedTransaction siTran = (SIUncoordinatedTransaction)tran;

            //We don't want to rollback if this is IdToTransactionTable.INVALID_TRANSACTION
            if (siTran != null && siTran != IdToTransactionTable.INVALID_TRANSACTION) siTran.rollback();

            // And respond with an error
            SIRollbackException r = new SIRollbackException(
               nls.getFormattedMessage("TRANSACTION_MARKED_AS_ERROR_SICO2008",
                                       new Object[]{ t },
                                       null),
               t
            );

            StaticCATHelper.sendExceptionToClient(r,
                                                  null,
                                                  conversation, requestNumber);
         }
         else
         {
            // At this point here the transaction will either be committed or rolled back.
            // Therefore, any subsequent operations will be invalid and will be thrown out
            // by the client. As such, remove it from the table.
            linkState.getTransactionTable().removeLocalTransaction(clientTransactionId);

            // Otherwise commit - note this cannot be null here, otherwise we would be marked as error
            SIUncoordinatedTransaction siTran = (SIUncoordinatedTransaction)tran;
            siTran.commit();

            // Respond to the client
            try
            {
               conversation.send(poolManager.allocate(),
                                 JFapChannelConstants.SEG_COMMIT_TRANSACTION_R,
                                 requestNumber,
                                 JFapChannelConstants.PRIORITY_MEDIUM,
                                 true,
                                 ThrottlingPolicy.BLOCK_THREAD,
                                 null);
            }
            catch (SIException e)
            {
               FFDCFilter.processException(e,
                                           CLASS_NAME + ".rcvCommitTransaction",
                                           CommsConstants.STATICCATTRANSACTION_COMMIT_01);

               if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

               SibTr.error(tc, "COMMUNICATION_ERROR_SICO2026", e);
            }
         }
      }
      catch (SIException e)
      {
         //No FFDC code needed
         //Only FFDC if we haven't received a meTerminated event.
         if(!((ConversationState)conversation.getAttachment()).hasMETerminated())
         {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvCommitTransaction",
                                        CommsConstants.STATICCATTRANSACTION_COMMIT_02);
         }

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

         StaticCATHelper.sendExceptionToClient(e,
                                               CommsConstants.STATICCATTRANSACTION_COMMIT_02,
                                               conversation, requestNumber);
      }

      request.release(allocatedFromBufferPool);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "rcvCommitTransaction");
   }

   /**
    * Rollback the transaction provided by the client.
    *
    * Mandatory Fields:
    * BIT16    SICoreConnectionObjectId
    * BIT32    Client transaction Id
    *
    * @param request
    * @param conversation
    * @param requestNumber
    * @param allocatedFromBufferPool
    * @param partOfExchange
    */
   static void rcvRollbackTransaction(CommsByteBuffer request, Conversation conversation,
                                      int requestNumber, boolean allocatedFromBufferPool,
                                      boolean partOfExchange)
   {
      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(tc, "rcvRollbackTransaction",
                                           new Object[]
                                           {
                                              request,
                                              conversation,
                                              ""+requestNumber,
                                              ""+allocatedFromBufferPool
                                            });

      short connectionObjectId = request.getShort();  // BIT16 ConnectionObjectId
      int clientTransactionId = request.getInt();     // BIT32 Client transaction Id

      if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
      {
         SibTr.debug(tc, "SICoreConnection Id:", connectionObjectId);
         SibTr.debug(tc, "Client transaction Id:", clientTransactionId);
      }

      try
      {
         // Get the transaction out of the table
         ServerLinkLevelState linkState = (ServerLinkLevelState) conversation.getLinkLevelAttachment();
         SITransaction tran = linkState.getTransactionTable().get(clientTransactionId);

         if (tran == IdToTransactionTable.INVALID_TRANSACTION)
         {
            Throwable e = linkState.getTransactionTable().getExceptionForRollbackOnlyLocalTransaction(clientTransactionId);

            //Remove transaction from the table
            linkState.getTransactionTable().removeLocalTransaction(clientTransactionId);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Looks like the transaction failed to create", e);
         }
         else
         {
            // At this point here the transaction will only be rolled back.
            // Therefore, any subsequent operations will be invalid and will be thrown out
            // by the client. As such, remove it from the table.
            linkState.getTransactionTable().removeLocalTransaction(clientTransactionId);

            SIUncoordinatedTransaction siTran = (SIUncoordinatedTransaction)tran;
            siTran.rollback();
         }

         try
         {
            conversation.send(poolManager.allocate(),
                              JFapChannelConstants.SEG_ROLLBACK_TRANSACTION_R,
                              requestNumber,
                              JFapChannelConstants.PRIORITY_MEDIUM,
                              true,
                              ThrottlingPolicy.BLOCK_THREAD,
                              null);
         }
         catch (SIException e)
         {
            FFDCFilter.processException(e,
                                        CLASS_NAME + ".rcvRollbackTransaction",
                                        CommsConstants.STATICCATTRANSACTION_ROLLBACK_01);

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

            SibTr.error(tc, "COMMUNICATION_ERROR_SICO2026", e);
         }
      }
      catch (SIException e)
      {
         //No FFDC code needed
         //Only FFDC if we haven't received a meTerminated event.
         if(!((ConversationState)conversation.getAttachment()).hasMETerminated())
         {
            FFDCFilter.processException(e,
                                       CLASS_NAME + ".rcvRollbackTransaction",
                                       CommsConstants.STATICCATTRANSACTION_ROLLBACK_02);
         }

         if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, e.getMessage(), e);

         StaticCATHelper.sendExceptionToClient(e,
                                               CommsConstants.STATICCATTRANSACTION_ROLLBACK_02,
                                               conversation, requestNumber);
      }

      request.release(allocatedFromBufferPool);

      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "rcvRollbackTransaction");
   }
}
