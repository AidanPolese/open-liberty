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

/**
 *
 */
public class MissingOptions {

    private final String[] onlyIfMissing;
    private final String[] onlyWhenIncluded;

    MissingOptions(String missing, String included) {
        if (missing == null) {
            onlyIfMissing = new String[0];
        } else {
            this.onlyIfMissing = missing.split(",");
        }

        if (included == null) {
            this.onlyWhenIncluded = new String[0];
        } else {
            this.onlyWhenIncluded = included.split(",");
        }
    }

    /**
     * @param aggregate
     * @return
     */
    boolean appliesTo(Set<String> aggregate) {
        for (String ifMissing : onlyIfMissing) {
            if (aggregate.contains(ifMissing))
                return false;
        }

        for (String included : onlyWhenIncluded) {
            if (aggregate.contains(included))
                return true;
        }

        // If length > 0, we failed to find a whenIncluded above.
        // If length == 0, no whenIncluded is specified, so we're good. 
        return (onlyWhenIncluded.length == 0);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (onlyIfMissing.length > 0) {
            builder.append(" ifMissing=");
            builder.append(IgnoreConstants.QUOTE);
            for (String missing : onlyIfMissing) {
                builder.append(missing);
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(IgnoreConstants.QUOTE);
        }

        if (onlyWhenIncluded.length > 0) {
            builder.append(" whenIncluded=");
            builder.append(IgnoreConstants.QUOTE);
            for (String included : onlyWhenIncluded) {
                builder.append(included);
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(IgnoreConstants.QUOTE);
        }

        return builder.toString();
    }
}
