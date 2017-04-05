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
 * 166318.3         090603 nyoung   First version
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.impl
 * 171905.5         010803 gatfora  GD flow restructuring to allow OutputHandlers
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matching package
 * SIB0163.mp.1     190907 nyoung   XD Integration.
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.matching;

import com.ibm.ws.sib.matchspace.MatchTarget;

//------------------------------------------------------------------------------
// MessageProcessorMatchTarget Interface
//------------------------------------------------------------------------------
/**
 * This interface must be implemented by objects that are to be associated
 * with filters in the matching space.
 */ //---------------------------------------------------------------------------
public class MessageProcessorMatchTarget extends MatchTarget
{
  // Types start at 0 and, for efficiency, should be increased
  // densly (i.e. don't skip numbers).
  // MatchTarget types are processed in MessageProcessor from lowest
  // index value to highest, so order can be significant.
  public static final int ACL_TYPE = 0;
  public static final int JS_SUBSCRIPTION_TYPE = 1;
  public static final int JS_CONSUMER_TYPE = 2;
  public static final int JS_NEIGHBOUR_TYPE = 3;
  public static final int APPLICATION_SIG_TYPE = 4;
  
  public static final int MAX_TYPE = APPLICATION_SIG_TYPE;
  // edit when more are added
  public static final int NUM_TYPES = MAX_TYPE + 1;

  // Names for target types, for use in debugging statements
  public static final String[] TARGET_NAMES =
    { "acl", "js subscription", "js consumer", "js neighbour", "reg application" };

  // Constructor (the only one) requires a type

  protected MessageProcessorMatchTarget(int type)
  {
    super(type);
  }

}
