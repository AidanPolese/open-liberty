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
package com.ibm.ws.config.xml.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.ibm.websphere.config.ConfigUpdateException;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.config.xml.internal.ConfigComparator.ComparatorResult;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.runtime.update.RuntimeUpdateManager;
import com.ibm.ws.runtime.update.RuntimeUpdateNotification;
import com.ibm.wsspi.kernel.service.utils.FrameworkState;
import com.ibm.wsspi.kernel.service.utils.TimestampUtils;

/**
 *
 */
class ConfigRefresher {

    static final TraceComponent tc = Tr.register(ConfigRefresher.class, XMLConfigConstants.TR_GROUP, XMLConfigConstants.NLS_PROPS);

    private final ChangeHandler changeHandler;
    private final ServerXMLConfiguration serverXMLConfig;
    private final ConfigurationMonitor configurationMonitor;

    /** service trackers */
    private final ServiceTracker<RuntimeUpdateManager, RuntimeUpdateManager> runtimeUpdateManagerTracker;
    private final ServiceTracker<Executor, Executor> executorTracker;
    private final ServiceTracker<MetaTypeRegistry, MetaTypeRegistry> metatypeTracker;

    private long configStartTime = 0;
    private Collection<Future<?>> futuresForChanges = null;

    ConfigRefresher(BundleContext bundleContext,
                    ChangeHandler changeHandler, ServerXMLConfiguration serverXMLConfig) {
        this.changeHandler = changeHandler;
        this.serverXMLConfig = serverXMLConfig;

        this.configurationMonitor = new ConfigurationMonitor(bundleContext, serverXMLConfig, this);

        runtimeUpdateManagerTracker = new ServiceTracker<RuntimeUpdateManager, RuntimeUpdateManager>(bundleContext, RuntimeUpdateManager.class.getName(), null);
        runtimeUpdateManagerTracker.open();

        executorTracker = new ServiceTracker<Executor, Executor>(bundleContext, java.util.concurrent.ExecutorService.class.getName(), null);
        executorTracker.open();

        metatypeTracker = new ServiceTracker<MetaTypeRegistry, MetaTypeRegistry>(bundleContext, MetaTypeRegistry.class.getName(), null);
        metatypeTracker.open();
    }

    void start() {
        configurationMonitor.registerService();
    }

    void stop() {
        configurationMonitor.stopConfigurationMonitoring();
        runtimeUpdateManagerTracker.close();
    }

    synchronized void refreshConfiguration() {
        if (FrameworkState.isStopping()) {
            // if the framework is stopping, just ignore incoming events
            return;
        }

        configStartTime = System.nanoTime();

        RuntimeUpdateManager runtimeUpdateManager = runtimeUpdateManagerTracker.getService();
        RuntimeUpdateNotification configUpdatesDelivered = runtimeUpdateManager.createNotification(RuntimeUpdateNotification.CONFIG_UPDATES_DELIVERED);
        futuresForChanges = null;

        try {
            Tr.audit(tc, "info.config.refresh.start");

            ServerConfiguration newConfiguration = serverXMLConfig.loadNewConfiguration();
            if (newConfiguration == null) {
                return;
            }

            ComparatorResult result = compareConfigurations(serverXMLConfig.getConfiguration(), newConfiguration);

            // Error condition -- A result with no changes will have result.hasDelta() == false
            if (result == null) {
                return;
            }

            Collection<ConfigurationInfo> configurations = null;

            if (!result.hasDelta()) {
                Tr.audit(tc, "info.config.refresh.nochanges");
            } else {
                // switch to new configuration & process changes
                try {
                    configurations = changeHandler.switchConfiguration(serverXMLConfig, result);
                } catch (ConfigUpdateException e) {
                    Tr.error(tc, "error.config.update.init", new Object[] { e.getMessage() });
                }
            }

            // update the file monitoring service
            configurationMonitor.updateFileMonitor(serverXMLConfig.getFilesToMonitor());
            configurationMonitor.updateDirectoryMonitor(serverXMLConfig.getDirectoriesToMonitor());

            if (configurations != null) {
                futuresForChanges = fireConfigurationChanges(configurations);
            }
        } catch (Exception e) {
            // Let the notification show that we got an error while making the configuration changes
            configUpdatesDelivered.setResult(e);
        } finally {
            changesEnded(configUpdatesDelivered);
        }
    }

    private ComparatorResult compareConfigurations(ServerConfiguration serverConfiguration, ServerConfiguration newConfiguration) {
        ConfigComparator comparator = new ConfigComparator(serverConfiguration, newConfiguration, metatypeTracker.getService());

        ComparatorResult result;
        try {
            result = comparator.computeDelta();
        } catch (ConfigUpdateException e1) {
            Tr.error(tc, "error.config.update.init", new Object[] { e1.getMessage() });
            return null;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "doRefreshConfiguration(): Configuration changes: " + result);
        }

        result.setNewConfiguration(newConfiguration);
        return result;
    }

    private Collection<Future<?>> fireConfigurationChanges(Collection<ConfigurationInfo> configurations) {
        /*
         * This avoids waiting on futures under BundleProcessor lock.
         * Otherwise, if executed under BundleProcessor lock it might cause
         * a deadlock.
         */
        Collection<Future<?>> futures = new ArrayList<Future<?>>();
        for (ConfigurationInfo info : configurations) {
            // create futures for configuration update events
            info.fireEvents(futures);
        }
        return futures;
    }

    void changesEnded(final RuntimeUpdateNotification configUpdatesDelivered) {
        final long endingConfigStartTime = configStartTime;
        final Collection<Future<?>> endingFuturesForChanges = futuresForChanges;

        configStartTime = 0;
        futuresForChanges = null;

        // Use an executor thread to end the config changes
        Executor executor = executorTracker.getService();
        if (executor != null) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    endConfigChanges(configUpdatesDelivered, endingConfigStartTime, endingFuturesForChanges);
                }
            });
        } else {
            endConfigChanges(configUpdatesDelivered, endingConfigStartTime, endingFuturesForChanges);
        }
    }

    private void endConfigChanges(RuntimeUpdateNotification configUpdatesDelivered, long endingConfigStartTime, Collection<Future<?>> endingFuturesForChanges) {
        final boolean noTimeout;
        if (endingFuturesForChanges != null) {
            // we made config updates which we have futures for.  wait for those futures to complete
            noTimeout = waitForAll(endingFuturesForChanges, 1, TimeUnit.MINUTES);
        } else {
            noTimeout = true;
        }

        configUpdatesDelivered.setResult(true);
        RuntimeUpdateManager runtimeUpdateManager = runtimeUpdateManagerTracker.getService();
        RuntimeUpdateNotification featureUpdatesCompleted = runtimeUpdateManager.getNotification(RuntimeUpdateNotification.FEATURE_UPDATES_COMPLETED);
        if (featureUpdatesCompleted != null) {
            featureUpdatesCompleted.waitForCompletion();
        }
        RuntimeUpdateNotification appsStarting = runtimeUpdateManager.getNotification(RuntimeUpdateNotification.APPLICATIONS_STARTING);
        if (appsStarting != null) {
            appsStarting.waitForCompletion();
        }

        if (endingFuturesForChanges != null) {
            if (noTimeout) {
                Tr.audit(tc, "info.config.refresh.stop", TimestampUtils.getElapsedTimeNanos(endingConfigStartTime));
            } else {
                Tr.warning(tc, "info.config.refresh.timeout");
            }
        }
    }

    @FFDCIgnore({ InterruptedException.class, ExecutionException.class, TimeoutException.class })
    private boolean waitForAll(Collection<Future<?>> futureList, long timeout, TimeUnit timeUnit) {
        long timeoutNanos = timeUnit.toNanos(timeout);
        for (Future<?> future : futureList) {
            if (future == null || future.isDone()) {
                continue;
            }
            if (timeoutNanos <= 0) {
                return false;
            }
            long startTime = System.nanoTime();
            try {
                future.get(timeoutNanos, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                return false;
            } catch (ExecutionException e) {
                return false;
            } catch (TimeoutException e) {
                return false;
            }
            long endTime = System.nanoTime();
            timeoutNanos -= (endTime - startTime);
        }
        return true;
    }
}
