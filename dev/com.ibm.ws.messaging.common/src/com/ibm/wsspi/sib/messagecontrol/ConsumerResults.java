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
 * ---------------  ------ -------- ------------------------------------------
 * SIB0163.mp.1     190907 nyoung   XD Integration.
 * ===========================================================================
 */
package com.ibm.wsspi.sib.messagecontrol;

/**
 * The ConsumerResults interface currently only wraps a ConsumerSet. It was introduced as a 
 * potential future container for additional items, such as application editioning support.
 * <p>
 * ConsumerResults are implemented by SIB. Instances are created by XD calling the 
 * createConsumerResults method on a MessagingEngineControl object.
 *
 */
public interface ConsumerResults
{
  /**
   * Retrieve the ConsumerSet wrapped by the ConsumerResults.
   * 
   * @return consumerSet
   */
  public ConsumerSet getConsumerSet();

}
