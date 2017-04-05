// IBM Confidential OCO Source Material
// Copyright IBM Corp. 2006, 2013
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d392996   EJB3      20060930 leealber : Initial Release
// RTC113511 RWAS90    20131009 bkail    : Use public visibility
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

/**
 * Enumeration of persistence unit scope used in JPA service processing.
 */
public enum JPAPuScope
{
    // Persistence xmls defined in the persistance archive.
    EJB_Scope,
    // Persistence xmls defined in the WebApp module.
    Web_Scope,
    // Persistence xmls defined in the EJB module.
    EAR_Scope,
    // Persistence xmls defined in the Application Client module.
    Client_Scope;
}
