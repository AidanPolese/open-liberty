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
  * 479100            020708 djvines  Provide different encoding levels
 * ============================================================================
 */
package com.ibm.ws.sib.api.jms;

/* ************************************************************************** */
/**
 * An EncodingLevel indicates how complete an encoding of a JmsDestination is required
 */
/* ************************************************************************** */
public enum EncodingLevel
{
  /** MINIMAL encoding includes the bare minimum of information (not even the name is included in this encoding, used for the JMSReplyTo originally from SIB) */
  MINIMAL,
  /** LIMITED encoding contains some of the information (used for JMSReplyTo's originally from MQ) */
  LIMITED,
  /** FULL encoding includes everything (used for JMSDestination's) */
  FULL
}
