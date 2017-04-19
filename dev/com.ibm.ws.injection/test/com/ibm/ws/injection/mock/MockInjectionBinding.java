/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injection.mock;

import java.lang.reflect.Member;

import javax.annotation.Resource;

import com.ibm.wsspi.injectionengine.ComponentNameSpaceConfiguration;
import com.ibm.wsspi.injectionengine.InjectionBinding;
import com.ibm.wsspi.injectionengine.InjectionException;
import com.ibm.wsspi.injectionengine.InjectionTargetContext;

/**
 *
 */
@Resource
public class MockInjectionBinding extends InjectionBinding<Resource> {

    private final Object ivReturnObj;

    /**
     * @param annotation
     * @param nameSpaceConfig
     */
    public MockInjectionBinding(Object returnObj) {
        super(MockInjectionBinding.class.getAnnotation(Resource.class), new ComponentNameSpaceConfiguration(null, null));
        ivReturnObj = returnObj;
    }

    /** {@inheritDoc} */
    @Override
    public void merge(Resource annotation, Class<?> instanceClass, Member member) throws InjectionException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void mergeSaved(InjectionBinding<Resource> binding) {
        // Nothing.
    }

    /** {@inheritDoc} */
    @Override
    protected Object getInjectionObjectInstance(Object targetObject,
                                                InjectionTargetContext targetContext) throws Exception {
        if (ivReturnObj == null)
            throw new NullPointerException("Expected Test Exception");

        return ivReturnObj;
    }

}
