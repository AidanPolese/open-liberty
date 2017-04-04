/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2011
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.osgi.metadata;

import com.ibm.websphere.csi.J2EEName;
import com.ibm.ws.container.service.metadata.extended.IdentifiableComponentMetaData;
import com.ibm.ws.runtime.metadata.MetaDataImpl;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.wsspi.webcontainer.metadata.WebCollaboratorComponentMetaData;
import com.ibm.wsspi.webcontainer.metadata.WebModuleMetaData;

/**
 *
 */
public class WebCollaboratorComponentMetaDataImpl extends MetaDataImpl implements WebCollaboratorComponentMetaData, IdentifiableComponentMetaData
{

   private J2EEName j2eeName;
   private WebModuleMetaData webModuleMetaData;

    /**
 * @param webModuleMetaData
 */
    public WebCollaboratorComponentMetaDataImpl(WebModuleMetaData webModuleMetaData) {
        super(0);
        this.webModuleMetaData = webModuleMetaData;
    }

    public void setJ2EEName(J2EEName j2eeName) {
        this.j2eeName = j2eeName;
    }

    @Override
    public J2EEName getJ2EEName() {
        return j2eeName;
    }

    /** {@inheritDoc} */
    @Override
    public ModuleMetaData getModuleMetaData() {
        return this.webModuleMetaData;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.ibm.ws.container.service.metadata.extended.IdentifiableComponentMetaData#getPersistentIdentifier()
     */
    @Override
    public String getPersistentIdentifier() {
        return "WEB#" + webModuleMetaData.getJ2EEName().toString();
    }
}
