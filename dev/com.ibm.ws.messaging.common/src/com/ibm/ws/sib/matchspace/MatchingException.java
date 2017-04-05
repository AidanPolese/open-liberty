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
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace component
 * 199184           220404 gatfora  Fixed javadoc problems
 * LIDB3706-5.213   220205 gatfora  Add missing Serial UID's
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace;

/**Thrown when a nonrecoverable error is found in the matching engine.
 *
 */
public class MatchingException extends Exception
{

  private static final long serialVersionUID = -8521349731067726076L;
  // JSA note: this exception class has been divorced from the Greyhound
  // GeneralException framework so that FormattedMessage could be integrated into the
  // Gryphon codebase.  The issue of a common exception framework across Gryphon and
  // Greyhound is TBD.

  public MatchingException()
  {
    super();
  }

  public MatchingException(String s)
  {
    super("Severe error in matching: " + s);
  }
  
  public MatchingException(Exception e)
  {
    super("Severe error in matching: ", e);
  }

}
