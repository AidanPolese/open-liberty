/*
 * COMPONENT_NAME: sib.exitpoint.systemcontext
 *
 *  ORIGINS: 27
 *
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
 * 
 *
 * Change activity:
 *
 * Reason          Date        Origin   Description
 * --------------- ----------- -------- ----------------------------------------
 * fSIB0006.ep.1   23-Sep-2005 nottinga Initial Test Drop.
 */
package com.ibm.ws.sib.exitpoint.systemcontext;

/**
 * <p>This class contains constants used by the SIB SystemContext Exitpoint
 *   framework.
 * </p>
 *
 * <p>SIB build component: sib.exitpoint.systemcontext</p>
 *
 * @author nottinga
 * @version 1.2
 * @since 1.0
 */
public class SystemContextConstants
{
  /** The SystemContextInvoker type for the JMS RA */
  public static final String JMS_TYPE = "SIB:JMS";
  /** The SystemContextInvoker type for the Core SPI RA */
  public static final String CORE_SPI_TYPE = "SIB:CORE";
}
