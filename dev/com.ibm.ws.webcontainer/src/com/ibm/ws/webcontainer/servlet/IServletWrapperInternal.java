/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2013
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.servlet;

import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;

/**
 *
 */
public interface IServletWrapperInternal  extends IServletWrapper {
    
    /**
     * Sets a flag if it is not set, and returns if this flag had previously been set.
     * This flag can be used to limit the number of warnings that are printed for the same servlet
     * @return true if the warning status flag was set, or false if it was already set. 
     */
    public boolean hitWarningStatus();
    


}
