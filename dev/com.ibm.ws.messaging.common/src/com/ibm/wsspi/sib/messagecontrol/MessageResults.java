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
 * MessageResults contain a classification identifier, that associates the message with 
 * an XD service policy. XD returns a MessageResults object in response to an
 * analyseMessage() call against a MessageController.
 * <p>
 * MessageResults are implemented by SIB. Instances are created by XD calling the 
 * createMessageResults method on a MessagingEngineControl object.
 */
public interface MessageResults
{
  /**
   * Retrieve the classification associated with these MessageResults.
   * 
   * @return the classification
   */
  public String getClassification();
}
