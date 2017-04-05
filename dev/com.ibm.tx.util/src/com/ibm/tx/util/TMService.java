package com.ibm.tx.util;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.transaction.NotSupportedException;

public interface TMService
{
    public enum TMStates {INACTIVE, RECOVERING, ACTIVE, STOPPING, STOPPED};

    public Object runAsSystem(PrivilegedExceptionAction a) throws PrivilegedActionException;

    public Object runAsSystemOrSpecified(PrivilegedExceptionAction a) throws PrivilegedActionException;

    public boolean isProviderInstalled(String providerId);

    public void asynchRecoveryProcessingComplete(Throwable t);

    public void start() throws Exception;

    public void start(boolean waitForRecovery) throws Exception;

    public void shutdown() throws Exception;

    public void shutdown(int timeout) throws Exception;

    public void checkTMState() throws NotSupportedException;
}