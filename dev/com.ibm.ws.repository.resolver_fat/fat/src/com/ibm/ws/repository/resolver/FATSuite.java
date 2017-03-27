/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.repository.resolver;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.repository.base.servers.LarsServerRule;
import com.ibm.ws.repository.base.servers.MassiveServerRule;
import com.ibm.ws.repository.resolver.internal.RepositoryResolveContextFatTest;

@RunWith(Suite.class)
@SuiteClasses({
               RepositoryResolverTest.RepositoryResolverTestLars.class,
               RepositoryResolverTest.RepositoryResolverTestMassive.class,
               RepositoryResolverTest.RepositoryResolverTestDirectory.class,
               RepositoryResolveContextFatTest.RepositoryResolveContextFatTestLars.class,
               RepositoryResolveContextFatTest.RepositoryResolveContextFatTestMassive.class,
               RepositoryResolveContextFatTest.RepositoryResolveContextFatTestDirectory.class,
})
public class FATSuite {

    @ClassRule
    public static LarsServerRule larsResource;
    static {
        if (!System.getProperty("java.version").startsWith("1.6")) {
            larsResource = new LarsServerRule();
        }
    }

    @ClassRule
    public static MassiveServerRule massiveResource = new MassiveServerRule();

}
