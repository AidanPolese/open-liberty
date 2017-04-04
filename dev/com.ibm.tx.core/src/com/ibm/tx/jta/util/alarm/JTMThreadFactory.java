package com.ibm.tx.jta.util.alarm;

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
// 07/07/26  johawkes      451211   Creation
// 09/06/02  mallam        596067   package move

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory for the JTM AlarmManager. Creates threads using a default
 * thread factory but makes them daemon threads so we don't prevent the JVM
 * from shutting down and so our shutdown hook will work.
 * 
 * It's way too early to be using trace in here btw.
 */
public class JTMThreadFactory implements ThreadFactory
{
    private final static ClassLoader THIS_CLASSLOADER = JTMThreadFactory.class.getClassLoader();
    private final ThreadFactory _factory = Executors.defaultThreadFactory();

    @Override
    public Thread newThread(final Runnable r)
    {
        return AccessController.doPrivileged(new PrivilegedAction<Thread>() {

            @Override
            public Thread run() {
                final Thread t = _factory.newThread(r);
                t.setDaemon(true);
                t.setContextClassLoader(THIS_CLASSLOADER);
                return t;
            }
        });
    }
}