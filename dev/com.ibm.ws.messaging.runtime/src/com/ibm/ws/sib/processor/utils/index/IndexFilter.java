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
 * 186484.4         130404 tevans   Modifications for runtime admin
 * 186484.6         220404 tevans   Re-write of destination lookups to enable runtime admin
 * ============================================================================
 */
package com.ibm.ws.sib.processor.utils.index;

/**
 * Class to represent the various lookups for different destination types
 */ 
public interface IndexFilter
{
  public boolean matches(Index.Type type);
}
