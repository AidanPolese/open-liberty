/*
 * 1.1 4/25/02
 *
 * IBM Confidential OCO Source Material
 * 5639-D57 (C) COPYRIGHT International Business Machines Corp. 2002
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.ws.http;

@Deprecated
public class MimeEntry {
    private final String type;
    private final String[] extensions;

    @Deprecated
    public MimeEntry(String type, String[] exts) {
        this.type = type;
        extensions = exts;
    }

    @Deprecated
    public String getType() {
        return type;
    }

    @Deprecated
    public String[] getExtensions() {
        return extensions;
    }

    //LI3816
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder(type);
        for (int i = 0; i < extensions.length; i++) {
            if (i == 0)
                temp.append("{").append(extensions[i]).append(",");
            else if (i != 0 && i != extensions.length - 1)
                temp.append(extensions[i]).append(",");
            else
                temp.append(extensions[i]).append("}");
        }
        return temp.toString();
    }
}