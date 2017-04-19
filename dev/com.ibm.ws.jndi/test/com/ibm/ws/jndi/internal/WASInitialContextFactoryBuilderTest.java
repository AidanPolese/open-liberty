/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * Issue       Date        Name        Description
 * ----------- ----------- ----------- ----------------------------------------
 * 12261       17-Jan-2010 mahrwald@uk Change headers for SCA NPE
 */
package com.ibm.ws.jndi.internal;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.jmock.Mockery;
import org.junit.Test;

public class WASInitialContextFactoryBuilderTest {
    WASInitialContextFactoryBuilder sut = new WASInitialContextFactoryBuilder();
    private static Context ctx = new Mockery().mock(Context.class);

    @Test
    public void testNoICFProperty() throws Exception {
        Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        assertNull(sut.createInitialContextFactory(env));

    }

    @Test
    public void testNonExistentICF() throws Exception {
        Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        env.put(InitialContext.INITIAL_CONTEXT_FACTORY, "does.not.exist");
        assertNull(sut.createInitialContextFactory(env));
    }

    public static class MyICF implements InitialContextFactory {
        public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
            return ctx;
        }
    }

    @Test
    public void testUserICF() throws Exception {
        Hashtable<Object, Object> env = new Hashtable<Object, Object>();
        env.put(InitialContext.INITIAL_CONTEXT_FACTORY, MyICF.class.getName());
        assertSame(ctx, sut.createInitialContextFactory(env).getInitialContext(env));
    }
}
