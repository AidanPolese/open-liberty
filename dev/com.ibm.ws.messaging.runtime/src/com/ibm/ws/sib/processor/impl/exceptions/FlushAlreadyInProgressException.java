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
 * 171905.41        091203 astley   non-concurrent flush and bug fixes
 * LIDB3706-5.247  180105 gatfora  Include a serialVersionUid for all serializable objects
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.exceptions;

/**
 * Thrown if an attempt is made to flush a stream when a flush
 * is already in progress.
 */
public class FlushAlreadyInProgressException extends Exception 
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -5677438044925077005L;

  /**
   * FlushAlreadyInProgressException is thrown if an attempt
   * is made to flush a stream for which a flush is already
   * in progress.
   */
  public FlushAlreadyInProgressException()
  {
    super();
  }

  /**
   * FlushAlreadyInProgressException is thrown if an attempt
   * is made to flush a stream for which a flush is already
   * in progress.
   *
   * @param arg0  Exception text
   */
  public FlushAlreadyInProgressException(String arg0)
  {
    super(arg0);
  }
}
