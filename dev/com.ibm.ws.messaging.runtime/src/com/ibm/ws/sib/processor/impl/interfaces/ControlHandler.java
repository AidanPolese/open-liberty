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
 * 171905.15        220903 tevans   Remote Flows
 * 171905.18        031003 tevans   ME-ME flows
 * 181796.1         051103 gatfora  New MS5 Core API
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * 419906           080307 cwilkin  Remove Cellules
 * PM56596.DEV      032012 chetbhat Managing conflicting ticks
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.mfp.control.ControlMessage;
import com.ibm.ws.sib.utils.SIBUuid8;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIRollbackException;

/**
 * An interface class for the different types of message handlers.
 */
public interface ControlHandler
{
  public void handleControlMessage(SIBUuid8 sourceMEUuid, ControlMessage cMsg)
    throws SIIncorrectCallException,
           SIResourceException,
           SIConnectionLostException,
           SIRollbackException;

  public long handleControlMessageWithReturnValue(SIBUuid8 sourceMEUuid, ControlMessage cMsg)
    throws SIIncorrectCallException,
           SIResourceException,
           SIConnectionLostException,
           SIRollbackException;

}
