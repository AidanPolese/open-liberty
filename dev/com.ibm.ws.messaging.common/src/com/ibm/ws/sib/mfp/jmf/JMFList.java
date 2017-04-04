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
 * The <b>JMFList</b> Interface is the union of <b>java.util.List</b> and
 * <b>JMFMessageData</b>. It also supplies a 'missing value' object which can be used in
 * any Object array or <b>Collection</b> that is destined to be turned into a
 * <b>JMFList</b> to indicate a value that is not present.
 *
 * <p>Those <b>java.util.List</b> methods that change the size of the list (e.g., add and
 * remove) are not provided: invoking those methods causes an
 * <b>UnsupportedOperationException</b>. Thus, a <b>JMFList</b> cannot be constructed
 * incrementally.  Instead, use any convenient <b>Collection</b> class or an
 * array to construct the equivalent data structure and use that on any
 * invocation of the <b>JMFMessageData.setValue</b> method in lieu of a <b>JMFList</b>.
 * No matter that it was specified as some other collection, a subsequent <b>getValue</b>
 * directed at the same field will always return a <b>JMFList</b>.  A <b>JMFList</b> is
 * mutable (its <b>set</b> methods work, even though <b>add</b> and <b>remove</b> do not).
 * Mutations made to a <b>JMFList</b> are reflected to the underlying message or message
 * part.
 */

import java.util.List;

public interface JMFList extends List, JMFMessageData {

  /** 
   * Missing value indicator 
   */
  public static final Object MISSING = new Object();
}
