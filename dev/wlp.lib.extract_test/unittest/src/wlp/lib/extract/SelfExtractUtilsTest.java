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
package wlp.lib.extract;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 *
 */
public class SelfExtractUtilsTest {
    private static final File testDir = new File("build/unittest/selfextractutilstest").getAbsoluteFile();

    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void beforeClass() throws Exception {
        Assert.assertTrue("mkdir -p " + testDir.getAbsolutePath(), testDir.mkdirs() || testDir.isDirectory());
        System.out.println("beforeClass() testDir set to " + testDir.getAbsolutePath());
    }

    @AfterClass
    public static void cleanUp() {
        System.out.println("Clean up testDir directory...");
        if (testDir.exists())
            deleteDir(testDir);
    }

    private static void delete(File file) {
        if (!file.exists()) {
            return;
        }

        File[] files = file.listFiles();
        if (files != null) {
            for (File child : files) {
                delete(child);
            }
        }

        System.out.println("Deleting " + file.getAbsolutePath());
        Assert.assertTrue("delete " + file, file.delete());
    }

    private static void deleteDir(File dir) {
        if (dir.exists()) {
            System.out.println("rm -rf " + dir.getAbsolutePath());
            delete(dir);
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                try {
                    source.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * This tests that an expected error is returned when attempting to read a malformed
     * manifest file.
     * 
     * In this case the eXtremeScale.server-1.1.0.mf manifest doesn't have a space after the
     * "Subsystem-Content:" header.
     * 
     * @throws Exception
     */
    @Test
    public void testReadMalformedManifest() throws Exception {
        String mfFileName = "eXtremeScale.server-1.1.0.mf";
        File manifestDir = new File(testDir, "lib/features/");

        assertTrue("Unable to create directory: " + manifestDir.getAbsolutePath(), manifestDir.mkdirs());

        // Good manifest file
        File mfGoodSource = new File("inputs/manifests/adminSecurity-1.0.mf");
        File mfGoodDestination = new File(manifestDir, "adminSecurity-1.0.mf");

        try {
            copyFile(mfGoodSource, mfGoodDestination);
        } catch (Exception e) {
            fail("Failed to copy file <" + mfGoodSource.getAbsolutePath() + "> to <"
                 + mfGoodDestination.getParent() + "> " + e.getMessage());
        }

        // Malformed manifest file
        File mfSource = new File("inputs/manifests/" + mfFileName);
        File mfDestination = new File(manifestDir, mfFileName);

        try {
            copyFile(mfSource, mfDestination);
        } catch (Exception e) {
            fail("Failed to copy file <" + mfSource.getAbsolutePath() + "> to <"
                 + mfDestination.getParent() + "> " + e.getMessage());
        }

        ReturnCode rc = SelfExtractUtils.processExecutableDirective(testDir);

        assertTrue("Expecting return code <" + ReturnCode.UNREADABLE + "> but received <" + rc.getCode() + ">",
                   rc.getCode() == ReturnCode.UNREADABLE);

        // Make sure the correct manifest file is specified in the error message
        assertTrue("Error message doesn't contain reference to " + mfFileName + " <" + rc.getErrorMessage() + ">",
                   rc.getErrorMessage().contains(mfFileName));
    }

}
