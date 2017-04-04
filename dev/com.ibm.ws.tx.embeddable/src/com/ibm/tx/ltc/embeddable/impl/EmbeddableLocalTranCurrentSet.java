/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009,2011 */
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
/*  Date      Programmer  Defect        Description                                  */
/*  --------  ----------  ------        -----------                                  */
/*  09-11-03  johawkes    F743-305.1    create                                       */
/*  11-11-24  johawkes    723423        Repackaging                                  */
/* ********************************************************************************* */

package com.ibm.tx.ltc.embeddable.impl;

import com.ibm.tx.ltc.impl.LocalTranCurrentImpl;
import com.ibm.ws.uow.UOWScopeCallback;
import com.ibm.ws.uow.UOWScopeCallbackManager;
import com.ibm.ws.util.WSThreadLocal;

/**
 * This class provides a way for Resource Manager Local Transactions (RMLTs)
 * accessed from an EJB or web component to be coordinated or contained within a
 * local transaction containment (LTC) scope. The LTC is what WebSphere provides
 * in the place of the <i>unspecified transaction context</i> described by the
 * EJB specification.
 * RMLTs are enlisted either to be coordinated by the LTC according to an external
 * signal or to be cleaned up at LTC end in the case that the application fails
 * in its duties.
 * The LocalTransactionCoordinator encapsulates details of local transaction
 * boundary and scopes itself either to the method invocation or ActivitySession.
 */

public class EmbeddableLocalTranCurrentSet extends com.ibm.tx.ltc.impl.LocalTranCurrentSet
{
    private static final UOWScopeCallbackManager _callbackManager;

    static
    {
        _callbackManager = new UOWScopeCallbackManager();

        // replace superclass members
        _context = new WSThreadLocal<LocalTranCurrentImpl>()
        {
            protected LocalTranCurrentImpl initialValue() { return new EmbeddableLocalTranCurrentImpl(_callbackManager); }
        };

        // set static member of superclass
        _instance = new EmbeddableLocalTranCurrentSet();
    }

    /**
     * Static instance method to return single instance
     */
    public static EmbeddableLocalTranCurrentSet instance()
    {
        return (EmbeddableLocalTranCurrentSet) _instance;
    }

    public void registerCallback(UOWScopeCallback callback)
    {
        _callbackManager.addCallback(callback);        
    }
}