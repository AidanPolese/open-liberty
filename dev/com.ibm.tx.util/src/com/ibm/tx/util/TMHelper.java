/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Developer  Defect    Description                                       */
/*  --------  ---------  ------    -----------                                       */
/*  09-11-09  johawkes   F743-305.1 EJB 3.1                                          */
/* ********************************************************************************* */
package com.ibm.tx.util;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;

import javax.transaction.NotSupportedException;

public class TMHelper
{
    private static final String TMHelperClass = "com.ibm.tx.jta.embeddable.impl.EmbeddableTMHelper";

    private static TMService s;

    public static void setTMService(TMService tms)
    {
        s = tms; // check s not null before/after?
    }

    public static Object runAsSystem(PrivilegedExceptionAction a) throws PrivilegedActionException
    {
        return s.runAsSystem(a);
    }

    public static Object runAsSystemOrSpecified(PrivilegedExceptionAction a) throws PrivilegedActionException
    {
        return s.runAsSystemOrSpecified(a);
    }

    public static boolean isProviderInstalled(String providerId)
    {
        return s.isProviderInstalled(providerId);
    }

    public static void asynchRecoveryProcessingComplete(Throwable t)
    {
        s.asynchRecoveryProcessingComplete(t);
    }

    public static void start() throws Exception
    {
        if (s == null)
        {
            try
            {
                s = (TMService) Class.forName(TMHelperClass).newInstance();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }

        s.start();
    }

    public static void start(boolean waitForRecovery) throws Exception
    {
        if (s == null)
        {
            try
            {
                s = (TMService) Class.forName(TMHelperClass).newInstance();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }

        s.start(waitForRecovery);
    }

    public static void shutdown() throws Exception
    {
        s.shutdown();
    }

    public static void shutdown(int timeout) throws Exception
    {
        s.shutdown(timeout);
    }

    public static void checkTMState() throws NotSupportedException
    {
        if (s == null)
        {
            try
            {
                s = (TMService) Class.forName(TMHelperClass).newInstance();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }

        s.checkTMState();
    }

    public static void start(Map<String, Object> properties) throws Exception
    {
        start(); // For now
    }
}