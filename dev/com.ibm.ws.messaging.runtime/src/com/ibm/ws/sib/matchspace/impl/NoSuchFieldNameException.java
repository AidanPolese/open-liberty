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
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * LIDB3706-5.212   220205 gatfora  Add missing Serial UID's
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

/**This exception is thrown when a request is made for a field in the
 * message by field name where the field name does not exist in the
 * message format.
 */
public class NoSuchFieldNameException extends Exception
{

  private static final long serialVersionUID = -4181374423206925206L;
  // JSA note: this exception class has been divorced from the Greyhound
  // GeneralException framework so that FormattedMessage could be integrated into the
  // Gryphon codebase.  The issue of a common exception framework across Gryphon and
  // Greyhound is TBD.

  public NoSuchFieldNameException(String fieldName)
  {
    super("NoSuchFieldName: " + fieldName);
  }
}
