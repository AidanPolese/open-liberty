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
 * Reason   Date   Origin   Description
 * -------- ------ -------- --------------------------------------------------
 * 162584   280303 baldwint Original
 * ===========================================================================
 */

package com.ibm.ws.sib.mfp.jmf;

/** 
 * A representation of enumerated types.  For use in tools and API mapping layers, the
 * JMFEnumType can record its associated enumerators as Strings, but most of JMF ignores
 * these and they are not propagated in schema propagations.  Rather, a JMFEnumType is
 * always encoded as a non-negative integer less than #enumerators.  The number of
 * enumerators (rather than their specific names) is propagated and is used to decide
 * whether two JMFEnumTypes are the same at runtime.
 */

public interface JMFEnumType extends JMFFieldDef {

  /** 
   * Provide the enumerators in the order of their assigned codes or null if the
   * enumerators are not locally known (never set, or not propagated).
   */
  public String[] getEnumerators();

  /** 
   * Set the enumerators in the order of their assigned codes 
   */
  public void setEnumerators(String[] val);

  /** 
   * Get the enumerator count.  This is always available, even if the enumerator 
   * strings are not
   */
  public int getEnumeratorCount();

  /** 
   * Set the enumerator count without setting the enumerators explicitly.  Sets
   * enumerators to null as a side-effect.
   */
  public void setEnumeratorCount(int count);
}
