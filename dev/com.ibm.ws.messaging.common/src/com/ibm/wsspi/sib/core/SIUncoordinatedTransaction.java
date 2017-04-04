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
 * 169897.0         240603 jroots   Updates for Milestone 3 Core API
 * 181796.0         041103 jroots   Move to com.ibm.wsspi
 * 201972.0         050704 jroots   Core SPI Exceptions rewrite          
 * 223986           170804 gatfora  Removal of SIErrorExceptions from method throws declarations
 * 199140           180804 gatfora  Cleanup javadoc      
 * 276259           130505 dware    Improve security related javadoc
 * ============================================================================
 */

package com.ibm.wsspi.sib.core;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIRollbackException;

/**
 SIUncoordinatedTransaction enables multiple updates to be grouped into single 
 atomic updates.
 <p>
 An application may call SICoreConnection.createUncoordinatedTransaction to 
 start a transaction that the application uses to define units of work involving 
 a single messaging engine.
 <p>
 This class has no security implications.
*/
public interface SIUncoordinatedTransaction extends SITransaction {
	
  /**
   Commits the transaction. If the transaction had already completed at the 
   time of the commit call, then SIInvalidCallException is thrown. If an 
   SIRollbackException is thrown, then the transaction has rolled back rather
   than committed. If any other exception is thrown, then the outcome of the
   transaction is unknown. 
   
   @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
   @throws com.ibm.wsspi.sib.core.exception.SIRollbackException
   @throws com.ibm.websphere.sib.exception.SIResourceException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
  */
  public void commit() 
    throws SIIncorrectCallException, 
           SIRollbackException,
           SIResourceException,
           SIConnectionLostException;

  /**
   Rolls back the transaction. If SIInvalidStateForOperationException is thrown, 
   then the transaction had already completed at the time the commit call was 
   issued. If any other exception is thrown, then the state of the transaction 
   is unknown, and can only be determined by examining the state of the 
   underlying data store (directly or indirectly) via later inquiry.
   
   @throws com.ibm.websphere.sib.exception.SIIncorrectCallException
   @throws com.ibm.wsspi.sib.core.exception.SIRollbackException
   @throws com.ibm.websphere.sib.exception.SIResourceException
   @throws com.ibm.wsspi.sib.core.exception.SIConnectionLostException
  */
  public void rollback() 
    throws SIIncorrectCallException, 
           SIResourceException,
           SIConnectionLostException;

}

