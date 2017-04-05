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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 180483.3        211003 sbhola   original
 * LIDB3706-5.247  180105 gatfora  Include a serialVersionUid for all serializable objects
 * ============================================================================
 */

package com.ibm.ws.sib.processor.impl.exceptions;


/**
 * The Component being called is closed
 */
public class ClosedException extends Exception
{  
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -7787063452957945077L;

  /**
   * Constructor
   */
  public ClosedException()
  {
    super();
  }

  /**
   * Constructor
   * @param arg extra information about the exception
   */
  public ClosedException(String arg)
  {
    super(arg);
  }
}
