/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2009, 2012
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
 * ----------- ----------- --------    ------------------------------------
 * 3322        02/09/09    bakowski    Add source header
 * 4089        20-Oct-2009 mahrwald@uk Change copyright
 * 4930        06-Jan-2010 djvines@uk  Don't guard calls to Tr.error
 * 4616        08-Feb-2010 djvines@uk  Update trace group
 * 5616        17-Feb-2010 djvines@uk  Add trace
 * 10253       14-Oct-2010 not@uk      Use Thread Context Classloader for launchClient
 * 10948       02-Nov-2010 not@uk      Remove code that incorrectly switches from public ICF to private one.
 * 12261       17-Jan-2010 mahrwald@uk Change headers for SCA NPE
 */

package com.ibm.ws.jndi.internal;

import static org.osgi.service.component.annotations.ConfigurationPolicy.IGNORE;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, configurationPolicy = IGNORE, property = "service.vendor=IBM")
public class WASInitialContextFactoryBuilder implements InitialContextFactoryBuilder {

    @Override
    public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment)
                    throws NamingException {

        String icfFactory = (String) environment.get(Context.INITIAL_CONTEXT_FACTORY);
        InitialContextFactory icf = null;

        if (icfFactory != null) {
            try {
                Class<?> clazz = Class.forName(icfFactory, true, getClassLoader());
                icf = (InitialContextFactory) clazz.newInstance();
            } catch (Exception e) {
                //auto FFDC
            }
        }

        return icf;
    }

    private ClassLoader getClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run()
            {
                return Thread.currentThread().getContextClassLoader();
            }

        });
    }
}
