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

import java.util.HashSet;
import java.util.Set;

class IgnoreBlock {
    final String forFeature;
    final Set<Ignore> ignores = new HashSet<Ignore>();

    public IgnoreBlock(String feature) {
        this.forFeature = feature;
    }

//    public void addMessageIgnore(String regex) {
//        Ignore i = new MessageIgnore(regex, forFeature);
//        ignores.add(i);
//    }
//
//    public void addComment(String value) {
//        Comment c = new Comment(value, forFeature);
//        ignores.add(c);
//    }
//
//    public void addPackageIgnore(String regex, boolean baseline, boolean runtime) {
//        PackageIgnore pi = new PackageIgnore(regex, forFeature, baseline, runtime);
//        ignores.add(pi);
//    }

    /**
     * @return
     */
    public int getSize() {
        return ignores.size();
    }

    /**
     * @param value
     * @return
     */
    public boolean appliesTo(String value) {
        return ((forFeature != null) && forFeature.equals(value));
    }

    /**
     * @param candidate
     */
    public void add(IgnoreBlock candidate) {
        if (!appliesTo(candidate.forFeature)) {
            // Make sure we're merging an IgnoreBlock for the same feature
            return;
        }

        ignores.addAll(candidate.ignores);

    }

}