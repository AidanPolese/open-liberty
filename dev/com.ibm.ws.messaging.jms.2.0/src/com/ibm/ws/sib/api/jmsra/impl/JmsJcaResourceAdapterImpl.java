/**
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
 * Reason            Date    Origin   Description
 * --------------  ------    -------- ------------------------------------------
 *                 06-May-03 djhoward Creation
 *                 13-Jun-03 pnickoll Updated with latest Rational Rose design
 * 174369.1        21-Aug-03 dcurrie  Add inbound implementation
 * 174369.3        19-Sep-03 djhoward Add Trace, ffdc and NLS  
 * 181796.6        05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 193189          09-Mar-04 pnickoll NLS Messaging standardisation.
 * 203656          17-May-04 dcurrie  Code cleanup
 * 195445.28       26-May-04 pnickoll Changing messaging prefix
 * 207370          04-Jun-04 pnickoll Updated this class to temporarily extend the SIB.ra class
 * 182745.10.1     14-Jul-04 dcurrie  Reformat
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra.impl;

import com.ibm.ws.sib.ra.inbound.impl.SibRaResourceAdapterImpl;

/**
 * Resource adapter implementation.
 */
public final class JmsJcaResourceAdapterImpl extends SibRaResourceAdapterImpl {

	/*
	 * Admin currently require a separate implementation of this class for the
	 * JMS resource adapter as they use the class name stored in the
	 * resources.xml to find JMS resources
	 */

}
