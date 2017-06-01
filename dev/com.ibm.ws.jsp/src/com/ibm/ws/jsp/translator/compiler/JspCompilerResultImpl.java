//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

/*
 * Created on Nov 3, 2003
 */

//PK72039      Add ability to continue to compile the rest of the JSPs during a batch compile failure  2008/10/21  Jay Sartoris

package com.ibm.ws.jsp.translator.compiler;

import com.ibm.wsspi.jsp.compiler.JspCompilerResult;
import java.util.List; //PK72039


/**
 */
public class JspCompilerResultImpl implements JspCompilerResult {
    private int compilerReturnValue=-1;
    private String compilerMessage=null;
    private List compilerFailureFileNames=null; //PK72039

    public JspCompilerResultImpl(int compilerRetVal, String compilerMsg){
        this.compilerReturnValue=compilerRetVal;
        this.compilerMessage=compilerMsg;
    }

    //PK72039 start
    public JspCompilerResultImpl(int compilerRetVal, String compilerMsg, List compilerFailureFN){
        this.compilerReturnValue=compilerRetVal;
        this.compilerMessage=compilerMsg;
        this.compilerFailureFileNames=compilerFailureFN;
    }
    //PK72039 start

    public int getCompilerReturnValue() {
        return compilerReturnValue;
    }

    public String getCompilerMessage() {
        return compilerMessage;
    }

    //PK72039 start
    public List getCompilerFailureFileNames() {
        return compilerFailureFileNames;
    }
    //PK72039 end
}
