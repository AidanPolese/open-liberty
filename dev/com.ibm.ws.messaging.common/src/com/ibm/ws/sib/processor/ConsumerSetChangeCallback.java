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
 * SIB0009.mp.02    220805 nyoung   Add support for Consumer Count monitoring
 * ============================================================================
 */
package com.ibm.ws.sib.processor;

/**
 ConsumerSetChangeCallback is an interface that can be implemented by a component
 that supports the registration of producers, to allow it to be alerted to changes
 in the set of potential consumers. The consumerSetChange method is called when 
 the potential set of consumers drops to zero or rises above zero.
*/

public interface ConsumerSetChangeCallback 
{
  public void consumerSetChange(boolean isEmpty);
}
