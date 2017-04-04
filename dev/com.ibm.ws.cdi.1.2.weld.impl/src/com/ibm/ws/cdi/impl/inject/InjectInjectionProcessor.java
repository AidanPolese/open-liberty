// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2010, 2015
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.cdi.impl.inject;

import java.lang.reflect.Member;

import javax.inject.Inject;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.cdi.interfaces.CDIRuntime;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionSimpleProcessor;

public class InjectInjectionProcessor extends InjectionSimpleProcessor<Inject> {

    private static final TraceComponent tc = Tr.register(InjectInjectionProcessor.class);
    private final CDIRuntime cdiRuntime;

    public InjectInjectionProcessor(CDIRuntime cdiRuntime) {
        super(Inject.class);
        this.cdiRuntime = cdiRuntime;
    }

    @Override
    public InjectionBinding<Inject> createInjectionBinding(Inject annotation, Class<?> instanceClass, Member member) throws InjectionException {

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "createInjectionBinding", new Object[] { annotation, instanceClass, member, this });

        InjectInjectionBinding iBinding = new InjectInjectionBinding(annotation, ivNameSpaceConfig, cdiRuntime);

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "createInjectionBinding", iBinding);

        return iBinding;

    }

    @Override
    protected boolean isNonJavaBeansPropertyMethodAllowed() {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + hashCode();
    }
}
