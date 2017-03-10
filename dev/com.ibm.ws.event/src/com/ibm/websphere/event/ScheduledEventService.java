/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.websphere.event;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A service that is based on the Java ScheduledExecutorService; however, it
 * will be throwing OSGi Events when the timed items are run.
 */
public interface ScheduledEventService {

    /**
     * Schedule a particular topic to be fired with the provided delay. An attempt
     * may be made during the delay to cancel this through the ScheduledFuture
     * interface; however, this is a best effort as it may be starting to run
     * during the cancel attempt.
     * 
     * @param topic
     * @param delay
     *            - use 0L for immediate scheduling
     * @param unit
     * @return ScheduledFuture<?>
     * @see ScheduledExecutorService#schedule(Runnable, long, TimeUnit)
     * @throws IllegalArgumentException
     *             if topic is missing
     * @throws IllegalStateException
     *             if the service is not running
     */
    ScheduledFuture<?> schedule(Topic topic, long delay, TimeUnit unit);

    /**
     * Schedule a particular topic to be fired with the provided delay. An attempt
     * may be made during the delay to cancel this through the ScheduledFuture
     * interface; however, this is a best effort as it may be starting to run
     * during the cancel attempt.
     * 
     * @param topic
     * @param context
     *            - optional properties to use on the Event
     * @param delay
     *            - use 0L for immediate scheduling
     * @param unit
     * @return ScheduledFuture<?>
     * @see ScheduledExecutorService#schedule(Runnable, long, TimeUnit)
     * @throws IllegalArgumentException
     *             if topic is missing
     * @throws IllegalStateException
     *             if the service is not running
     */
    ScheduledFuture<?> schedule(Topic topic, Map<?, ?> context, long delay, TimeUnit unit);

    /**
     * Schedule a particular topic to be fired after the provided initial delay.
     * It
     * will then repeatedly fire at the period iterations. An attempt
     * may be made during the delay to cancel this through the ScheduledFuture
     * interface; however, this is a best effort as it may be starting to run
     * during the cancel attempt.
     * 
     * @param topic
     * @param initialDelay
     *            - use 0L for immediate scheduling
     * @param period
     *            - must be greater than 0
     * @param unit
     * @return ScheduledFuture<?>
     * @see ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
     * @throws IllegalArgumentException
     *             if topic is missing
     * @throws IllegalStateException
     *             if the service is not running
     */
    ScheduledFuture<?> schedule(Topic topic, long initialDelay, long period, TimeUnit unit);

    /**
     * Schedule a particular topic to be fired after the provided initial delay.
     * It
     * will then repeatedly fire at the period iterations. An attempt
     * may be made during the delay to cancel this through the ScheduledFuture
     * interface; however, this is a best effort as it may be starting to run
     * during the cancel attempt.
     * 
     * @param topic
     * @param context
     *            - optional properties to use on the Event
     * @param initialDelay
     *            - use 0L for immediate scheduling
     * @param period
     *            - must be greater than 0
     * @param unit
     * @return ScheduledFuture<?>
     * @see ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
     * @throws IllegalArgumentException
     *             if topic is missing
     * @throws IllegalStateException
     *             if the service is not running
     */
    ScheduledFuture<?> schedule(Topic topic, Map<?, ?> context, long initialDelay, long period, TimeUnit unit);
}
