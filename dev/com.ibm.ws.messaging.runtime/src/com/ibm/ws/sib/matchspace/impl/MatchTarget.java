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
 * 166318.2         220503 nyoung   Remove WMQI/Gryphon-specific MatchTargets
 * 166318.3         090603 nyoung   Split into 2 classes, cf MessageProcessorMatchTarget
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace component
 * 166318.11        091003 nyoung   Make abstract.
 * 189721           020304 gatfora  Added missing FFDC statements.
 * SIB0155.mspac.1  120606 nyoung   Repackage MatchSpace RAS.
 * 399452           161106 nyoung   FFDC instrumentation complaints.
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.impl;

import com.ibm.ws.sib.matchspace.utils.FFDC;

/**
 * This class must be extended by subclasses whose instances are to be associated
 * with filters in the matching space.
 */
public abstract class MatchTarget implements Cloneable
{
  private static final Class cclass = MatchTarget.class;
  // The type is set by the constructor.  The index property is
  // set by MatchSpace.  The type(), setIndex(), and getIndex() methods
  // are not overrideable.  A typical MatchTarget specialization implements equals(),
  // hashCode() and whatever other behavior it likes.
  private int type;
  private int index;

  // Constructor (the only one) requires a type

  protected MatchTarget(int type)
  {
    this.type = type;
  }

  //------------------------------------------------------------------------------
  // Method: MatchTarget.type
  //------------------------------------------------------------------------------
  /** Returns an integer describing the type of this MatchTarget.<p>
   *
   * This type will be passed to implementations of SearchResults when
   * a group of MatchTargets are added at match time.<p>
   *
   * The type code returned should be defined as a constant in subclasses.
   *
   * Created: 98-10-09
   */
  //---------------------------------------------------------------------------
  public final int type()
  {
    return type;
  }

  //------------------------------------------------------------------------------
  // Method: MatchTarget.setIndex
  //------------------------------------------------------------------------------
  /** Records an integer that is used inside the matcher to manage the target efficiently.
   * This method need not be implemented by subclasses and shouldn't be overridden. */
  public final void setIndex(int index)
  {
    this.index = index;
  }

  //------------------------------------------------------------------------------
  // Method: MatchTarget.getIndex
  //------------------------------------------------------------------------------
  /** Returns the integer recorded by setIndex. */
  public final int getIndex()
  {
    return index;
  }

  //------------------------------------------------------------------------------
  // Method: MatchTarget.duplicate
  //------------------------------------------------------------------------------
  /** Creates a clone of this MatchTarget.  Override only if the system clone support does
   * not produce a correct result.
   **/
  public MatchTarget duplicate()
  {
    try
    {
      return (MatchTarget) clone();
    }
    catch (CloneNotSupportedException e)
    {
      // No FFDC Code Needed.
      // FFDC driven by wrapper class.
      FFDC.processException(cclass,
          "com.ibm.ws.sib.matchspace.MatchTarget.duplicate",
          e,
          "1:112:1.15");        
      // should not happen
      throw new IllegalStateException();
    }
  }
}
