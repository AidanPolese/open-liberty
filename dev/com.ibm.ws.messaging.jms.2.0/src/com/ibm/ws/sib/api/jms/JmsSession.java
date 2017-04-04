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
 *                          matrober Original (Created on 17-Feb-03)
 * 170067            200603 matrober Refactor JMS interfaces (com.ibm.websphere)
 * 174896            220803 matrober JavaDoc public interfaces
 * 197921.1          040504 jhumber  New Javadoc tags
 * 308128.3          211105 holdeni  Remove @ibm-api javadoc tag. This is not a published API 
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms;

import javax.jms.Session;

/**
 * Contains provider specific methods relating to the javax.jms.Session interface. 
 * 
 * @ibm-was-base 
 */
public interface JmsSession extends Session
{

}
