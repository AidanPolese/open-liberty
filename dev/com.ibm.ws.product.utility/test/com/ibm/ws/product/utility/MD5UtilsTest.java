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
package com.ibm.ws.product.utility;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;

import com.ibm.ws.product.utility.extension.MD5Utils;

/**
 * Test the MD5 Utilities works as expect.
 */
public class MD5UtilsTest {

    private static final String TEST_STR = "abc";
    private static final String TEST_STR_MD5_VALUE = "900150983cd24fb0d6963f7d28e17f72";

    private static final String TEST_FILE_NAME = "md5Tests/TestFile.txt";
    private static final String TEST_FILE_MD5_VALUE = "91d4999210e9ffa1444b37b9ca8364b2";

    @Test
    public void testGetMD5String() {
        String calculatedValue = MD5Utils.getMD5String(TEST_STR);
        Assert.assertEquals("The expected MD5 value of string abc is " + TEST_STR_MD5_VALUE, TEST_STR_MD5_VALUE, calculatedValue);
    }

    @Test
    public void testGetFileMD5String() {
        File testFile;
        String calculatedValue;
        try {
            testFile = new File(ClassLoader.getSystemResource(TEST_FILE_NAME).toURI());
            calculatedValue = MD5Utils.getFileMD5String(testFile);
        } catch (URISyntaxException e) {
            Assert.fail("Can not find mock file!");
            return;
        } catch (IOException e) {
            Assert.fail("Can not read mock file!");
            return;
        }
        Assert.assertEquals("The expected MD5 value of file " + testFile.getName(), TEST_FILE_MD5_VALUE, calculatedValue);
    }

}
