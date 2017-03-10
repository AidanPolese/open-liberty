/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.install.CancelException;
import com.ibm.ws.install.InstallException;
import com.ibm.ws.kernel.feature.provisioning.ProvisioningFeatureDefinition;
import com.ibm.ws.repository.resources.RepositoryResource;

public abstract class AbstractDirector {

    static final String DEFAULT_TO_EXTENSION = "default";

    Product product;
    EventManager eventManager;
    Logger logger = null;

    boolean enableEvent = true;

    AbstractDirector(Product product, EventManager eventManager, Logger logger) {
        this.product = product;
        this.eventManager = eventManager;
        this.logger = logger;
    }

    void fireProgressEvent(int state, int progress, String message) {
        try {
            fireProgressEvent(state, progress, message, false);
        } catch (InstallException e) {
        }
    }

    void fireProgressEvent(int state, int progress, String message, boolean allowCancel) throws InstallException {
        log(Level.FINEST, message);
        if (enableEvent) {
            try {
                eventManager.fireProgressEvent(state, progress, message);
            } catch (CancelException ce) {
                if (allowCancel)
                    throw ce;
                else
                    log(Level.FINEST, "fireProgressEvent caught cancel exception: " + ce.getMessage());
            } catch (Exception e) {
                log(Level.FINEST, "fireProgressEvent caught exception: " + e.getMessage());
            }
        }
    }

    void log(Level level, String msg) {
        if (msg != null && !msg.isEmpty())
            logger.log(level, msg);
    }

    void log(Level level, String msg, Exception e) {
        if (e != null)
            logger.log(level, msg, e);
    }

    boolean isEmpty(Map<String, List<List<RepositoryResource>>> installResources) {
        if (installResources == null)
            return true;
        for (List<List<RepositoryResource>> targetList : installResources.values()) {
            for (List<RepositoryResource> mrList : targetList) {
                if (!mrList.isEmpty())
                    return false;
            }
        }
        return true;
    }

    boolean isEmpty(List<List<RepositoryResource>> installResources) {
        if (installResources == null)
            return true;
        for (List<RepositoryResource> mrList : installResources) {
            if (!mrList.isEmpty())
                return false;
        }
        return true;
    }

    boolean containFeature(Map<String, ProvisioningFeatureDefinition> installedFeatures, String feature) {
        if (installedFeatures.containsKey(feature))
            return true;
        for (ProvisioningFeatureDefinition pfd : installedFeatures.values()) {
            String shortName = InstallUtils.getShortName(pfd);
            if (shortName != null && shortName.equalsIgnoreCase(feature))
                return true;
        }
        return false;
    }

    Collection<String> getFeaturesToInstall(Collection<String> requiredFeatures, boolean download) {
        Collection<String> featuresToInstall = new ArrayList<String>(requiredFeatures.size());
        if (!requiredFeatures.isEmpty()) {
            Map<String, ProvisioningFeatureDefinition> installedFeatures = product.getFeatureDefinitions();
            for (String feature : requiredFeatures) {
                if (download || !containFeature(installedFeatures, feature))
                    featuresToInstall.add(feature);
            }
        }
        return featuresToInstall;
    }

    boolean containScript(List<File> filesInstalled) {
        for (File f : filesInstalled) {
            String path = f.getAbsolutePath().toLowerCase();
            if (path.contains("/bin/") || path.contains("\\bin\\")) {
                return true;
            }
        }
        return false;
    }
}
