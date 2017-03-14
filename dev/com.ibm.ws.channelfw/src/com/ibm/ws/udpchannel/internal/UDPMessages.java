//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//d306341		mjohn256  Add RAS logging support to UDP Channel.
//-------------------------------------------------------------------------------
package com.ibm.ws.udpchannel.internal;

/**
 * Definitions for RAS tracing variables and any translated message.
 */
public interface UDPMessages {

    /**
     * Controls the logical grouping assigned for channel messages and traces.
     */
    String TR_GROUP = "UDPChannel";

    /**
     * Controls the logical grouping assigned for proxy app channel messages and
     * traces.
     */
    String TR_MSGS = "com.ibm.ws.udpchannel.internal.resources.UDPMessages";

}
