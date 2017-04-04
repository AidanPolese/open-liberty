//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.
//  feature LIDB4147-9 "Integrate Unified Expression Language"  2006/08/14  Scott Johnson


package com.ibm.ws.jsp.translator.visitor.validator;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.el.FunctionMapper;

import com.ibm.ws.jsp.translator.utils.FunctionSignature;

public class ValidateFunctionMapper extends FunctionMapper  {
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3977017366255185969L;
	private HashMap fnMap = new HashMap();
	private HashMap sigMap = new HashMap();
    
    public void mapFunction(String fnQName, Method method) {
        fnMap.put(fnQName, method);
    }

    public Method resolveFunction(String prefix, String localName) {
        return (Method) fnMap.get(prefix + ":" + localName);
    }

    public void mapSignature(String fnQName, FunctionSignature signature) {
        sigMap.put(fnQName, signature);
    }

    public FunctionSignature getSignature(String fnQName) {
        return (FunctionSignature) sigMap.get(fnQName);   
    }
    
    //LIDB4147-9 Begin
    public Map getFnMap() {
    	return fnMap;
    }
    //LIDB4147-9 End
}
