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
package com.ibm.ws.container.service.state.internal;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import test.common.ComponentContextMockery;

import com.ibm.ws.container.service.app.deploy.ModuleInfo;
import com.ibm.ws.container.service.state.ModuleStateListener;
import com.ibm.ws.container.service.state.StateChangeException;

@RunWith(JMock.class)
public class ModuleStateChangeListenerTest {
    private static final String REF_LISTENERS = "moduleStateListeners";

    private final Mockery mockery = new Mockery();
    private final ComponentContextMockery ccMockery = new ComponentContextMockery(mockery);

    @Test
    public void testModuleStateStarted() throws StateChangeException {
        ComponentContext cc = mockery.mock(ComponentContext.class);
        final ModuleStateListener listener = mockery.mock(ModuleStateListener.class);
        final ModuleInfo info = mockery.mock(ModuleInfo.class);

        ServiceReference<ModuleStateListener> listenerRef = ccMockery.mockService(cc, REF_LISTENERS, listener);
        mockery.checking(new Expectations() {
            {

                one(listener).moduleStarted(with(info));
            }
        });

        StateChangeServiceImpl scs = new StateChangeServiceImpl();

        scs.activate(cc);
        scs.addModuleStateListener(listenerRef);
        scs.fireModuleStarted(info);
    }

    @Test
    public void testModuleStateStarting() throws StateChangeException {
        ComponentContext cc = mockery.mock(ComponentContext.class);
        final ModuleStateListener listener = mockery.mock(ModuleStateListener.class);
        final ModuleInfo info = mockery.mock(ModuleInfo.class);

        ServiceReference<ModuleStateListener> listenerRef = ccMockery.mockService(cc, REF_LISTENERS, listener);
        mockery.checking(new Expectations() {
            {

                one(listener).moduleStarting(with(info));
            }
        });

        StateChangeServiceImpl scs = new StateChangeServiceImpl();

        scs.activate(cc);
        scs.addModuleStateListener(listenerRef);
        scs.fireModuleStarting(info);
    }

    @Test
    public void testModuleStateStopped() throws StateChangeException {
        ComponentContext cc = mockery.mock(ComponentContext.class);
        final ModuleStateListener listener = mockery.mock(ModuleStateListener.class);
        final ModuleInfo info = mockery.mock(ModuleInfo.class);

        ServiceReference<ModuleStateListener> listenerRef = ccMockery.mockService(cc, REF_LISTENERS, listener);
        mockery.checking(new Expectations() {
            {

                one(listener).moduleStopped(with(info));
            }
        });

        StateChangeServiceImpl scs = new StateChangeServiceImpl();

        scs.activate(cc);
        scs.addModuleStateListener(listenerRef);
        scs.fireModuleStopped(info);
    }

    @Test
    public void testModuleStateStopping() throws StateChangeException {
        ComponentContext cc = mockery.mock(ComponentContext.class);
        final ModuleStateListener listener = mockery.mock(ModuleStateListener.class);
        final ModuleInfo info = mockery.mock(ModuleInfo.class);

        ServiceReference<ModuleStateListener> listenerRef = ccMockery.mockService(cc, REF_LISTENERS, listener);
        mockery.checking(new Expectations() {
            {

                one(listener).moduleStopping(with(info));
            }
        });

        StateChangeServiceImpl scs = new StateChangeServiceImpl();

        scs.activate(cc);
        scs.addModuleStateListener(listenerRef);
        scs.fireModuleStopping(info);
    }

}
