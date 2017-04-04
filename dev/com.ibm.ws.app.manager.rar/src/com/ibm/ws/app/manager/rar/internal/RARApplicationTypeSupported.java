/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.rar.internal;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import com.ibm.wsspi.application.handler.ApplicationTypeSupported;

@Component(service = ApplicationTypeSupported.class, immediate = true,
           configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { "service.vendor=IBM", "type:String=rar" })
public class RARApplicationTypeSupported implements ApplicationTypeSupported {

    @Activate
    protected void activate(ComponentContext context) {
        //
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        //
    }
}
