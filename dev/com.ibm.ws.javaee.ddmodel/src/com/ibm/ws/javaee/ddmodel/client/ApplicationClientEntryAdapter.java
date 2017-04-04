/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.client;

import org.osgi.framework.ServiceReference;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.javaee.dd.client.ApplicationClient;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.EntryAdapter;
import com.ibm.wsspi.artifact.ArtifactEntry;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

public final class ApplicationClientEntryAdapter implements EntryAdapter<ApplicationClient> {
    private static final int DEFAULT_MAX_VERSION = ApplicationClient.VERSION_7;

    private ServiceReference<ApplicationClientDDParserVersion> versionRef;
    private volatile int version = DEFAULT_MAX_VERSION;

    public synchronized void setVersion(ServiceReference<ApplicationClientDDParserVersion> reference) {

        versionRef = reference;
        version = (Integer) reference.getProperty(ApplicationClientDDParserVersion.VERSION);
    }

    public synchronized void unsetVersion(ServiceReference<ApplicationClientDDParserVersion> reference) {
        if (reference == this.versionRef) {
            versionRef = null;
            version = DEFAULT_MAX_VERSION;
        }
    }

    @FFDCIgnore(ParseException.class)
    @Override
    public ApplicationClient adapt(Container root, OverlayContainer rootOverlay, ArtifactEntry artifactEntry, Entry entryToAdapt) throws UnableToAdaptException {
        String path = artifactEntry.getPath();
        ApplicationClient appClient = (ApplicationClient) rootOverlay.getFromNonPersistentCache(path, ApplicationClient.class);
        if (appClient == null) {
            try {
                ApplicationClientDDParser ddParser = new ApplicationClientDDParser(root, entryToAdapt, version);
                appClient = ddParser.parse();
            } catch (ParseException e) {
                throw new UnableToAdaptException(e);
            }

            rootOverlay.addToNonPersistentCache(path, ApplicationClient.class, appClient);
        }

        return appClient;
    }
}
