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
 * Reason          Date        Origin   Description
 * --------------- ----------- -------- ----------------------------------------
 * f179630.1       04-Nov-2003 nottinga Initial Code Drop
 * f201972.0       05-Jul-2004 jroots   Core SPI Exceptions rewrite
 * f201972.11      27-Jul-2004 nottinga Moved to exception package.
 * LIDB3706-5.201   220205 gatfora  Add missing serialVersionUID 
 * PK67950.1       25-Jul-2008 egglestn Remove the initCause override (which disabled it)
 * ============================================================================
 */

package com.ibm.wsspi.sib.core.exception;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;

/**
 * <p>This exception is thrown if more information is required.</p>
 * 
 * <p>SIB build component: sib.core.selector</p>
 * 
 * <p>This exception is thrown by the SICoreConnectionFactorySelector if the FactoryType
 *   passed in to the getSICoreConnectionFactory methods requires some additional information
 *   in order to obtain the SICoreConnectionFactory the FactoryType refers to.
 * </p>
 */
public class SIInsufficientDataForFactoryTypeException
  extends SIIncorrectCallException
{
  private static final long serialVersionUID = 3983956926597066625L;
  /* ------------------------------------------------------------------------ */
  /* SIInsufficientDataForFactoryTypeException constructor                                    
  /* ------------------------------------------------------------------------ */
  /**
   * This constructor creates the Exception with an explanatory error message.
   * @param message The associated error message.
   */
  public SIInsufficientDataForFactoryTypeException(String message)
  {
    super(message);
  }

}
