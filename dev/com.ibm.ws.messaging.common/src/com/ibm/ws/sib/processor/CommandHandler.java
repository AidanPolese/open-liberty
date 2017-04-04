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
 * SIB0009.core.01  150805 rjnorris Add invokeCommand() to CoreConnection
 * 377093.2         110706 tpm      TransactionalCommandHandler support
 * ===========================================================================
 */

package com.ibm.ws.sib.processor;

import java.io.Serializable;

/**
 * @author rjnorris
 * CommandHandlers get invoked only by the non-tx flavour of invokeCommand.
 */
public interface CommandHandler 
{
  
  /**
   *   
   * @param commandName  The command to be invoked  
   * @param commandData  The data to pass on command invocation
   *
   * @return the return value of the invoked command. This may be a serialized exception.
   */
  public Serializable invoke( String commandName, Serializable commandData); 

}

