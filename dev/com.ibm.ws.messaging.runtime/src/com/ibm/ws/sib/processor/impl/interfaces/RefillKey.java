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
 * 546149           040908 cwilkin  Implement callback for refilling
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

public interface RefillKey 
{
  public void setLatestTick(long refillTick);
}
