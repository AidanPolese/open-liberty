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
package com.ibm.ws.webcontainer.security.metadata;

import java.util.List;

/**
 * Represents a collection of security constraint objects.
 */
public class SecurityConstraintCollectionImpl implements SecurityConstraintCollection {

    private final List<SecurityConstraint> securityConstraints;

    public SecurityConstraintCollectionImpl(List<SecurityConstraint> securityConstraints) {
        this.securityConstraints = securityConstraints;
    }

    /** {@inheritDoc} */
    @Override
    public MatchResponse getMatchResponse(String resourceName, String method) {
        if (securityConstraints == null || securityConstraints.isEmpty()) {
            return MatchResponse.NO_MATCH_RESPONSE;
        }

        return MatchingStrategy.match(this, resourceName, method);
    }

    @Override
    public List<SecurityConstraint> getSecurityConstraints() {
        return securityConstraints;
    }

    public void addSecurityConstraints(List<SecurityConstraint> securityConstraints) {
        this.securityConstraints.addAll(securityConstraints);
    }

}
