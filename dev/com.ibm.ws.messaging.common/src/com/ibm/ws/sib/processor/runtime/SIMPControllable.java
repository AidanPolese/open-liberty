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
 * 192832.3         110304 mcobbett Initial Creation
 * 186484.4         050404 tevans   Continued controllable interfaces
 * 186484.5         220404 ajw      Further Continued controllable interfaces
 * 186484.6         220404 tevans   Re-write of destination lookups to enable runtime admin
 * 186484.7         270404 tevans   More runtime control interfaces and implementation
 * 202387           100504 gatfora  getID should be getId.
 * 196675.1.7.1     030604 tevans   MBean Registration enhancements
 * 452517           210807 cwilkin  Remove formatState
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime;

import com.ibm.ws.sib.admin.Controllable;

/**
 * All controllable sub-interfaces from the mesasge processor component
 * are sub-interfaces of this super-interface.
 */
public interface SIMPControllable extends Controllable
{
  /**
   * Returns the name of this controllable object. This value is used as part of
   * the name by which the object is registered. In the current implementation,
   * this value is used as the "name" of the JMX MBean to represent the external
   * configuration name of this object. A controllable object must have a name,
   * and it must be unique within the type of object, as specified by the class
   * ControllableType.
   *
   * The only currently supported Controllable objects are those which represent
   * "Message Points" (or "localization points"). The value returned by this method
   * should be either be the name of the destination for which this object is the
   * Message Point
   * @return
   */
  public String getName();

  /**
   * Returns an internal component identifier for this object. If the object which
   * implements this interface does not use such internal identifiers, then null is
   * returned.
   * @return
   */
  public String getId();
}
