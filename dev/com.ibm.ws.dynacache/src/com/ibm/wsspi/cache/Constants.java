//1.2, 3/14/06
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
package com.ibm.wsspi.cache;

/**
 * This class provides Dynacache constants that are used by other components.
 * @ibm-spi 
 */
public class Constants {
   
    /**
     * When an attribute with this name is set to true by the WebContainer it informs Dynacache
     * that the current request is a Remote Request Dispatcher Request and buffering should be
     * used for the next include.
     */
     public static final String IBM_DYNACACHE_RRD_BUFFERING = "IBM-DYNACACHE-RRD-BUFFERING";
	
    /**
     * The Remote Request Dispatcher Rules are added to a HashMap and then set as a request attribute
     * using this name.  This HashMap is used by the WebContainer.
     */
     public static final String IBM_DYNACACHE_RRD_ESI = "IBM-DYNACACHE-RRD-ESI";
	
    /**
     * Used by the WebContainer to get the locale Remote Request Dispatcher Rule from the HashMap
     */
     public static final String IBM_DYNACACHE_RRD_LOCALE = "locale";
	
    /**
     * Used by the WebContainer to get the requestType Remote Request Dispatcher Rule from the HashMap
     */
     public static final String IBM_DYNACACHE_RRD_REQUEST_TYPE = "requestType";
	
}