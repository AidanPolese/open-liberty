/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.internal;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.wsspi.logging.Introspector;

@Component(service = Introspector.class,
           property = { "service.vendor=IBM" })
public class InjectionIntrospector implements Introspector {
    private OSGiInjectionEngineImpl injectionEngine;

    @Reference(service = OSGiInjectionEngineImpl.class)
    protected void setInjectionEngine(OSGiInjectionEngineImpl injectionEngine) {
        this.injectionEngine = injectionEngine;
    }

    protected void unsetInjectionEngine(OSGiInjectionEngineImpl injectionEngine) {}

    @Override
    public String getIntrospectorName() {
        return "InjectionIntrospector";
    }

    @Override
    public String getIntrospectorDescription() {
        return "Injection java: namespace dump";
    }

    @Override
    public void introspect(PrintWriter writer) {
        writer.println();
        writer.println("======================================================================================");
        writer.println("Beginning of Dump");
        writer.println("======================================================================================");
        writer.println();

        OSGiInjectionScopeData globalData = injectionEngine.getInjectionScopeData(null);
        globalData.introspect(writer);

        writer.println("======================================================================================");
        writer.println("End of Dump");
        writer.println("======================================================================================");
    }
}
