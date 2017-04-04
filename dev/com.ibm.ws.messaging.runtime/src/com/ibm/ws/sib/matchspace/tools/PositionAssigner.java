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
 
package com.ibm.ws.sib.matchspace.tools;

import com.ibm.ws.sib.matchspace.Identifier;

/** The PositionAssigner assigns ordinal positions to Identifiers based purely on 
 * when they were first seen.
 *
 * For purposes of ordinal position assignment, two Identifiers are the same if their
 * names are the same and their basic type (STRING vs NUMERIC vs BOOLEAN vs UNKNOWN) is
 * the same.  A LIST identifier will never appear in a SimpleTest so it is not assigned an
 * ordinal position.
 * **/

public interface PositionAssigner 
{
  // The assignPosition method

  public void assign(Identifier id);
}
