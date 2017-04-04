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
 * 520472           220508 cwilkin  Gathering reattaching
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

public interface ConsumerKey
{
  public static final int CLOSED_DUE_TO_DELETE = 1;
  public static final int CLOSED_DUE_TO_RECEIVE_EXCLUSIVE = 2;
  public static final int CLOSED_DUE_TO_ME_UNREACHABLE = 3;
  
}
