/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2012
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.webcontainer.util;

/**
 *
 */
public class EDRPathEntry {
    private String path = null;
    private boolean containerRelative = false;
    
    public EDRPathEntry(String filePath, boolean inContainer){
        path = filePath;
        containerRelative = inContainer;
    }
    
    public String getPath(){
        return path;
    }
    
    public boolean inContainer(){
        return containerRelative;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "EDRPathEntry [containerRelative=" + containerRelative + ", path=" + path + "]";
    }
}
