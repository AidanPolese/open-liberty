/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.permissions;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.javaee.dd.permissions.PermissionsConfig;
import com.ibm.ws.javaee.ddmodel.DDParser.ParseException;
import com.ibm.ws.javaee.version.JavaEEVersion;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

/**
 *
 */
public final class PermissionsAdapter implements ContainerAdapter<PermissionsConfig> {

    private ServiceReference<JavaEEVersion> versionRef;
    private volatile Version platformVersion = JavaEEVersion.DEFAULT_VERSION;

    public synchronized void setVersion(ServiceReference<JavaEEVersion> reference) {
        versionRef = reference;
        platformVersion = Version.parseVersion((String) reference.getProperty("version"));
    }

    public synchronized void unsetVersion(ServiceReference<JavaEEVersion> reference) {
        if (reference == this.versionRef) {
            versionRef = null;
            platformVersion = JavaEEVersion.DEFAULT_VERSION;
        }
    }

    @FFDCIgnore(ParseException.class)
    @Override
    public PermissionsConfig adapt(Container root, OverlayContainer rootOverlay, ArtifactContainer artifactContainer, Container containerToAdapt) throws UnableToAdaptException {
        Entry ddEntry = containerToAdapt.getEntry(PermissionsConfig.DD_NAME);
        if (ddEntry != null) {
            try {
                PermissionsConfigDDParser ddParser = new PermissionsConfigDDParser(containerToAdapt, ddEntry);
                PermissionsConfig permissionsConfig = ddParser.parse();
                return permissionsConfig;
            } catch (ParseException e) {
                throw new UnableToAdaptException(e);
            }
        }
        return null;
    }
}
