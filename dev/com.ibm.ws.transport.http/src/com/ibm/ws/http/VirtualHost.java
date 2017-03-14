/*
 * 1.2 4/26/02
 *
 * IBM Confidential OCO Source Material
 * 5639-D57 (C) COPYRIGHT International Business Machines Corp. 2002
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 */
package com.ibm.ws.http;

import java.util.HashMap;

@Deprecated
public class VirtualHost {
    private final String name;
    private final Alias[] aliases;
    private final MimeEntry[] mimeEntries;
    private final HashMap mimeMap;

    @Deprecated
    public VirtualHost(String name, Alias[] aliases, MimeEntry[] mimeEntries) {
        this.name = name;
        this.aliases = aliases;
        this.mimeEntries = mimeEntries;

        mimeMap = new HashMap(mimeEntries.length);
        for (int i = 0; i < mimeEntries.length; ++i) {
            String[] exts = mimeEntries[i].getExtensions();
            if (exts != null) {
                String type = mimeEntries[i].getType();
                for (int j = 0; j < exts.length; ++j) {
                    mimeMap.put(exts[j], type);
                }
            }
        }
    }

    @Deprecated
    public String getName() {
        return name;
    }

    public Alias[] getAliases() {
        return aliases;
    }

    public MimeEntry[] getMimeEntries() {
        return mimeEntries;
    }

    @Deprecated
    public String getMimeType(String extension) {
        return (String) mimeMap.get(extension);
    }
}