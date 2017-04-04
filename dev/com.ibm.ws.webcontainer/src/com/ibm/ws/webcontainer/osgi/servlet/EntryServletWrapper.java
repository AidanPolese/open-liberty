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
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//       PM92967         08/12/13      bowitten             ISSUES WITH DOWNLOAD OF FILES GREATER THAN 8GB.


package com.ibm.ws.webcontainer.osgi.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor;
import com.ibm.ws.webcontainer.servlet.FileServletWrapper;
import com.ibm.wsspi.adaptable.module.Entry;
import com.ibm.wsspi.adaptable.module.UnableToAdaptException;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;

/**
 *
 */
public class EntryServletWrapper extends FileServletWrapper {

    private Entry entry;
    private long fileSize = -1; // PM92967
    
    public EntryServletWrapper(IServletContext parent, DefaultExtensionProcessor parentProcessor, Entry entry)
    {
        super(parent, parentProcessor);
        this.entry = entry;
    }
    
    public String getServletName()
    {
        return "Entry wrapper";
    }
    
    public String getName()
    {
        return "Entry wrapper";
    }
    
    protected InputStream getInputStream() throws IOException {
        try {
            return entry.adapt(InputStream.class);
        } catch (UnableToAdaptException e) {
            throw new IllegalStateException(e);
        }
    }
    
    protected RandomAccessFile getRandomAccessFile() throws IOException {
        return null;
    }
    
    protected long getLastModified() {
        return entry.getLastModified();
    }
    
    // PM92967, added method
    protected long getFileSize(boolean update) {
        if (fileSize == -1 || update) {
                fileSize = entry.getSize();
        }
        
        return fileSize;
    }
        /* (non-Javadoc)
         * @see com.ibm.wsspi.webcontainer.servlet.IServletWrapper#setParent(com.ibm.wsspi.webcontainer.servlet.IServletContext)
         */
        public void setParent(IServletContext parent)
        {
                // do nothing

        }
        
        public boolean isAvailable (){
                return this.entry.getSize() != 0;
        }
}
