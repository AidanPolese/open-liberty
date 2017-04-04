/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.jsf;

import org.osgi.framework.ServiceReference;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.javaee.dd.jsf.FacesConfig;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.version.FacesVersion;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.EntryAdapter;
import com.ibm.wsspi.artifact.ArtifactEntry;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */
public class FacesConfigEntryAdapter implements EntryAdapter<FacesConfig> {

    private static final int DEFAULT_JSF_LOADED_VERSION = FacesConfig.VERSION_2_0;

    private ServiceReference<FacesVersion> versionRef;
    private volatile int version = DEFAULT_JSF_LOADED_VERSION;

    public synchronized void setVersion(ServiceReference<FacesVersion> reference) {

        versionRef = reference;
        version = (Integer) reference.getProperty("version");
    }

    public synchronized void unsetVersion(ServiceReference<FacesVersion> reference) {
        if (reference == this.versionRef) {
            versionRef = null;
            version = DEFAULT_JSF_LOADED_VERSION;
        }
    }

    @FFDCIgnore(ParseException.class)
    @Override
    public FacesConfig adapt(Container root, OverlayContainer rootOverlay, ArtifactEntry artifactEntry, Entry entryToAdapt) throws UnableToAdaptException {
        if (entryToAdapt != null) {
            try {
                FacesConfigDDParser ddParser = new FacesConfigDDParser(root, entryToAdapt, version);
                FacesConfig facesConfig = ddParser.parse();
                return facesConfig;
            } catch (ParseException e) {
                throw new UnableToAdaptException(e);
            }
        }
        return null;
    }

}
