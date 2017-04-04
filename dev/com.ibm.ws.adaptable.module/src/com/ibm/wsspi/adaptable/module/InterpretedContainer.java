/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.adaptable.module;

import com.ibm.ws.adaptable.module.structure.StructureHelper;

/**
 *
 */
public interface InterpretedContainer extends Container {
    /**
     * Set a structure helper into this interpreted container.<p>
     * Structure helpers can promote Containers to become isRoot = true.
     * 
     * @param sh
     * @throws IllegalStateException if a StructureHelper is already set.
     */
    public void setStructureHelper(StructureHelper sh);
}
