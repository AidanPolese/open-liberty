/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package wlp.lib.extract;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for IFixUtils
 */
public class IFixTest {

    static File libFixes = new File("lib/fixes");
    static File ifix_xml = new File("lib/fixes/testIFix.xml");

    @BeforeClass
    public static void setup() throws Exception {
        if (!libFixes.exists()) {
            libFixes.mkdirs();
        }
        if (!ifix_xml.exists()) {
            PrintWriter pw = new PrintWriter(ifix_xml);
            pw.print(iFixText);
            pw.close();
        }
    }

    @AfterClass
    public static void cleanup() throws Exception {
        libFixes.delete();
        ifix_xml.delete();
    }

    @Test
    public void testParser() throws Exception {
        IFixUtils.ParsedIFix p = IFixUtils.parse(ifix_xml);

        assertEquals("Wrong ifix id", p.id, "8.5.0.1-WS-WASProd_WLPArchive-IFPM77826");
        assertEquals("Wrong number of fixed files", 8, p.fixedFiles.size());
        assertTrue("Fix file lib/com.ibm.ws.artifact.api.overlay_1.0.1.20121128-1822.jar not found",
                   p.fixedFiles.contains("lib/com.ibm.ws.artifact.api.overlay_1.0.1.20121128-1822.jar"));
        assertTrue("Fix file lib/com.ibm.ws.kernel.service_1.0.1.jar not found",
                   p.fixedFiles.contains("lib/com.ibm.ws.kernel.service_1.0.1.jar"));
    }

    @Test
    public void testWeDetectAnExtractedFileThatWouldHaveBeenIFixed() throws Exception {
        List<String> extractedFiles = new ArrayList<String>();
        extractedFiles.add("lib/com.ibm.ws.artifact.api.loose_1.0.1.20121128-1822.jar");
        List iFixes = IFixUtils.listIFixesThatMustBeReapplied(new File("."), extractedFiles);
        assertEquals("Wrong number of iFixes diagnosed", 1, iFixes.size());
        assertEquals("Wrong iFix diagnosed", "8.5.0.1-WS-WASProd_WLPArchive-IFPM77826", iFixes.get(0));
    }

    /*
     * Sometimes - depending on how you run the extractor - the extracted files can have a wlp/-like prefix.
     */
    @Test
    public void testForExtractedFilesWithPrefixes() throws Exception {
        List<String> extractedFiles = new ArrayList<String>();
        extractedFiles.add("/path/to/wlp/lib/com.ibm.ws.artifact.api.loose_1.0.1.20121128-1822.jar");
        List iFixes = IFixUtils.listIFixesThatMustBeReapplied(new File("."), extractedFiles);
        assertEquals("Wrong number of iFixes diagnosed in wlp/ prefix case", 1, iFixes.size());
        assertEquals("Wrong iFix diagnosed in wlp/ prefix case", "8.5.0.1-WS-WASProd_WLPArchive-IFPM77826", iFixes.get(0));
    }

    @Test
    public void testThingsWorkWhenNoIFixAffected() throws Exception {
        List<String> extractedFiles = new ArrayList<String>();
        extractedFiles.add("lib/this.bundle.was.extracted.but.affects.no.ifix.jar");
        List iFixes = IFixUtils.listIFixesThatMustBeReapplied(new File("."), extractedFiles);
        assertTrue("Affected ifix detected in error", iFixes.isEmpty());
    }

    @Test
    public void testWeHandleBundleVersionsCorrectly() throws Exception {
        List<String> extractedFiles = new ArrayList<String>();
        extractedFiles.add("lib/com.ibm.ws.artifact.api.zip_1.0.1.jar");
        List iFixes = IFixUtils.listIFixesThatMustBeReapplied(new File("."), extractedFiles);
        assertEquals("Wrong number of iFixes diagnosed", 1, iFixes.size());
        assertEquals("Wrong iFix diagnosed in bundle case", "8.5.0.1-WS-WASProd_WLPArchive-IFPM77826", iFixes.get(0));
    }

    @Test
    public void testWeHandlePrefixedBundleChangesCorrectly() throws Exception {
        List<String> extractedFiles = new ArrayList<String>();
        extractedFiles.add("../a/path/to/wlp/lib/com.ibm.ws.artifact.api.zip_1.0.1.jar");
        List iFixes = IFixUtils.listIFixesThatMustBeReapplied(new File("."), extractedFiles);
        assertEquals("Wrong number of iFixes diagnosed in wlp/ prefix case", 1, iFixes.size());
        assertEquals("Wrong iFix diagnosed in wlp/ prefix bundle case", "8.5.0.1-WS-WASProd_WLPArchive-IFPM77826", iFixes.get(0));
    }

    @Test
    public void testWeHandleAToolingInstall() throws Exception {
        List<String> extractedFiles = new ArrayList<String>();
        extractedFiles.add("../a/path/to/dev/spec/spec.bundle_1.0.1.jar");
        List iFixes = IFixUtils.listIFixesThatMustBeReapplied(new File("."), extractedFiles);
        assertEquals("Wrong number of iFixes diagnosed in wlp/ prefix case", 1, iFixes.size());
        assertEquals("Wrong iFix diagnosed in wlp/ prefix bundle case", "8.5.0.1-WS-WASProd_WLPArchive-IFPM77826", iFixes.get(0));
    }

    @Test
    public void testSupersededIFixXMLs() throws Exception {
        // Ifix1 is superseded by ifix2
        Set apars1 = new HashSet();
        apars1.add("Apar1");
        apars1.add("Apar2");
        createIfixXML(new File(libFixes, "ifix1.xml"), "ifix1", apars1);
        // Ifix2 is superseded by ifix3
        Set apars2 = new HashSet();
        apars2.add("Apar1");
        apars2.add("Apar2");
        apars2.add("Apar3");
        createIfixXML(new File(libFixes, "ifix2.xml"), "ifix2", apars2);
        // This is the ifix that should be reported.
        Set apars3 = new HashSet();
        apars3.add("Apar1");
        apars3.add("Apar2");
        apars3.add("Apar3");
        apars3.add("Apar4");
        createIfixXML(new File(libFixes, "ifix3.xml"), "ifix3", apars3);
        // Ifix4 has been superseded by ifix 3.
        Set apars4 = new HashSet();
        apars4.add("Apar2");
        apars4.add("Apar4");
        createIfixXML(new File(libFixes, "ifix4.xml"), "ifix4", apars4);
        // Ifix5 has not been superseded.
        Set apars5 = new HashSet();
        apars5.add("Apar1");
        apars5.add("Apar5");
        createIfixXML(new File(libFixes, "ifix5.xml"), "ifix5", apars5);

        Set ifixXMLs = IFixUtils.filterSupersededIFixXmls(new File("."));

        boolean ifix1Found = false;
        boolean ifix2Found = false;
        boolean ifix3Found = false;
        boolean ifix4Found = false;
        boolean ifix5Found = false;

        for (Iterator iter = ifixXMLs.iterator(); iter.hasNext();) {
            File currXMLFile = (File) iter.next();
            if ("ifix1.xml".equals(currXMLFile.getName()))
                ifix1Found = true;
            else if ("ifix2.xml".equals(currXMLFile.getName()))
                ifix2Found = true;
            else if ("ifix3.xml".equals(currXMLFile.getName()))
                ifix3Found = true;
            else if ("ifix4.xml".equals(currXMLFile.getName()))
                ifix4Found = true;
            else if ("ifix5.xml".equals(currXMLFile.getName()))
                ifix5Found = true;
        }

        Assert.assertFalse("ifix1 incorrectly listed in ifix superseded list", ifix1Found);
        Assert.assertFalse("ifix2 incorrectly listed in ifix superseded list", ifix2Found);
        Assert.assertTrue("ifix3 not found in ifix superseded list", ifix3Found);
        Assert.assertFalse("ifix4 incorrectly listed in ifix superseded list", ifix4Found);
        Assert.assertTrue("ifix5 not found in ifix superseded list", ifix5Found);
    }

    /**
     * This method generates the ifix xml files that contain the files that are in the ifix install. You can choose to include jars or static files
     * via the booleans.
     * 
     * @param ifixFile - The Ifix File to write to.
     * @param ifixFiles - A Map of ifix files. The key is the relative location to the install root of the file, and the value is the actual file.
     * @param ifixApars - A set of String containing the apar numbers.
     */
    public static void createIfixXML(File ifixFile, String ifixName, Set ifixApars) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        buffer.append("  <fix id=\"" + ifixName + "\" version=\"1.0.0\">\n");
        buffer.append("  <applicability>\n");
        buffer.append("    <offering id=\"com.ibm.websphere.BASE.v85\" tolerance=\"[8.5.1,8.5.2)\"/>\n");
        buffer.append("    <offering id=\"com.ibm.websphere.DEVELOPERS.v85\" tolerance=\"[8.5.1,8.5.2)\"/>\n");
        buffer.append("    <offering id=\"com.ibm.websphere.ND.v85\" tolerance=\"[8.5.1,8.5.2)\"/>\n");
        buffer.append("  </applicability>\n");
        buffer.append("  <categories/>\n");
        buffer.append("  <information name=\"" + ifixName + "\" version=\"8.5.1.20121128_1822\">Web application response times are very slow</information>\n");
        buffer.append("  <property name=\"com.ibm.ws.superseded.apars\" value=\"PM70625\"/>\n");
        buffer.append("  <property name=\"recommended\" value=\"false\"/>\n");
        buffer.append("  <resolves problemCount=\"" + ifixApars.size() + "\" description=\"This fix resolves APARS:\" showList=\"true\">\n");
        for (Iterator iter = ifixApars.iterator(); iter.hasNext();) {
            String apar = (String) iter.next();
            buffer.append("    <problem id=\"com.ibm.ws.apar." + apar + "\" displayId=\"" + apar + "\" description=\"" + apar + "\"/>\n");
        }
        buffer.append("  </resolves>\n");
        buffer.append("  <updates>\n");
        buffer.append("    <file date=\"2012-11-28 18:22\" hash=\"b9603bc05d858e6d0a2dc57f2ac6a6b8\" size=\"191\" id=\"lib/test1-1.0.0.20130101.jar\"/>\n");
        buffer.append("  </updates>\n");
        buffer.append("</fix>\n");

        createFile(ifixFile, buffer);
    }

    /**
     * This writes a StringBuffer out to the required file.
     * 
     * @param fileToWrite - The file to write the buffer to.
     * @param buffer - A String buffer containing the contents of the file
     */
    public static File createFile(File fileToWrite, StringBuffer buffer) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileToWrite);
            fos.write(buffer.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return fileToWrite;
    }

    public static final String iFixText = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                                          + "<fix id=\"8.5.0.1-WS-WASProd_WLPArchive-IFPM77826\" version=\"8.5.1.20121128_1822\">"
                                          + "<applicability>"
                                          + "<offering id=\"com.ibm.websphere.BASE.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.EXPRESS.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.ND.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.BASETRIAL.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.EXPRESSTRIAL.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.NDTRIAL.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.WEBENAB.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.DEVELOPERS.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.zOS.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "<offering id=\"com.ibm.websphere.DEVELOPERSILAN.v85\" tolerance=\"[8.5.1,8.5.2)\"/>"
                                          + "</applicability>"
                                          + "<categories/>"
                                          + "<information name=\"8.5.0.1-WS-WASProd_WLPArchive-IFPM77826\" version=\"8.5.1.20121128_1822\">Web application response times are very slow</information>"
                                          + "<property name=\"com.ibm.ws.superseded.apars\" value=\"PM70625\"/>"
                                          + "<property name=\"recommended\" value=\"false\"/>"
                                          + "<resolves problemCount=\"2\" description=\"This fix resolves APARS:\" showList=\"true\">"
                                          + "<problem id=\"com.ibm.ws.apar.PM77826\" displayId=\"PM77826\" description=\"PM77826\"/>"
                                          + "<problem id=\"com.ibm.ws.apar.PM70625\" displayId=\"PM70625\" description=\"PM70625\"/>"
                                          + "</resolves>"
                                          + "<updates>"
                                          + "<file date=\"2012-11-28 18:22\" hash=\"7afb211071a3c08f103692e39bd97b51\" size=\"41903\" id=\"lib/com.ibm.ws.artifact.api.overlay_1.0.1.20121128-1822.jar\"/>"
                                          + "<file date=\"2012-11-28 18:22\" hash=\"39db8e9f1800965c36119651b3a32695\" size=\"197373\" id=\"lib/com.ibm.ws.classloading_1.0.1.20121128-1822.jar\"/>"
                                          + "<file date=\"2012-10-22 18:08\" hash=\"1fbd375da50a0fbca3a24f70c0bb7992\" size=\"121748\" id=\"lib/com.ibm.ws.jndi_1.0.1.20121022-1808.jar\"/>"
                                          + "<file date=\"2012-11-28 18:22\" hash=\"6e82f7692cac816f655877865b9e23c6\" size=\"62879\" id=\"lib/com.ibm.ws.artifact.api.zip_1.0.1.20121128-1822.jar\"/>"
                                          + "<file date=\"2012-11-28 18:22\" hash=\"1905727b0b04137c7b92736ff65a614d\" size=\"77473\" id=\"lib/com.ibm.ws.artifact.api.loose_1.0.1.20121128-1822.jar\"/>"
                                          + "<file date=\"2012-11-28 18:22\" hash=\"a4032fe03645f75d335b6e922ac2fcf7\" size=\"22775\" id=\"lib/com.ibm.ws.artifact.api.file_1.0.1.20121128-1822.jar\"/>"
                                          + "<file date=\"2013-11-28 18:22\" hash=\"401136452a53470c6fe369a4e32b1cec\" size=\"142023\" id=\"lib/com.ibm.ws.kernel.service_1.0.1.jar\"/>"
                                          + "<file date=\"2013-11-28 18:22\" hash=\"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn\" size=\"999999\" id=\"dev/spec/spec.bundle_1.0.1.patch.jar\"/>"
                                          + "</updates>"
                                          + "</fix>";
}
