/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.security.registry;

import java.util.ArrayList;
import java.util.List;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Container used by UserRegistry to return search operation results.
 * UserRegistry implementations should use this
 * to set the list of matched search entries and to indicate if there are more
 * users/groups in the UserRegistry than the limit (if applicable) requested.
 */
@Trivial
public class SearchResult {
    private final boolean hasMore;
    private final List<String> list;

    /**
     * Construct a SearchResult indicating no such results match the search criteria.
     * Initializes the internal state such that getList() returns an empty
     * List<String> and hasMore() is false().
     */
    public SearchResult() {
        this.list = new ArrayList<String>();
        this.hasMore = false;
    }

    /**
     * Construct a SearchResult containing the specified results list, and indicates if more
     * entries than the specified limit were found.
     * 
     * @param list List of Strings representing the entries which match the search criteria
     * @param hasMore Indicates there are more users/groups in the registry which match the search criteria
     */
    public SearchResult(List<String> list, boolean hasMore) {
        this.list = list;
        this.hasMore = hasMore;
    }

    /**
     * @return List of Strings representing the entries which match the search criteria
     */
    public List<String> getList() {
        return list;
    }

    /**
     * @return Indicates if there are more results in the UserRgistry which match the search criteria
     */
    public boolean hasMore() {
        return hasMore;
    }

    /**
     * {@inheritDoc} Expresses the internal state in a String-ified fashion.
     */
    @Override
    public String toString() {
        return "SearchResult hasMore=" + hasMore + " " + list.toString();
    }
}
