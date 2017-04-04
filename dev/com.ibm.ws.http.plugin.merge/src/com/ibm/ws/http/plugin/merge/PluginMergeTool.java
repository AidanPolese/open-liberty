package com.ibm.ws.http.plugin.merge;
/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */


/**
 * Merge generated plugin-cfg.xml files
 */
public interface PluginMergeTool {

    /**
     * Merges the list of plugin-cfg.xml files
     * 
     * @param argv
     */
    public void merge(String[] argv);
}
