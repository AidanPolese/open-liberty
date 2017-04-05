package com.ibm.tx.jta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransactionManagerFactory
{
    protected static ExtendedTransactionManager _tranManager;

    public static ExtendedTransactionManager getTransactionManager()
    {
        if(_tranManager == null)
        {
            loadTranManager();
        }

        return _tranManager;        
    }

    // used by for example WAS TMFactory to set it's TM here to be consistent
    public static void setTransactionManager(ExtendedTransactionManager mgr)
    {
        _tranManager = mgr;
    }


    private static void loadTranManager()
    {
        final Class<?> clazz;
        final Method m;

        try
        {
            clazz = Class.forName("com.ibm.tx.jta.impl.TranManagerSet");
            m = clazz.getMethod("instance", (Class[])null);

            _tranManager = (ExtendedTransactionManager) m.invoke(null, (Object[])null);
        }
        catch(SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(NoSuchMethodException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(IllegalArgumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(InvocationTargetException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}