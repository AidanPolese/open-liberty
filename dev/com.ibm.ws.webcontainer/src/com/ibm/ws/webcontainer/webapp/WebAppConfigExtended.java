/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2015
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.webapp;

import com.ibm.wsspi.webcontainer.metadata.WebModuleMetaData;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

/**
 * RTC 160610. Contains methods moved from WebAppConfig which should not be SPI.  
 */
public interface WebAppConfigExtended extends WebAppConfig {

    /**
     * Returns the Module metadata associated with this config
     * 
     * @return
     */
    public WebModuleMetaData getMetaData();

}