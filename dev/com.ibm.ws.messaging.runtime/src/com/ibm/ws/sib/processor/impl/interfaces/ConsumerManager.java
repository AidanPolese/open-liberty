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
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0002.mp.1     210605 tevans   PEV Prototype
 * SIB0002.mp.3     270605 tpm      RMQ Browser Session support
 * 355323           220306 tevans   RMQSessionDroppedException handling
 * 368547.1         010606 tevans   Missing exceptions
 * 409469           281206 tevans   Refactor LME
 * SIB0163.mp.1     190907 nyoung   XD Integration.
 * SIB0113a.mp.12   110108 cwilkin  Message Gathering
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import java.util.List;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.processor.impl.BaseDestinationHandler;
import com.ibm.ws.sib.processor.impl.BrowserSessionImpl;
import com.ibm.ws.sib.processor.impl.JSConsumerSet;
import com.ibm.ws.sib.processor.impl.MessageProcessor;
import com.ibm.ws.sib.processor.impl.OrderingContextImpl;
import com.ibm.ws.sib.transactions.TransactionCommon;
import com.ibm.ws.sib.utils.SIBUuid12;
import com.ibm.wsspi.sib.core.SelectionCriteria;
import com.ibm.wsspi.sib.core.exception.SIDestinationLockedException;
import com.ibm.wsspi.sib.core.exception.SIDiscriminatorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SISelectorSyntaxException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;

public interface ConsumerManager extends Browsable
{
  /**
   * Attach a new ConsumerPoint to this ConsumerManager.
   * 
   * @param consumerPoint The consumer point being attached
   * @param selector  The Filter that the consumer has specified
   * @param discriminator  The discriminator that the consumer has specified
   * @param connectionUuid  The connections UUID
   * @param readAhead  If the consumer can read ahead
   * @param forwardScanning  If the consumer is forward scanning
   * @param consumerSet  If XD classification is enabled, this specifies the ConsumerSet that
   * this consumer belongs to.
   * @return The ConsumerKey object which was created for this consumer point.
   * being deleted
   * @throws SISessionDroppedException 
   */
  public ConsumerKey attachConsumerPoint(
      ConsumerPoint consumerPoint,
      SelectionCriteria criteria,
      SIBUuid12 connectionUuid,
      boolean readAhead,
      boolean forwardScanning,
      JSConsumerSet consumerSet)
    throws SINotPossibleInCurrentConfigurationException, SIDestinationLockedException, SISelectorSyntaxException, SIDiscriminatorSyntaxException, SIResourceException, SISessionDroppedException;
  
  /**
   * Detach a consumer point from this Consumer Manager.
   * 
   * @param consumerKey The ConsumerKey object of the consumer point
   * being detached
   */
  public void detachConsumerPoint(ConsumerKey consumerKey)
    throws SIResourceException, SINotPossibleInCurrentConfigurationException;
    
  
  /**
   * Attach a BrowserSession to this ConsumerManager
   * @param browserSession  The browser session to attach
   */
  public void attachBrowser(BrowserSessionImpl browserSession)
    throws SINotPossibleInCurrentConfigurationException, SIResourceException;
    
  /**
   * Detach a BrowserSession from this ConsumerManager
   * @param browserSession  The browser session to detach
   */
  public void detachBrowser(BrowserSessionImpl browserSession);    
    
  /**
   * Get the number of consumers on this ConsumerDispatcher
   * <p>
   * Feature 166832.23
   * 
   * @return number of consumers.
   */
  public int getConsumerCount();
  
  /**
   * Used by the unit tests to return the list of consumer points
   * This list is cloned to stop illegal access to the ConsumerPoints
   * controlled by this ConsumerDispatcher
   * @return
   */
  public List getConsumerPoints();
  
  /**
   * 
   */
  public void setReadyForUse();

  /**
   * @return
   */
  public boolean isLocked();

  /**
   * @return
   */
  public BaseDestinationHandler getDestination();

  /**
   * @return
   */
  public MessageProcessor getMessageProcessor();

  /**
   * @param consumerKey
   * @param orderingGroup
   * @return
   * @throws SIResourceException
   * @throws SISessionDroppedException 
   */
  public ConsumerKeyGroup joinKeyGroup(ConsumerKey consumerKey, OrderingContextImpl orderingGroup) throws SIResourceException, SISessionDroppedException;

  public boolean isNewTransactionAllowed(TransactionCommon transaction);
}
