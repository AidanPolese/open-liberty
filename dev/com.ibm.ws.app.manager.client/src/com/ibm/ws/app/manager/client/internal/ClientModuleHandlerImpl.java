/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.client.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.app.manager.module.internal.ModuleHandler;
import com.ibm.ws.app.manager.module.internal.ModuleHandlerBase;
import com.ibm.ws.container.service.app.deploy.extended.ModuleRuntimeContainer;

@Component(service = ModuleHandler.class,
           property = { "service.vendor=IBM", "type:String=client" })
public class ClientModuleHandlerImpl extends ModuleHandlerBase {

    @Reference(target = "(type=client)")
    protected void setClientContainer(ModuleRuntimeContainer clientContainer) {
        super.setModuleRuntimeContainer(clientContainer);
    }
}
