/* 
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
 * 
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace;

/**Thrown when a recoverable error is found in the matching engine.
 *
 */

public class BadMessageFormatMatchingException 
  extends Exception

{
  private static final long serialVersionUID = -9028567829578930282L;
    // JSA note: this exception class has been divorced from the Greyhound
    // GeneralException framework so that FormattedMessage could be integrated into the
    // Gryphon codebase.  The issue of a common exception framework across Gryphon and
    // Greyhound is TBD.

  public BadMessageFormatMatchingException()
  {
    super();
  }

  public BadMessageFormatMatchingException(String s)
  {
    super("Error in matching: " + s);
  }
  
  public BadMessageFormatMatchingException(Exception e)
  {
    super("Error in matching: ", e);
  }  
}
