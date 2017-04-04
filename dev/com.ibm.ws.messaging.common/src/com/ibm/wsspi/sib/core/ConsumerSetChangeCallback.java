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
 * F011127          280611 chetbhat Add support for Consumer Count monitoring
 * ============================================================================
 */
package com.ibm.wsspi.sib.core;

/**
 ConsumerSetChangeCallback is an interface that can be implemented by a component
 that supports the registration of producers, to allow it to be alerted to changes
 in the set of potential consumers. The consumerSetChange method is called when 
 the potential set of consumers drops to zero or rises above zero.
 */

public interface ConsumerSetChangeCallback 
{
	/**
	 * This is the callback function which will be called when the potential set of consumers drops to zero or rises above zero.
	 *  
	 * @param isEmpty - Will be true when the potential set of consumers drops to zero and false when the potential set of consumers rises above zero.
	 */
	public void consumerSetChange(boolean isEmpty);
}
