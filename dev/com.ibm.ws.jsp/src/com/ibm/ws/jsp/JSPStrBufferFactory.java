//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

/*
 * Created on Feb 14, 2006
 *
 * JSPStrBufferFactory.java
 */
// Change History
// Create for defect  347278
package com.ibm.ws.jsp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSPStrBufferFactory {
    static private Logger logger;
	private static final String CLASS_NAME="com.ibm.ws.jsp.JSPStrBufferFactory";
    static {
        logger = Logger.getLogger("com.ibm.ws.jsp");
    }
    static Class buf=null;
    
    static public void set(Class buffer){
    	buf=buffer;
    }
    static public synchronized JSPStrBuffer getJSPStrBuffer() {
    	JSPStrBuffer strBuf=null;
		if (buf!=null) {
			try {
				strBuf = (JSPStrBuffer)buf.newInstance();
			} catch (Exception e) {
		        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)) {
		            logger.logp(Level.WARNING, CLASS_NAME, "getJSPStrBuffer", "unable to create instance of ["+buf.getName()+"]");
			        StringWriter sw = new StringWriter();
			        PrintWriter pw = new PrintWriter(sw);
			        e.printStackTrace(pw);
			        pw.flush();
			        String stackTrace=sw.toString();
		            logger.logp(Level.WARNING, CLASS_NAME, "getJSPStrBuffer", "stack trace: ["+stackTrace+"]");
		            logger.logp(Level.WARNING, CLASS_NAME, "getJSPStrBuffer", "returning default ["+JSPStrBufferImpl.class.getName()+"]");
		        }
		        strBuf=new JSPStrBufferImpl();
			}
		}
		else {
	        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled()&&logger.isLoggable(Level.WARNING)) {
	            logger.logp(Level.WARNING, CLASS_NAME, "getJSPStrBuffer", "buf is null; JSPStrBufferFactory.set() must be called before getJSPStrBuffer() can be called.");
	            logger.logp(Level.WARNING, CLASS_NAME, "getJSPStrBuffer", "returning default ["+JSPStrBufferImpl.class.getName()+"]");
	        }
	        strBuf=new JSPStrBufferImpl();
		}
        return strBuf;
    }
}
