//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.visitor.generator;

import com.ibm.ws.jsp.JspCoreException;

public class BodyGenerator extends CodeGeneratorBase {
    public void startGeneration(int section, JavaCodeWriter writer) throws JspCoreException {
    }

    public void endGeneration(int section, JavaCodeWriter writer)  throws JspCoreException {
        if (section == CodeGenerationPhase.METHOD_SECTION) {
            if (writer instanceof FragmentHelperClassWriter.FragmentWriter) {
                FragmentHelperClassWriter.FragmentWriter fragmentWriter = (FragmentHelperClassWriter.FragmentWriter)writer;
                if (persistentData.get("methodNesting") == null) {
                    persistentData.put("methodNesting", new Integer(0));
                }
                int methodNesting =  ((Integer)persistentData.get("methodNesting")).intValue();
                fragmentHelperClassWriter.closeFragment(fragmentWriter, methodNesting);
            }
        }
    }
}
