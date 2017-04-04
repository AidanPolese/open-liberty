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
 * ---------------  ------ -------- -------------------------------------------
 * 166318.10        230903 nyoung   Create for matchspace component
 * 182345           111103 gatfora  IllegalArgumentExceptions incorrectly thrown
 * 181801.10        031217 vaughton Switch trace groups to utils
 * 195445.12        100604 gatfora  Update to new message file.
 * SIB0155.mspac.1  120606 nyoung   Repackage MatchSpace RAS.
 * ============================================================================
 */

package com.ibm.ws.sib.matchspace.utils;

public interface MatchSpaceConstants
{
    /*************************************************************************/
    /*                        Trace System Properties                        */
    /*************************************************************************/

    public final static String MSG_GROUP_LISTS      = "SIBMatchSpace";
    public final static String MSG_GROUP_UTILS      = "SIBMatchSpace";

    public final static String MSG_BUNDLE = "com.ibm.ws.sib.matchspace.CWSIHMessages";
}
