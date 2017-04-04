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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version X copied from CMVC
 * ============================================================================
 */
package com.ibm.ws.sib.processor.exceptions;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.processor.SIMPConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/**
 * @author caseyj
 *
 * Exception which indicates a message processor is corrupt.
 */
public class SIMPMessageProcessorCorruptException extends RuntimeException
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = 683173378952312210L;

  /**
   * Initialise trace for the component.
   */
  private static final TraceComponent tc =
    SibTr.register(
      SIMPMessageProcessorCorruptException.class,
      SIMPConstants.MP_TRACE_GROUP,
      SIMPConstants.RESOURCE_BUNDLE);

  
  /**
   * Empty constructor
   */
  public SIMPMessageProcessorCorruptException()
  {
    super();
  }

  /**
   * @param message  The message text
   */
  public SIMPMessageProcessorCorruptException(String message)
  {
    super(message);
  }

  /**
   * @param message  The message text.
   * @param cause  The initial exception
   */
  public SIMPMessageProcessorCorruptException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * @param cause The initial exception
   */
  public SIMPMessageProcessorCorruptException(Throwable cause)
  {
    super(cause);
  }
}
