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
package com.ibm.ws.kernel.feature.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ibm.ws.kernel.feature.provisioning.ProvisioningFeatureDefinition;
import com.ibm.ws.kernel.feature.resolver.FeatureResolver.Repository;

/**
 *
 */
public class RestrictedFeatureRespository implements Repository {
    private final Repository repo;
    private final Collection<String> restricted;
    private final Collection<String> restrictedAttempts = new ArrayList<String>();

    public RestrictedFeatureRespository(Repository repo, Collection<String> restricted) {
        this.repo = repo;
        this.restricted = restricted;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.kernel.feature.resolver.FeatureResolver.Repository#getAutoFeatures()
     */
    @Override
    public Collection<ProvisioningFeatureDefinition> getAutoFeatures() {
        return repo.getAutoFeatures();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.kernel.feature.resolver.FeatureResolver.Repository#getFeature(java.lang.String)
     */
    @Override
    public ProvisioningFeatureDefinition getFeature(String featureName) {
        ProvisioningFeatureDefinition result = repo.getFeature(featureName);
        if (result == null) {
            return null;
        }
        if (restricted.contains(result.getSymbolicName())) {
            // record the restricted attemp
            if (!restrictedAttempts.contains(result.getSymbolicName())) {
                restrictedAttempts.add(result.getSymbolicName());
            }
            return null;
        }
        return result;
    }

    Collection<String> getRestrictedFeatureAttempts() {
        return restrictedAttempts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.kernel.feature.resolver.FeatureResolver.Repository#getConfiguredTolerates(java.lang.String)
     */
    @Override
    public List<String> getConfiguredTolerates(String baseSymbolicName) {
        return repo.getConfiguredTolerates(baseSymbolicName);
    }

}
