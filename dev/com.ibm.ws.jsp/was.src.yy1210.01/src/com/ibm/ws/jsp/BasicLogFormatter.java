//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

/*
 * Created on Jul 8, 2003
 *
 * BasicLogFormatter.java
 */
package com.ibm.ws.jsp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Scott Johnson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BasicLogFormatter extends Formatter {

    private String lineSeparator = (String) java.security.AccessController.doPrivileged(
    	            new java.security.PrivilegedAction() {public Object run() 
    	            {return System.getProperty("line.separator");}});


    /* (non-Javadoc)
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    public String format(LogRecord record) {
        StringBuffer sb = new StringBuffer();
        String message = formatMessage(record);
        sb.append(message);
        sb.append(lineSeparator);
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
            sb.append(sw.toString());
            } catch (Exception ex) {
            	if (record.getThrown().getCause() != null){
					sb.append(record.getThrown().getCause().getLocalizedMessage());
            	}else{
					sb.append(record.getThrown().getLocalizedMessage());
            	}
            	
            }
        }
        return sb.toString();
    }

}
