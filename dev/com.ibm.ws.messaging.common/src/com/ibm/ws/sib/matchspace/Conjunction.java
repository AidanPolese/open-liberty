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

/** This interface represents a conjunction of tests after DNF transformation.  The tests are
 * isolated into a set of simple tests and a set of complex tests.  By design, a
 * Conjunction is initialized incrementally.  The constructors create an initial,
 * incompletely initialized Conjunction with zero or one tests.  The <b>and</b> methods
 * add one test at a time.  The <b>organize</b> method computes the final form of the
 * Conjunction for use by MatchSpace.
 **/

public interface Conjunction {

  /** Add a SimpleTest to the Conjunction, searching for contradictions.
   *
   * @param newTest the new SimpleTest to be added
   *
   * @return true if the new test is compatible with the old (a false return means the
   * conjunction will always be false because the test is always false)
   **/

  boolean and(SimpleTest newTest);

  /** Add a residual test to the Conjunction
   *
   * @param newResid the residual test to add
   **/

  void and(Selector newResid);


  /** Organize the Conjunction into its final useful form for MatchSpace.
   *
   * @return true if the Conjunction is still capable of being true, false if a
   * contradiction was detected during the organize step.
   *
   * @exception IllegalStateException if the Resolver assigned ordinalPosition information
   * incorrectly so that the simple tests cannot be ordered.
   **/

  public boolean organize();
  
  /**
   * Returns the simpleTests.
   * @return SimpleTest[]
   */
  public SimpleTest[] getSimpleTests();
  
  /**
   * Returns the residual.
   * @return Selector
   */
  public Selector getResidual();  
}
