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
 * Reason            Date   Origin   Description
 * ---------------   ------ -------- ------------------------------------------
 *                   250903 jonrober Original
 * 197921.1          040504 jhumber  New Javadoc tags
 * 225815            200804 matrober ibm-spi tags in internal files
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms;

import javax.jms.JMSException;

/**
 * Provides an interface so that delete() of JmsTemporaryQueueImpl
 * and JmsTemporaryTopicImpl can be reached by common code
 * 
 * This class is specifically NOT tagged as ibm-spi because by definition it is not
 * intended for use by either customers or ISV's.
 */
public interface JmsTemporaryDestinationInternal {

	public void delete() throws JMSException;

}
