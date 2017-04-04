//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.resource;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.ibm.wsspi.jsp.resource.JspInputSource;

public abstract class ResourcesImpl {
    protected JspInputSource inputSource = null;
    protected File sourceFile = null;
    protected long sourceFileTimestamp = 0;
    protected File generatedSourceFile = null;
    protected File classFile = null;
    protected String className = null;
	protected String packageName = null;
    protected File webinfClassFile = null;
    protected boolean keepgenerated = false;
    protected boolean keepGeneratedclassfiles = true;
    
    public File getClassFile() {
        return classFile;
    }

    public String getClassName() {
        return className;
    }

    public File getGeneratedSourceFile() {
        return generatedSourceFile;
    }

    public JspInputSource getInputSource() {
        return inputSource;
    }

    public boolean isOutdated() {
        return ResourceUtil.isOutdated(sourceFile, generatedSourceFile, classFile, webinfClassFile);
    }

    public void sync() {
        ResourceUtil.sync(sourceFile, generatedSourceFile, classFile, className, keepgenerated, keepGeneratedclassfiles);
        sourceFileTimestamp = sourceFile.lastModified();
    }
    
    public void setCurrentRequest(HttpServletRequest request){}

	public String getPackageName() {
		return packageName;
	}
    
    public boolean isExternallyTranslated() {
        return (sourceFileTimestamp != sourceFile.lastModified()) ? true : false;
    }
}
