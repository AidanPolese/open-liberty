/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.kernel.boot;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.kernel.boot.commandline.CreateCommandTest;
import com.ibm.ws.kernel.boot.commandline.DumpCommandTest;
import com.ibm.ws.kernel.boot.commandline.PauseResumeCommandTest;
import com.ibm.ws.kernel.boot.commandline.StartCommandTest;
import com.ibm.ws.kernel.boot.commandport.ServerCommandPortTest;
import com.ibm.ws.kernel.boot.internal.commands.LogLevelPropertyTest;
import com.ibm.ws.kernel.boot.internal.commands.PackageCommandTest;
import com.ibm.ws.kernel.provisioning.KernelChangeTest;
import com.ibm.ws.kernel.provisioning.ProvisioningTest;
import com.ibm.wsspi.kernel.embeddable.EmbeddedServerAddProductExtensionMultipleTest;
import com.ibm.wsspi.kernel.embeddable.EmbeddedServerAddProductExtensionTest;
import com.ibm.wsspi.kernel.embeddable.EmbeddedServerMergeProductExtensionTest;
import com.ibm.wsspi.kernel.embeddable.EmbeddedServerTest;

/**
 * Collection of a few fast example tests
 */
@RunWith(Suite.class)
/*
 * The classes specified in the @SuiteClasses annotation
 * below should only be mainline test cases that complete
 * in a combined total of 5 minutes or less.
 */
@SuiteClasses({
                EmbeddedServerTest.class,
                EmbeddedServerAddProductExtensionTest.class,
                EmbeddedServerAddProductExtensionMultipleTest.class,
                ProvisioningTest.class,
                KernelChangeTest.class,
                ServerStartTest.class,
                ServerStartAsServiceTest.class,
                ShutdownTest.class,
                ServerCommandPortTest.class,
                DumpCommandTest.class,
                PackageCommandTest.class,
                LogLevelPropertyTest.class,
                CreateCommandTest.class,
                StartCommandTest.class,
                ServerClasspathTest.class,
                ServerStartJVMOptionsTest.class,
                PauseResumeCommandTest.class,
                EmbeddedServerMergeProductExtensionTest.class
})
public class FATSuite {}
