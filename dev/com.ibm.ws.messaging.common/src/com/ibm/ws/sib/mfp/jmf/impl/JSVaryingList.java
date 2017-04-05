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
 * 178725  040116 auerbach Created file
 * ===========================================================================
 */

package com.ibm.ws.sib.mfp.jmf.impl;

import com.ibm.ws.sib.mfp.jmf.JMFList;

/**  This interface extends JMFList with the additional methods
 * common to both JSVaryingListImpl and JSCompatibleBoxList.   The box manager
 * and its ancillaries (JSBoxedListImpl and JSIndirectBoxedListImpl) need this
 * abstraction to function correctly in the presence or absence of a compatibility
 * layer.
 */
interface JSVaryingList extends JMFList {
  int getIndirection();
  JSField getElementType();
}
