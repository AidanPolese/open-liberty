/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/* ORIGINS: 27                                                                */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004          */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/* DESCRIPTION:                                                               */
/*                                                                            */
/* Change History:                                                            */
/*                                                                            */
/* Date      Programmer  Defect         Description                           */
/* --------  ----------  ------         -----------                           */
/* 04-01-09  awilkins    LIDB2775-53.5  Creation - z/OS code merge            */
/* 04-03-24  awilkins  LIDB2775.53.5.1  Move to public component              */
/* 04-03-26  awilkins  LIDB2775-53.5.2  More z/OS code merge changes          */
/* 07-12-02  irobins   486371           Remove dependency on com.ibm.ejs.util */
/*                                                                            */
/* ************************************************************************** */

package com.ibm.ws.recoverylog.spi;

import java.util.HashMap;

import com.ibm.tx.util.logging.Tr;
import com.ibm.tx.util.logging.TraceComponent;

public class FailureScopeManager
{
    private static final TraceComponent tc = Tr.register(FailureScopeManager.class, TraceConstants.TRACE_GROUP, null);       
    
    private static HashMap _failureScopeFactoriesById = new HashMap();
    private static HashMap _failureScopeFactoriesByClass = new HashMap();
    
    public static void registerFailureScopeFactory(Byte id, Class failureScopeClass, FailureScopeFactory factory)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "registerFailureScopeFactory", new Object[] {id, failureScopeClass, factory});
        
        _failureScopeFactoriesById.put(id, factory);
        _failureScopeFactoriesByClass.put(failureScopeClass, factory);
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "registerFailureScopeFactory");
    }
    
    public static byte[] toByteArray(FailureScope failureScope)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "toByteArray", failureScope);
        
        byte[] bytes = null;
        
        final Class failureScopeClass = failureScope.getClass();
        
        if (tc.isDebugEnabled()) Tr.debug(tc, "FailureScope class: " + failureScopeClass);
               
        final FailureScopeFactory failureScopeFactory = (FailureScopeFactory)_failureScopeFactoriesByClass.get(failureScopeClass);
        
        if (failureScopeFactory != null)
        {
            bytes = failureScopeFactory.toByteArray(failureScope);
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "toByteArray");
        return bytes;
    }
    
    public static FailureScope toFailureScope(byte[] bytes)
    {
        if (tc.isEntryEnabled()) Tr.entry(tc, "toFailureScope", bytes);
        
        // The first byte in the given array is the failure scope factory's
        // identifier. Extract it from the array and use it to lookup the
        // appropriate factory.
        final Byte factoryId = new Byte(bytes[0]);
        final FailureScopeFactory failureScopeFactory = (FailureScopeFactory)_failureScopeFactoriesById.get(factoryId);
        
        FailureScope failureScope = null;
        
        if (failureScopeFactory != null)
        {
            failureScope = failureScopeFactory.toFailureScope(bytes);
        }
        
        if (tc.isEntryEnabled()) Tr.exit(tc, "toFailureScope", failureScope);
        return failureScope;
    }
}
