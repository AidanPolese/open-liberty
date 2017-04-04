//
// COMPONENT_NAME: WAS.transactions
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007, 2016
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// DESCRIPTION:
//
// Change History:
//
//
// Date      Programmer    Defect   Description
// --------  ----------    ------   -----------
// 08/22/16  orcook        224032   add doPriv
// 07/08/16  awilkins      459938   Purge executor to clear cancelled alarms
// 09/06/02  mallam        596067   package move

package com.ibm.tx.jta.util.alarm;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ibm.tx.util.alarm.Alarm;
import com.ibm.tx.util.alarm.AlarmListener;
import com.ibm.tx.util.alarm.AlarmManager;

public class AlarmManagerImpl implements AlarmManager
{
    private static final int POOL_SIZE = 10;
    private final ScheduledExecutorService _scheduler;

    public AlarmManagerImpl()
    {
        _scheduler = Executors.newScheduledThreadPool(POOL_SIZE, new JTMThreadFactory());
    }

    @Override
    public Alarm scheduleAlarm(final long millisecondDelay, AlarmListener listener, Object context)
    {
        final Runnable command = new AlarmListenerWrapper(listener, context);
        ScheduledFuture<?> future = null;

        future = AccessController.doPrivileged(new PrivilegedAction<ScheduledFuture<?>>() {
            @Override
            public ScheduledFuture<?> run() {
                return _scheduler.schedule(command, millisecondDelay, TimeUnit.MILLISECONDS);
            }
        });

        final Alarm alarmImpl = new AlarmImpl(future, (ThreadPoolExecutor) _scheduler);

        return alarmImpl;
    }

    @Override
    public Alarm scheduleDeferrableAlarm(long millisecondDelay, AlarmListener listener, Object context)
    {
        return scheduleAlarm(millisecondDelay, listener, context);
    }

    private static class AlarmListenerWrapper implements Runnable
    {
        private final ClassLoader _contextClassLoader;
        private final Object _context;
        private final AlarmListener _alarmListener;

        public AlarmListenerWrapper(AlarmListener alarmListener, Object context)
        {
            _alarmListener = alarmListener;
            _context = context;
            _contextClassLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

                @Override
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }

        @Override
        public void run()
        {
            final ClassLoader originalLoader = setTCCL(_contextClassLoader);
            try {
                _alarmListener.alarm(_context);
            } finally {
                setTCCL(originalLoader);
            }
        }

        private ClassLoader setTCCL(final ClassLoader classLoader) {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

                @Override
                public ClassLoader run() {
                    Thread currentThread = Thread.currentThread();
                    ClassLoader originalLoader = currentThread.getContextClassLoader();
                    currentThread.setContextClassLoader(classLoader);
                    return originalLoader;
                }
            });
        }
    }

    @Override
    public Alarm scheduleAlarm(long millisecondDelay, AlarmListener listener)
    {
        return scheduleAlarm(millisecondDelay, listener, null);
    }

    @Override
    public Alarm scheduleDeferrableAlarm(long millisecondDelay, AlarmListener listener)
    {
        return scheduleAlarm(millisecondDelay, listener, null);
    }

    @Override
    public void shutdown()
    {
        _scheduler.shutdown();
    }

    @Override
    public void shutdownNow()
    {
        _scheduler.shutdownNow();
    }
}