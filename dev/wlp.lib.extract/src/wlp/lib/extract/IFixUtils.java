/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package wlp.lib.extract;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility methods related to iFix processing
 */
public class IFixUtils {
    public static class ParsedIFix {
        String id;
        List fixedFiles;
        List apars;

        public ParsedIFix(String id, List fileNames, List aparNames) {
            this.id = id;
            fixedFiles = fileNames;
            apars = aparNames;
        }
    }

    /**
     * Parse an iFix.xml file
     *
     * @param fixFile
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static ParsedIFix parse(File fixFile) throws ParserConfigurationException, IOException, SAXException {
        String id = null;
        List fileNames = new ArrayList();
        List apars = new ArrayList();
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document d = db.parse(fixFile);
        NodeList nl = d.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element && n.getNodeName().equalsIgnoreCase("fix")) {
                id = ((Element) n).getAttribute("id");
                NodeList fixElements = n.getChildNodes();
                for (int j = 0; j < fixElements.getLength(); j++) {
                    Node subNode = fixElements.item(j);
                    if (subNode.getNodeName().equalsIgnoreCase("updates")) {
                        NodeList fileNodes = subNode.getChildNodes();
                        for (int k = 0; k < fileNodes.getLength(); k++) {
                            Node fileNode = fileNodes.item(k);
                            if (fileNode instanceof Element) {
                                fileNames.add(((Element) fileNode).getAttribute("id"));
                            }
                        }
                    } else if (subNode.getNodeName().equalsIgnoreCase("resolves")) {
                        NodeList problemNodes = subNode.getChildNodes();
                        for (int k = 0; k < problemNodes.getLength(); k++) {
                            Node problemNode = problemNodes.item(k);
                            if (problemNode instanceof Element) {
                                apars.add(((Element) problemNode).getAttribute("displayId"));
                            }
                        }
                    }
                }
            }
        }
        ParsedIFix result = new ParsedIFix(id, fileNames, apars);
        return result;
    }

    /**
     * For a given list of newly extracted files, determine whether any of those files would have been ifixed
     * had they been present before the iFixes in $wlpDir/lib/fixes were applied.
     *
     * @param wlpDir
     * @param extractedFiles - the files we've just extracted into wlpDir
     * @return List of iFixes to reapply
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    static List listIFixesThatMustBeReapplied(File wlpDir, List extractedFiles) throws ParserConfigurationException, IOException, SAXException {
        Set fixFiles = filterSupersededIFixXmls(wlpDir);
        List result = new ArrayList();
        if (fixFiles != null) {
            for (Iterator iter = fixFiles.iterator(); iter.hasNext();) {
                File f = (File) iter.next();
                ParsedIFix iFix = parse(f);
                inner: for (Iterator j = iFix.fixedFiles.iterator(); j.hasNext();) {
                    String fixedFile = (String) j.next();
                    for (Iterator k = extractedFiles.iterator(); k.hasNext();) {
                        String extractedFile = (String) k.next();
                        if (matchesIFixedFile(extractedFile, fixedFile)) {
                            result.add(iFix.id);
                            break inner;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * This method returns a Set containing File Objects representing all ifix xml files.
     *
     * @param wlpDir - the Liberty install dir.
     * @return - A Set of File objects for the ifix xml files.
     */
    private static Set getAllFixXMLs(File wlpDir) {
        Set result = new HashSet();
        File libFixes = new File(wlpDir, "lib/fixes");
        File[] xmlFiles = libFixes.listFiles();
        if (xmlFiles != null && xmlFiles.length > 0)
            result.addAll(Arrays.asList(xmlFiles));

        return result;
    }

    /**
     * This method loads all of the ifix xml files, and checks to see which ones have been superseded by other xmls, and only
     * returns those that haven't been superseded. This is to prevent the ifix re application message being issued for an apar which
     * has been superseded by another.
     *
     * @param wlpInstallationDirectory - The Liberty install dir.
     * @return A Set of File objects for valid Ifixs that haven't been superseded by others.
     */
    public static Set filterSupersededIFixXmls(File wlpInstallationDirectory) throws ParserConfigurationException, IOException, SAXException {

        // Get the full list of XML files
        Set ifixXMLs = getAllFixXMLs(wlpInstallationDirectory);
        Set validIFixXMLs = new HashSet();

        // Iterate over each one and then check every other ifix file against it.
        for (Iterator iter = ifixXMLs.iterator(); iter.hasNext();) {
            boolean superseded = false;
            File f1 = (File) iter.next();
            ParsedIFix iFix1 = parse(f1);
            for (Iterator iter2 = ifixXMLs.iterator(); iter2.hasNext();) {
                // Don't check the same ifix file against itself.
                File f2 = (File) iter2.next();
                ParsedIFix iFix2 = parse(f2);
                if (iFix1.id.equals(iFix2.id)) {
                    //Nop
                } else {
                    if (isSupersededBy(iFix1.apars, iFix2.apars))
                        superseded = true;
                }
            }
            // If we haven't been superseded, then add it to the return set.
            if (!superseded)
                validIFixXMLs.add(f1);
        }

        return validIFixXMLs;

    }

    /**
     * This method compares the list of Apar strings from 1 ifix xml, against
     * another xml. If the 2nd xml contains all of the apars Strings, then the 1st
     * ifix xml has been superseded.
     *
     * @param apars1 - The list of apars from the ifix to check.
     * @param apars2 - A list of apars from another ifix xml
     * @return - a boolean indicating whether the all apars strings from the 1st fix are
     *         contained within another ifix xml.
     */
    private static boolean isSupersededBy(List apars1, List apars2) {
        boolean result = true;
        // Now iterate over the current list of problems, and see if the incoming IFixInfo contains all of the problems from this IfixInfo.
        // If it does then return true, to indicate that this IFixInfo object has been superseded.
        for (Iterator iter1 = apars1.iterator(); iter1.hasNext();) {
            boolean currAparMatch = false;
            String currApar1 = (String) iter1.next();
            for (Iterator iter2 = apars2.iterator(); iter2.hasNext();) {
                String currApar2 = (String) iter2.next();
                if (currApar1.equals(currApar2)) {
                    currAparMatch = true;
                }
            }
            if (!currAparMatch)
                result = false;
        }

        return result;

    }

    /**
     * Does a file that we've just extracted look like it might have been affected had it been present
     * when one of our iFixes, that we applied earlier, was applied?
     *
     * Extracted file may be of the form
     * ../path/to/wlp/lib/com.ibm.ws.a.jar,
     * or if laid down by a tooling install,
     * ../some/path/to/lib/com.ibm.ws.a.jar
     * whereas iFixedFile will be relative to the wlp/ directory, i.e. lib/com.ibm.ws.a.jar
     *
     * @param extractedFile
     * @param iFixedFile
     * @return
     */
    static boolean matchesIFixedFile(String extractedFile, String iFixedFile) {
        /*
         *
         * Thus we use .endsWith() rather than .equals()
         */
        if (extractedFile.endsWith(iFixedFile)) {
            return true; // File names the same: match
        }

        if (!extractedFile.regionMatches(true, extractedFile.length() - 4, ".jar", 0, 4)) {
            return false; // Extracted file doesn't end in .jar: no match
        }

        String extractedBundle = extractedFile.substring(0, extractedFile.lastIndexOf("."));

        // There's another case: tooling might lay down ../../some/path/to/lib/something.jar
        //                                              ../another/path/to/dev/spi/spec/something.jar
        // We have to match this to lib/something_weHavePatched
        //                       or dev/spi/spec/something_WeHavePatched
        //
        // We'll match by splitting patched and extracted on the last /
        //   we need extractedDir to endWith patchedDir and
        //           patchedFile to startWith extractedFile
        //
        //
        String extractedDir = extractedBundle.substring(0, extractedBundle.lastIndexOf("/"));
        String fixedDir = iFixedFile.substring(0, iFixedFile.lastIndexOf("/"));

        String extractedFileName = extractedBundle.substring(extractedBundle.lastIndexOf("/") + 1);
        String fixedFileName = iFixedFile.substring(iFixedFile.lastIndexOf("/") + 1);

        return extractedDir.endsWith(fixedDir) && fixedFileName.startsWith(extractedFileName);

    }
}
