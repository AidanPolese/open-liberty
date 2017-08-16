package com.ibm.ws.concurrent.persistent.fat.demo;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.websphere.simplicity.Machine;

import componenttest.topology.impl.LibertyFileManager;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

@RunWith(Suite.class)
@SuiteClasses({ PersistentExecutorDemoTest.class })
public class FATSuite {
    static LibertyServer server = LibertyServerFactory.getLibertyServer("com.ibm.ws.concurrent.persistent.fat.demo");

    /**
     * Copies the simulated GA repository local files (saved during compilation) to the server install root.
     *
     * @param traceTag The tag String to be used to log info.
     */
    @BeforeClass
    public static void beforeSuite() throws Exception {
        // Delete the Derby-only database that is used by the persistent scheduled executor
        Machine machine = server.getMachine();
        String installRoot = server.getInstallRoot();
        LibertyFileManager.deleteLibertyDirectoryAndContents(machine, installRoot + "/usr/shared/resources/data/persistdemodb");
    }
}