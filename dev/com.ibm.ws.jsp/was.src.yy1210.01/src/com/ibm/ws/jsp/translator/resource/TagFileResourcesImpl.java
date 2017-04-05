//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.resource;

import java.io.File;

import javax.servlet.jsp.tagext.TagFileInfo;

import com.ibm.ws.jsp.Constants;
import com.ibm.ws.jsp.JspOptions;
import com.ibm.ws.jsp.taglib.TagLibraryInfoImpl;
import com.ibm.ws.jsp.translator.utils.NameMangler;
import com.ibm.wsspi.jsp.context.JspCoreContext;
import com.ibm.wsspi.jsp.resource.JspInputSource;
import com.ibm.wsspi.jsp.resource.translation.TagFileResources;

public class TagFileResourcesImpl extends ResourcesImpl implements TagFileResources {
    public TagFileResourcesImpl(JspInputSource inputSource, TagFileInfo tfi, JspOptions options, JspCoreContext context) {
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
        className = NameMangler.mangleClassName(className);

        File generatedSourceDir = new File(options.getOutputDir().getPath() + File.separator + tagFilePath);
        String convertedName = generatedSourceDir.getPath() + File.separator + className;
        generatedSourceFile = new File(convertedName + ".java");
        classFile = new File(convertedName + ".class");

        String webinfClassFilePath = context.getRealPath("/WEB-INF/classes") + File.separator + tagFilePath;
        webinfClassFile = new File(webinfClassFilePath + File.separator + className + ".class");

        keepgenerated = options.isKeepGenerated();
        keepGeneratedclassfiles = options.isKeepGeneratedclassfiles();
        sourceFileTimestamp = sourceFile.lastModified();
    }

    public boolean isOutdated() {
        return ResourceUtil.isTagFileOutdated(sourceFile, generatedSourceFile, classFile, webinfClassFile);
    }

    public void syncGeneratedSource() {
        ResourceUtil.syncGeneratedSource(sourceFile, generatedSourceFile);
    }

    public void sync() {
        ResourceUtil.syncTagFile(sourceFile, generatedSourceFile, classFile, keepgenerated, keepGeneratedclassfiles);
        sourceFileTimestamp = sourceFile.lastModified();
    }
}
