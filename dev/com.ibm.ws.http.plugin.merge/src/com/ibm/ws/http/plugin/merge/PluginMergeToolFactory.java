/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.http.plugin.merge;

import com.ibm.ws.http.plugin.merge.internal.PluginMergeToolImpl;

/**
 *
 */
public class PluginMergeToolFactory {

    public static final PluginMergeTool mergeTool = new PluginMergeToolImpl();

    public static PluginMergeTool getMergeToolInstance() {
        return mergeTool;
    }

}
