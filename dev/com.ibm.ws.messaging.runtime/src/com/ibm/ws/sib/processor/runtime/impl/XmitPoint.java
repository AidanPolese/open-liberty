/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------------
 * 186484.18        290604 tevans   Remote queue point control improvements
 * SIB0102.mp.2     151106 cwilkin  Link Transmission Controllables
 * SIB0105.mp.5     040607 cwilkin  Link Transmission Health States
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.runtime.impl;

public interface XmitPoint extends ControlAdapter
{
  public void setXmitQueuePointControl(XmitPointControl xmitControl);
  public void dereferenceXmitQueuePointControl();
}
