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
package com.ibm.ws.featureverifier.migrator;

import java.util.Set;

import com.ibm.ws.featureverifier.migrator.IgnoreMigrator.Section;

abstract class Ignore {
    protected final String feature;
    private final Section section;

    public Ignore(Section s, String feature) {
        this.section = s;
        this.feature = feature;
    }

    /**
     * @return
     */
    public Section getSection() {
        return this.section;
    }

    /**
     * @param aggregate
     * @return
     */
    public boolean appliesTo(Set<String> aggregate) {
        // If the feature name is null, include it in every config
        if (feature == null)
            return true;

        return aggregate.contains(feature);
    }
}