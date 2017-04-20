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
 * 186484.9         060504 tevans   Extended runtime control implementation
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.test;

import com.ibm.ws.sib.admin.Controllable;
import com.ibm.ws.sib.admin.ControllableType;
import com.ibm.ws.sib.admin.RuntimeEventListener;
import com.ibm.ws.sib.admin.SIBExceptionInvalidValue;
import com.ibm.ws.sib.admin.exception.NotRegisteredException;

/**
 * Interface gives the unit test cases a "back door" into the 
 * controllable registration service.
 */
public interface RegistrationServiceBackDoor
{
  /**
   * If the unit test has a reference to the resource's controllable
   * implementatation object, he can add a runtime event listener of 
   * his own, to listen for specific events coming from that resource.
   * <p>
   * Calling it again forces the old event listener to be abandoned. 
   * @param listener
   * @param controllable
   */
  public void setControllableEventListener( 
    RuntimeEventListener listener,
    Controllable controllable,
    ControllableType type 
  ) throws SIBExceptionInvalidValue, NotRegisteredException ;
  
  /**
   * Allows the caller to look up a controllable by its ID.
   * @return null if not found, or the controllable if it is found.
   */
  public Controllable findControllableById( 
    String id , ControllableType type ) 
    throws SIBExceptionInvalidValue 
  ;
}
