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
 * 186484.10        170504 tevans   MBean Registration
 * 196675.1.7.1     030604 tevans   MBean Registration enhancements
 * 186484.18        290604 tevans   Remote queue point control improvements
 * 229588           070904 gatfora  Remove the removed getIdentifier use
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

/**
 * @author tevans
 *
 * Provides some default ControlAdapter behaviour
 */
public abstract class AbstractControlAdapter implements ControlAdapter
{
  //These methods are only used if the ControlAdapter is to be registered as an
  //MBean. If it is an MBean then it should extend AbstractRegisteredControlAdapter
  //instead of this class
  public String getUuid(){return null;}
  public String getConfigId(){return null;}
  public String getRemoteEngineUuid(){return null;}
  public void registerControlAdapterAsMBean(){}
  public void deregisterControlAdapterMBean(){} 
}
