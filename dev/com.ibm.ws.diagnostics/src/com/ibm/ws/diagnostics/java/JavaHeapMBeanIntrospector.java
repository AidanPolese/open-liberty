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
package com.ibm.ws.diagnostics.java;

import java.io.PrintWriter;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.ibm.ws.diagnostics.AbstractMBeanIntrospector;
import com.ibm.wsspi.logging.Introspector;

public class JavaHeapMBeanIntrospector extends AbstractMBeanIntrospector implements Introspector {
    @Override
    public String getIntrospectorName() {
        return "JavaHeapInfo";
    }

    @Override
    public String getIntrospectorDescription() {
        return "Information about the heap from the Memory related MXBeans";
    }

    @Override
    public void introspect(PrintWriter out) throws MalformedObjectNameException {
        introspect(new ObjectName("java.lang:type=Memory"), null, out);
        introspect(new ObjectName("java.lang:type=MemoryManager,*"), null, out);
        introspect(new ObjectName("java.lang:type=MemoryPool,*"), null, out);
    }
}
