/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.install;

public class RepositoryConfig {
    private final String id;
    private final String url;
    private final String apiKey;
    private final String user;
    private final String userPwd;

    public RepositoryConfig(String id, String url, String apiKey, String user, String userPwd) {
        this.id = id;
        this.url = url;
        this.apiKey = apiKey == null ? "0" : apiKey;
        this.user = user == null ? null : user.trim();
        this.userPwd = userPwd;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getUser() {
        return user;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public boolean isLibertyRepository() {
        return id.equalsIgnoreCase(RepositoryConfigUtils.WLP_REPO) && url == null;
    }

    @Override
    public String toString() {
        return "RepositoryConfig(" + id + "," + url + "," + user + "," + (userPwd == null ? "null)" : "******)");
    }
}
