// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.webcontainer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.wsspi.webcontainer.logging.LoggerFactory;

class FileResource implements ExtDocRootFile{
	private static Logger logger = LoggerFactory.getInstance().getLogger("com.ibm.ws.webcontainer.util");
	private static final String CLASS_NAME="com.ibm.ws.webcontainer.util.FileResource";
	
	private File file;
	protected FileResource ( File file){
		this.file = file;
	}
	public InputStream getIS() throws IOException{
		if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable (Level.FINE)) 
			logger.logp(Level.FINE, CLASS_NAME,"getIS", "return FileResource inputstream file -->" + file);
		return new FileInputStream (file);
	}
	public File  getMatch(){
		return file;
	}

}