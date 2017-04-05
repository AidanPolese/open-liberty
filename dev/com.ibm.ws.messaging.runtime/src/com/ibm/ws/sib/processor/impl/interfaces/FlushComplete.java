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
 * 171905.41        091203 astley   non-concurrent flush and bug fixes
 * 186445.3         200104 millwood Move to admin destination definition
 * 175637.2.1       160204 millwood Alias destinations refactoring
 * 175637.2.2       170204 millwood Alias support
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;


/**
 * An interface to signal completion of a flush.
 */
public interface FlushComplete {
  /**
   * The flush for the given destination has completed and a new
   * stream ID has been created.
   *
   * @param dest The DestinationHandler for which a stream was flushed.
   */
  public void flushComplete(DestinationHandler destinationHandler);
}
