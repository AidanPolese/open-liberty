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
 *  Reason           Date    Origin     Description
 * --------------- -------- ---------- ----------------------------------------
 * SIB0155.mspac.1 120606   nyoung   Repackage MatchSpace RAS.
 * ============================================================================
 */

package com.ibm.ws.sib.matchspace.utils;

/**
 * @author Neil Young
 * 
 * Dump Object state.
 */
public interface Printable
{
  /**
   * Print a dump of the Objects state.
   * 
   * @param java.io.PrintWriter where state is to be printed.   
   */
  public abstract void print(java.io.PrintWriter printWriter);

} // interface Printable.
