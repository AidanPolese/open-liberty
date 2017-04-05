/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * SIB0105.mp.5     071106 cwilkin  Original
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import java.util.Locale;

public interface HealthState {

  static public final int GREEN = 2;
  static public final int AMBER = 1;
  static public final int RED = 0;
 
  public int getState();
  
  public String getHealthReason(Locale l);
  
}
