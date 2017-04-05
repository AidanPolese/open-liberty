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
 * Flows specify the weighting associated with a particular classification.
 * <p>
 * The classification string should exactly match the classification identifier 
 * string returned by the analyseMessage() methods supported by the MessageController
 * interface.
 * <p>
 * The weighting value is a non-negative value which is used to weight messages of the 
 * specified classification with respect to other message classifications listed in the 
 * Weighting array (it is not necessary for all weighting values in the array to add up 
 * to 100). The set of weightings will result in assigning each message classification a 
 * probability of dispatch.
 * <p>
 * Flow objects are implemented by SIB. Instances are created by XD calling the 
 * createFlow method on a MessagingEngineControl object.
 */
public interface Flow
{
  /**
   * Retrieve the classification specified for this flow.
   * 
   * @return the classification
   */
  public String getClassification();

  /**
   * Retrieve the weighting associated with this flow.
   * 
   * @return the weighting
   */
  public int getWeighting();
}
