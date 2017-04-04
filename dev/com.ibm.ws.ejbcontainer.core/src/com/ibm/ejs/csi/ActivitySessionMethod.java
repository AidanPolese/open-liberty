/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.csi;

import java.util.List;
import com.ibm.websphere.csi.ActivitySessionAttribute;

public class ActivitySessionMethod
{
    private ActivitySessionAttribute asAttr;
    private List methodElements;

    public ActivitySessionMethod(ActivitySessionAttribute asAttr, List methodElements) {
        this.asAttr = asAttr;
        this.methodElements = methodElements;
    }

    public ActivitySessionAttribute getActivitySessionAttribute() {
        return asAttr;
    }

    public List getMethodElements() {
        return methodElements;
    }
}
