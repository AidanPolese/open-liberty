//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997-2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

// Change History
// Feature LIDB4293-2 - "In-memory translation/compilation of JSPs" 2006/11/11 Scott Johnson

package com.ibm.ws.jsp.inmemory.generator;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.ws.jsp.JspCoreException;
import com.ibm.ws.jsp.configuration.JspConfiguration;
import com.ibm.ws.jsp.inmemory.resource.InMemoryTagFileResources;
import com.ibm.ws.jsp.translator.visitor.JspVisitorInputMap;
import com.ibm.ws.jsp.translator.visitor.configuration.JspVisitorUsage;
import com.ibm.ws.jsp.translator.visitor.generator.FragmentHelperClassWriter;
import com.ibm.ws.jsp.translator.visitor.generator.GenerateTagFileVisitor;
import com.ibm.wsspi.jsp.context.JspCoreContext;

public class InMemoryGenerateTagFileVisitor extends GenerateTagFileVisitor {
    
    public InMemoryGenerateTagFileVisitor(JspVisitorUsage visitorUsage,
                                           JspConfiguration jspConfiguration, 
                                           JspCoreContext context, 
                                           HashMap resultMap,
                                           JspVisitorInputMap inputMap) 
                                           throws JspCoreException {
        super(visitorUsage, jspConfiguration, context, resultMap, inputMap);
    }
    
    protected void createWriter(String filePath, String className, Map customTagMethodJspIdMap) throws JspCoreException { //232818
        this.filePath = filePath;
        InMemoryTagFileResources tagFileFiles = (InMemoryTagFileResources)inputMap.get("TagFileFiles");
        Map cdataJspIdMap = (Map)inputMap.get("CdataJspIdMap");
        try {
        	((CharArrayWriter)tagFileFiles.getGeneratedSourceWriter()).reset();
			writer = new InMemoryWriter(tagFileFiles.getGeneratedSourceWriter(), jspElementMap, cdataJspIdMap, customTagMethodJspIdMap);
		} catch (IOException e) {
            throw new JspCoreException(e);
		}
        fragmentHelperClassWriter = new FragmentHelperClassWriter(className);
        boolean reuseTags = false;
        if (jspOptions.isUsePageTagPool() ||
            jspOptions.isUseThreadTagPool()) {
            reuseTags = true;                
        }
        fragmentHelperClassWriter.generatePreamble(reuseTags);
    }
    
}
