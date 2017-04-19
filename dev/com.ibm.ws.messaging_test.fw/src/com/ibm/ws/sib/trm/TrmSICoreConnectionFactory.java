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
 * Reason            Date      Origin   Description
 * ---------------   ------    -------- ----------------------------------------
 *                   18-Jul-03 dcurrie  Creation 
 * 181796.6          05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * ============================================================================
 */
 
package com.ibm.ws.sib.trm;

import com.ibm.ws.sib.api.jmsra.stubs.TrmSICoreConnectionFactoryStub;
import com.ibm.wsspi.sib.core.SICoreConnectionFactory;

/**
 * Version of TrmSICoreConnectionFactory that hooks into core API stubs.
 */
public abstract class TrmSICoreConnectionFactory implements SICoreConnectionFactory
{

  public static TrmSICoreConnectionFactory getInstance()
  {
    return new TrmSICoreConnectionFactoryStub();
  }

}
