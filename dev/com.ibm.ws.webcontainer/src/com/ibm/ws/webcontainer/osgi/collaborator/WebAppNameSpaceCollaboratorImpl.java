/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.webcontainer.osgi.collaborator;

import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;
import com.ibm.wsspi.webcontainer.collaborator.IWebAppNameSpaceCollaborator;

/*
 * In WAS this class manages the association of the ComponentMetaData object with the thread 
 * when other components (or application code) are invoked. It uses the deprecated getThreadContext
 * method and operates directly on the thread context.  
 */

public class WebAppNameSpaceCollaboratorImpl implements IWebAppNameSpaceCollaborator
{
    private final ComponentMetaDataAccessorImpl cmdai;
    
    public WebAppNameSpaceCollaboratorImpl(){
        cmdai = ComponentMetaDataAccessorImpl.getComponentMetaDataAccessor();
    }

    /*
     * Argument type is 'Object' to fit with common webcontainer code.
     */
    public void preInvoke(Object compMetaData)
    {
        cmdai.beginContext((ComponentMetaData)compMetaData);   
    }
 
    public void postInvoke()
    {
        cmdai.endContext();
    }
}