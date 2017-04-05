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
package com.ibm.ws.app.manager.ejb.internal;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.app.manager.module.internal.ModuleHandler;
import com.ibm.ws.app.manager.module.internal.ModuleHandlerBase;
import com.ibm.ws.container.service.app.deploy.extended.ModuleRuntimeContainer;
import com.ibm.ws.kernel.LibertyProcess;

@Component(service = ModuleHandler.class,
           property = { "service.vendor=IBM", "type:String=ejb" })
public class EJBModuleHandlerImpl extends ModuleHandlerBase {

    @Reference(service = LibertyProcess.class, target = "(wlp.process.type=server)")
    protected void setLibertyProcess(ServiceReference<LibertyProcess> reference) {}

    protected void unsetLibertyProcess(ServiceReference<LibertyProcess> reference) {}

    @Reference(target = "(type=ejb)")
    protected void setEjbContainer(ModuleRuntimeContainer ejbContainer) {
        super.setModuleRuntimeContainer(ejbContainer);
    }
}
