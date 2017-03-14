package com.ibm.ws.event.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WorkStageExecutorServiceFactoryTest {

    final Mockery context = new JUnit4Mockery();
    final ExecutorService executorService = Executors.newSingleThreadExecutor();

    WorkStageExecutorServiceFactory factory = null;

    @Before
    public void initialize() {
        factory = new WorkStageExecutorServiceFactory();
    }

    @After
    public void destroy() {
        factory = null;
    }

    @Test
    public void testSetUnsetWorkStageManager() {
        assertNull(factory.executorService);

        factory.setExecutorService(executorService);
        assertSame(factory.executorService, executorService);

        factory.unsetExecutorService(executorService);
        assertNull(factory.executorService);
    }

    @Test
    public void testGetExecutorService() {
        final String executorName = "testExecutorName";
        factory.setExecutorService(executorService);
        assertNotNull(factory.getExecutorService(executorName));
        assertSame(factory.getExecutorService(executorName), executorService);
    }
}
