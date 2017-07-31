/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package app1.web;

import javax.servlet.annotation.WebServlet;

import org.junit.Test;

import componenttest.app.FATServlet;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/TestServletA")
public class TestServletA extends FATServlet {

    @Test
    public void testServer1() throws Exception {
        System.out.println("Test is running.");
    }

    @Test
    @Mode(TestMode.LITE)
    public void liteTest() throws Exception {
        System.out.println("LITE test is running.");
    }

    @Test
    @Mode(TestMode.FULL)
    public void testFull() throws Exception {
        System.out.println("This test should only run in Full or higher mode!");
    }

    @Test
    @Mode(TestMode.QUARANTINE)
    public void testQuarantine() throws Exception {
        System.out.println("This test should only run in Quarantine mode!");
    }
}
