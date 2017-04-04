/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.container.service.metadata.extended;

import com.ibm.ws.container.service.app.deploy.extended.ExtendedModuleInfo;
import com.ibm.ws.container.service.metadata.MetaDataException;

public interface NestedModuleMetaDataFactory {
    void createdNestedModuleMetaData(ExtendedModuleInfo moduleInfo) throws MetaDataException;
}
