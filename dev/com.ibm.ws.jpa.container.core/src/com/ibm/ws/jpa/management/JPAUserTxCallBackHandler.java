// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2007, 2012
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAUserTxCallBackHandler.java
//
// Source File Description:
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d416151.3 EJB3      20070306 leealber : Extend-scoped support
// d416151.3.7 EJB3    20070501 leealber : Add isAnyTraceEnabled() test
// d443713   EJB3      20070604 leealber : Guard NPE in Tr.entry
// d515803   WAS70     20080610 tkb      : Perf: Remove JPA hooks in pre/postInvoke
// F743-18776
//           WAS80     20100122 bkail    : Use EmbeddableTransactionManagerFactory
// 723423    WAS85     20111124 johawkes : Repackaging tx.embeddable
// F87075    WAS855    20121115 mblyakh  : Refactor extended context registration path
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa.management;

import static com.ibm.ws.jpa.management.JPAConstants.JPA_RESOURCE_BUNDLE_NAME;
import static com.ibm.ws.jpa.management.JPAConstants.JPA_TRACE_GROUP;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.Transaction.UOWCallback;
import com.ibm.ws.Transaction.UOWCoordinator;
import com.ibm.ws.jpa.JPAExPcBindingContext;
import com.ibm.ws.jpa.JPAExPcBindingContextAccessor;

/**
 * User Transaction callback handler.
 * 
 * A singleton UOWCallback handler of this class is created and registered to the transaction manager.
 * The UOWCallback.contextChange() method of this handler will be notified when an user transaction's
 * begin() or end() method is called.
 * 
 * The goal of this callback is to allow JPA Service to perform proper joinTransaction semantic for
 * extend-scoped persistence context processing for SFSB using BMT demarcation per JPA 5.9.1 requirement.
 */
public final class JPAUserTxCallBackHandler implements UOWCallback
{
    private static final TraceComponent tc = Tr.register(JPAUserTxCallBackHandler.class,
                                                         JPA_TRACE_GROUP,
                                                         JPA_RESOURCE_BUNDLE_NAME);

    private AbstractJPAComponent ivAbstractJPAComponent;

    /**
     * Constructs the Singleton instance of JPAUserTxCallBackHandler.
     */
    public JPAUserTxCallBackHandler(AbstractJPAComponent abstractJPAComponent)
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
            Tr.debug(tc, "<init>");

        ivAbstractJPAComponent = abstractJPAComponent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.Transaction.UOWCallback#contextChange(int,
     * com.ibm.ws.Transaction.UOWCoordinator)
     */
    public void contextChange(int typeOfChange, UOWCoordinator UOW) throws IllegalStateException
    {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "contextChange : " + typeOfChange + ", " + UOW);

        String changeType = "Unknown";
        // Determine the Tx change type and process
        switch (typeOfChange) {
            case PRE_BEGIN:
                changeType = "Pre_Begin";
                break;

            case POST_BEGIN:
                changeType = "Post_Begin";
                handleUserTxCallback(true);
                break;

            case PRE_END:
                changeType = "Pre_End";
                break;

            case POST_END:
                changeType = "Post_End";
                handleUserTxCallback(false);
                break;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "contextChange : " + changeType);
    }

    /**
     * When UserTransaction begin/end method is called, this method will mark this condition in
     * the current component/business method invocation in the current SFSB binding context. This
     * allows joinTransaction to be performed when any EntityManager interface method is called.
     */
    private final void handleUserTxCallback(boolean txBegin)
    {
        // Obtain the extended-scoped persistence context binding accessor from
        // the JPA Service, which was registered by the EJB Container.     d515803
        JPAExPcBindingContextAccessor exPcAccessor = ivAbstractJPAComponent.getExPcBindingContext(); // F743-18776

        // This should never occur, but if it somehow does, then just disable
        // this callback so other transactions may continue working.       d515803
        if (exPcAccessor == null)
        {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                Tr.debug(tc, "handleUserTxCallback : disabled - no Context Accessor");
            return;
        }

        // retrieve the caller's binding context from the thread
        JPAExPcBindingContext callerContext = exPcAccessor.getExPcBindingContext();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.entry(tc, "handleUserTxCallback : User Tx begin=" + txBegin + ", Caller binding context=\n\t--> "
                         + (callerContext != null ? callerContext.thisToString() : "{<<NONE>>}")); // d443713

        // (JPA 5.9.1 Container Responsibilities)
        // For stateful session beans with extended persistence contexts:
        // ** When a business method of the stateful session bean is invoked, if the stateful
        // session bean uses bean managed transaction demarcation and a UserTransaction is
        // begun within the method, the container associates the persistence context with
        // the JTA transaction and calls EntityManager.joinTransaction.
        if (callerContext != null &&
            callerContext.isBmt() &&
            callerContext.getExPcPuIds().length > 0)
        {
            callerContext.setBmtUserTxBegunInMethod(txBegin);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled())
            Tr.exit(tc, "handleUserTxCallback : " + callerContext);
    }
}
