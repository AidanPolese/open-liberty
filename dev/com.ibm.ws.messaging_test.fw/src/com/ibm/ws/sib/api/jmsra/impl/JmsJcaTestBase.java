/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * --------------- --------- -------- --------------------------------------------
 * 169626.7        28-Jul-03 pnickoll Created
 * 181796.6        05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 179630.1.1      10-Nov-03 djhoward Use new mediation method to obtain SICoreConnection
 * 187870          27-Feb-04 sambo    Split out outbound resource adapter for client install
 * 199220          20-Apr-04 pnickoll Corrected javadoc and removed unneeded import
 * LIDB3706-5.264  03-Feb-05 kingdon  Add serial version UID
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra.impl;

import java.util.Map;

import javax.security.auth.Subject;

import com.ibm.js.test.LoggingTestCase;
import com.ibm.ws.sib.api.jmsra.JmsJcaManagedConnectionFactory;
import com.ibm.ws.sib.api.jmsra.stubs.SICoreConnectionStub;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SICoreConnectionFactory;
import com.ibm.wsspi.sib.core.SICoreConnectionFactorySelector;
import com.ibm.wsspi.sib.core.selector.FactoryType;

/**
 * @author pnickoll
 */
public class JmsJcaTestBase extends LoggingTestCase {

    // Added at version 1.11
    private static final long serialVersionUID = -5819101423903871610L;

    /**
     * Constructor for JmsJcaTestBase.
     * 
     * @param arg0
     */
    public JmsJcaTestBase(String arg0) {
        super(arg0);
    }

    /*
     * Helper functions
     */

    protected SICoreConnection createSimpConnection() {
        return createSimpConnection(null, null);
    }

    protected SICoreConnection createSimpConnection(Subject subject, Map props) {
        SICoreConnection simpConn = null;
        try {
            // 179630.1.1
            SICoreConnectionFactory trmSIConnFact = SICoreConnectionFactorySelector.getSICoreConnectionFactory(FactoryType.TRM_CONNECTION);
            simpConn = trmSIConnFact.createConnection(subject, props);
        } catch (Exception ex) {
            // We should not reach this point
            ex.printStackTrace();
            error(ex);
        }
        return simpConn;
    }

    protected SICoreConnection createSimpConnection(String userName, String password, Map props) {
        SICoreConnection simpConn = null;
        try {
            // 179630.1.1
            SICoreConnectionFactory trmSIConnFact = SICoreConnectionFactorySelector.getSICoreConnectionFactory(FactoryType.TRM_CONNECTION);
            simpConn = trmSIConnFact.createConnection(userName, password, props);
        } catch (Exception ex) {
            // We should not reach this point
            ex.printStackTrace();
            error(ex);
        }
        return simpConn;
    }

    public Subject createSubject(String user, String password, JmsJcaManagedConnectionFactory factory) {
        Subject subject = new Subject();

        return subject;
    }

    public void assertEquivalent(SICoreConnection con1, SICoreConnection con2) {
        if (!con1.isEquivalentTo(con2)) {
            error("The two connections should be equivalent");
        }
        if (con1 == con2) {
            error("The two connection are the same but one should be a clone");
        }
    }

    public void assertSimpCorrect(SICoreConnection coreConn, String userName, String password) {
        assertTrue(coreConn instanceof SICoreConnectionStub);
        SICoreConnectionStub coreStub = (SICoreConnectionStub) coreConn;
        assertEquals("Usernames don't match", coreStub.getUserName(), userName);
        if (password == null) {
            assertTrue((coreStub.getPassword() == null) || (coreStub.getPassword().equals("")));
        } else {
            assertEquals("Passwords don't match", coreStub.getPassword(), password);
        }
    }

    public void assertSimpCorrect(SICoreConnection coreConn, Subject subject) {
        assertTrue(coreConn instanceof SICoreConnectionStub);
        SICoreConnectionStub coreStub = (SICoreConnectionStub) coreConn;
        assertEquals("Subjects don't match", coreStub.getSubject(), subject);
    }

    public void assertSimpCorrect(SICoreConnection coreConn, JmsJcaManagedConnectionFactory jcaMCF) {
        assertTrue(coreConn instanceof SICoreConnectionStub);
        SICoreConnectionStub coreStub = (SICoreConnectionStub) coreConn;
        assertEquals("Usernames don't match", coreStub.getUserName(), jcaMCF.getUserName());
        if (jcaMCF.getPassword() == null) {
            assertTrue((coreStub.getPassword() == null) || (coreStub.getPassword().equals("")));
        } else {
            assertEquals("Passwords don't match", coreStub.getPassword(), jcaMCF.getPassword());
        }
    }

}
