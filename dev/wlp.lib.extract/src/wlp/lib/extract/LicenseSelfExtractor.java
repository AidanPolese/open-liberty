/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2016
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package wlp.lib.extract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class LicenseSelfExtractor extends SelfExtractor {

    private static final String LA_FILES_PREFIX = "lafiles/";
    private static final String FEATURES_PREFIX = "lib/features";
    private static final String ASSETS_PREFIX = "lib/assets";
    private static final String TAG_FILES_PREFIX = "lib/versions/tags/";
    private static final String WAS_PROPERTIES_FILE = "lib/versions/WebSphereApplicationServer.properties";
    private static final String FIX_NAME_FRAGMENT_FIX_PACK = " Fix Pack ";
    private static final Pattern validNumericVersion = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)$");
    private static final String DOWNGRADESupportedVersion = "8.5.5.8";
    public static final String[][] DOWNGRADE_EDITION_MATCH = new String[][] {
                                                                              { "LIBERTY_CORE", "BASE", "BASE_ILAN", "ND" },
                                                                              { "BASE", "ND" },
    };
    public static final String[][] UPGRADE_EDITION_MATCH = new String[][] {
                                                                            { "ND", "BASE", "LIBERTY_CORE", "BASE_ILAN" },
                                                                            { "BASE", "LIBERTY_CORE", "BASE_ILAN" },
    };

    private final LicenseArchive licenseArchive;

    protected LicenseSelfExtractor(JarFile jar, LicenseProvider licenseProvider, Attributes attributes) throws IOException {
        super(jar, licenseProvider, attributes);
        licenseArchive = new LicenseArchive();
    }

    public String getExtractSuccessMessageKey() {
        return "extractLicenseSuccess";
    }

    public String getExtractInstructionMessageKey() {
        return "extractLicenseInstruction";
    }

    public ReturnCode extract(File wlpInstallDir, ExtractProgress ep) {

        ChangeHistory history = new ChangeHistory();
        if (ep == null) {
            ep = new NullExtractProgress();
        }

        File libertyPropsFile = new File(wlpInstallDir, WAS_PROPERTIES_FILE);
        Properties libertyProps = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(libertyPropsFile);
            libertyProps.load(is);
        } catch (IOException ioe) {
            return new ReturnCode(ReturnCode.BAD_OUTPUT, "extractFileError", ioe.getMessage());
        } finally {
            SelfExtractUtils.tryToClose(is);
        }
        try {
            WASProperties _libertyProps = new WASProperties(libertyProps, libertyPropsFile, history);

            // Write WebSphereApplicationServer.properties.
            _libertyProps.setProductEdition(licenseArchive.getWASProperties().getProductEdition());
            _libertyProps.setProductLicenseType(licenseArchive.getWASProperties().getProductLicenseType());
            _libertyProps.store();

            // Replace license files.
            File libertyLicenseDir = new File(wlpInstallDir, LA_FILES_PREFIX);
            cleanDirectory(libertyLicenseDir, history);
            writeLicenseFiles(licenseArchive, libertyLicenseDir, history, ep);

            // Replace tag files.
            File libertyTagsDir = new File(wlpInstallDir, TAG_FILES_PREFIX);
            String fixpackVersion = getFixpackVersionFromFxtagFile(libertyTagsDir);
            //boolean is855Tags = findFxtagFile(libertyTagsDir).getName().endsWith(".fxtag");
            boolean is855Tags = _libertyProps.getProductVersion().startsWith("8.5");
            cleanDirectory(libertyTagsDir, history);
            writeTagFiles(licenseArchive, libertyTagsDir, history, ep, is855Tags);
            if (fixpackVersion != null) {
                updateFixPackVersion(libertyTagsDir, fixpackVersion);
            } else {
                updateFixPackVersion(libertyTagsDir, _libertyProps.getProductVersion());
            }

        } catch (IOException ioe) {
            history.rollback();
            return new ReturnCode(ReturnCode.BAD_OUTPUT, "extractFileError", ioe.getMessage());
        }
        return ReturnCode.OK;
    }

    private static String getFixpackVersionFromFxtagFile(File libertyTagsDir) throws IOException {
        final File fxtagFile = findFxtagFile(libertyTagsDir);
        if (fxtagFile == null) {
            return null;
        }
        final Document doc = toDOM(fxtagFile);
        if (fxtagFile.getName().endsWith(".fxtag")) {
            Node n = doc.getDocumentElement().getFirstChild();
            while (n != null) {
                if (Node.ELEMENT_NODE == n.getNodeType() &&
                    "FixVersion".equals(n.getNodeName())) {
                    n = n.getFirstChild();
                    if (n != null && Node.TEXT_NODE == n.getNodeType()) {
                        return n.getNodeValue();
                    }
                    return null;
                }
                n = n.getNextSibling();
            }
        } else {
            Node n = doc.getDocumentElement().getOwnerDocument().getFirstChild();
            while (n != null) {
                if ("SoftwareIdentity".equals(n.getNodeName())) {
                    Element e = (Element) n;
                    return e.getAttribute("version");
                }
                n = n.getNextSibling();
            }
        }

        return null;
    }

    private static void updateFixPackVersion(File libertyTagsDir, String version) throws IOException {
        final File fxtagFile = findFxtagFile(libertyTagsDir);
        if (fxtagFile == null) {
            return;
        }
        if (fxtagFile.getName().endsWith(".fxtag")) {
            updateFixpackVersionInFxtagFile(fxtagFile, version);
        } else {
            updateFixpackVersionInISOTagFile(fxtagFile, version);
        }
    }

    private static void updateFixpackVersionInFxtagFile(File fxtagFile, String version) throws IOException {
        final Document doc = toDOM(fxtagFile);
        Node n = doc.getDocumentElement().getFirstChild();
        while (n != null) {
            if (Node.ELEMENT_NODE == n.getNodeType()) {
                final String nodeName = n.getNodeName();
                if ("FixName".equals(nodeName)) {
                    // Replace the version number in the FixName
                    // e.g. WebSphere Application Server Network Deployment v8.5.5 Fix Pack 6
                    // ---> WebSphere Application Server Network Deployment v8.5.5 Fix Pack 5
                    Node child = n.getFirstChild();
                    if (child != null && Node.TEXT_NODE == child.getNodeType()) {
                        int index = version.lastIndexOf('.');
                        if (index != 1) {
                            final String baseVersion = version.substring(0, index);
                            final String fixpackNumber = version.substring(index + 1);
                            String s = child.getNodeValue();
                            index = s.lastIndexOf(FIX_NAME_FRAGMENT_FIX_PACK);
                            if (index != -1) {
                                s = s.substring(0, index);
                                index = s.lastIndexOf('v');
                                if (index != -1) {
                                    StringBuffer buffer = new StringBuffer();
                                    buffer.append(s.substring(0, index + 1));
                                    buffer.append(baseVersion);
                                    buffer.append(FIX_NAME_FRAGMENT_FIX_PACK);
                                    buffer.append(fixpackNumber);
                                    n.replaceChild(doc.createTextNode(buffer.toString()), child);
                                }
                            }
                        }
                    }
                } else if ("FixVersion".equals(nodeName)) {
                    // Replace the FixVersion
                    // e.g. 8.5.5.6 ---> 8.5.5.5
                    Node child = n.getFirstChild();
                    Text fixVersionText = doc.createTextNode(version);
                    if (child == null) {
                        n.appendChild(fixVersionText);
                    } else if (Node.TEXT_NODE == child.getNodeType()) {
                        n.replaceChild(fixVersionText, child);
                    }
                } else if ("FixID".equals(nodeName)) {
                    // Replace the version number in the FixID
                    // e.g. wlp-nd-runtime-8.5.5.6 ---> wlp-nd-runtime-8.5.5.5
                    Node child = n.getFirstChild();
                    if (child != null && Node.TEXT_NODE == child.getNodeType()) {
                        String s = child.getNodeValue();
                        int index = s.lastIndexOf('-');
                        if (index != -1) {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append(s.substring(0, index + 1));
                            buffer.append(version);
                            n.replaceChild(doc.createTextNode(buffer.toString()), child);
                        }
                    }
                }
            }
            n = n.getNextSibling();
        }
        toFile(doc, fxtagFile);
    }

    private static void updateFixpackVersionInISOTagFile(File fxtagFile, String version) throws IOException {
        final Document doc = toDOM(fxtagFile);
        Node n = doc.getDocumentElement().getOwnerDocument().getFirstChild();
        while (n != null) {
            if ("SoftwareIdentity".equals(n.getNodeName())) {
                Element e = (Element) n;
                e.setAttribute("version", version);
                String tagId = e.getAttribute("tagId");
                tagId = tagId.replaceFirst("\\d+\\.\\d+\\.\\d+\\.\\d+", version);
                e.setAttribute("tagId", tagId);
                n = n.getFirstChild();
                continue;
            } else if ("Meta".equals(n.getNodeName())) {
                Element e = (Element) n;
                if (e.getAttribute("fixId") != null && !!!e.getAttribute("fixId").isEmpty()) {
                    e.setAttribute("fixId", version);
                }
            }
            n = n.getNextSibling();
        }
        if (!!!fxtagFile.getName().contains(version)) {
            //not same version, also need to update the name of the fix tag file.
            File fxtagFileNew = new File(fxtagFile.getParentFile(), fxtagFile.getName().replaceAll("\\d+\\.\\d+\\.\\d+\\.\\d+", version));
            fxtagFile.delete();
            toFile(doc, fxtagFileNew);
        } else {
            toFile(doc, fxtagFile);
        }

    }

    private static File findFxtagFile(File libertyTagsDir) {
        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                Pattern pattern = Pattern.compile("ibm.com_WebSphere_Application_Server_Liberty(.*?)-\\d+\\.\\d+\\.\\d+\\.\\d+\\.swidtag");
                return name.endsWith(".fxtag") || pattern.matcher(new File(name).getName()).matches();
            }
        };
        // Return the first .fxtag file we find. We assume there's only one.
        final File[] fxtagFiles = libertyTagsDir.listFiles(filter);
        if (fxtagFiles != null && fxtagFiles.length > 0) {
            return fxtagFiles[0];
        }
        return null;
    }

    private static Document toDOM(File file) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setCoalescing(true);
            dbf.setExpandEntityReferences(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            // Register an error handler to prevent XML parsers such as Xerces
            // from writing error messages directly to System.err.
            db.setErrorHandler(new ErrorHandler() {
                public void warning(SAXParseException exception) throws SAXException {}

                public void fatalError(SAXParseException exception) throws SAXException {}

                public void error(SAXParseException exception) throws SAXException {}
            });
            return db.parse(file);
        } catch (ParserConfigurationException pce) {
            throw new IOException(pce);
        } catch (SAXException spe) {
            throw new IOException(spe);
        }
    }

    private static void toFile(Document doc, File file) throws IOException {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(doc), new StreamResult(file));
        } catch (TransformerConfigurationException tce) {
            throw new IOException(tce);
        } catch (TransformerException te) {
            throw new IOException(te);
        }
    }

    public static final void cleanDirectory(File dir, ChangeHistory history) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (!file.isDirectory()) {
                    if (!file.canWrite() && !file.setWritable(true)) {
                        throw new IOException("java.io.File.canWrite()");
                    }
                }
            }
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (!file.isDirectory()) {
                    if (!file.canRead()) {
                        throw new IOException("java.io.File.canRead()");
                    }
                    // back up file in memory.
                    byte[] bytes = writeFileToMemory(file);
                    if (!file.delete()) {
                        throw new IOException("java.io.File.delete()");
                    }
                    // write deleted file to change history.
                    history.deletedFile(file.getAbsolutePath(), bytes);
                }
            }
        }
    }

    public static byte[] writeFileToMemory(File file) throws IOException {
        byte[] buffer = new byte[2048];
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            int len;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos.toByteArray();
        } finally {
            SelfExtractUtils.tryToClose(fis);
            SelfExtractUtils.tryToClose(baos);
        }
    }

    public void writeLicenseFiles(LicenseArchive archive, File toDir, ChangeHistory history, ExtractProgress ep) throws IOException {
        writeArchiveFiles(archive.getLicenseFiles(), toDir, history, ep);
    }

    public void writeTagFiles(LicenseArchive archive, File toDir, ChangeHistory history, ExtractProgress ep, boolean is855Tags) throws IOException {
        writeArchiveFiles(archive.getTagFiles(is855Tags), toDir, history, ep);
    }

    private void writeArchiveFiles(Iterator files, File toDir, ChangeHistory history, ExtractProgress ep) throws IOException {
        while (files.hasNext()) {
            LicenseArchive.Entry fileEntry = (LicenseArchive.Entry) files.next();
            File toFile = new File(toDir, fileEntry.getFileName());

            byte[] buffer = new byte[2048];
            InputStream is = null;
            OutputStream os = null;

            try {
                is = fileEntry.getInputStream();
                os = new FileOutputStream(toFile);
                int len;
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            } catch (IOException ioe) {
                // Record in change history if the file was actually created.
                if (toFile.exists()) {
                    history.createdFile(toFile.getAbsolutePath());
                }
                throw ioe;
            } finally {
                SelfExtractUtils.tryToClose(is);
                SelfExtractUtils.tryToClose(os);
            }
            history.createdFile(toFile.getAbsolutePath());
            ep.extractedFile(fileEntry.getPath());
        }
    }

    public ReturnCode validate(File outputDir) {
        ReturnCode result = validateProductMatches(outputDir, productMatches, false);
        if (result.getCode() == 0) {
            // Verify that the following files and directories exist.
            File libertyLicenseDir = new File(outputDir, LA_FILES_PREFIX);
            if (!libertyLicenseDir.exists()) {
                return new ReturnCode(ReturnCode.BAD_OUTPUT, "LICENSE_invalidInstall", libertyLicenseDir.getAbsolutePath());
            }
            File libertyTagsDir = new File(outputDir, TAG_FILES_PREFIX);
            if (!libertyTagsDir.exists()) {
                return new ReturnCode(ReturnCode.BAD_OUTPUT, "LICENSE_invalidInstall", libertyTagsDir.getAbsolutePath());
            }
            File libertyPropsFile = new File(outputDir, WAS_PROPERTIES_FILE);
            if (!libertyPropsFile.exists()) {
                return new ReturnCode(ReturnCode.BAD_OUTPUT, "LICENSE_invalidInstall", libertyPropsFile.getAbsolutePath());
            }
            try {
                // Verify that each feature with an Applies-To: applies to the new product edition
                return validateInstalledFeatures(outputDir, licenseArchive.getWASProperties());
            } catch (SelfExtractorFileException sefe) {
                return new ReturnCode(ReturnCode.NOT_FOUND, "fileProcessingException", new Object[] { sefe.getFileName(), sefe.getCause() });
            }
        }
        return result;
    }

    public static String getFeatureSymbolicName(Attributes attrs) {
        String manifestSymbolicName = attrs.getValue("Subsystem-SymbolicName");
        int i = manifestSymbolicName.indexOf(";");
        if (i >= 0)
            manifestSymbolicName = manifestSymbolicName.substring(0, i);
        return manifestSymbolicName.trim();
    }

    private static ReturnCode validateAddonsFeatures(File featuresDir, WASProperties props, List invalidAddonsFeatures) throws SelfExtractorFileException {

        // Create fileFilter to get just the manifest files.
        FilenameFilter manifestFilter = createManifestFilter();
        if (featuresDir.exists()) {
            File[] manifestFiles = featuresDir.listFiles(manifestFilter);
            if (manifestFiles != null) {
                String invalidFeatures = "";
                for (int i = 0; i < manifestFiles.length; ++i) {
                    FileInputStream fis = null;
                    File currentManifestFile = null;
                    try {
                        currentManifestFile = manifestFiles[i];
                        fis = new FileInputStream(currentManifestFile);
                        Manifest currentManifest = new Manifest(fis);

                        Attributes attrs = currentManifest.getMainAttributes();
                        String appliesTo = attrs.getValue("IBM-AppliesTo");
                        if (appliesTo == null || appliesTo.length() == 0) {
                            continue;
                        }
                        List productMatches = parseAppliesTo(appliesTo);
                        if (productMatches.isEmpty()) {
                            continue;
                        }
                        Iterator matches = productMatches.iterator();
                        while (matches.hasNext()) {
                            ProductMatch match = (ProductMatch) matches.next();
                            int result = props.matches(match);
                            if (result == ProductMatch.NOT_APPLICABLE) {
                                continue;
                            } else if (result == ProductMatch.INVALID_EDITION) {

                                List longIDs = new ArrayList();
                                Iterator matchesItr = match.getEditions().iterator();

                                while (matchesItr.hasNext()) {
                                    String shortID = (String) matchesItr.next();
                                    String editionName = InstallUtils.getEditionName(shortID);
                                    longIDs.add(editionName);
                                }

                                invalidFeatures += (invalidFeatures.length() == 0 ? "" : " ") + getFeatureSymbolicName(attrs);
                            } else if (result == ProductMatch.INVALID_INSTALL_TYPE) {
                                return new ReturnCode(ReturnCode.BAD_OUTPUT, "LICENSE_replace_invalidInstallType", new Object[] { props.getProductInstallType(),
                                                                                                                                  match.getInstallType() });
                            } else if (result == ProductMatch.INVALID_LICENSE) {
                                return new ReturnCode(ReturnCode.BAD_OUTPUT, "LICENSE_invalidLicense", new Object[] { props.getProductLicenseType(),
                                                                                                                      match.getLicenseType() });
                            }
                            break;
                        }
                    } catch (FileNotFoundException fnfe) {
                        throw new SelfExtractorFileException(currentManifestFile.getAbsolutePath(), fnfe);
                    } catch (IOException ioe) {
                        throw new SelfExtractorFileException(currentManifestFile.getAbsolutePath(), ioe);
                    } finally {
                        SelfExtractUtils.tryToClose(fis);
                    }
                }
                if (invalidFeatures.length() > 0) {
                    invalidAddonsFeatures.add(invalidFeatures);
                }
            }
        }
        return ReturnCode.OK;
    }

    private static int versionCompare(String version1, String version2) {
        String[] ver1 = version1.split("\\.");
        String[] ver2 = version2.split("\\.");
        int i = 0;
        while (i < ver1.length && i < ver2.length && ver1[i].equals(ver2[i])) {
            i++;
        }
        if (i < ver1.length && i < ver2.length) {
            int diff = Integer.valueOf(ver1[i]).compareTo(Integer.valueOf(ver2[i]));
            return Integer.signum(diff);
        }
        return Integer.signum(ver1.length - ver2.length);
    }

    private static ReturnCode validateInstalledFeatures(File outputDir, WASProperties props) throws SelfExtractorFileException {

        String licenseEdition = props.getProductEdition();
        File libertyPropsFile = new File(outputDir, WAS_PROPERTIES_FILE);
        Properties libertyProps = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(libertyPropsFile);
            libertyProps.load(is);
        } catch (IOException ioe) {
            return new ReturnCode(ReturnCode.BAD_OUTPUT, "extractFileError", ioe.getMessage());
        } finally {
            SelfExtractUtils.tryToClose(is);
        }
        String wlpProductEdition = libertyProps.getProperty("com.ibm.websphere.productEdition");
        String wlpProductVersion = libertyProps.getProperty("com.ibm.websphere.productVersion");

        // Liberty v8.5.5.7 and lower do not support IBM-AppliesTo feature attribute.
        // Therefore, installer can not validate installed features for license downgrade.
        // Stop installation and issue error message.
        if (versionCompare(wlpProductVersion, DOWNGRADESupportedVersion) < 0) {
            for (int i = 0; i < DOWNGRADE_EDITION_MATCH.length; i++) {
                if (DOWNGRADE_EDITION_MATCH[i][0].equalsIgnoreCase(licenseEdition)) {
                    for (int j = 1; j < DOWNGRADE_EDITION_MATCH[i].length; j++) {
                        if (DOWNGRADE_EDITION_MATCH[i][j].equalsIgnoreCase(wlpProductEdition)) {
                            return new ReturnCode(ReturnCode.BAD_OUTPUT, "LICENSE_downgrade_block_8557_low", new Object[] { DOWNGRADESupportedVersion,
                                                                                                                            wlpProductVersion
                            });
                        }
                    }
                }
            }
        }

        File featuresDir = new File(outputDir, FEATURES_PREFIX);
        List invalidFeatures = new ArrayList();
        ReturnCode rc = validateAddonsFeatures(featuresDir, props, invalidFeatures);
        if (rc != ReturnCode.OK)
            return rc;

        if (invalidFeatures.size() > 0) {

            return new ReturnCode(ReturnCode.BAD_OUTPUT, "LICENSE_replace_invalidEditonFeatures", new Object[] { InstallUtils.getEditionName(props.getProductEdition()),
                                                                                                                 invalidFeatures.get(0),
                                                                                                                 InstallUtils.getEditionName(wlpProductEdition),
                                                                                                                 "\"bin" + System.getProperty("file.separator") + "installUtility uninstall "
                                                                                                                                                                 + invalidFeatures.get(0)
                                                                                                                                                                 + "\"" });
        }

        //Do not check addons in upgrade scenario
        for (int i = 0; i < UPGRADE_EDITION_MATCH.length; i++) {
            if (UPGRADE_EDITION_MATCH[i][0].equalsIgnoreCase(licenseEdition)) {
                for (int j = 1; j < UPGRADE_EDITION_MATCH[i].length; j++) {
                    if (UPGRADE_EDITION_MATCH[i][j].equalsIgnoreCase(wlpProductEdition)) {
                        return ReturnCode.OK;
                    }
                }
            }
        }

        File addonsDir = new File(outputDir, ASSETS_PREFIX);
        List invalidAddons = new ArrayList();
        rc = validateAddonsFeatures(addonsDir, props, invalidAddons);
        if (rc != ReturnCode.OK)
            return rc;

        if (invalidFeatures.size() > 0) {

            return new ReturnCode(ReturnCode.BAD_OUTPUT, "LICENSE_replace_invalidEditonFeatures", new Object[] { InstallUtils.getEditionName(props.getProductEdition()),
                                                                                                                 invalidAddons.get(0),
                                                                                                                 InstallUtils.getEditionName(wlpProductEdition),
                                                                                                                 "bin/installUtility install " + invalidAddons.get(0) });
        }
        return ReturnCode.OK;
    }

    public final class LicenseArchive {

        public final class Entry {
            private final JarEntry jarEntry;

            public Entry(JarEntry jarEntry) {
                this.jarEntry = jarEntry;
            }

            public InputStream getInputStream() throws IOException {
                return jarFile.getInputStream(jarEntry);
            }

            public String getPath() {
                return jarEntry.getName();
            }

            public String getFileName() {
                String name = getPath();
                int lastSlash = name.lastIndexOf('/');
                if (lastSlash != -1) {
                    name = name.substring(lastSlash + 1);
                }
                return name;
            }
        }

        private static final String WLP_ROOT = "wlp/";
        private static final String WLP_LA_FILES_PREFIX = WLP_ROOT + LA_FILES_PREFIX;
        private static final String WLP_TAG_FILES_PREFIX = WLP_ROOT + TAG_FILES_PREFIX;
        private static final String WLP_WAS_PROPERTIES_FILE = WLP_ROOT + WAS_PROPERTIES_FILE;
        private static final String WLP_ISO_TAG_SUFFIX = ".swidtag";

        private final List laFiles = new ArrayList(); // List<Entry>
        private final List isoTagFiles = new ArrayList(); // List<Entry>
        private final List tagFiles = new ArrayList(); // List<Entry>
        private final WASProperties properties;

        public LicenseArchive() throws IOException {
            Enumeration jarEntries = jarFile.entries();
            final Properties props = new Properties();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
                String name = jarEntry.getName();
                if (name.startsWith(WLP_LA_FILES_PREFIX)) {
                    if (!jarEntry.isDirectory() && name.substring(WLP_LA_FILES_PREFIX.length()).indexOf('/') == -1) {
                        laFiles.add(new Entry(jarEntry));
                    }
                } else if (name.startsWith(WLP_TAG_FILES_PREFIX)) {
                    if (!jarEntry.isDirectory() && name.substring(WLP_TAG_FILES_PREFIX.length()).indexOf('/') == -1) {
                        if (name.endsWith(WLP_ISO_TAG_SUFFIX))
                            isoTagFiles.add(new Entry(jarEntry));
                        else
                            tagFiles.add(new Entry(jarEntry));
                    }
                } else if (name.equals(WLP_WAS_PROPERTIES_FILE)) {
                    props.load(jarFile.getInputStream(jarEntry));
                }
            }
            properties = new WASProperties(props);
            // Remove the fixpack number from the version loaded from the
            // properties file so that we match installed features against
            // the base version.
            String version = properties.getProductVersion();
            Matcher versionMatcher = validNumericVersion.matcher(version);
            if (versionMatcher.matches()) {
                int index = version.lastIndexOf('.');
                if (index != -1) {
                    properties.props.setProperty("com.ibm.websphere.productVersion", version.substring(0, index));
                }
            }
        }

        // Returns Iterator<Entry>
        public Iterator getLicenseFiles() {
            return laFiles.iterator();
        }

        // Returns Iterator<Entry>
        public Iterator getTagFiles(boolean is855Tags) {
            if (is855Tags)
                return tagFiles.iterator();
            return isoTagFiles.iterator();
        }

        public WASProperties getWASProperties() {
            return properties;
        }
    }

    public static final class WASProperties {

        private final Properties props;
        private final File outputFile;
        private final ChangeHistory history;
        private final Map changedProps;

        public WASProperties(Properties props) {
            this(props, null, null);
        }

        public WASProperties(Properties props, File outputFile, ChangeHistory history) {
            this.props = props;
            this.outputFile = outputFile;
            this.history = history;
            this.changedProps = (outputFile != null) ? new HashMap() : null;
        }

        public String getProductVersion() {
            return props.getProperty("com.ibm.websphere.productVersion");
        }

        public void setProductVersion(String version) {
            if (outputFile == null) {
                throw new IllegalStateException("Read only");
            }
            setProperty("com.ibm.websphere.productVersion", version);
        }

        public String getProductInstallType() {
            return props.getProperty("com.ibm.websphere.productInstallType");
        }

        public void setProductInstallType(String installType) {
            if (outputFile == null) {
                throw new IllegalStateException("Read only");
            }
            setProperty("com.ibm.websphere.productInstallType", installType);
        }

        public String getProductEdition() {
            return props.getProperty("com.ibm.websphere.productEdition");
        }

        public void setProductEdition(String productEdition) {
            if (outputFile == null) {
                throw new IllegalStateException("Read only");
            }
            setProperty("com.ibm.websphere.productEdition", productEdition);
        }

        public String getProductLicenseType() {
            return props.getProperty("com.ibm.websphere.productLicenseType");
        }

        public void setProductLicenseType(String productLicenseType) {
            if (outputFile == null) {
                throw new IllegalStateException("Read only");
            }
            setProperty("com.ibm.websphere.productLicenseType", productLicenseType);
        }

        private void setProperty(String property, String value) {
            // Back up old values so that they can be restored if the license install fails.
            String[] oldNewValuePair = (String[]) changedProps.get(property);
            if (oldNewValuePair == null) {
                oldNewValuePair = new String[2];
                oldNewValuePair[0] = props.getProperty(property);
                changedProps.put(property, oldNewValuePair);
            }
            props.setProperty(property, value);
            oldNewValuePair[1] = value;
        }

        public int matches(ProductMatch match) {
            return match.matches(props);
        }

        public void store() throws IOException {
            if (outputFile == null) {
                throw new IllegalStateException("Read only");
            }
            OutputStream os = null;
            try {
                os = new FileOutputStream(outputFile);
                props.store(os, null);
                history.changedProps(outputFile.getAbsolutePath(), new HashMap(changedProps));
                changedProps.clear();
            } finally {
                SelfExtractUtils.tryToClose(os);
            }
        }
    }
}
