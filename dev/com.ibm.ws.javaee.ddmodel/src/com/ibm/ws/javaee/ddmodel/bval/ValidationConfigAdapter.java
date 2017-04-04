/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.bval;

import com.ibm.ws.container.service.app.deploy.WebModuleInfo;
import com.ibm.ws.javaee.dd.bval.ValidationConfig;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.NonPersistentCache;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.adaptable.module.adapters.ContainerAdapter;
import com.ibm.wsspi.artifact.ArtifactContainer;
import com.ibm.wsspi.artifact.overlay.OverlayContainer;

public final class ValidationConfigAdapter implements ContainerAdapter<ValidationConfig> {

    @Override
    public ValidationConfig adapt(Container root,
                                  OverlayContainer rootOverlay,
                                  ArtifactContainer artifactContainer,
                                  Container containerToAdapt) throws UnableToAdaptException {

        NonPersistentCache cache = containerToAdapt.adapt(NonPersistentCache.class);
        WebModuleInfo moduleInfo = (WebModuleInfo) cache.getFromCache(WebModuleInfo.class);

        Entry ddEntry;
        if (moduleInfo != null) {
        	ddEntry = containerToAdapt.getEntry("WEB-INF/classes/META-INF/validation.xml");
            if (ddEntry == null) {
                ddEntry = containerToAdapt.getEntry("WEB-INF/validation.xml");
            }
        } else {
            ddEntry = containerToAdapt.getEntry("META-INF/validation.xml");
        }

        return ddEntry == null ? null : ddEntry.adapt(ValidationConfig.class);
    }

}
