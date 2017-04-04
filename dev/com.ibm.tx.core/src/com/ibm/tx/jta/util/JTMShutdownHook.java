package com.ibm.tx.jta.util;
//
// COMPONENT_NAME: WAS.transactions
//
// ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007
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

import com.ibm.tx.TranConstants;
import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;
import com.ibm.tx.util.TMHelper;

/**
 * This singleton is registered as a shutdown hook in TxTMHelper such
 * that its run() method gets invoked as the JVM is going down. The
 * intention is to attempt to gracefully shutdown even when the JVM is killed.
 */
public class JTMShutdownHook extends Thread
{
    private static final TraceComponent tc = Tr.register(JTMShutdownHook.class, TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private static Thread _hook = new JTMShutdownHook("JTMShutdownHook");

    private JTMShutdownHook(String name)
    {
        super(name);
    }
    
    public static Thread instance()
    {
        return _hook;
    }
    
    public void run()
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "run");

        try
        {
            // Use no arg shutdown so we can affect what happens via config
            TMHelper.shutdown();
        }
        catch(Exception e)
        {
            // Ah well. At least we tried. This is probably an NPE cos we're already shutdown.
        }

        if (tc.isEntryEnabled()) Tr.exit(tc, "run");
    }
}