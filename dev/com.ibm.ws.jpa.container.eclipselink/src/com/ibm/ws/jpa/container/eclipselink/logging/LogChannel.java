// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2014
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPALogChannel.java
//
// Source File Description:
//
//     LogChannel represents a logging channel.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// rtc124672 900       20140214 rgcurtis  : Initial Release
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.container.eclipselink.logging;

import org.eclipse.persistence.logging.SessionLogEntry;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.jpa.management.JPAConstants;

/**
 * Commented out till we break EclipseLink / OpenJPA dependencies out of the container.
 */
@Trivial
class LogChannel {
    private final TraceComponent _tc;

    /**
     * Log levels (per EclipseLink)
     * <ul>
     * <li>8=OFF
     * <li>7=SEVERE
     * <li>6=WARNING
     * <li>5=INFO
     * <li>4=CONFIG
     * <li>3=FINE
     * <li>2=FINER
     * <li>1=FINEST
     * <li>0=TRACE
     * </ul>
     * 
     * @param channel
     */
    LogChannel(String channel) {
        _tc = Tr.register(channel, LogChannel.class, JPAConstants.JPA_TRACE_GROUP, JPAConstants.JPA_RESOURCE_BUNDLE_NAME);
    }

    boolean shouldLog(int level) {
        switch (level) {
            case 8:
                return false;
            case 7: // SEVERE
                return _tc.isErrorEnabled();
            case 6: // WARN
                return _tc.isWarningEnabled();
            case 5: // INFO
                return _tc.isInfoEnabled();
            case 4: // CONFIG
                return _tc.isConfigEnabled();
            case 3: // FINE
                return _tc.isEventEnabled();
            case 2: // FINER
                return _tc.isEntryEnabled();
            case 1: // FINEST
            case 0: // TRACE
            default:
                return _tc.isDebugEnabled();
        }// end switch
    }

    /**
     * This method will only be called AFTER affirming that we should be logging
     */
    void log(SessionLogEntry entry, String formattedMessage) {
        int level = entry.getLevel();
        Throwable loggedException = entry.getException();

        String msgParm;
        if ((formattedMessage == null || formattedMessage.equals("")) && loggedException != null) {
            msgParm = loggedException.toString();
        } else {
            msgParm = formattedMessage;
        }

        switch (level) {
            case 8:
                return;
            case 7: // SEVERE
                Tr.error(_tc, "PROVIDER_ERROR_CWWJP9992E", msgParm);
                break;
            case 6:// WARN
                Tr.warning(_tc, "PROVIDER_WARNING_CWWJP9991W", msgParm);
                break;
            case 5: // INFO
                Tr.info(_tc, "PROVIDER_INFO_CWWJP9990I", msgParm);
                break;
            case 4: // CONFIG
                // I'm not sure what to do with this level... just do (log) it.
                Tr.info(_tc, "PROVIDER_INFO_CWWJP9990I", msgParm);
                break;
            case 3: // FINE
            case 2: // FINER
            case 1: // FINEST
            case 0: // TRACE
                Tr.debug(_tc, formattedMessage);
                break;
        }// end switch

        // if there is an exception - only log it to trace
        if (_tc.isDebugEnabled() && loggedException != null) {
            Tr.debug(_tc, "throwable", loggedException);
        }
    }
}
