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
 * ---------------  ------ -------- -------------------------------------------
 * 190632.0.14      280404 caseyj   Implement MQLink corruption
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * LIDB3706-5.248   180105 gatfora  Include a serialVersionUid for all serializable objects
 * ============================================================================
 */
package com.ibm.ws.sib.processor.exceptions;

import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;

/**
 * Exception which indicates a destination was corrupt.
 */
public class SIMPMQLinkCorruptException extends SINotPossibleInCurrentConfigurationException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -7517653780546136093L;

  /**
   * Empty constructor
   */
  public SIMPMQLinkCorruptException()
  {
    super();
  }

  /**
   * @param message  The message text
   */
  public SIMPMQLinkCorruptException(String message)
  {
    super(message);
  }

  /**
   * @param message  The message text.
   * @param cause  The initial exception
   */
  public SIMPMQLinkCorruptException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @param cause The initial exception
   */
  public SIMPMQLinkCorruptException(Throwable cause)
  {
    super(cause);
  }
}
