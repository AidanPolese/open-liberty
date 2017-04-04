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
 * 166318.3         090603 nyoung   Replace Gryphon utilities with JDK Equivalents 
 * 166318.4         160603 nyoung   Move to sib.processor project
 * 166318.10        230903 nyoung   Move to matchspace component
 * ===========================================================================
 */

package com.ibm.ws.sib.matchspace;

/** A marker interface to be implemented along with a Resolver and an MatchSpaceKey in order
 * to speed up access to Identifier values by whatever means necessary
 **/

public interface ValueAccessor {
  // Marker
}
