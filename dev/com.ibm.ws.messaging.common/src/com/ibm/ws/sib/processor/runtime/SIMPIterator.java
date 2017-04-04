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
 * 186484.6         220404 tevans   Re-write of destination lookups to enable runtime admin
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import java.util.Iterator;

public interface SIMPIterator extends Iterator
{
  /**
   * Indicate that this iterator is finished with and resources can be released
   */
  public void finished();
}
