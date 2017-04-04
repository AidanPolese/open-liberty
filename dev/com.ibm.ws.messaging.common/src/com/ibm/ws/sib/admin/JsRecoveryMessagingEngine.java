/* 
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- ---------------------------------------------
 * F008622        071211  urwashi  Initial version.
 * =============================================================================
 */
package com.ibm.ws.sib.admin;

import com.ibm.ws.sib.utils.SIBUuid8;

public interface JsRecoveryMessagingEngine extends JsMessagingEngine {
	public void setUuid(SIBUuid8 meUUid);
	public void setMeName(String meName); 
	public void setBusName(String busName);
	public void setMessageStore(Object msgStore);
	public void setBus(JsEObject busConfigObject);
	public void setDataStoreExists(boolean dataStoreExists);
	public void setFileStoreExists(boolean fileStoreExists);
}
