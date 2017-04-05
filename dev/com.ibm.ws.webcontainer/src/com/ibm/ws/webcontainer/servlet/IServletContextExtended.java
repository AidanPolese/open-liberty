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
package com.ibm.ws.webcontainer.servlet;

import com.ibm.ws.webcontainer.session.IHttpSessionContext;
import com.ibm.wsspi.webcontainer.collaborator.ICollaboratorHelper;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 *  RTC 160610. Contains methods moved from IServletContext which should not be spi.
 */
public interface IServletContextExtended extends IServletContext {

    public IHttpSessionContext getSessionContext();
    
    public ICollaboratorHelper getCollaboratorHelper();

}
