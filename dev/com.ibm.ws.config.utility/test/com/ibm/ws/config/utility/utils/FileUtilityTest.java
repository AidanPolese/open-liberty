/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.utility.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.PrintStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;

import com.ibm.ws.config.utility.IFileUtility;
import com.ibm.ws.config.utility.utils.FileUtility;

/**
 *
 */
public class FileUtilityTest {
    private final Mockery mock = new JUnit4Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    private final PrintStream stdout = mock.mock(PrintStream.class, "stdout");
    private final File file = mock.mock(File.class, "file");
    private final File parent = mock.mock(File.class, "parent");
    private final File grandParent = mock.mock(File.class, "grandParent");
    private final String SLASH = String.valueOf(File.separatorChar);
    private final IFileUtility fileUtil = new FileUtility("WLP_INSTALL_DIR", "WLP_USER_DIR");

    @After
    public void tearDown() {
        mock.assertIsSatisfied();
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.FileUtility#getServersDir()}.
     */
    @Test
    public void getServersDir_noSetVariable() {
        IFileUtility fileUtil = new FileUtility(null, null);
        assertFalse("fileUtil.getInstallDir is null", fileUtil.getInstallDir() == null);
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.FileUtility#getServersDir()}.
     */
    @Test
    public void getInstallDir() {
        IFileUtility fileUtil = new FileUtility("WLP_INSTALL_DIR", null);
        assertEquals("Did not get expected value for WLP_INSTALL_DIR",
                     "WLP_INSTALL_DIR" + SLASH, fileUtil.getInstallDir());
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.FileUtility#getServersDir()}.
     */
    @Test
    public void getInstallDir_windows() {
        String platform = System.getProperty("os.name");
        if (platform.toLowerCase().startsWith("win")) {
            IFileUtility fileUtil = new FileUtility(null, null);
            char driveLetter = fileUtil.getInstallDir().charAt(0);
            assertTrue("Did not get captial letter in WLP_INSTALL_DIR",
                       driveLetter >= 'A' && driveLetter <= 'Z');
        } else {
            System.out.println("Skipping test as platform is not Windows. We are on: " + platform);
        }
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.FileUtility#createParentDirectory(java.io.PrintStream, java.io.File)}.
     */
    @Test
    public void createParentDirectory_noParent() {

        mock.checking(new Expectations() {
            {
                one(file).getParentFile();
                will(returnValue(null));
            }
        });
        assertTrue("If we don't have a parent, we should return null",
                   fileUtil.createParentDirectory(stdout, file));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.FileUtility#createParentDirectory(java.io.PrintStream, java.io.File)}.
     */
    @Test
    public void createParentDirectory_parentExists() {

        mock.checking(new Expectations() {
            {
                one(file).getParentFile();
                will(returnValue(parent));
                one(parent).exists();
                will(returnValue(true));
            }
        });
        assertTrue("If the parent exists, return true",
                   fileUtil.createParentDirectory(stdout, file));
    }

    /**
     * Test method for {@link com.ibm.ws.config.utility.utils.FileUtility#createParentDirectory(java.io.PrintStream, java.io.File)}.
     */
    @Test
    public void createParentDirectory_succeedCreate() {
        mock.checking(new Expectations() {
            {
                one(file).getParentFile();
                will(returnValue(parent));
                one(parent).exists();
                will(returnValue(false));
                one(parent).getParentFile();
                will(returnValue(grandParent));
                one(grandParent).exists();
                will(returnValue(true));
                one(parent).mkdir();
                will(returnValue(true));
            }
        });
        assertTrue("If parent's parent exists and parent is created, return true",
                   fileUtil.createParentDirectory(stdout, file));
    }

}
