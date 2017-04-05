package com.ibm.tx.jta;
//
// COMPONENT_NAME: WAS.transactions
//
//  ORIGINS: 27
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007, 2009
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
// Date      Programmer    Defect   Description
// --------  ----------    ------   -----------
// 07/08/01  johawkes      451213.1 Creation
// 09/06/15  mallam        596067   package moves

import java.lang.reflect.Method;

import com.ibm.tx.util.logging.FFDCFilter;

public class TransactionInflowManagerFactory
{
    private static TransactionInflowManager _tranInflowManager;

    public static synchronized TransactionInflowManager getTransactionInflowManager()
    {
        if(_tranInflowManager == null)
        {
            loadTranInflowManager();
        }

        return _tranInflowManager;        
    }

    private static void loadTranInflowManager()
    {
        try
        {
            final Class<?> clazz = Class.forName("com.ibm.tx.jta.impl.TransactionInflowManagerImpl");
            final Method m = clazz.getMethod("instance", (Class[])null);

            _tranInflowManager = (TransactionInflowManager) m.invoke(null, (Object[])null);
        }
        catch(Exception e)
        {
            FFDCFilter.processException(e, "com.ibm.tx.jta.TransactionInflowManagerFactory.loadTranInflowManager", "40");
            _tranInflowManager = null;
        }
    }
}