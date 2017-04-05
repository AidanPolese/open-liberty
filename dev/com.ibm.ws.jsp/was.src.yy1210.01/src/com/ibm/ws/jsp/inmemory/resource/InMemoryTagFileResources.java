//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

// Change History
// Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson

package com.ibm.ws.jsp.inmemory.resource;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagFileInfo;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.taglib.TagLibraryInfoImpl;
import com.ibm.wsspi.jsp.context.JspCoreContext;
import com.ibm.wsspi.jsp.context.translation.JspTranslationEnvironment;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.translation.TagFileResources;

public class InMemoryTagFileResources implements TagFileResources, InMemoryResources {
    protected JspInputSource inputSource = null;
    protected File sourceFile = null;
    protected long sourceFileTimestamp = 0;
    protected File generatedSourceFile = null;
    protected String className = null;
    protected String packageName = null;
    protected CharArrayWriter generatedSourceWriter = null;
    protected Map<String,byte[]> classBytesMap = new HashMap<String,byte[]>();

    public InMemoryTagFileResources(JspInputSource inputSource, TagFileInfo tfi, JspCoreContext context, JspTranslationEnvironment env) {
        this.inputSource = inputSource;
        if (inputSource.getAbsoluteURL().getProtocol().equals("file")) {
            sourceFile = new File(context.getRealPath(inputSource.getRelativeURL()));
        }
        else {
            String file = inputSource.getContextURL().getFile();
            sourceFile = new File(file.substring(file.indexOf("file:")+5, file.indexOf("!/")));
        }
        String tagFilePath = null;
        String tldOriginatorId = null;

        TagLibraryInfoImpl tli = (TagLibraryInfoImpl)tfi.getTagInfo().getTagLibrary();
        tldOriginatorId = tli.getOriginatorId();

        if (tfi.getPath().startsWith("/WEB-INF/tags")) {
            tagFilePath = tfi.getPath().substring(tfi.getPath().indexOf("/WEB-INF/tags") + 13);
        }
        else if (tfi.getPath().startsWith("/META-INF/tags")) {
            tagFilePath = tfi.getPath().substring(tfi.getPath().indexOf("/META-INF/tags") + 14);
        }
        tagFilePath = tagFilePath.substring(0, tagFilePath.lastIndexOf("/"));
        tagFilePath = tagFilePath.replace('/', File.separatorChar);
        tagFilePath = Constants.TAGFILE_PACKAGE_PATH + tldOriginatorId + tagFilePath;
        packageName = tagFilePath.replace(File.separatorChar, '.');

        className = tfi.getPath();
        className = className.substring(className.lastIndexOf('/') + 1);
        className = className.substring(0, className.indexOf(".tag"));
        className = env.mangleClassName(className);

        generatedSourceFile = new File(System.getProperty("java.io.tmpdir")+File.separator+tagFilePath+sourceFile.getName()+".gen");
        generatedSourceWriter = new CharArrayWriter();
    }
    
    public void syncGeneratedSource() {
    }

    public String getClassName() {
        return className;
    }

    public File getGeneratedSourceFile() {
        return generatedSourceFile;
    }

    public Writer getGeneratedSourceWriter() {
        return generatedSourceWriter;
    }
    
    public char[] getGeneratedSourceChars() {
        return generatedSourceWriter.toCharArray();
    }
    
    public JspInputSource getInputSource() {
        return inputSource;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isExternallyTranslated() {
        return false;
    }

    public boolean isOutdated() {
        if (sourceFile.lastModified() > sourceFileTimestamp)
            return true;
        else 
            return false;
    }

    public void sync() {
        sourceFileTimestamp = sourceFile.lastModified();
        generatedSourceWriter.reset();
    }
    
    public void setCurrentRequest(HttpServletRequest request) {}
    
    public byte[] getClassBytes(String className) {
        return classBytesMap.get(className);
    }

    public void setClassBytes(byte[] bytes, String className) {
        classBytesMap.put(className, bytes);
    }
}
