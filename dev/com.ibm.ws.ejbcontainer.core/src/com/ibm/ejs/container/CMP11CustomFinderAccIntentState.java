/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1997, 2003
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.container;

public class CMP11CustomFinderAccIntentState {
    String methodName;
    boolean customFinderWithUpdateIntent;
    boolean readOnlyAttr;

    public CMP11CustomFinderAccIntentState(String mName,
                                           boolean cfwupdateintent,
                                           boolean readonly)
    {
        methodName = mName;
        customFinderWithUpdateIntent = cfwupdateintent;
        readOnlyAttr = readonly;
    }

    public boolean isCustomFinderWithUpdateIntent() {
        return customFinderWithUpdateIntent;
    }

    public boolean isReadOnly() {
        return readOnlyAttr;
    }

    public String getCustomFinderMethodname() {
        return methodName;
    }

    public String toString() {
        return "[" + methodName + " RO " + String.valueOf(readOnlyAttr) + " CFRO " + String.valueOf(customFinderWithUpdateIntent) + "]";
    }
}
