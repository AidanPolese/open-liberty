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
 * 170807.7          220803 amardeep Original, based on JmsTopicConnFactory
 * 174896            030903 matrober JavaDoc public interfaces
 * 197921.1          040504 jhumber  New Javadoc tags 
 * ============================================================================
 */

package com.ibm.websphere.sib.api.jms;

import javax.jms.TopicConnectionFactory;

/**
 * Contains provider specific methods relating to the javax.jms.TopicConnectionFactory
 * interface. 
 * 
 * @ibm-api
 * @ibm-was-base 
 *
 */
public interface JmsTopicConnectionFactory
  extends JmsConnectionFactory, TopicConnectionFactory
{
}
