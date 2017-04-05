package com.ibm.ws.objectManager;

/*
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-H88, 5724-J08, 5724-I63, 5655-W65, 5724-H89, 5722-WE2   Copyright IBM Corp., 2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 *
 * Change activity:
 *
 *  Reason           Date    Origin   Description
 * --------------- -------- -------- ------------------------------------------
 *  251161         07/04/05 gareth    Add ObjectManager code to CMVC
 *  284293         01/07/05 gareth    Add ObjectManager trace group
 *  SIB0003.ms.25  08/11/05 gareth    Remove dependency on SIB
 *  343689         03/04/06 gareth    Improve ObjectManager trace output
 * ============================================================================
 */

public interface ObjectManagerConstants
{
    /*************************************************************************/
    /* Trace System Properties */
    /*************************************************************************/

    public final static String MSG_GROUP = "ObjectManager";
    public final static String MSG_GROUP_LOG = "ObjectManagerLog";
    public final static String MSG_GROUP_STORE = "ObjectManagerStore";
    public final static String MSG_GROUP_TRAN = "ObjectManagerTransactions";
    public final static String MSG_GROUP_LISTS = "ObjectManagerLists";
    public final static String MSG_GROUP_MAPS = "ObjectManagerMaps";
    public final static String MSG_GROUP_OBJECTS = "ObjectManagerObjects";
    public final static String MSG_GROUP_EXCEPTIONS = "ObjectManagerExceptions";
    public final static String MSG_GROUP_UTILS = "ObjectManagerUtils";

    public final static String MSG_BUNDLE = "com.ibm.ws.objectManager.CWSOMMessages";
}
