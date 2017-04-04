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
 * TBD              260303 astley   First version
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.selector.impl
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * LIDB3706-5.212   220205 gatfora  Add missing Serial UID's
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.selector.impl;

// Gross hack to make the MatchParserTokenManager throw a IllegalStateException rather
// than an Error when something serious goes wrong.  These conditions represent something
// more serious than just an invalid selector (hence IllegalStateException is appropriate
// rather than ParseException), but in server type environments they are still something
// we want to catch and recover from (and we don't want to catch java.lang.Error in most
// contexts).

class Error extends IllegalStateException
{
  private static final long serialVersionUID = 7347119971472877739L;
  public Error()
  {
  }
  public Error(String msg)
  {
    super(msg);
  }
}
