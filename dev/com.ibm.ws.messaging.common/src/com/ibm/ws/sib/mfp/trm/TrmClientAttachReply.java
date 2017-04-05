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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 164548          030502 susana   Original
 * 173084          030730 vaughton Enhance failureReason field to List
 * ============================================================================
 */
package com.ibm.ws.sib.mfp.trm;

import java.util.List;

/**
 * TrmClientAttachReply extends the general TrmFirstContactMessage
 * interface and provides get/set methods for all fields specific to a
 * TRM Client Attach request.
 *
 */
public interface TrmClientAttachReply extends TrmFirstContactMessage {

  /* **************************************************************************/
  /* Get Methods                                                              */
  /* **************************************************************************/

  /**
   *  Get the Return Code from the message.
   *
   *  @return An Integer return code.
   */
  public Integer getReturnCode();

  /**
   *  Get the failure reason from the message.
   *
   *  @return A List of Strings containing the failure reason, if any.
   *          If there was not a failure, null will be returned.
   */
  public List getFailureReason();


  /* **************************************************************************/
  /* Set Methods                                                              */
  /* **************************************************************************/

  /**
   *  Set the Return Code in the message.
   *
   *  @param value An int return code.
   */
  public void setReturnCode(int value);

  /**
   *  Set the failure reason in the message.
   *
   *  @param value A List of Strings containing the failure reason.
   */
  public void setFailureReason(List value);

}
