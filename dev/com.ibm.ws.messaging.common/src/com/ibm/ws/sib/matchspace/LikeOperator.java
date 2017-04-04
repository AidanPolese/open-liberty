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
 * 166318.9         160903 nyoung   First version - Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace component
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace;

/** Subinterface of Operator that is to be implemented by a class that provides a LIKE expression as a parameterized unary
 * operator
 **/

public interface LikeOperator extends Operator {

  /**
   * Returns the escape.
   * @return char
   */
  public char getEscape(); 

  /**
   * Returns the escaped.
   * @return boolean
   */
  public boolean isEscaped(); 


  /**
   * Returns the pattern.
   * @return String
   */
  public String getPattern();
 
}
