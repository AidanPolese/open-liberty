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
 * 
 * 
 * Change activity:
 *
 * Issue       Date        Name     Description
 * ----------- ----------- -------- ------------------------------------
 */
package com.ibm.ws.request.probe.bci.internal;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.request.probe.RequestProbeService;
import com.ibm.wsspi.request.probe.bci.RequestProbeHelper;


/**
 * This is a transformer class which is going to transform the class.
 * This extends java.lang.instrument.ClassFileTransformer.
 * 
 * We are adding RequestProbeTransformer using instrumentation.addTransformer() call
 */

public class RequestProbeTransformer implements ClassFileTransformer {

    private Instrumentation inst = null;
    private static final TraceComponent tc = Tr.register(RequestProbeTransformer.class);

    /**
     * @param instrumentation
     */
    public RequestProbeTransformer(Instrumentation instrumentation) {
        this.inst = instrumentation;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        byte[] ba = null;
        //Transform the classes for which 'Bridge' class is active.
        if (!RequestProbeHelper.interestedClass(className)) {
            return null;
        }

        if(TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()){
        	Tr.debug(tc, "Transforming class", className);
        }
        
        ba = transformClassForRequestProbe(classfileBuffer, className);
        return ba;
    }

    /**
     * @param className
     */
    private byte[] transformClassForRequestProbe(byte[] cBuffer, String nameOfClass) {

        ClassReader reader = new ClassReader(cBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = writer;
        visitor = new RequestProbeClassVisitor(visitor, nameOfClass);
        reader.accept(visitor, reader.SKIP_FRAMES);
        return writer.toByteArray();
    
    }

    
    /**
     * 
     */
    public void retransformRequestProbeRelatedClasses() {
        for (Class<?> clazz : this.inst.getAllLoadedClasses()) {
            classAvailable(clazz);
        }

    }


    /**
     * 
     */
    public void retransformClass(String stringClass) {
        for (Class<?> clazz : this.inst.getAllLoadedClasses()) {        	
        	if(clazz.getName().equals(stringClass.replace("/", "."))){        	
        		classAvailable(clazz);
        		//Fix to handle the issue of class getting loaded more than once.
        		//return;
        	}
        }

    }
    
    
    /**
     * @param clazz
     */
    private void classAvailable(Class<?> clazz) {
        if (RequestProbeHelper.interestedClass(clazz.getName().replace(".", "/"))) {
            try {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Re-transforming class", clazz.getName(), this.inst.isRedefineClassesSupported(), this.inst.isRetransformClassesSupported());
                }
                this.inst.retransformClasses(new Class[] { clazz });
            } catch (Exception e) {
                //Tr.error(tc, "REQUEST_PROBE_METHOD_WARNING", clazz.getName(), e);
                FFDCFilter.processException(e, RequestProbeTransformer.class.getName() + ".classAavilable", "126");
            }
        }
    }

}
