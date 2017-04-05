/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5639-D57,5630-A36,5630-A37,Copyright IBM Corp. 2012
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
 * SIB0136b.msp.1   080207 nyoung   Stage 2 implementation of XPath Selector support.
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.selector.impl;

/**
 * The XPath Compiler has encountered an XPath syntax error in the Selector.
 */
public class InvalidXPathSyntaxException extends Exception 
{
  private static final long serialVersionUID = -6330981097856396962L;
  public InvalidXPathSyntaxException(String selector)
  {
    super("Invalid XPath syntax: " + selector);
  }
}
