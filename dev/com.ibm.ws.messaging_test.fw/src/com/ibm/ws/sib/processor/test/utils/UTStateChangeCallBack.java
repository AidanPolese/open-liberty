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
 * SIB0211.mp.2     080607 nyoung   Support for dynamic PSB configuration
 * ============================================================================
 */
package com.ibm.ws.sib.processor.test.utils;

public interface UTStateChangeCallBack
{
  public void stateChange(int state);
}
