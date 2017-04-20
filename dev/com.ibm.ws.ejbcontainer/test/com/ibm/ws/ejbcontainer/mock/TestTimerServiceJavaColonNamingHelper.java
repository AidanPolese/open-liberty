/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.mock;

import javax.ejb.TimerService;

import com.ibm.ws.ejbcontainer.osgi.internal.naming.TimerServiceJavaColonNamingHelper;

public class TestTimerServiceJavaColonNamingHelper extends TimerServiceJavaColonNamingHelper {

    private boolean timerServiceActive = false;
    private final TimerService timerService;

    public TestTimerServiceJavaColonNamingHelper(TimerService context) {
        timerService = context;
    }

    public void setTimerServiceActive(boolean timerServiceActive) {
        this.timerServiceActive = timerServiceActive;
    }

    @Override
    protected boolean isTimerServiceActive() {
        return timerServiceActive;
    }

    @Override
    protected TimerService getTimerService() {
        return timerService;
    }
}
