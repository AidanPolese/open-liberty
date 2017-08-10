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

class MessageIgnore extends Ignore {

    protected final String regex;
    private final LoadRuleOptions loadRuleOptions;
    private final MissingOptions missingOptions;

    /**
     * @param s the section where the ignore lives
     * @param feature the feature the ignore applies to
     * @param value the regular expression that matches the error
     */
    public MessageIgnore(Section s, String feature, String value, MissingOptions missing, LoadRuleOptions loadRules) {
        super(s, feature);
        this.regex = value;
        this.loadRuleOptions = loadRules;
        this.missingOptions = missing;
    }

    @Override
    public boolean appliesTo(Set<String> aggregate) {

        if (missingOptions != null && !missingOptions.appliesTo(aggregate))
            return false;

        return super.appliesTo(aggregate);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("  <message feature=");
        builder.append(IgnoreConstants.QUOTE);
        builder.append(feature);
        builder.append(IgnoreConstants.QUOTE);

        if (missingOptions != null) {
            builder.append(missingOptions.toString());
        }

        if (loadRuleOptions != null) {
            builder.append(loadRuleOptions.toString());
        }

        builder.append(">");
        builder.append(regex);
        builder.append("</message>");

        return builder.toString();
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

        if (!(o instanceof MessageIgnore))
            return false;

        MessageIgnore ignore = (MessageIgnore) o;

        if (!feature.equals(ignore.feature))
            return false;

        return ignore.toString().equals(toString());

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
        result = prime * result + ((regex == null) ? 0 : regex.hashCode());
        result = prime * result + ((feature == null) ? 0 : feature.hashCode());

        return result;
    }

}