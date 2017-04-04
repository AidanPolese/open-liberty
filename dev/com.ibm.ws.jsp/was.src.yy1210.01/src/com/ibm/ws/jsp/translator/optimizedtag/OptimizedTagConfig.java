//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.optimizedtag;

public class OptimizedTagConfig {
    protected String tlibUri = null;
    protected String tlibversion = null;
    protected String shortName = null;
    protected Class optClass = null;
    
    public OptimizedTagConfig() {}
      
    public String getShortName() {
        return shortName;
    }

    public String getTlibUri() {
        return tlibUri;
    }

    public String getTlibversion() {
        return tlibversion;
    }

    public void setShortName(String string) {
        shortName = string;
    }

    public void setTlibUri(String string) {
        tlibUri = string;
    }

    public void setTlibversion(String string) {
        tlibversion = string;
    }
    
    public Class getOptClass() {
        return optClass;
    }

    public void setOptClass(Class class1) {
        optClass = class1;
    }
}
