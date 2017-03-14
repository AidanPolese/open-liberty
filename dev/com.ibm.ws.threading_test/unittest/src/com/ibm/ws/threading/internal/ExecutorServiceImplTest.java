/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.threading.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Test;

import junit.framework.Assert;

public class ExecutorServiceImplTest {
    class ReturnsTrueCallable implements Callable<Boolean> {
        @Override
        public Boolean call() {
            return true;
        }
    }

    class ReturnsBooleanCallable implements Callable<Boolean> {
        private final ExecutorService es;

        public ReturnsBooleanCallable(ExecutorService es) {
            this.es = es;
        }

        @Override
        public Boolean call() {
            try {
                Callable<Boolean> c = new ReturnsTrueCallable();
                Future<Boolean> f = es.submit(c);
                return f.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Ensure that the ExecutorServiceImpl cannot get into a hang, by submitting Callables in a way to
     * force a hang when the pool size is too low. The ExecutorServiceImpl should be able to detect
     * the hang and compensate by adding threads to break it out of the deadlock.
     */
    @Test(timeout = 60000)
    public void testExecutorHang() throws Exception {
        ExecutorServiceImpl executorService = new ExecutorServiceImpl();
        Map<String, Object> componentConfig = new HashMap<String, Object>(6);
        componentConfig.put("name", "testExecutor");
        componentConfig.put("rejectedWorkPolicy", "CALLER_RUNS");
        componentConfig.put("stealPolicy", "STRICT");
        componentConfig.put("keepAlive", 10);
        componentConfig.put("coreThreads", 2);
        componentConfig.put("maxThreads", 1000);
        executorService.activate(componentConfig);

        // submit a bunch of quick running work so that the thread pool controller sees very high
        // throughput at a poolSize of 2 threads, making the base throughput algorithm reluctant
        // to increase the number of threads further
        for (int i = 0; i < 1000; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {}
            });
        }

        ArrayList<ReturnsBooleanCallable> alc = new ArrayList<ReturnsBooleanCallable>(10);
        ArrayList<Future<Boolean>> alf = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < 20; i++) {
            alc.add(new ReturnsBooleanCallable(executorService));
        }

        // each ReturnsBooleanCallable submits a child ReturnsTrueCallable and then waits on the result...
        // submitting so many of these at once when the pool size is low will deadlock the pool unless
        // the pool size is increased
        for (ReturnsBooleanCallable rbc : alc) {
            alf.add(executorService.submit(rbc));
        }
        for (Future<Boolean> f : alf) {
            f.get();
        }
    }

    @Test
    public void testCreateExecutor() throws Exception {
        ExecutorServiceImpl executorService = new ExecutorServiceImpl();
        Map<String, Object> componentConfig = new HashMap<String, Object>(6);
        componentConfig.put("name", "testExecutor");
        componentConfig.put("rejectedWorkPolicy", "CALLER_RUNS");
        componentConfig.put("stealPolicy", "STRICT");
        componentConfig.put("keepAlive", 10);

        // Normal Case (maxThreads > coreThreads)
        componentConfig.put("coreThreads", 10);
        componentConfig.put("maxThreads", 20);
        executorService.activate(componentConfig);
        ThreadPoolExecutor executor = executorService.getThreadPool();

        Assert.assertEquals(10, executor.getCorePoolSize());
        Assert.assertEquals(20, executor.getMaximumPoolSize());

        // coreThreads > maxThreads
        componentConfig.put("coreThreads", 20);
        componentConfig.put("maxThreads", 10);
        executorService.modified(componentConfig);
        executor = executorService.getThreadPool();

        Assert.assertEquals(10, executor.getCorePoolSize());
        Assert.assertEquals(10, executor.getMaximumPoolSize());

        // maxThreads < 0
        componentConfig.put("coreThreads", 10);
        componentConfig.put("maxThreads", -1);
        executorService.modified(componentConfig);
        executor = executorService.getThreadPool();

        Assert.assertEquals(10, executor.getCorePoolSize());
        Assert.assertEquals(Integer.MAX_VALUE, executor.getMaximumPoolSize());

        // coreThreads < 0 (simply make sure an IllegalArgumentException isn't thrown)
        componentConfig.put("coreThreads", -1);
        componentConfig.put("maxThreads", 10);
        executorService.modified(componentConfig);
        executor = executorService.getThreadPool();

        // both < 0 (simply make sure an IllegalArgumentException isn't thrown)
        componentConfig.put("coreThreads", -1);
        componentConfig.put("maxThreads", -1);
        executorService.modified(componentConfig);

        // use a very large number of coreThreads to verify that the ThreadPoolController
        // does not shrink the coreThreads below the specified value
        componentConfig.put("coreThreads", 75);
        componentConfig.put("maxThreads", 150);
        executorService.modified(componentConfig);
        executor = executorService.getThreadPool();

        Assert.assertEquals(75, executor.getCorePoolSize());
        Assert.assertEquals(150, executor.getMaximumPoolSize());

        // sleep long enough for the ThreadPoolController to run for 2 cycles, to verify
        // that it does not shrink the core size
        Thread.sleep(3000);

        Assert.assertEquals(75, executor.getCorePoolSize());
        Assert.assertEquals(150, executor.getMaximumPoolSize());
    }

    @Test(timeout = 60000)
    public void testExecutorShutdown() throws Exception {
        ExecutorServiceImpl executorService = new ExecutorServiceImpl();
        Map<String, Object> componentConfig = new HashMap<String, Object>(6);
        componentConfig.put("name", "testExecutor");
        componentConfig.put("rejectedWorkPolicy", "CALLER_RUNS");
        componentConfig.put("stealPolicy", "STRICT");
        componentConfig.put("keepAlive", 600);
        componentConfig.put("coreThreads", 10);
        componentConfig.put("maxThreads", 20);
        executorService.activate(componentConfig);
        ThreadPoolExecutor oldThreadPool = executorService.getThreadPool();

        // prestart the core threads so we can later verify that they successfully go
        // away after the executor is modified
        oldThreadPool.prestartAllCoreThreads();

        componentConfig.put("name", "testExecutor2");
        executorService.modified(componentConfig);
        ThreadPoolExecutor newThreadPool = executorService.getThreadPool();

        // ensure that a new pool got created when we modified the executor
        Assert.assertNotSame(oldThreadPool, newThreadPool);

        // ensure that the old pool shrinks down to 0 size (the test will timeout
        // after a minute if the pool never shrinks)
        while (oldThreadPool.getPoolSize() > 0) {
            Thread.sleep(100);
        }

        // ensure that we can still submit work to the old pool even though the
        // executor service is using a new pool
        oldThreadPool.submit(new Runnable() {
            @Override
            public void run() {}
        }).get();

        // ensure that the pool size shrinks back down to 0
        while (oldThreadPool.getPoolSize() > 0) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testThreadPoolControllerThreadPool() throws Exception {
        ExecutorServiceImpl executorService = new ExecutorServiceImpl();
        Map<String, Object> componentConfig = new HashMap<String, Object>(6);
        componentConfig.put("name", "testExecutor");
        componentConfig.put("rejectedWorkPolicy", "CALLER_RUNS");
        componentConfig.put("stealPolicy", "STRICT");
        componentConfig.put("keepAlive", 10);
        componentConfig.put("coreThreads", 5);
        componentConfig.put("maxThreads", 5);

        executorService.activate(componentConfig);

        ThreadPoolExecutor executorPool = executorService.getThreadPool();
        ThreadPoolExecutor controllerPool = executorService.threadPoolController.threadPool;
        Assert.assertSame("Executor thread pool not the same as controller thread pool after initial creation", executorPool, controllerPool);

        ThreadFactory tf = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread();
            }
        };

        executorService.setThreadFactory(tf);

        executorPool = executorService.getThreadPool();
        controllerPool = executorService.threadPoolController.threadPool;
        Assert.assertSame("Executor thread pool not the same as controller thread pool after setThreadFactory", executorPool, controllerPool);

        executorService.unsetThreadFactory(tf);

        executorPool = executorService.getThreadPool();
        controllerPool = executorService.threadPoolController.threadPool;
        Assert.assertSame("Executor thread pool not the same as controller thread pool after unsetThreadFactory", executorPool, controllerPool);
    }
}
