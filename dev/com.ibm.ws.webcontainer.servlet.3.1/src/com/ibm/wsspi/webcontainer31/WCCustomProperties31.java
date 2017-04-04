/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2014
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.wsspi.webcontainer31;

import com.ibm.ws.webcontainer.WebContainer;
import com.ibm.wsspi.tcpchannel.TCPReadRequestContext;
import com.ibm.wsspi.webcontainer.WCCustomProperties;

/**
 * A new class which houses the servlet 3.1 custom properties
 */
public class WCCustomProperties31 extends WCCustomProperties {
    
    static {
        setCustomPropertyVariables(); //initilizes all the variables
    }
    
    public static int UPGRADE_READ_TIMEOUT; //The timeout to use when the request has been upgraded and a read is happening
    public static int UPGRADE_WRITE_TIMEOUT; //The timeout to use when the request has been upgraded and a write is happening
    
    public static void setCustomPropertyVariables() {
        
        UPGRADE_READ_TIMEOUT = Integer.valueOf(customProps.getProperty("upgradereadtimeout", Integer.toString(TCPReadRequestContext.NO_TIMEOUT))).intValue();
        UPGRADE_WRITE_TIMEOUT = Integer.valueOf(customProps.getProperty("upgradewritetimeout", Integer.toString(TCPReadRequestContext.NO_TIMEOUT))).intValue();
    }
}
