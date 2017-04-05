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
 * created          190705 rjnorris Initial version  
 * SIB0009.mp.01    210705 rjnorris Add support for multiple selectionCriteria on DurableSub
 * ===========================================================================
 */

package com.ibm.ws.sib.processor.exceptions;

public class SIMPSelectionCriteriaNotFoundException extends SIMPException {

  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -3386782715363250456L;

  public SIMPSelectionCriteriaNotFoundException(String msg) {
    super(msg);
  }
  
  public SIMPSelectionCriteriaNotFoundException(Throwable t) {
    super(t);
  }
}
