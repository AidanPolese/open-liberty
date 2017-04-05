/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * SIB0002.tran.2   050805 tevans   Reset Change history - previous WAS602.SIB
 * SIB0002.tran.2   050805 tevans   New Transactions interfaces
 * SIB0002.mp.15    170805 tevans   Transactional PEV Producers and Consumers
 * SIB0002.mp.16    180805 tpm      hasRMQResources flag
 * SIB0002.mp.20    300805 tpm      unit test cleanup
 * 277326.1         051005 tpm      XAResourceFactory refactor
 * 318614.1         071105 tevans   Support remote transaction recovery from PEV
 * 334076           211205 tevans   Fix subordinate transaction error checking
 * 306998.22        050106 gatfora  Trace performance improvements
 * 364681           260406 tevans   Memory leak in PEV (transaction callbacks)
 * 369973.2         060706 tevans   Synchronized access to transaction common
 * PK40556          060307 tevans   Make createLocalTransactionWithSubordinates private
 * =================================================================================
 */

package com.ibm.ws.sib.processor.impl.store;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.msgstore.MessageStore;
import com.ibm.ws.sib.msgstore.transactions.ExternalAutoCommitTransaction;
import com.ibm.ws.sib.msgstore.transactions.Transaction;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.processor.impl.MessageProcessor;
import com.ibm.ws.sib.transactions.LocalTransaction;
import com.ibm.ws.sib.transactions.TransactionCommon;
import com.ibm.ws.sib.transactions.TransactionFactory;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.SIXAResource;

public final class SIMPTransactionManager
{
  private MessageStore msgStore;

  private static TraceComponent tc =
    SibTr.register(
      SIMPTransactionManager.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);
  
  /**
   * NLS for component
   */
  private static final TraceNLS nls = TraceNLS.getTraceNLS(SIMPConstants.RESOURCE_BUNDLE);
  
  private TransactionFactory transactionFactory;

  private MessageProcessor messageProcessor;

  private String localRMName;
  
  /** Constructor of the Transaction Manager object 
   */
  public SIMPTransactionManager(MessageProcessor messageProcessor, MessageStore msgStore)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "SIMPTransactionManager", new Object[]{messageProcessor, msgStore});

    this.messageProcessor = messageProcessor;
    this.msgStore = msgStore;
    transactionFactory = msgStore.getTransactionFactory();
    localRMName = "WebSphere PM Resource Manager "+
                  messageProcessor.getMessagingEngineName()+"-"+
                  messageProcessor.getMessagingEngineBus();

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "SIMPTransactionManager", this);
  }

  public String getLocalRMName()
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "getLocalRMName");
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "getLocalRMName", localRMName);
    
    return localRMName;
  }
  
  /** 
   * Creates a local transaction.
   * 
   * @return The uncoordinated transaction
   */
  public LocalTransaction createLocalTransaction(boolean useSingleResourceOnly)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "createLocalTransaction");

    LocalTransaction tran = null;
    
    //Venu Removing the createLocalTransactionWithSubordinates() as it has to happen only
    // in case of PEV resources
    tran = transactionFactory.createLocalTransaction();
    
    
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "createLocalTransaction", tran);

    return tran;
  }
  
  /**
   * Creates a Auto Commit Transaction
   * 
   * @return Transaction  The auto commit transaction object
   */
  public ExternalAutoCommitTransaction createAutoCommitTransaction() 
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "createAutoCommitTransaction");

    ExternalAutoCommitTransaction transaction = 
      transactionFactory.createAutoCommitTransaction();

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "createAutoCommitTransaction", transaction);

    return transaction;
  }

  /**
   * Creates an XA transaction resource
   * 
   * @return a new XA resource
   */
  public SIXAResource createXAResource(boolean useSingleResource)
  {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.entry(tc, "createXAResource", new Boolean(useSingleResource));

    SIXAResource resource = null;

      //get the message store resource
    resource = transactionFactory.createXAResource();
    
      
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
      SibTr.exit(tc, "createXAResource", resource);

    return resource;
  }
  
  public MessageStore getMessageStore()
  {
    return msgStore;
  }
  
  /**
   * @param transactionCommon
   * @return
   * @throws SIResourceException
   */
  public Transaction resolveAndEnlistMsgStoreTransaction(TransactionCommon transactionCommon) throws SIResourceException
  {
	    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
	        SibTr.entry(tc, "resolveAndEnlistMsgStoreTransaction", transactionCommon);
	      
	      Transaction msgStoreTran = null;
	      if(transactionCommon != null)
	      {      
	       
	          msgStoreTran = (Transaction) transactionCommon;
	        
	      }
	      
	      if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
	        SibTr.exit(tc, "resolveAndEnlistMsgStoreTransaction", msgStoreTran);
	      
	      return msgStoreTran;
	    }
}
