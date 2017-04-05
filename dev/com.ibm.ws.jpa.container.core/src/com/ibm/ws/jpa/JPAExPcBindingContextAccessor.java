// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// %I% %W% %G% %U%
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2008, 2012
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAExPcBindingContextAccessor.java
//
// Source File Description:
//
//     Extend-scoped persistence context binding context accessor interface.
//
// Source File Description:
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d515803   WAS70     20080606 tkb      : Perf: Remove JPA hooks in pre/postInvoke
// d741678   WAS855    20121116 tkb      : Remove dependency on EJBException
// --------- --------- -------- --------- -----------------------------------------
package com.ibm.ws.jpa;

/**
 * Extend-scoped persistence context binding context accessor interface. <p>
 * 
 * An EJB Container implementation must be provided for this interface,
 * providing a mechanism to determine the current extended-scoped persistence
 * context binding context active on the thread. <p>
 */
public interface JPAExPcBindingContextAccessor
{
    /**
     * Returns the binding context for the currently active extended-scoped
     * persistence context for the thread of execution. Null will be returned
     * if an extended-scoped persistence context is not currently active. <p>
     * 
     * @return binding context for currently active extended-scoped
     *         persistence context.
     */
    public JPAExPcBindingContext getExPcBindingContext();

    /**
     * Constructs an EJBException with the specified detailed message. <p>
     * 
     * Allows the JPA code to have no dependencies on classes in
     * the javax.ejb package. <p>
     */
    // d741678
    public RuntimeException newEJBException(String msg);
}
