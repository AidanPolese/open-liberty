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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 370507          060631 mphillip Original
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import com.ibm.wsspi.sib.core.SIMessageHandleRestorer;

/**
 * JsMessageHandleRestorer is the internal interface for restoring any
 * JsMesasgeHandles from their flattened forms.
 * It extends the Core SPI SIMessageHandleRestorer will be used by WPS.
 */
public abstract class JsMessageHandleRestorer extends SIMessageHandleRestorer {

}
