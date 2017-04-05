//IBM Confidential OCO Source Material
//	5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997-2004 
//	The source code for this program is not published or otherwise divested
//	of its trade secrets, irrespective of what has been deposited with the
//	U.S. Copyright Office.

package com.ibm.ws.jsp.translator.optimizedtag.impl;

import javax.servlet.jsp.JspWriter;

public class JSTLOutUtil {
    public static void writeOut(JspWriter writer, Object value, boolean escapeXml) throws java.io.IOException{
        if (value instanceof java.io.Reader) {
            if (escapeXml) {
                java.io.Reader reader = (java.io.Reader)value;
                int c;
                while ((c = reader.read()) != -1) {
                    escapeCharAndWrite((char)c, writer);
                }
            }
            else {
                java.io.Reader reader = (java.io.Reader)value;
                char[] buf = new char[4096];
                int count;
                while ((count=reader.read(buf, 0, 4096)) != -1) {
                    writer.write(buf, 0, count);
                }
            }
        }
        else {
            if (escapeXml) {
                String text = value.toString();
                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    escapeCharAndWrite(c, writer);
                }
            }
            else {
                writer.print(value.toString());
            }
        }
    }

    private static void escapeCharAndWrite(char c, JspWriter writer) throws java.io.IOException {
        switch (c) {
            case '&':
                writer.print("&amp;");
                break;
            case '<':
                writer.print("&lt;");
                break;
            case '>':
                writer.print("&gt;");
                break;
            case '"':
                writer.print("&#034;");
                break;
            case '\'':
                writer.print("&#039;");
                break;
            default:
                writer.print(c);
                break;
        }
    }
}
