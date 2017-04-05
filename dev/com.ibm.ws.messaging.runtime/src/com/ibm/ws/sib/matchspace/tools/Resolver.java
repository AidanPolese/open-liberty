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
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.selector.impl
 * 166318.4         160603 nyoung   Move to com.ibm.ws.sib.processor.matchspace.selector.impl
 * 171415           100703 gatfora  Removal of complile time warnings.
 * 166318.9         160903 nyoung   Restructure mspace interfaces
 * 166318.10        230903 nyoung   Move to matchspace component
 * 166318.13        201103 auerbach Remove obsolete/unused list support
 * ===========================================================================
 */
package com.ibm.ws.sib.matchspace.tools;

import com.ibm.ws.sib.matchspace.Identifier;
import com.ibm.ws.sib.matchspace.Selector;

/** Implementations of this interface are passed to the Transformer.resolve method and
 * have an opportunity to inspect and replace each Identifier in Selector tree.
 **/

public interface Resolver {
  /** Inspect and optionally replace one Identifier.  If the replacement is an Identifier,
   * this method is permitted to modify its argument and return the modified Identifier
   * rather than allocating a new one (since the semantics of Transformer.resolve is
   * "update in place").
   *
   * @param id the Identifier to process
   * 
   * @param positionAssigner the PositionAssigner to use to assign ordinal positions on
   * a first-come-first-served basis for the appropriate scope.  The Resolver may use this
   * to assign ordinal positions or may do so based on some other criterion in which case
   * the argument is ignored.
   *
   * @return a replacement Selector tree.  This may be the original Identifier,
   * unmodified, the original Identifier, modified, or a completely new tree.  The new
   * tree will NOT be rescanned by the Transformer.resolve method, so, if it contains any
   * Identifiers they must be pre-resolved.
   **/

  public Selector resolve(Identifier id, PositionAssigner positionAssigner);
}
