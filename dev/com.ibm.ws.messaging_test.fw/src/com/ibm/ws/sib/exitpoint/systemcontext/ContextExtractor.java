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
 * Reason          Date      Origin   Description
 * --------------- ------    -------- ---------------------------------------
 * 191963.2        30-Apr-04 pnickoll  Original
 * ============================================================================
 */
package com.ibm.ws.sib.exitpoint.systemcontext;

import com.ibm.wsspi.sib.core.SIBusMessage;

public class ContextExtractor
{
	
	public static boolean extractContext (SIBusMessage msg)
	{
		return true;
	}
	
	public static void removeExtractedContext ()
	{
	}
}
