/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Material
 * 
 * IBM WebSphere
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
 * ---------------  ------ -------- ------------------------------------------
 * 240316           131204 gatfora  Original
 * LIDB3706-5.247  180105 gatfora  Include a serialVersionUid for all serializable objects
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.exceptions;

/**
 * @author gatfora
 *
 * Used in the SourceStream class (writeAckPrefix) to indicate that
 * an unexpected message was received.
 */
public class InvalidMessageException extends Exception
{
  /** The serial version UID, for version to version compatability */
  private static final long serialVersionUID = -2845187083546468027L;

}
