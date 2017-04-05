package com.ibm.tx.jta.config;

/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5639-D57,5630-A36,5630-A37,5724-D18 (C) COPYRIGHT International Business Machines Corp. 2007,2013 */
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
/*  yy-mm-dd  Developer  Defect    Description                                       */
/*  --------  ---------  ------    -----------                                       */
/*  07-12-13  johawkes   487810    Server-wide LPS enablement                        */
/*  09-06-02  mallam               package move                                      */
/*  10-03-17  hursdlg    PM07874   Audit recovery logging                            */
/*  11-12-22  johawkes   725030    Liberty 57863                                     */
/*  13-01-31  nyoung     735581.11 I'face change to support Liberty Integration      */
/*  13-03-20  bkail      RTC97013  Use com.ibm.wsspi.resource.ResourceFactory        */
/*  13-05-20  johawkes   F099608   XAResource.setTransactionTimeout                  */
/*  13-10-18  slaterpa   PM99381   Add wsatPrepareOrder                              */
/* ********************************************************************************* */

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;

import com.ibm.tx.config.ConfigurationProvider;
import com.ibm.tx.config.RuntimeMetaDataProvider;
import com.ibm.tx.jta.util.alarm.AlarmManagerImpl;
import com.ibm.tx.util.alarm.AlarmManager;
import com.ibm.wsspi.resource.ResourceFactory;

public class DefaultConfigurationProvider implements ConfigurationProvider {
    private static AlarmManager _alarmManager;
    private byte[] _applId;

    public DefaultConfigurationProvider() {
        _alarmManager = new AlarmManagerImpl();
    }

    private static final RuntimeMetaDataProvider _runtimeMetaDataProvider = new RuntimeMetaDataProvider() {
        @Override
        public int getTransactionTimeout() {
            return 0;
        }

        @Override
        public boolean isClientSideJTADemarcationAllowed() {
            return false;
        }

        @Override
        public boolean isHeuristicHazardAccepted() {
            // return server wide value
            return _acceptHeuristicHazard;
        }

        @Override
        public boolean isUserTransactionLookupPermitted(String name) {
            return true;
        }
    };

    @Override
    public AlarmManager getAlarmManager() {
        return _alarmManager;
    }

    @Override
    public int getClientInactivityTimeout() {
        return 60;
    }

    protected static int _heuristicCompletionDirction = ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_ROLLBACK;

    @Override
    public int getHeuristicCompletionDirection() {
        return _heuristicCompletionDirction;
    }

    @Override
    public String getHeuristicCompletionDirectionAsString() {
        String hcd = "ROLLBACK";

        switch (_heuristicCompletionDirction) {
            case ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_COMMIT:
                hcd = "COMMIT";
                break;
            case ConfigurationProvider.HEURISTIC_COMPLETION_DIRECTION_MANUAL:
                hcd = "MANUAL";
                break;

            default:
                break;
        }
        return hcd;
    }

    protected static int _heuristicRetryInterval = 0;

    @Override
    public int getHeuristicRetryInterval() {
        return _heuristicRetryInterval;
    }

    protected static int _heuristicRetryLimit = 0;

    @Override
    public int getHeuristicRetryLimit() {
        return _heuristicRetryLimit;
    }

    @Override
    public int getMaximumTransactionTimeout() {
        return 300;
    }

    @Override
    public RuntimeMetaDataProvider getRuntimeMetaDataProvider() {
        return _runtimeMetaDataProvider;
    }

    public int _totalTransactionLifetimeTimeout = 120;

    @Override
    public int getTotalTransactionLifetimeTimeout() {
        return _totalTransactionLifetimeTimeout;
    }

    protected static String _logDir = "logs/recovery";

    @Override
    public String getTransactionLogDirectory() {
        return _logDir;
    }

    @Override
    public String getServerName() {
        return "default";
    }

    protected static int _logSize = 1024;

    @Override
    public int getTransactionLogSize() {
        return _logSize;
    }

    protected static boolean _logForHeuristicReporting = false;

    @Override
    public boolean isLoggingForHeuristicReportingEnabled() {
        return _logForHeuristicReporting;
    }

    protected static Level _level = Level.OFF;

    @Override
    public Level getTraceLevel() {
        // Property override for trace
        final String level = System.getProperty("com.ibm.tx.traceLevel");
        if (level != null) {
            return Level.parse(level);
        }

        return _level;
    }

    protected static int _defaultMaximumShutdownDelay = 2;

    @Override
    public int getDefaultMaximumShutdownDelay() {
        return _defaultMaximumShutdownDelay;
    }

    protected static boolean _acceptHeuristicHazard = true;

    @Override
    public boolean isAcceptHeuristicHazard() {
        return _acceptHeuristicHazard;
    }

    // Default for Jet is disable extra recovery audit logging
    protected static boolean _auditRecovery = false;

    @Override
    public boolean getAuditRecovery() {
        // Property override
        final String flag = java.security.AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("com.ibm.tx.auditRecovery");
            }
        });

        if (flag != null) {
            return Boolean.parseBoolean(flag);
        }

        return _auditRecovery;
    }

    protected static boolean _recoverOnStartup;

    @Override
    public boolean isRecoverOnStartup() {
        return _recoverOnStartup;
    }

    protected static boolean _waitForRecovery;

    @Override
    public boolean isWaitForRecovery() {
        return _waitForRecovery;
    }

    @Override
    public ResourceFactory getResourceFactory() {
        return null;
    }

    protected boolean _propagateXAResourceTransactionTimeout = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
            return Boolean.getBoolean("com.ibm.websphere.tx.propagateXAResourceTransactionTimeout");
        }
    });

    @Override
    public boolean getPropagateXAResourceTransactionTimeout() {
        return _propagateXAResourceTransactionTimeout;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.tx.config.ConfigurationProvider#getRecoveryIdentity()
     */
    @Override
    public String getRecoveryIdentity() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.tx.config.ConfigurationProvider#getRecoveryGroup()
     */
    @Override
    public String getRecoveryGroup() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setApplId(byte[] name) {
        // Store the applId.
        _applId = name;
    }

    @Override
    public byte[] getApplId() {
        // Determine the applId.
        final byte[] result = _applId;

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.tx.config.ConfigurationProvider#shutDownFramework()
     */
    @Override
    public void shutDownFramework() {
        // TODO Auto-generated method stub

    }
}