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
 * Reason           Date  Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 174624.1         210803 gatfora  NLS of Exceptions
 * 175766           030903 gatfora  Package restructuring
 * LIDB3706-5.247  180105 gatfora  Include a serialVersionUid for all serializable objects
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.exceptions;

/**
 * Invalid stream Exception is called from the PersistentStore class.
 */
public final class InvalidStreamException extends Exception
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = 617046342844616334L;

  /**
   * InvalidStreamException is thrown when selecting an item 
   * stream from the persistent store that is not valid.
   */
  public InvalidStreamException()
  {
    super();
  }

  /**
   * InvalidStreamException is thrown when selecting an item 
   * stream from the persistent store that is not valid.
   *
   * @param arg0  Exception text
   */
  public InvalidStreamException(String arg0)
  {
    super(arg0);
  }

}
