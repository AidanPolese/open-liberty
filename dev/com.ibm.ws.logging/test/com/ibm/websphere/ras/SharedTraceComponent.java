package com.ibm.websphere.ras;

import java.lang.reflect.Method;

public class SharedTraceComponent extends TraceComponent {
    public SharedTraceComponent(Class<?> myClass) {
        super(myClass);
    }

    public SharedTraceComponent(Class<?> myClass, String traceSpec) {
        super(myClass);
        super.setTraceSpec(traceSpec);
    }

    public static void setAnyTracingEnabled(boolean useFineTrace) {
        TraceComponent.setAnyTracingEnabled(useFineTrace);
    }

    public static SharedTraceComponent createTcClassBundle(Class<?> myClass, String bundleName) {
        SharedTraceComponent tc = new SharedTraceComponent(myClass);
        tc.setResourceBundleName(bundleName);
        return tc;
    }

    public static void setTraceSpec(TraceComponent tc, String spec) throws Exception {
        Method m = TraceComponent.class.getDeclaredMethod("setTraceSpec", String.class);
        m.setAccessible(true);

        m.invoke(tc, spec);
    }
}
