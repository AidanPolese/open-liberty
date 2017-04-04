package com.ibm.ws.jtaextensions;

/*************************************************************************** 
 COMPONENT_NAME: WAS.transactions                                           

 ORIGINS: 27                                                               

 IBM Confidential OCO Source Material                                       
 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2006, 2008 
 The source code for this program is not published or otherwise divested    
 of its trade secrets, irrespective of what has been deposited with the     
 U.S. Copyright Office.                                                     

 @(#) 1.10 SERV1/ws/code/was.transaction/src/com/ibm/ws/jtaextensions/ExtendedJTATransactionFactory.java, WAS.transactions, WASX.SERV1 5/22/08 11:01:02 [

 DESCRIPTION:                                                              

 Change History:                                                           

 Date      Programmer    Defect    Description                             
 --------  ----------    ------    -----------                             
 02-02-14  awilkins      LIDB850   Creation                                
 27-02-02  beavenj       LIDB1220.151.1 Code instrumented for FFDC work    
 22-08-02  sykesm        143104    Support 390 transaction management      
 21/02/03  gareth     LIDB1673.19  Make any unextended code final          
 23-03-04  mdobbie     LIDB3133-23 Added SPI classification                
 14-02-06  johawkes      347212    New ras & ffdc                          
 23-02-06  johawkes      349301    Old ras & ffdc                          
 22-05-08  johawkes      522569    Perf trace                              
 ************************************************************************** 
 */

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.tx.TranConstants;
import com.ibm.websphere.jtaextensions.ExtendedJTATransaction;
import com.ibm.ws.ffdc.FFDCFilter;

/**
 * 
 * This class is registered in the component name space
 * and provides user applications with the instance
 * of ExtendedJTATransaction. It can also be accessed
 * directly to provide the instance to other internal
 * classes.
 * 
 * <p> This class is private to WAS.
 * Any use of this class outside the WAS Express/ND codebase
 * is not supported.
 * 
 */
public final class ExtendedJTATransactionFactory implements ObjectFactory {
    private static final TraceComponent tc = Tr.register(
                                                         ExtendedJTATransactionFactory.class
                                                         , TranConstants.TRACE_GROUP, TranConstants.NLS_FILE);

    private final static String extendedJTATxImplKey = "com.ibm.ws.transaction.ExtendedJTATransaction"; // d143104A

    // singleton instance of ExtendedJTATransaction
    private static ExtendedJTATransaction instance;

    // Implementation of ObjectFactory method.
    // Return the ExtendedJTATransaction object. 
    @Override
    public Object getObjectInstance(Object refObj, Name name, Context nameCtx, Hashtable env) throws Exception {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "getObjectInstance", "" + name);
        }

        ExtendedJTATransaction extJTATran = null;

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "class name: " + (refObj instanceof Reference ? ((Reference) refObj).getClassName() : null));
        }

        extJTATran = refObj instanceof Reference && ((Reference) refObj).getFactoryClassName() == null ? createExtendedJTATransaction() : null;

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "getObjectInstance: " + extJTATran);
        }

        return extJTATran;
    }

    // Returns the singleton instance of ExtendedJTATransaction
    // If the instance does not exist, it is created and then
    // returned.
    public static ExtendedJTATransaction createExtendedJTATransaction() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "createExtendedJTATransaction");
        }

        // If we haven't already loaded and
        // instantiated ExtendedJTATransactionImpl
        // do so now.
        if (instance == null) {
            try {
                // LIBERTY avoid use of implfactory
                instance = new ExtendedJTATransactionImpl();
            } catch (Exception e) {
                FFDCFilter.processException(e, "com.ibm.ws.jtaextensions.ExtendedJTATransactionFactory.createExtendedJTATransaction", "95");
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "ExtendedJTATransactionImpl load and instantiation failed");
                }
            }
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "createExtendedJTATransaction", instance);
        }

        return instance;
    }

    // Get the singleton instance of ExtendedJTATransaction
    public static ExtendedJTATransaction getExtendedJTATransaction() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "getExtendedJTATransaction", instance);
        }
        return instance;
    }
}
