package com.ibm.ws.Transaction.JTS;

/* ***************************************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                                                      */
/*                                                                                                       */
/*  ORIGINS: 27                                                                                          */
/*                                                                                                       */
/* IBM Confidential OCO Source Material                                                                  */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2004, 2009 */
/* The source code for this program is not published or otherwise divested                               */
/* of its trade secrets, irrespective of what has been deposited with the                                */
/* U.S. Copyright Office.                                                                                */
/*                                                                                                       */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                                         */
/*                                                                                                       */
/*  DESCRIPTION:                                                                                         */
/*                                                                                                       */
/*  Change History:                                                                                      */
/*                                                                                                       */
/*  Date      Programmer  Defect         Description                                                     */
/*  --------  ----------  ------         -----------                                                     */
/*  05/09/02   gareth     ------         Move to JTA implementation                                      */
/*  17/09/02   gareth     ------         Add partner log support                                         */
/*  21/10/02   gareth     1449           Tidy up messages and exceptions                                 */
/*  01/11/02   hursdlg    1478           Add epoch support                                               */
/*  20/01/03   gareth     LIDB1673.1     Add JTA2 messages                                               */
/*  30/01/03   mallam     LIDB1673.24    Inactivity timer                                                */
/*  21/02/03   gareth     LIDB1673.19    Make any unextended code final                                  */
/*  28/02/03   hursdlg    LIDB1673.19.1  Remove redundant code                                           */
/*  17/03/03   mallam     157629         use correct total timeout                                       */
/*  04/04/03   hursdlg    LIDB1673.22    Save ExtClassLoader                                             */
/*  11/06/03   hursdlg    169107         Update recoverylog interfaces                                   */
/*  22/09/03   hursdlg    174209         Migrate logs                                                    */
/*  20/10/03   mallam     LIDB1673-13    New WCCM attributes                                             */
/*  20/11/03   johawkes   182862         Remove static partner log dependencies                          */
/*  13/04/04   beavenj    LIDB1578.1     Initial support for ha-recovery                                 */
/*  21/05/04   beavenj    LIDB1578.7     FFDC                                                            */
/*  14/06/04   johawkes   209345         Organise imports                                                */
/*  28/09/04   awilkins   227752.2       Add getter/setter for TxServiceImpl                             */
/*  14/06/05   johawkes   266145.3       Move custom properties to WCCM                                  */
/*  27/07/05   hursdlg    292064         Add support for ClusterMemberService ready for LI3187-25        */
/*  06/01/06   johawkes   306998.12      Use TraceComponent.isAnyTracingEnabled()                        */
/*  29/11/06   maples     402670         LI4119-19 code review changes                                   */
// 07/04/12 johawkes LIDB4171-35    Componentization
// 07/04/12 johawkes 430278         Further componentization
// 07/05/01 johawkes 434414         Remove WAS dependencies
// 07/06/06 johawkes 443467         Repackaging
// 07/08/16 johawkes 451213         Moved LPS back into JTM
// 09/06/02 mallam   596067         package moves for Aries/osgi
/* ***************************************************************************************************** */

import javax.transaction.SystemException;

import com.ibm.tx.TranConstants;
import com.ibm.tx.jta.impl.FailureScopeController;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.ws.recoverylog.spi.FailureScope;
import com.ibm.ws.recoverylog.spi.RecoveryDirector;
import com.ibm.ws.recoverylog.spi.RecoveryDirectorImpl;
import com.ibm.ws.recoverylog.spi.RecoveryLogManager;

public final class Configuration
{
    private static final TraceComponent tc = Tr.register(Configuration.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private static String serverName;
    private static byte[] applId;
    private static int currentEpoch = 1;

    private static RecoveryLogManager _logManager;
    private static ClassLoader _classLoader;

    private static FailureScopeController _failureScopeController;

    /**
     * Sets the name of the server.
     * 
     * @param name The server name. Non-recoverable servers have null.
     */
    public static final void setServerName(String name)
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "setServerName", name);

        // Store the server name.
        serverName = name;
    }

    /**
     * Returns the name of the server.
     * <p>
     * Non-recoverable servers may not have a name, in which case the method returns
     * null.
     * 
     * @return The server name.
     */
    public static final String getServerName()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getServerName", serverName);
        return serverName;
    }

    /**
     * Determines whether the JTS instance is recoverable.
     * 
     * @return Indicates whether the JTS is recoverable.
     */
    public static final boolean isRecoverable()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "isRecoverable");

        // This JTS is recoverable if there is a server name.
        // boolean result = (serverName != null);
        // JTA2 - we are recoverable if we have a working log...
        // We can have a serverName but no working log either because
        // a) the log config or store is invalid
        // b) the log config indicates no logging.
        //
        boolean result = false;
        if (_failureScopeController != null)
        {
            result = (_failureScopeController.getTransactionLog() != null);
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "isRecoverable", Boolean.valueOf(result));
        return result;
    }

    /**
     * Sets the current epoch value for this server instance.
     * 
     * Initially on a cold start the valus is 1, and this is
     * incremented on each warm start after extracting the previous
     * value from the transactions log. The epoch value is used to
     * create unique global transaction identifiers. On each cold
     * start we also create a new applId, so the applid and epoch
     * will guarantee uniqueness of a server instance.
     * 
     * @param number The new retry count.
     */
    public static final void setCurrentEpoch(int number)
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "setCurrentEpoch", number);

        currentEpoch = number;
    }

    /**
     * Returns the current epoch value for this server instance.
     * 
     * @return int value.
     */
    public static final int getCurrentEpoch()
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "getCurrentEpoch", currentEpoch);
        return currentEpoch;
    }

    /**
     * Sets the applId of the server.
     * 
     * @param name The applId. Non-recoverable servers may have an applId but no name.
     */
    public static final void setApplId(byte[] name)
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "setApplId", name);

        // Store the applId.
        applId = name;
    }

    /**
     * Returns the applId of the server.
     * <p>
     * Non-recoverable servers may have an applid but not a name.
     * 
     * @return The applId of the server.
     */
    public static final byte[] getApplId()
    {
        // Determine the applId.
        final byte[] result = applId;

        if (tc.isDebugEnabled())
            Tr.debug(tc, "getApplId", result);
        return result;
    }

    public static void setLogManager(RecoveryLogManager logManager)
    {
        _logManager = logManager;
    }

    public static RecoveryLogManager getLogManager()
    {
        return _logManager;
    }

    public static void setFailureScopeController(FailureScopeController fsm)
    {
        if (tc.isDebugEnabled())
            Tr.debug(tc, "setFailureScopeController", fsm);
        _failureScopeController = fsm;
    }

    public static FailureScopeController getFailureScopeController()
    {
        try
        {
            if (_failureScopeController == null)
            {
                final RecoveryDirector recoveryDirector = RecoveryDirectorImpl.instance();
                if (recoveryDirector != null)
                {
                    final FailureScope currentFailureScope = recoveryDirector.currentFailureScope();
                    final FailureScopeController fsc = new FailureScopeController(currentFailureScope);
                    setFailureScopeController(fsc);
                }
            }
        } catch (SystemException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (tc.isDebugEnabled())
            Tr.debug(tc, "getFailureScopeController", _failureScopeController);
        return _failureScopeController;
    }
}