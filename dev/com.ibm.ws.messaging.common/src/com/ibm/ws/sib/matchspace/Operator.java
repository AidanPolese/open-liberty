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

/** This interface is to be implemented by classes that represent one Operator in a 
 * Selector expression 
 */

public interface Operator extends Selector {

  /** Assign the appropriate type to an operator based on its op code and operands.  Also
   * assigns a type to operands of previously UNKNOWN type if a more precise type can be
   * inferred.  Called as a subroutine of the constructors and also by Transformer.resolve
   * after an operand has a type assigned by the Resolver.
   **/

  public void assignType();

  public int hashCode();

  /**
   * Returns the op.
   * @return int
   */
  public int getOp(); 

  /**
   * Returns the operands.
   * @return Selector[]
   */
  public Selector[] getOperands();
  
  public void setOperand(int i, Selector operand);  
}
