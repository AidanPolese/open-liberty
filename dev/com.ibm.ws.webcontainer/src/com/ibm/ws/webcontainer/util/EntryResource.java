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

import java.io.IOException;
import java.io.InputStream;

import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;

/**
 *
 */
public class EntryResource implements ExtDocRootFile{

    Entry entry = null;
    
    public EntryResource(Entry contained){
        entry = contained;
    }

    public InputStream getIS() throws IOException {
        try {
            return entry.adapt(InputStream.class);
        } catch (UnableToAdaptException e) {
            return null;
        }
    }

    public long getLastModified() {
        return entry.getLastModified();
    }

    public String getPath() {
        return entry.getPath();
    }
    
    public Entry getEntry(){
        return entry;
    }

}
