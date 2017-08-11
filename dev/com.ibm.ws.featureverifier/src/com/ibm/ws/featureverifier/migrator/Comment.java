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

class Comment extends Ignore {

    private final String comment;
    private final MissingOptions missingOptions;

    /**
     * @param value
     * @param feature
     * @param ifMissing
     */
    public Comment(Section s, String feature, String value, MissingOptions missingOptions) {
        super(s, feature);
        this.comment = value;
        this.missingOptions = missingOptions;
    }

    @Override
    public String toString() {
        return "  <!-- " + comment + " -->";
    }

    @Override
    public boolean appliesTo(Set<String> aggregate) {
        if (missingOptions != null && !missingOptions.appliesTo(aggregate))
            return false;

        return super.appliesTo(aggregate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        if (!(o instanceof Comment))
            return false;

        Comment c = (Comment) o;

        return c.toString().equals(toString());

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((feature == null) ? 0 : feature.hashCode());

        return result;
    }

}