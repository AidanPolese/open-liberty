/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.classloading.internal;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.classloading.ClassGenerator;
import com.ibm.wsspi.kernel.service.utils.ConcurrentServiceReferenceSet;

public class ClassGeneratorManager implements ClassGenerator {
    private static final TraceComponent tc = Tr.register(ClassGeneratorManager.class);

    private final ConcurrentServiceReferenceSet<ClassGenerator> generatorRefs;

    public ClassGeneratorManager(ConcurrentServiceReferenceSet<ClassGenerator> generatorRefs) {
        this.generatorRefs = generatorRefs;
    }

    @Override
    public byte[] generateClass(String name, ClassLoader loader) throws ClassNotFoundException {
        for (ClassGenerator generator : generatorRefs.services()) {
            byte[] bytes = generator.generateClass(name, loader);
            if (bytes != null) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                    Tr.debug(tc, "class " + name + " generated by " + generator);
                return bytes;
            }
        }
        return null;
    }
}
