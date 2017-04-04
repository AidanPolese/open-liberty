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
 * ---------------  ------ -------- -------------------------------------------------
 * TBD              260303 astley   First version
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace component
 * 182345           111103 gatfora  IllegalArgumentExceptions incorrectly thrown
 * 184028           261103 gatfora  Remove the WsException
 * LIDB3706-5.213   220205 gatfora  Add missing Serial UID's
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace;

/**
 * The topic string contained an illegal syntactic structure.
*/

public class InvalidTopicSyntaxException 
  extends Exception {

  private static final long serialVersionUID = -8827539539658780651L;
  public InvalidTopicSyntaxException(String msg)
    {
    super(msg);
    }
  }
