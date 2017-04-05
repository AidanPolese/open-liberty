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
 * 377093.2         110706 tpm      Add tx invokeCommand() to CoreConnection
 * ===========================================================================
 */
package com.ibm.ws.sib.processor;

import java.io.Serializable;

import com.ibm.wsspi.sib.core.SITransaction;

/**
 * @author tpm
 * TransactionalCommandHandlers get invoked by the tx and non-tx versions 
 * of invokeCommand.
 */
public interface TransactionalCommandHandler extends CommandHandler
{
  
  /**   
   * @param commandName  The command to be invoked  
   * @param commandData  The data to pass on command invocation
   * @param transaction  The transaction under which any msg store updates 
   *                     caused by this command should occur. 
   *                     May be null.
   *                     
   *
   * @return the return value of the invoked command. This may be a serialized exception.
   */
  public Serializable invoke( String commandName, Serializable commandData, SITransaction transaction); 

}
