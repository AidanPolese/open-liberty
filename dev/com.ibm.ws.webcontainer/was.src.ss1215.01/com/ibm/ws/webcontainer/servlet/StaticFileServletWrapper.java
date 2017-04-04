// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2008
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//       508566          04/09/08      mmolden            PERF: File Serving Performance improvement

package com.ibm.ws.webcontainer.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;


public class StaticFileServletWrapper extends FileServletWrapper {
    private File file;
    RandomAccessFile raf = null;
    private int contentLength = -1;
    
    public StaticFileServletWrapper(IServletContext parent, DefaultExtensionProcessor parentProcessor, File file)
    {
        super(parent, parentProcessor);
        this.file = file;
    }
    
    public String getServletName()
    {
        return "Static File wrapper";
    }
    
    public String getName()
    {
        return "Static File wrapper";
    }
    
    protected InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
    
    protected RandomAccessFile getRandomAccessFile() throws IOException {
    	raf= new RandomAccessFile(file,"rw");
    	return raf;
    }
    
    protected long getLastModified() {
        return file.lastModified();
    }
    
    protected int getContentLength(boolean update) {
    	if (update){
	    	contentLength = (int)file.length();
	        return contentLength;
    	} else {
    		if (contentLength==-1){
    			contentLength = (int)file.length();
    	        return contentLength;
    		}
    		else {
    			return contentLength;
    		}
    	}
    }
    
    protected int getContentLength(){
    	return getContentLength(true);
    }
	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.servlet.IServletWrapper#setParent(com.ibm.wsspi.webcontainer.servlet.IServletContext)
	 */
	public void setParent(IServletContext parent)
	{
		// do nothing

	}
	
	// begin 268176    Welcome file wrappers are not checked for resource existence    WAS.webcontainer
	public boolean isAvailable (){
		return this.file.exists();
	}
	// end 268176    Welcome file wrappers are not checked for resource existence    WAS.webcontainer




}
