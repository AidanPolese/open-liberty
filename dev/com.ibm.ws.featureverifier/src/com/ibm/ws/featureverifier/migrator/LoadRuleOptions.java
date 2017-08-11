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

/**
 *
 */
public class LoadRuleOptions {

    private final boolean onlyForLoadRules;
    private final boolean allowUnusedForLoadRules;

    LoadRuleOptions(String only, String allow) {
        onlyForLoadRules = Boolean.valueOf(only);
        allowUnusedForLoadRules = Boolean.valueOf(allow);
    }

    @Override
    public String toString() {
        return " onlyForLoadRules=\"" + onlyForLoadRules + "\" allowUnusedForLoadRules=\"" + allowUnusedForLoadRules + "\"";
    }
}
