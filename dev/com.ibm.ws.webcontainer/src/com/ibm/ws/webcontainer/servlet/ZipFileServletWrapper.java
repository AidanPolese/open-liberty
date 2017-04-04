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
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.ws.webcontainer.extension.DefaultExtensionProcessor;
import com.ibm.ws.webcontainer.util.ZipFileResource;
import com.ibm.ws.webcontainer.webapp.WebAppDispatcherContext;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;


public class ZipFileServletWrapper extends FileServletWrapper {
    private long fileSize = -1; // PM92967
	private ZipFileResource zipFileResource;
    
    public ZipFileServletWrapper(IServletContext parent, DefaultExtensionProcessor parentProcessor, ZipFileResource zipFileResource)
    {
        super(parent, parentProcessor);
        this.zipFileResource = zipFileResource;
        isZip=true;
    }

	public String getServletName()
    {
        return "Zip File wrapper";
    }

    public String getName()
    {
        return "Zip File wrapper";
    }

    protected InputStream getInputStream() throws IOException {
        return this.zipFileResource.getIS();
    }
    
    protected long getLastModified() {
    	long time = zipFileResource.getZipEntry().getTime();
        return time;
    }
    
    protected int getContentLength() {
        return getContentLength(true);
    }
    
    // PM92967, added method
    protected long getFileSize(boolean update) {
        if (fileSize == -1 || update) {
                fileSize = zipFileResource.getZipEntry().getSize();
        }
        
        return fileSize;
    }
    
	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.servlet.IServletWrapper#setParent(com.ibm.wsspi.webcontainer.servlet.IServletContext)
	 */
	public void setParent(IServletContext parent)
	{
		// nothing

	}
	// begin 268176    Welcome file wrappers are not checked for resource existence    WAS.webcontainer
	public boolean isAvailable (){
		return new File(zipFileResource.getZipFile().getName()).exists();
	}
	// end 268176    Welcome file wrappers are not checked for resource existence    WAS.webcontainer

	@Override
	protected RandomAccessFile getRandomAccessFile() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void handleRequest(ServletRequest req, ServletResponse res, WebAppDispatcherContext dispatchContext) throws Exception {
		try {
			super.handleRequest(req, res,dispatchContext);
		} finally {
	        if ( System.getSecurityManager() != null){
	        	try {
			         AccessController.doPrivileged(new PrivilegedExceptionAction<Object>()  {
			             public Object run() throws IOException {
			            	 zipFileResource.getZipFile().close();
			                 return null;
			             }
			        });
	        	} catch (PrivilegedActionException pae) {
	        		throw new IOException(pae.getMessage());
	        	}
		    } else {
			    zipFileResource.getZipFile().close();
		    }    
		}
	}
}
