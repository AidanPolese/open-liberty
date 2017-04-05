/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.diagnostics;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ejs.container.ContainerProperties;
import com.ibm.ws.ejbcontainer.diagnostics.IntrospectionWriter;
import com.ibm.ws.ejbcontainer.osgi.internal.EJBRuntimeImpl;
import com.ibm.wsspi.logging.Introspector;

/**
 * Diagnostic handler to capture the internal state of the EJB Container runtime.
 */
@Component(service = Introspector.class)
public class EJBContainerIntrospector implements Introspector {

    private EJBRuntimeImpl runtime;

    @Override
    public String getIntrospectorName() {
        return "EJBContainerIntrospection";
    }

    @Override
    public String getIntrospectorDescription() {
        return "EJB Container Internal State Information";
    }

    @Override
    public void introspect(PrintWriter out) {
        final IntrospectionWriter writer = new IntrospectionWriterImpl(out);

        EJBRuntimeImpl ejbRuntime = runtime;
        if (ejbRuntime != null) {
            ejbRuntime.introspect(writer);
        } else {
            writer.begin("EJBRuntimeImpl = null");
            writer.end();
        }

        ContainerProperties.introspect(writer);
    }

    @Reference
    protected void setEjbRuntime(EJBRuntimeImpl ejbRuntime) {
        this.runtime = ejbRuntime;
    }

    protected void unsetEjbRuntime(EJBRuntimeImpl ejbRuntime) {
        this.runtime = null;
    }
}
