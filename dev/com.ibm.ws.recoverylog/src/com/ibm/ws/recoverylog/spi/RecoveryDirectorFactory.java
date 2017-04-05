/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2007, 2009    */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* @(#) 1.11 SERV1/ws/code/recovery.log/src/com/ibm/ws/recoverylog/spi/RecoveryDirectorFactory.java, WAS.transactions, WASX.SERV1, qq1230.02 7/21/09 10:02:45 [7/25/12 20:18:36]                                              */
/*                                                                            */
/* DESCRIPTION:                                                               */
/*                                                                            */
/* Change History:                                                            */
/*                                                                            */
/* Date      Programmer    Defect      Description                            */
/* --------  ----------    ------      -----------                            */
/* 06/06/03  beavenj       LIDB2472.2  Create                                 */
/*  03-08-01   irobins       171437.26 AccessController package change        */
/* 28/08/03  kaczyns       LIDB2561.1  390 recovery director                  */
/* 11/11/03  hursdlg       LIDB2775    Merge zOS and distributed code         */
/* 04-03-23  awilkins      195493      Temporarily disable use of z/OS log    */
/* 04-03-24  awilkins LIDB2775.53.5.1  Exception chaining                     */
/* 04-05-14  hursdlg       195493.1    Enable use of z/OS log                 */
/* 09-07-21  johawkes      602532      Remove WAS dependency                  */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

//------------------------------------------------------------------------------
//Class: RecoveryDirectorFactory
//------------------------------------------------------------------------------
/**
 * Factory class to create and allows access to the RecoveryDirector. This is the Liberty version of this Factory
 * which creates a LibertyRecoveryDirectorImpl instance when requested by the TxTMHelper.
 */
public class RecoveryDirectorFactory
{
    /**
     * WebSphere RAS TraceComponent registration
     */
    private static final TraceComponent tc = Tr.register(RecoveryDirectorFactory.class,
                                                         TraceConstants.TRACE_GROUP, null);

    /**
     * The single instance of the RecoveryDirector implementation class.
     */
    static RecoveryDirector _recoveryDirector;

    //------------------------------------------------------------------------------
    // Method: RecoveryDirectorFactory.RecoveryDirectorFactory
    //------------------------------------------------------------------------------
    /**
     * Private construcor to prevent this object being created.
     */
    protected RecoveryDirectorFactory() {}

    //------------------------------------------------------------------------------
    // Method: RecoveryDirectorFactory.recoveryDirector()
    //------------------------------------------------------------------------------
    /**
     * Returns the singleton instance of the RecoveryDirector class. This method
     * uses reflection rather then a direct reference to the underlying class to avoid
     * a cyclic build dependency.
     * 
     * @return RecoveryDirector The singleton instance of the RecoveryDirectorImpl.
     */
    public static RecoveryDirector recoveryDirector() throws InternalLogException
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "recoveryDirector");

        // If the recovery director is null its an error in JET
        if (_recoveryDirector == null)
        {
            final InternalLogException ile = new InternalLogException();
            if (tc.isEntryEnabled())
                Tr.exit(tc, "recoveryDirector", ile);
            throw ile;
        }

        if (tc.isEntryEnabled())
            Tr.exit(tc, "recoveryDirector", _recoveryDirector);
        return _recoveryDirector;
    }

    /**
     * Create a RecoveryDirector singleton
     * 
     * @return RecoveryDirector instance
     */
    public static RecoveryDirector createRecoveryDirector()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "createRecoveryDirector");
        _recoveryDirector = LibertyRecoveryDirectorImpl.instance();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "createRecoveryDirector", _recoveryDirector);
        return _recoveryDirector;
    }

    public static void reset()
    {
        if (tc.isEntryEnabled())
            Tr.entry(tc, "reset");
        LibertyRecoveryDirectorImpl.reset();

        if (tc.isEntryEnabled())
            Tr.exit(tc, "reset");
    }
}