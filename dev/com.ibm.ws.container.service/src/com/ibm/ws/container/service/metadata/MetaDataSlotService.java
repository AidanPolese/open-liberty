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
package com.ibm.ws.container.service.metadata;

import com.ibm.ws.runtime.metadata.ApplicationMetaData;
import com.ibm.ws.runtime.metadata.ComponentMetaData;
import com.ibm.ws.runtime.metadata.MetaData;
import com.ibm.ws.runtime.metadata.MetaDataSlot;
import com.ibm.ws.runtime.metadata.MethodMetaData;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.ws.threadContext.ComponentMetaDataAccessorImpl;

/**
 * Service for arbitrary services to reserve storage in metadata objects. It is expected that a metadata listener service will reserve a slot when it is instantiated, populate the
 * slot with its data when the metadata is created, obtain its data at runtime when either passed a metadata object from a container or from the active component via
 * {@link ComponentMetaDataAccessorImpl}, and clean up its metadata when the metadata is destroyed.
 */
public interface MetaDataSlotService {
    /**
     * Reserve a slot in all metadata objects of the specified type.
     * 
     * @param metaDataClass {@link ApplicationMetaData}, {@link ModuleMetaData}, {@link ComponentMetaData}, or {@link MethodMetaData}
     * @return the slot
     */
    MetaDataSlot reserveMetaDataSlot(Class<? extends MetaData> metaDataClass);
}
