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
 * 166318.3         090603 nyoung   First Version
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.selector.impl
 * 166318.8         300603 nyoung   Implement PositionAssigner class
 * 171415           100703 gatfora  Removal of complile time warnings.
 * 174606           190803 gatfora  Removal of compile warnings.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace.impl component
 * 166318.13        201103 auerbach Remove obsolete/unused list support
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.selector.impl;

import com.ibm.ws.sib.matchspace.Identifier;
import com.ibm.ws.sib.matchspace.Selector;
import com.ibm.ws.sib.matchspace.tools.Resolver;
import com.ibm.ws.sib.matchspace.tools.PositionAssigner;

/** A Resolver that does nothing more than assign ordinalPosition
 * uniquely based on the name and type.  This is the minimum required resolution
 * in order to use an Identifier in MatchSpace.  An instance of
 * this Resolver can also be used as a shared resource by other
 * resolvers wishing to assign ordinal positions on the same basis.
 */
public class MinimalResolver implements Resolver {

  // Implement resolve
  public Selector resolve(Identifier id, PositionAssigner positionAssigner) {
    positionAssigner.assign(id);
    return id;
  }
}
