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
package com.ibm.ws.ejbcontainer.osgi.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 *
 */
public class PassivationDirectoryTest {

    private static final File TMP_DIR = new File("bin_test/tmp").getAbsoluteFile();

    @Test
    public void testDirectoryCreation() {
        assertTrue("mkdir " + TMP_DIR, TMP_DIR.mkdir());

        File passivationDir = new File(TMP_DIR, "passivation");

        assertFalse(passivationDir.exists());
        EJBRuntimeImpl.createPassivationDirectory(passivationDir);
        assertTrue(passivationDir.isDirectory());

        assertTrue("delete " + passivationDir, passivationDir.delete());
        assertTrue("delete " + TMP_DIR, TMP_DIR.delete());
    }

    @Test
    public void testFilesDeletion() throws IOException {
        assertTrue("mkdir " + TMP_DIR, TMP_DIR.mkdir());

        File passivationDir = new File(TMP_DIR, "passivation");

        EJBRuntimeImpl.createPassivationDirectory(passivationDir);

        // Create files 
        File file1 = new File(passivationDir, "file1.txt");
        File file2 = new File(passivationDir, "file2.txt");
        File file3 = new File(passivationDir, "file3.txt");
        File file4 = new File(passivationDir, "file4.txt");

        assertTrue("createNewFile " + file1, file1.createNewFile());
        assertTrue("createNewFile " + file2, file2.createNewFile());
        assertTrue("createNewFile " + file3, file3.createNewFile());
        assertTrue("createNewFile " + file4, file4.createNewFile());

        // After calling the method, the files should be deleted
        assertTrue(passivationDir.list().length == 4);
        EJBRuntimeImpl.createPassivationDirectory(passivationDir);
        assertTrue(passivationDir.list().length == 0);

        assertTrue("delete " + passivationDir, passivationDir.delete());
        assertTrue("delete " + TMP_DIR, TMP_DIR.delete());
    }
}
