/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.wim;

import java.util.Map;

/**
 * TODO fold into ConfiguredRepository
 */
public interface RepositoryConfig {

    boolean isReadOnly();

    void resetConfig();

    String getReposId();

    Map<String, String> getRepositoryBaseEntries();

    String[] getRepositoriesForGroups();

}
