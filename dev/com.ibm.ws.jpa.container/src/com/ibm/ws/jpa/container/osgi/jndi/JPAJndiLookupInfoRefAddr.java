// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.3 SERV1/ws/code/ecutils/src/com/ibm/ws/util/JPAJndiLookupInfoRefAddr.java, WAS.ejbcontainer, WASX.SERV1 7/14/11 12:52:21
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2006, 2011
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  JPAJndiLookupInfoRefAddr.java
//
// Source File Description:
//
//      Naming InfoRefAddr object used for JPA @PersistenceUnit and @PersistenceContext
//      JNDI Reference binding.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d392996   EJB3      20060930 leealber : Initial Release
// d510184   WAS70     20080424 tkb      : Create separate EMF for each java:comp
// F46994.1  WAS85     20110712 tkb      : remove tWAS Naming dependencies
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ws.jpa.container.osgi.jndi;

import javax.naming.RefAddr;

/*
 * Naming InfoRefAddr object used for JPA @PersistenceUnit and @PersistenceContext
 * JNDI Reference binding.
 */
public class JPAJndiLookupInfoRefAddr extends RefAddr {
    private static final long serialVersionUID = -3175608835610568086L;

    public static final String Addr_Type = "JPAJndiLookupInfo";

    // Info object
    JPAJndiLookupInfo ivInfo = null;

    /**
     * Constructs a new instance of JPAJndiLookupInfoRefAddr with JPAJndiLookupInfo.
     */
    public JPAJndiLookupInfoRefAddr(JPAJndiLookupInfo info) {
        super(Addr_Type);
        this.ivInfo = info;
    }

    /**
     * @see javax.naming.RefAddr#getContent()
     */
    @Override
    public Object getContent() {
        return ivInfo;
    }
}
