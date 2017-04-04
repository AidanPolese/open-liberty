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
 * 236016           051004 nyoung   Add new exception to support selector stream EOF.
 * LIDB3706-5.212   220205 gatfora  Add missing Serial UID's
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.selector.impl;

import java.io.IOException;

/**
 * The MatchParser has encountered EOF in the Selector InputStream.
 */
public class SelectorStreamEOFException extends IOException 
{
  private static final long serialVersionUID = -6330981097856396962L;
  public Throwable fillInStackTrace() 
  {
    return null;
  }  
}
