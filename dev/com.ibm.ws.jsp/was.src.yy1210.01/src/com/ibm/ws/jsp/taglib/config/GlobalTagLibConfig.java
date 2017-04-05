//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.taglib.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GlobalTagLibConfig {
    private String jarName = null;
    private URL jarURL = null;
    private ClassLoader classloader = null;
    private List tldPathList = null;
    
    public GlobalTagLibConfig() {
        tldPathList = new ArrayList();
    }
    
    public String getJarName() {
        return jarName;
    }

    public List getTldPathList() {
        return tldPathList;
    }
    
    public void setJarName(String string) {
        jarName = string;
    }

	public void setJarURL(URL jarURL) {
		this.jarURL = jarURL;
	}
    
	public URL getJarURL() {
		return jarURL;
	}
    
	public ClassLoader getClassloader() {
		return classloader;
	}
    
	public void setClassloader(ClassLoader classloader) {
		this.classloader = classloader;
	}
}
