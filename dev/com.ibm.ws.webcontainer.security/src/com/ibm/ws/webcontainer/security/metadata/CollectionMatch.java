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

/**
 * Represents a match in the WebResourceCollection.
 * It contains the pattern that was matched, the match type.
 */
public class CollectionMatch {

    public static final CollectionMatch RESPONSE_NO_MATCH = new CollectionMatch("", MatchType.NO_MATCH);
    public static final CollectionMatch RESPONSE_DENY_MATCH = new CollectionMatch("", MatchType.DENY_MATCH);
    public static final CollectionMatch RESPONSE_DENY_MATCH_BY_OMISSION = new CollectionMatch("", MatchType.DENY_MATCH_BY_OMISSION);
    public static final CollectionMatch RESPONSE_EXACT_MATCH_BY_OMISSION = new CollectionMatch("", MatchType.EXACT_MATCH_BY_OMISSION);
    public static final CollectionMatch RESPONSE_PERMIT = new CollectionMatch("", MatchType.PERMIT);

    private final String urlPattern;
    private final MatchType matchType;

    public CollectionMatch(String urlPattern, MatchType matchType) {
        this.urlPattern = urlPattern;
        this.matchType = matchType;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public boolean isExactMatch() {
        return MatchType.EXACT_MATCH.equals(matchType);
    }

    public boolean isPathMatch() {
        return MatchType.PATH_MATCH.equals(matchType);
    }

    public boolean isExtensionMatch() {
        return MatchType.EXTENSION_MATCH.equals(matchType);
    }

    public boolean isDenyMatch() {
        return MatchType.DENY_MATCH.equals(matchType);
    }

    public boolean isDenyMatchByOmission() {
        return MatchType.DENY_MATCH_BY_OMISSION.equals(matchType);
    }

    public boolean isPermitMatch() {
        return MatchType.PERMIT.equals(matchType);
    }

    public enum MatchType {
        EXACT_MATCH,
        PATH_MATCH,
        EXTENSION_MATCH,
        NO_MATCH,
        DENY_MATCH,
        DENY_MATCH_BY_OMISSION,
        EXACT_MATCH_BY_OMISSION,
        PERMIT
    }
}
