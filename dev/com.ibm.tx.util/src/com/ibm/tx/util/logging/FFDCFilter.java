/* **************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                             */
/*                                                                              */
/* IBM Confidential OCO Source Material                                         */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2012 */
/* The source code for this program is not published or otherwise divested      */
/* of its trade secrets, irrespective of what has been deposited with the       */
/* U.S. Copyright Office.                                                       */
/*                                                                              */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                */
/*                                                                              */
/*  Change History:                                                             */
/*                                                                              */
/*  Date      Programmer  Defect      Description                               */
/*  --------  ----------  ------      -----------                               */
/*  12-05-24  timmccor    734766      Enhance FFDC error reporting              */
/* **************************************************************************** */
package com.ibm.tx.util.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;

public class FFDCFilter
{
    private static FFDCFilterer f;

    static
    {
        String FFDCFiltererClass = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("com.ibm.tx.ffdcfilterer");
            }
        });

        if (FFDCFiltererClass == null)
        {
            FFDCFiltererClass = "com.ibm.ws.tx.util.logging.WASFFDCFilter";
        }

        try
        {
            final String finalFFDCFiltererClass = FFDCFiltererClass;
            Class<?> FFDCFiltererClassObj = AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                @Override
                public Class<?> run() throws ClassNotFoundException {
                    return Class.forName(finalFFDCFiltererClass);
                }
            });
            f = (FFDCFilterer) FFDCFiltererClassObj.newInstance();
        } catch (Exception e)
        {
            try
            {
                Class<?> FFDCFiltererClassObj = AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
                    @Override
                    public Class<?> run() throws ClassNotFoundException {
                        return Class.forName("com.ibm.tx.jta.util.logging.TxFFDCFilter");
                    }
                });
                f = (FFDCFilterer) FFDCFiltererClassObj.newInstance();
            } catch (Exception e1)
            {
                f = null;
                e1.printStackTrace();
            }
        }

        if (f == null)
        {
            f = new FFDCFilterer()
            {
                @Override
                public void processException(Throwable e, String s1, String s2, Object o)
                {
                    processException(e, s1, s2, o, null);
                }

                @Override
                public void processException(Throwable e, String s1, String s2)
                {
                    processException(e, s1, s2, null, null);
                }

                @Override
                public void processException(Throwable th, String sourceId,
                                             String probeId, Object[] objectArray)
                {
                    processException(th, sourceId, probeId, null, objectArray);
                }

                @Override
                public void processException(Throwable th, String sourceId,
                                             String probeId, Object callerThis, Object[] objectArray)
                {
                    System.out.println("Method: " + sourceId);
                    System.out.println("Probe id: " + probeId);
                    th.printStackTrace(System.out);

                    if (callerThis != null)
                    {
                        System.out.println(callerThis);
                    }

                    if (objectArray != null)
                    {
                        for (Object o : objectArray)
                        {
                            System.out.println(o);
                        }
                    }
                }
            };
        }
    }

    public static void processException(Throwable e, String s1, String s2, Object o)
    {
        f.processException(e, s1, s2, o);
    }

    public static void processException(Throwable e, String s1, String s2)
    {
        f.processException(e, s1, s2);
    }

    public static void processException(Throwable th, String sourceId, String probeId, Object[] objectArray)
    {
        f.processException(th, sourceId, probeId, objectArray);
    }

    public static void processException(Throwable th, String sourceId, String probeId, Object callerThis, Object[] objectArray)
    {
        f.processException(th, sourceId, probeId, callerThis, objectArray);
    }
}