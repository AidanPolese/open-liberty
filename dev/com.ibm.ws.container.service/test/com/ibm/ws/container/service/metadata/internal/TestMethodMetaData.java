/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.metadata.internal;

import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.MethodMetaData;

public class TestMethodMetaData extends TestMetaDataImpl implements MethodMetaData {
    @Override
    public ComponentMetaData getComponentMetaData() {
        return null;
    }
}
