package com.ibm.ws.featureverifier.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.aries.util.VersionRange;
import org.apache.aries.util.manifest.ManifestHeaderProcessor;
import org.apache.aries.util.manifest.ManifestHeaderProcessor.NameValuePair;
import org.apache.aries.util.manifest.ManifestProcessor;
import org.osgi.framework.Version;

/**
 * Kept as a single file to make it easier to share..
 *
 * If we decide to check this thing in, then we could
 * split out the various subclasses etc.
 *
 * "Much that once tWAS is lost, for none now remain who remember it."
 */
public class Report {

    public final static String fileSep = Pattern.quote(File.separator);
    private File baseDir;
    private File newBuildDir;
    private File outDir;
    private final static boolean useCache = false;

    // Helper method for get the file content
    static List<String> fileToLines(File filename) {
        List<String> lines = new LinkedList<String>();
        String line = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }

    private static class ManifestDiff {
        static enum HeaderType {
            SYMBOLICNAME, IMPORT, STRING, IGNORE
        };

        static final Map<String, HeaderType> headerTypes = new HashMap<String, HeaderType>();
        static {
            headerTypes.put("Subsystem-SymbolicName", HeaderType.SYMBOLICNAME);
            headerTypes.put("IBM-AppliesTo", HeaderType.SYMBOLICNAME);
            headerTypes.put("IBM-ShortName", HeaderType.STRING);
            headerTypes.put("IBM-Feature-Version", HeaderType.STRING);
            headerTypes.put("IBM-License-Agreement", HeaderType.STRING);
            // TEMP, can unignore once things have settled down
            headerTypes.put("IBM-License-Information", HeaderType.IGNORE);
            // TEMP, ignore
            headerTypes.put("IBM-ProductID", HeaderType.IGNORE);
            headerTypes.put("Subsystem-Description", HeaderType.STRING);
            headerTypes.put("Subsystem-License", HeaderType.STRING);
            headerTypes.put("Subsystem-Localization", HeaderType.STRING);
            headerTypes.put("Subsystem-ManifestVersion", HeaderType.STRING);
            headerTypes.put("Subsystem-Name", HeaderType.STRING);
            headerTypes.put("Subsystem-Type", HeaderType.STRING);
            headerTypes.put("Subsystem-Vendor", HeaderType.STRING);
            headerTypes.put("Subsystem-Version", HeaderType.STRING);
            headerTypes.put("IBM-API-Package", HeaderType.IMPORT);
            headerTypes.put("IBM-SPI-Package", HeaderType.IMPORT);
            headerTypes.put("IBM-Provision-Capability", HeaderType.IMPORT);
            headerTypes.put("Subsystem-Content", HeaderType.IMPORT);
            headerTypes.put("Bnd-LastModified", HeaderType.IGNORE);
            headerTypes.put("Tool", HeaderType.IGNORE);
        }

        final Map<String, String> deletedHeaders = new TreeMap<String, String>();
        final Map<String, String> addedHeaders = new TreeMap<String, String>();

        enum Why {
            ADDED, REMOVED, CHANGED
        };

        public static class Change implements Comparable<Change> {
            String oldValue;
            String newValue;

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result
                         + ((newValue == null) ? 0 : newValue.hashCode());
                result = prime * result
                         + ((oldValue == null) ? 0 : oldValue.hashCode());
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                Change other = (Change) obj;
                if (newValue == null) {
                    if (other.newValue != null)
                        return false;
                } else if (!newValue.equals(other.newValue))
                    return false;
                if (oldValue == null) {
                    if (other.oldValue != null)
                        return false;
                } else if (!oldValue.equals(other.oldValue))
                    return false;
                return true;
            }

            @Override
            public int compareTo(Change o) {
                if (o == null) {
                    return -1;
                } else if (oldValue.equals(o.oldValue)) {
                    return newValue.compareTo(o.newValue);
                } else {
                    return oldValue.compareTo(o.oldValue);
                }
            }
        }

        final Map<String, Map<Why, Set<Change>>> changedHeaders = new TreeMap<String, Map<Why, Set<Change>>>();

        public boolean hasChanges() {
            return !deletedHeaders.isEmpty() | !addedHeaders.isEmpty() | !changedHeaders.isEmpty();
        }

        private void compareAttributes(Attributes oldAttribs, Attributes newAttribs) {

            if ((newAttribs.getValue("Created-By") != null) &&
                (oldAttribs.getValue("Created-By") == null)) {
                System.out.println("BND style feature conversion, temporarily ignoring this.");
                return;
            }
            //new headers..
            for (Entry<Object, Object> o : newAttribs.entrySet()) {
                String header = o.getKey().toString();
                if (!headerTypes.containsKey(header) || headerTypes.get(header) != HeaderType.IGNORE) {
                    String value = o.getValue().toString();
                    if (oldAttribs.getValue(header) == null) {
                        addedHeaders.put(header, value);
                    }
                }
            }

            //removed headers..
            for (Entry<Object, Object> o : oldAttribs.entrySet()) {
                String header = o.getKey().toString();
                if (!headerTypes.containsKey(header) || headerTypes.get(header) != HeaderType.IGNORE) {
                    String value = o.getValue().toString();
                    if (newAttribs.getValue(header) == null) {
                        deletedHeaders.put(header, value);
                    }
                }
            }

            //changed headers..
            for (Entry<Object, Object> o : oldAttribs.entrySet()) {
                String header = o.getKey().toString();
                String value = o.getValue().toString();
                if (newAttribs.getValue(header) != null) {
                    String nValue = newAttribs.getValue(header);
                    HeaderType h = headerTypes.containsKey(header) ? headerTypes.get(header) : HeaderType.STRING;
                    switch (h) {
                        case IGNORE: {
                            break;
                        }
                        case STRING: {
                            if (!nValue.equals(value)) {
                                if (!changedHeaders.containsKey(header)) {
                                    changedHeaders.put(header, new HashMap<Why, Set<Change>>());
                                }
                                if (!changedHeaders.get(header).containsKey(Why.CHANGED)) {
                                    changedHeaders.get(header).put(Why.CHANGED, new TreeSet<Change>());
                                }
                                Change c = new Change();
                                c.oldValue = value;
                                c.newValue = nValue;
                                changedHeaders.get(header).get(Why.CHANGED).add(c);
                            }
                            break;
                        }
                        case SYMBOLICNAME: {
                            NameValuePair oldName = ManifestHeaderProcessor.parseBundleSymbolicName(value);
                            NameValuePair newName = ManifestHeaderProcessor.parseBundleSymbolicName(nValue);
                            if (oldName.getAttributes() != null && newName.getAttributes() != null) {
                                // Ignore the productVersion attribute so we don't get version change spam for every release
                                oldName.getAttributes().remove("productVersion");
                                newName.getAttributes().remove("productVersion");

                                //TEMP, also ignore product edition
                                oldName.getAttributes().remove("productEdition");
                                newName.getAttributes().remove("productEdition");

                                // Also temporary, tons of spam from license change
                                oldName.getAttributes().remove("http://www.ibm.com/licenses/wlp-featureterms-restricted-v1");
                                newName.getAttributes().remove("http://www.ibm.com/licenses/wlp-featureterms-v1");
                            }
                            if (!oldName.getName().equals(newName.getName()) | (oldName.getAttributes() != null && !oldName.getAttributes().equals(newName.getAttributes()))) {
                                if (!changedHeaders.containsKey(header)) {
                                    changedHeaders.put(header, new HashMap<Why, Set<Change>>());
                                }
                                if (!changedHeaders.get(header).containsKey(Why.CHANGED)) {
                                    changedHeaders.get(header).put(Why.CHANGED, new TreeSet<Change>());
                                }
                                Change c = new Change();
                                c.oldValue = value;
                                c.newValue = nValue;
                                changedHeaders.get(header).get(Why.CHANGED).add(c);
                            }
                            break;
                        }
                        case IMPORT: {
                            Map<String, Map<String, String>> oldValue = ManifestHeaderProcessor.parseImportString(value);
                            Map<String, Map<String, String>> newValue = ManifestHeaderProcessor.parseImportString(nValue);

                            Set<String> keysToCompareContent = newValue.keySet();

                            if (!oldValue.keySet().equals(newValue.keySet())) {

                                if (!changedHeaders.containsKey(header)) {
                                    changedHeaders.put(header, new HashMap<Why, Set<Change>>());
                                }
                                if (!changedHeaders.get(header).containsKey(Why.CHANGED)) {
                                    changedHeaders.get(header).put(Why.CHANGED, new TreeSet<Change>());
                                }

                                Set<String> overlap = new HashSet<String>(oldValue.keySet());
                                overlap.retainAll(newValue.keySet());

                                keysToCompareContent = overlap;

                                Set<String> removed = new TreeSet<String>(oldValue.keySet());
                                removed.removeAll(overlap);

                                Set<String> added = new TreeSet<String>(newValue.keySet());
                                added.removeAll(overlap);

                                if (!removed.isEmpty()) {
                                    if (!changedHeaders.get(header).containsKey(Why.REMOVED)) {
                                        changedHeaders.get(header).put(Why.REMOVED, new TreeSet<Change>());
                                    }
                                    for (String rem : removed) {
                                        //rebuild the entry..
                                        Map<String, String> params = oldValue.get(rem);
                                        String rebuilt = rem;
                                        if (params != null) {
                                            for (Map.Entry<String, String> e : params.entrySet()) {
                                                rebuilt += ";" + e.getKey() + "=\"" + e.getValue() + "\"";
                                            }
                                        }
                                        Change c1 = new Change();
                                        c1.oldValue = rebuilt;
                                        c1.newValue = "";
                                        changedHeaders.get(header).get(Why.REMOVED).add(c1);
                                    }
                                }
                                if (!added.isEmpty()) {
                                    if (!changedHeaders.get(header).containsKey(Why.ADDED)) {
                                        changedHeaders.get(header).put(Why.ADDED, new TreeSet<Change>());
                                    }
                                    for (String add : added) {
                                        //rebuild the entry..
                                        Map<String, String> params = newValue.get(add);
                                        String rebuilt = add;
                                        if (params != null) {
                                            for (Map.Entry<String, String> e : params.entrySet()) {
                                                rebuilt += ";" + e.getKey() + "=\"" + e.getValue() + "\"";
                                            }
                                        }
                                        Change c1 = new Change();
                                        c1.oldValue = "";
                                        c1.newValue = rebuilt;
                                        changedHeaders.get(header).get(Why.ADDED).add(c1);
                                    }
                                }
                            }

                            //now we process the overlapping keys..

                            for (String key : keysToCompareContent) {
                                Map<String, String> oldEntrySet = oldValue.get(key);
                                Map<String, String> newEntrySet = newValue.get(key);
                                if ((oldEntrySet == null && newEntrySet != null) |
                                    (oldEntrySet != null && !oldEntrySet.equals(newEntrySet))) {

                                    if (!changedHeaders.containsKey(header)) {
                                        changedHeaders.put(header, new HashMap<Why, Set<Change>>());
                                    }
                                    if (!changedHeaders.get(header).containsKey(Why.CHANGED)) {
                                        changedHeaders.get(header).put(Why.CHANGED, new TreeSet<Change>());
                                    }

                                    //rebuild the entry..
                                    Map<String, String> oldparams = oldValue.get(key);
                                    String oldRebuilt = key;
                                    if (oldparams != null) {
                                        for (Map.Entry<String, String> e : oldparams.entrySet()) {
                                            oldRebuilt += ";" + e.getKey() + "=\"" + e.getValue() + "\"";
                                        }
                                    }
                                    Map<String, String> newparams = newValue.get(key);
                                    String newRebuilt = key;
                                    if (newRebuilt != null) {
                                        for (Map.Entry<String, String> e : newparams.entrySet()) {
                                            newRebuilt += ";" + e.getKey() + "=\"" + e.getValue() + "\"";
                                        }
                                    }

                                    Change c = new Change();
                                    c.oldValue = oldRebuilt;
                                    c.newValue = newRebuilt;
                                    changedHeaders.get(header).get(Why.CHANGED).add(c);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }

        public ManifestDiff(File oldDir, String oldSourcePath, File newDir, String newSourcePath) {
            File oldM = new File(oldDir, oldSourcePath);
            File newM = new File(newDir, newSourcePath);
            if (newM.getName().startsWith("com.ibm.websphere.appserver") && !oldM.getName().startsWith("com.ibm.websphere.appserver")) {
                System.out.println("BND conversion from " + oldM.getName() + " to " + newM.getName() + " - Ignoring");
                return;
            }
            if (oldM.exists() && oldM.isFile() && newM.exists() && newM.isFile()) {
                FileInputStream oldFis = null;
                FileInputStream newFis = null;
                try {
                    oldFis = new FileInputStream(oldM);
                    newFis = new FileInputStream(newM);
                    try {
                        Manifest o = ManifestProcessor.parseManifest(oldFis);
                        Manifest n = ManifestProcessor.parseManifest(newFis);

                        Attributes oma = o.getMainAttributes();
                        Attributes nma = n.getMainAttributes();

                        compareAttributes(oma, nma);

                        for (Entry<String, Attributes> x : o.getEntries().entrySet()) {
                            //System.out.println("Comparing "+String.valueOf(x.getKey())+" ");
                            Attributes oa = x.getValue();
                            Attributes na = n.getAttributes(x.getKey());

                            compareAttributes(oa, na);
                        }

                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                } catch (FileNotFoundException f) {
                    f.printStackTrace();
                } finally {
                    try {
                        oldFis.close();
                        newFis.close();
                    } catch (IOException io) {
                    }
                }
            } else {
                System.out.println("Error performing tricorder scan of " + oldSourcePath + " with " + newSourcePath + " Paths were not found.");
            }
        }
    }

    private static class VersionedBundle implements Comparable<VersionedBundle> {
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                     + ((symbolicName == null) ? 0 : symbolicName.hashCode());
            result = prime * result
                     + ((vrString == null) ? 0 : vrString.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            VersionedBundle other = (VersionedBundle) obj;
            if (symbolicName == null) {
                if (other.symbolicName != null)
                    return false;
            } else if (!symbolicName.equals(other.symbolicName))
                return false;
            if (vrString == null) {
                if (other.vrString != null)
                    return false;
            } else if (!vrString.equals(other.vrString))
                return false;
            return true;
        }

        final String vrString;
        final VersionRange vr;
        final String symbolicName;

        public VersionedBundle(String symbolicName, String versionRange) {
            vrString = versionRange;
            vr = new VersionRange(versionRange);
            this.symbolicName = symbolicName;
        }

        @Override
        public int compareTo(VersionedBundle o) {
            if (o == null)
                return -1;

            if (!symbolicName.equals(o.symbolicName)) {
                return vrString.compareTo(o.vrString);
            } else {
                return symbolicName.compareTo(symbolicName);
            }
        }
    }

    private static class InvalidFeatureException extends Exception {

        /**  */
        private static final long serialVersionUID = -1586275845855237346L;
    }

    private static class FeatureFile {
        private static final String FEATURE_KIND = "kind";
        private static final String BETA_FEATURE = "beta";

        private boolean isBeta = false;

        public FeatureFile(File baseDir, String fileName) {
            File f = new File(baseDir, fileName);
            if (!f.exists()) {
                System.out.println("Could not find feature file " + f.getAbsolutePath());
                isBeta = false;
                return;
            }

            BufferedReader reader = null;
            try {
                InputStream is = new FileInputStream(f);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(FEATURE_KIND)) {
                        String value = line.substring(FEATURE_KIND.length() + 1);
                        value = value.trim();
                        if (value.equalsIgnoreCase(BETA_FEATURE)) {
                            isBeta = true;
                            return;
                        }
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                isBeta = false;
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

        boolean isBeta() {
            return isBeta;
        }
    }

    private static class FeatureInfo implements Comparable<FeatureInfo> {

        final File baseDir;
        final String featureSourceData;
        final String symbolicName;
        final boolean singleton;
        final String visibility;
        final boolean bndBased;
        final Set<String> metatypes;
        final Set<VersionedBundle> bundles;

        private NameValuePair getDetailsFromManifest(File manifest, Set<VersionedBundle> bundles, Map<String, Set<String>> features) {
            NameValuePair name = null;
            try {
                InputStream is = new FileInputStream(manifest);
                try {
                    Manifest m = ManifestProcessor.parseManifest(is);
                    String nameStr = m.getMainAttributes().getValue("Subsystem-SymbolicName");
                    name = ManifestHeaderProcessor.parseBundleSymbolicName(nameStr);

                    String contentString = m.getMainAttributes().getValue("Subsystem-Content");
                    Map<String, Map<String, String>> content = ManifestHeaderProcessor.parseImportString(contentString);
                    for (Entry<String, Map<String, String>> contentItem : content.entrySet()) {
                        if (!contentItem.getValue().containsKey("type") || contentItem.getValue().get("type").equals("osgi.bundle")) {
                            String version = contentItem.getValue().get("version");
                            if (version == null || version.trim().equals("")) {
                                version = "0.0.0";
                            }
                            VersionedBundle vr = new VersionedBundle(contentItem.getKey(), version);
                            bundles.add(vr);
                        } else if (contentItem.getValue().get("type").equals("osgi.subsystem.feature")) {
                            String tolerates = contentItem.getValue().get("ibm.tolerates:");
                            String preferred = contentItem.getKey();
                            Set<String> tolerated = new TreeSet<String>();
                            if (tolerates != null) {
                                String parts[] = tolerates.split(",");
                                String base = preferred.substring(0, preferred.lastIndexOf('-'));
                                for (String part : parts) {
                                    tolerated.add(base + "-" + part);
                                }
                            }
                            features.put(preferred, Collections.unmodifiableSet(tolerated));
                        }
                    }
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            } catch (IOException io) {
                System.err.println("Manifest " + manifest.getAbsolutePath());
                io.printStackTrace();
            }
            return name;
        }

        public boolean matchesBundle(String name, Version v) {
            for (VersionedBundle vb : bundles) {
                if (vb.symbolicName.equals(name)) {
                    if (vb.vr.matches(v)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public FeatureInfo(File baseDir, String source, Set<String> metatypes) throws InvalidFeatureException {
            this.baseDir = baseDir;
            Set<VersionedBundle> bundles = new TreeSet<VersionedBundle>();
            Map<String, Set<String>> features = new TreeMap<String, Set<String>>();
            String src = null;
            if (source.endsWith(".feature")) {
                String parts[] = source.split(Report.fileSep);
                File project = new File(baseDir, parts[0]);
                File build = new File(project, "build");
                File manifest = new File(build, "subsystem.mf");
                if (manifest.exists() && manifest.isFile()) {
                    bndBased = true;
                    src = parts[0] + File.separator + "build" + File.separator + "subsystem.mf";
                    NameValuePair parsedName = getDetailsFromManifest(manifest, bundles, features);
                    symbolicName = parsedName.getName();
                    String visibilityStr = null;
                    String singletonStr = null;
                    if (parsedName.getAttributes() != null) {
                        visibilityStr = parsedName.getAttributes().get("visibility:");
                        singletonStr = parsedName.getAttributes().get("singleton:");
                    }
                    visibility = visibilityStr == null ? "private" : visibilityStr;
                    singleton = singletonStr == null ? false : Boolean.valueOf(singletonStr);
                } else {
                    // manifest will not be produced for kind=beta. Check if that's the case here
                    FeatureFile feature = new FeatureFile(baseDir, source);
                    if (!feature.isBeta()) {
                        throw new InvalidFeatureException();
                    } else {
                        throw new InvalidFeatureException();
                    }
                }
            } else {
                src = source;
                bndBased = false;
                NameValuePair parsedName = getDetailsFromManifest(new File(baseDir, source), bundles, features);
                symbolicName = parsedName.getName();
                String visibilityStr = null;
                String singletonStr = null;
                if (parsedName.getAttributes() != null) {
                    visibilityStr = parsedName.getAttributes().get("visibility:");
                    singletonStr = parsedName.getAttributes().get("singleton:");
                }
                visibility = visibilityStr == null ? "private" : visibilityStr;
                singleton = singletonStr == null ? false : Boolean.valueOf(singletonStr);
            }

            if (symbolicName == null) {
                throw new IllegalStateException(source);
            }
            this.featureSourceData = src;
            this.metatypes = Collections.unmodifiableSet(metatypes);

            this.bundles = Collections.unmodifiableSet(bundles);

        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                     + ((symbolicName == null) ? 0 : symbolicName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FeatureInfo other = (FeatureInfo) obj;
            if (symbolicName == null) {
                if (other.symbolicName != null)
                    return false;
            } else if (!symbolicName.equals(other.symbolicName))
                return false;
            return true;
        }

        @Override
        public int compareTo(FeatureInfo o) {
            if (o == null)
                return -1;
            else
                return symbolicName.compareTo(o.symbolicName);
        }

    }

    private void usage(String args[]) {
        System.out.println("Report <baseline repo workspace dir> <new build repo workspace dir> <html output dir>");
        if (args.length > 0) {
            int arg = 0;
            for (String s : args) {
                System.out.println(" Actual Arg " + (arg++) + " : '" + s + "'");
            }
        }
        System.exit(-1);
    }

    private void unZip(File zip, File dest) throws IOException {
        System.out.println(" Probing binary starsystem of  " + zip.getAbsolutePath() + " from location " + dest.getAbsolutePath());
        dest.mkdirs();
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zip));
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            String name = ze.getName();
            File out = new File(dest, name);
            File parent = new File(out.getParent());
            parent.mkdirs();
            System.out.println(" Unzipping file to  " + out.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(out);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private void parseArgs(String args[]) throws IOException {
        if (args.length != 3) {
            usage(args);
        }

        String baseDirString = args[0];
        baseDir = new File(baseDirString);
        if (!baseDir.exists()) {
            System.out.println("Bad base dir argument '" + baseDir + "'");
            usage(args);
        }

        String newBuildDirString = args[1];
        newBuildDir = new File(newBuildDirString);
        if (!newBuildDir.exists() || !newBuildDir.isDirectory()) {
            System.out.println("Bad new build dir argument '" + newBuildDirString + "'");
            usage(args);
        }

        String outDirString = args[2];
        outDir = new File(outDirString);
        if (!outDir.exists() || !outDir.isDirectory()) {
            System.out.println("Bad output dir argument '" + outDirString + "'");
            usage(args);
        }

        if (!baseDir.isDirectory()) {
            //we now support loading the base from a zip file.. by extracting it ;p
            if (baseDir.getName().toLowerCase().endsWith(".zip")) {
                System.out.println("Using starbase in a box " + baseDir);
                File baseExtracted = new File(outDir, "Extracted");
                unZip(baseDir, baseExtracted);
                baseDir = baseExtracted;
                System.out.println("Using starbase " + baseDir);
            } else {
                System.out.println("Bad base dir argument '" + baseDir + "'");
                usage(args);
            }
        } else {
            System.out.println("Using starbase " + baseDir);
        }
    }

    private void findFiles(File base, File currentDir, Map<Pattern, Set<String>> results) {
        int count = 0;
        int max = 0;
        int percent = 0;
        if (base.equals(currentDir)) {
            max = count = currentDir.listFiles().length;
            if (useCache) {
                //see if we can reload from the cache..
                File cache = new File(base, ".fcache");
                if (cache.exists() && cache.isFile()) {
                    System.out.print("Restructuring dilithium crystals using " + currentDir.getPath() + " [");
                    Properties p = new Properties();
                    try {
                        FileInputStream fis = new FileInputStream(cache);
                        try {
                            p.load(fis);
                            max = count = p.size();
                            Map<Pattern, Set<String>> cacheResults = new HashMap<Pattern, Set<String>>();
                            int pkey = 0;
                            while (p.containsKey("Pattern-" + pkey)) {
                                String patternString = p.getProperty("Pattern-" + pkey);
                                String countString = p.getProperty("Pattern-" + pkey + ".count");
                                String prefixString = p.getProperty("Pattern-" + pkey + ".prefix");
                                Pattern pattern = null;
                                for (Pattern resultPattern : results.keySet()) {
                                    if (resultPattern.toString().equals(patternString)) {
                                        pattern = resultPattern;
                                        break;
                                    }
                                }
                                if (pattern == null) {
                                    System.out.println("Unknown pattern " + patternString);
                                    pattern = Pattern.compile(patternString);
                                }
                                Set<String> values = new TreeSet<String>();
                                int pmax = Integer.parseInt(countString);
                                for (int i = 0; i < pmax; i++) {
                                    values.add(p.getProperty(prefixString + "." + i));
                                    if (i == 0)
                                        count -= 4;
                                    else
                                        count--;
                                    int newPercent = (int) ((50.0 / max) * count);
                                    if (newPercent != percent) {
                                        System.out.print(".");
                                        System.out.flush();
                                        percent = newPercent;
                                    }
                                }
                                cacheResults.put(pattern, values);
                                pkey++;
                            }
                            //now all of property file is loaded without io error etc.. transfer results to real map & return
                            results.putAll(cacheResults);
                            System.out.println("]");
                            return;
                        } catch (IOException io) {
                        } finally {
                            if (fis != null) {
                                try {
                                    fis.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                    }
                    System.out.println("]");
                } else {
                    System.out.println("Warp Engines are offline.");
                }
            }
            //still here? then the cache either didn't exist.. or was bad.
            System.out.print("Engaging sector search using maximum sensor sweep at " + currentDir.getPath() + " [");
        }

        for (File f : currentDir.listFiles()) {
            if (count > 0) {
                count--;
                int newPercent = (int) ((50.0 / max) * count);
                if (newPercent != percent) {
                    System.out.print(".");
                    System.out.flush();
                    percent = newPercent;
                }
            }

            if (f.isDirectory()) {
                //skip .dirs =)
                if (!f.getName().startsWith(".")) {
                    findFiles(base, f, results);
                }
            } else {
                Path basePath = Paths.get(base.toURI());
                Path filePath = Paths.get(f.toURI());
                Path relativePath = basePath.relativize(filePath);
                String fPathAsString = relativePath.toString();

                if (f.getName().toLowerCase().endsWith(".mf")) {
                    System.out.println("base: " + basePath);
                    System.out.println("file: " + filePath);
                    System.out.println("relative: " + fPathAsString);
                    for (Entry<Pattern, Set<String>> e : results.entrySet()) {
                        System.out.println(e.getKey().matcher(fPathAsString).matches() + "  :  " + e.getKey().toString());
                    }
                }

                for (Entry<Pattern, Set<String>> e : results.entrySet()) {
                    if (e.getKey().matcher(fPathAsString).matches()) {
                        e.getValue().add(fPathAsString);
                    }
                }
            }
        }

        if (base.equals(currentDir)) {
            System.out.println("]");
            if (useCache) {
                System.out.print("Realigning main deflector dish [");
                //update the cache, because we didn't load from it.
                Properties p = new Properties();
                char prefix = 'a';
                int pcount = 0;
                count = max = results.size();
                for (Entry<Pattern, Set<String>> result : results.entrySet()) {
                    count--;
                    int newPercent = (int) ((50.0 / max) * count);
                    if (newPercent != percent) {
                        System.out.print(".");
                        System.out.flush();
                        percent = newPercent;
                    }
                    Pattern pattern = result.getKey();
                    Set<String> values = result.getValue();
                    p.setProperty("Pattern-" + pcount, pattern.toString());
                    p.setProperty("Pattern-" + pcount + ".count", "" + values.size());
                    p.setProperty("Pattern-" + pcount + ".prefix", "" + prefix);
                    int vcount = 0;
                    for (String value : values) {
                        p.setProperty(prefix + "." + vcount, value);
                        vcount++;
                    }
                    pcount++;
                    prefix++;
                }
                File cache = new File(base, ".fcache");
                try {
                    FileOutputStream fos = new FileOutputStream(cache);
                    try {
                        p.store(fos, "file cache for api/spi review");
                    } catch (IOException io) {
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException io) {
                        }
                    }
                } catch (FileNotFoundException e) {
                }
                System.out.println("]");
            }
        }
    }

    private void populateMaps(File baseDir, Set<String> featurePaths, Set<String> metatypePaths, Map<String, FeatureInfo> nameToFeatureMap,
                              Map<String, FeatureInfo> sourceToFeatureMap) {
        int count = featurePaths.size();;
        int max = count;
        int percent = 0;
        System.out.print("Populating feature maps [");
        //now all the info from the dir is in memory, it gets a bit quicker =)
        Set<FeatureInfo> allFeatures = new TreeSet<FeatureInfo>();
        Set<String> metatypePathsToProcess = new TreeSet<String>(metatypePaths);
        for (String featurePath : featurePaths) {

            count--;
            int newPercent = (int) ((50.0 / max) * count);
            if (newPercent != percent) {
                System.out.print(".");
                System.out.flush();
                percent = newPercent;
            }

            Set<String> metaTypesForFeature = new TreeSet<String>();
            //1st dir of featurePath is the project dir..
            String[] parts = featurePath.split(fileSep);
            Set<String> toRemove = new HashSet<String>();
            for (String metatypePath : metatypePathsToProcess) {
                String[] mparts = metatypePath.split(fileSep);
                if (parts[0].equals(mparts[0])) {
                    toRemove.add(metatypePath);
                    metaTypesForFeature.add(metatypePath);
                    //System.out.println(" recognized "+metatypePath+" as part of "+featurePath);
                }
            }
            metatypePathsToProcess.removeAll(toRemove);

            try {
                FeatureInfo fi = new FeatureInfo(baseDir, featurePath, metaTypesForFeature);
                allFeatures.add(fi);
            } catch (InvalidFeatureException ex) {
                // OK
            }
        }
        System.out.println("]");

        if (metatypePathsToProcess.size() > 0) {
            //these ones we need to match by their bundle owners.. tricksy.
            for (String path : metatypePathsToProcess) {
                String[] parts = path.split(fileSep);
                File project = new File(baseDir, parts[0]);
                File build = new File(project, "build");
                File lib = new File(build, "lib");
                if (lib.listFiles() != null) {
                    for (File f : lib.listFiles()) {
                        if (f.getName().endsWith(".jar")) {
                            try {
                                JarFile jf = new JarFile(f);
                                try {
                                    Manifest m = jf.getManifest();
                                    String name = m.getMainAttributes().getValue("Bundle-SymbolicName");
                                    String version = m.getMainAttributes().getValue("Bundle-Version");
                                    Version v = Version.parseVersion(version);
                                    Set<FeatureInfo> rebuilt = new HashSet<FeatureInfo>();
                                    for (FeatureInfo fi : allFeatures) {
                                        if (fi.matchesBundle(name, v)) {
                                            Set<String> newMetas = new TreeSet<String>(fi.metatypes);
                                            newMetas.add(path);
                                            try {
                                                rebuilt.add(new FeatureInfo(fi.baseDir, fi.featureSourceData, newMetas));
                                            } catch (InvalidFeatureException ex) {
                                                // OK
                                            }
                                            break;
                                        }
                                    }
                                    allFeatures.addAll(rebuilt);
                                } finally {
                                    if (jf != null) {
                                        jf.close();
                                    }
                                }
                            } catch (IOException io) {
                                io.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (new File(project, "build.xml").exists())
                        System.out.println("Unable to match a jar for " + path);
                }
            }
        }

        for (FeatureInfo fi : allFeatures) {
            if (!excludedFeatures.contains(fi.symbolicName)) {
                nameToFeatureMap.put(fi.symbolicName, fi);
                sourceToFeatureMap.put(fi.featureSourceData, fi);
            }
        }
    }

    /**
     * These are all auto generated based on information in other features. The changes will show up in the report under the
     * other features, so no need to review these.
     */
    private final String[] editionFeatures = { "com.ibm.websphere.appserver.baseBundle",
                                               "com.ibm.websphere.appserver.libertyCoreBundle",
                                               "com.ibm.websphere.appserver.ndControllerBundle",
                                               "com.ibm.websphere.appserver.ndMemberBundle",
                                               "com.ibm.websphere.appserver.zosBundle",
                                               "com.ibm.websphere.appserver.zosCoreBundle" };

    private final List<String> excludedFeatures = new ArrayList<String>(Arrays.asList(editionFeatures));

    private void buildIndexes(File repoDir, Map<String, FeatureInfo> nameToFeatureMap, Map<String, FeatureInfo> sourceToFeatureMap) {
        Map<Pattern, Set<String>> patternsToSeek = new HashMap<Pattern, Set<String>>();

        Pattern oldManifests = Pattern.compile("(?!.*(test|bvt|fat|build).*).*" + fileSep + "features" + fileSep + "(?!.*(test|bvt|fat|build).*).*.mf");
        patternsToSeek.put(oldManifests, new TreeSet<String>());

        Pattern manifests = Pattern.compile("build.image" + fileSep + "wlp" + fileSep + "lib" + fileSep + "features" + fileSep + "(?!.*(protected\\.|test|bvt|fat|build).*).*.mf");
        patternsToSeek.put(manifests, new TreeSet<String>());

        // Gather BND style features
        Pattern bnds = Pattern.compile("(?!.*(test|bvt|fat|build).*).*\\.feature");
        patternsToSeek.put(bnds, new TreeSet<String>());

        Pattern metatypes = Pattern.compile("(?!.*(test|bvt|fat|build).*).*" + fileSep + "metatype" + fileSep + "(?!.*(test|bvt|fat|build).*).*.xml");
        patternsToSeek.put(metatypes, new TreeSet<String>());

        findFiles(repoDir, repoDir, patternsToSeek);

        Set<String> featureSources = new TreeSet<String>();
        featureSources.addAll(patternsToSeek.get(manifests));
        featureSources.addAll(patternsToSeek.get(bnds));
        featureSources.addAll(patternsToSeek.get(oldManifests));

        populateMaps(repoDir, featureSources, patternsToSeek.get(metatypes), nameToFeatureMap, sourceToFeatureMap);
    }

    private String getFileLog(FeatureInfo fi) {
        String logpath = "other.html";
        String path = fi.featureSourceData;
        String parts[] = path.split(fileSep);
        String project = parts[0];
        if ("build.image".equals(project)) {
            // bnd style feature from build.image
            project = parts[4];
        }
        try {
            if (project.matches("com.ibm.ws.jms.*|com.ibm.ws.messaging.*|javax.jms.*")) {
                logpath = "messaging.html";
            } else if (project.matches("com.ibm.ws.transport.iiop.*|com.ibm.ws.security.csiv2.*|org.apache.yoko.*")) {
                logpath = "orb.html";
            } else if (project.matches("com.ibm.ws.wsecurity.*|javax.jaspic.*")) {
                logpath = "security.html";
            } else if (project.matches("com.ibm.ws.xlsp.*|com.ibm.ws.jaxws.*")) {
                logpath = "jaxws.html";
            } else if (project.matches("openwebbeans-.*")) {
                logpath = "cdi.html";
            } else if (project.startsWith("com.ibm.ws.")) {
                logpath = project.substring("com.ibm.ws.".length());
                if (logpath.indexOf('.') != -1) {
                    logpath = logpath.substring(0, logpath.indexOf('.'));
                }
                logpath += ".html";
            } else if (project.startsWith("com.ibm.websphere.appserver.")) {
                // BND based feature
                logpath = project.substring("com.ibm.websphere.appserver.".length());
                // Strip off version info
                if (logpath.indexOf('-') != -1) {
                    logpath = logpath.substring(0, logpath.lastIndexOf('-'));
                }

                if (logpath.startsWith("adminCenter")) {
                    logpath = "adminCenter";
                }

                logpath += ".html";
            } else if (project.startsWith("javax.j2ee.")) {
                logpath = project.substring("javax.j2ee.".length());
                if (logpath.indexOf('.') != -1) {
                    logpath = logpath.substring(0, logpath.indexOf('.'));
                }
                logpath += ".html";
            } else if (project.startsWith("javax.")) {
                logpath = project.substring("javax.".length());
                if (logpath.indexOf('.') != -1) {
                    logpath = logpath.substring(0, logpath.indexOf('.'));
                }
                logpath += ".html";
            } else {
                logpath = "other.html";
            }
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("error '" + project + "'");
            throw e;
        }
        return logpath;
    }

    private void reportRemovedFeatures(Map<String, FeatureInfo> baseNameToFeatureMap, Map<String, FeatureInfo> newNameToFeatureMap,
                                       Map<String, File> logFilesUsed) {
        System.out.println("Looking for Quarks:");
        for (Entry<String, FeatureInfo> feature : baseNameToFeatureMap.entrySet()) {
            if (!newNameToFeatureMap.containsKey(feature.getKey())) {
                System.out.println(" Detected missing feature " + feature.getKey() + " previously at " + feature.getValue().featureSourceData);

                copyOldFile(feature.getValue().featureSourceData);

                String logFileName = getFileLog(feature.getValue());
                PrintWriter output = getLog(logFileName, logFilesUsed);

                output.println("<div class=\"feature\">");
                output.println("  <div class=\"oldfilename\">" + feature.getValue().featureSourceData + "</div>");
                output.println("  <div class=\"symbolicName\">" + feature.getValue().symbolicName + "</div>");
                output.println("  <div class=\"visibility\">" + feature.getValue().visibility + "</div>");
                output.println("  <div class=\"singleton\">" + feature.getValue().singleton + "</div>");
                output.println("  <div class=\"bndBased\">" + feature.getValue().bndBased + "</div>");
                output.println("  <div class=\"AMD\">DELETED</div>");
                output.println("</div>");
                output.flush();
            }
        }
    }

    private void reportAddedFeatures(Map<String, FeatureInfo> baseNameToFeatureMap, Map<String, FeatureInfo> newNameToFeatureMap, FeatureCheckerOutput fco,
                                     Map<String, File> logFilesUsed) {
        System.out.println("Looking for Singularities:");
        for (Entry<String, FeatureInfo> feature : newNameToFeatureMap.entrySet()) {
            if (!baseNameToFeatureMap.containsKey(feature.getKey())) {
                System.out.println(" Detected added feature " + feature.getKey() + " present at " + feature.getValue().featureSourceData);

                copyNewFile(feature.getValue().featureSourceData);

                String logFileName = getFileLog(feature.getValue());
                PrintWriter output = getLog(logFileName, logFilesUsed);

                output.println("<div class=\"feature\">");
                output.println("  <div class=\"filename\">" + feature.getValue().featureSourceData + "</div>");
                output.println("  <div class=\"symbolicName\">" + feature.getValue().symbolicName + "</div>");
                output.println("  <div class=\"visibility\">" + feature.getValue().visibility + "</div>");
                output.println("  <div class=\"singleton\">" + feature.getValue().singleton + "</div>");
                output.println("  <div class=\"bndBased\">" + feature.getValue().bndBased + "</div>");
                output.println("  <div class=\"AMD\">ADDED</div>");

                //new features should have an entry in the junit xmls ..
                if (fco.hasProblemsForFeature(feature.getKey())) {
                    output.println("  <div class=\"checkerdetail\">");

                    System.out.println(" - Feature checker detailed information : ");
                    for (String s : fco.getNewProblems(feature.getKey())) {
                        output.println("    <div class=\"detailinfo\">");
                        System.out.println(s);
                        //we have to escape s.
                        String escaped = s.replaceAll("<", "&lt;");
                        escaped = escaped.replaceAll(">", "&gt;");
                        output.println(escaped);
                        output.println("    </div>");
                    }
                    output.println("  </div>");
                }
                output.println("</div>");
                output.flush();
            }
        }
    }

    //not a great routine, it doesn't check for locks etc..
    //but it's not too important.
    private void cleanupDir(File dir, String pattern) {
        if (dir.listFiles() != null) {
            for (File old : dir.listFiles()) {
                if (pattern == null || old.getName().endsWith(pattern)) {
                    if (old.isDirectory()) {
                        cleanupDir(old, pattern);
                    }
                    old.delete();
                }
            }
        }
    }

    private void copyFile(File src, File dest) {
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }
            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(src).getChannel();
                destination = new FileOutputStream(dest).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
        } catch (IOException io) {
            System.err.println(src.getAbsolutePath() + "-->" + dest.getAbsolutePath());
            io.printStackTrace();
        }
    }

    private void copyOldFile(String path) {
        File compare = new File(outDir, "compare");
        File oldDir = new File(compare, "old");
        File src = new File(baseDir, path);
        File dest = new File(oldDir, path);
        dest.getParentFile().mkdirs();
        copyFile(src, dest);
    }

    private void copyNewFile(String path) {
        File compare = new File(outDir, "compare");
        File newDir = new File(compare, "new");
        File src = new File(newBuildDir, path);
        File dest = new File(newDir, path);
        dest.getParentFile().mkdirs();
        copyFile(src, dest);
    }

    private PrintWriter getLog(String logFileName, Map<String, File> logFilesUsed) {
        PrintWriter output = null;
        File logOutput;

        boolean first = false;
        if (logFilesUsed.containsKey(logFileName)) {
            logOutput = logFilesUsed.get(logFileName);
        } else {
            logOutput = new File(outDir, logFileName);
            logFilesUsed.put(logFileName, logOutput);
            first = true;
        }

        try {
            FileWriter fw = new FileWriter(logOutput, true);
            output = new PrintWriter(fw);
            if (first) {
                output.println("<!doctype public \"-//W3C//DTD XHTML 1.0 Transitional//EN\"  system \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
                               + "\n<html><head>"
                               + "\n  <link rel=\"stylesheet\" type=\"text/css\" href=\"review.css\" />"
                               + "\n  <link rel=\"stylesheet\" href=\"//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css\">"
                               + "\n  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js\"></script>"
                               + "\n  <script src=\"https://code.jquery.com/ui/1.11.4/jquery-ui.min.js\"></script>"
                               + "\n  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\">"
                               + "\n  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css\">"
                               + "\n  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js\"></script>"
                               //+ "\n  <script type=\"text/javascript\" src=\"codemirror.min.js\"></script>"
                               + " \n  <script type=\"text/javascript\" src=\"https://cdn.rawgit.com/wickedest/Mergely/master/lib/codemirror.min.js\"></script>"
                               //+ "\n  <link type=\"text/css\" rel=\"stylesheet\" href=\"codemirror.css\" />"
                               + " \n  <link type=\"text/css\" rel=\"stylesheet\" href=\"https://cdn.rawgit.com/wickedest/Mergely/master/lib/codemirror.css\"/>"
                               //+ "\n  <script type=\"text/javascript\" src=\"mergely.js\"></script>"
                               + "\n  <script type=\"text/javascript\" src=\"https://cdn.rawgit.com/wickedest/Mergely/master/lib/mergely.min.js\"></script>"
                               //+ "\n  <link type=\"text/css\" rel=\"stylesheet\" href=\"mergely.css\" />"
                               + " \n  <link type=\"text/css\" rel=\"stylesheet\" href=\"https://cdn.rawgit.com/wickedest/Mergely/master/lib/mergely.css\" />"
                               + "\n  <script src=\"review.js\"></script>"
                               + "\n</head><body>");
                output.flush();
            }
            return output;
        } catch (IOException e) {
            System.err.println("Unable to write to " + logOutput.getAbsolutePath());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getCurrentDate() {
        DateFormat df = new SimpleDateFormat(ReportConstants.DATE_FORMAT);
        return df.format(new Date());
    }

    public String getBaselineDate() {
        try {
            File timestamp = new File(baseDir, ReportConstants.TIMESTAMP_FILE);
            if (!timestamp.exists()) {
                return "Unknown";
            }

            BufferedReader br = new BufferedReader(new FileReader(timestamp));
            String time = br.readLine();
            DateFormat df = new SimpleDateFormat(ReportConstants.DATE_FORMAT);

            return df.format(new Date(Long.valueOf(time)));

        } catch (IOException ex) {
            return "Unknown";
        }
    }

    public String getPreviousReviewDates() {
        StringBuffer dates = new StringBuffer();
        File[] reviewed = baseDir.listFiles(new ReviewFileFilter());
        for (File f : reviewed) {
            String name = f.getName();
            name = name.substring(8, name.length() - 5);
            DateFormat dateTime = new SimpleDateFormat(ReportConstants.DATE_TIME_FORMAT);
            DateFormat date = new SimpleDateFormat(ReportConstants.DATE_FORMAT);
            try {
                dates.append(date.format(dateTime.parse(name)));
                dates.append(", ");
            } catch (ParseException ex) {
                System.out.println("ERROR: Couldn't parse date: " + name);
            }
        }
        if (dates.length() > 0) {
            dates.deleteCharAt(dates.length() - 2);
        }
        return dates.toString();
    }

    private void closeLogs(Map<String, File> logFilesUsed) {
        for (File log : logFilesUsed.values()) {
            try {
                FileWriter fw = new FileWriter(log, true);
                PrintWriter output = new PrintWriter(fw);
                output.println("</body></html>");
                output.close();
            } catch (IOException e) {
                System.err.println("Unable to write to " + log.getAbsolutePath());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        Map<String, File> dummy = new HashMap<String, File>();
        PrintWriter index = getLog("index.html", dummy);
        index.println("<h2>API/SPI, Metatype, and Feature Changes</h2>");
        index.println("<h3>Baseline Date: " + getBaselineDate() + "</h3>");
        index.println("<h3>Report Date: " + getCurrentDate() + "</h3>");
        index.println("<h3>Previous Reviews: " + getPreviousReviewDates() + "</h3>");
        index.println("<table class=\"table table-striped\">");
        List<File> logs = new ArrayList<File>(logFilesUsed.values());
        Collections.sort(logs);
        for (File log : logs) {

            index.println("<tr><td><a href=\"" + log.getName() + "\">" + log.getName() + "</a></td></tr>");
        }
        index.println("</table>");
        index.println("</body></html>");
        index.close();

    }

    private void reportDifferencesForFeatures(Map<String, FeatureInfo> baseNameToFeatureMap, Map<String, FeatureInfo> newNameToFeatureMap,
                                              FeatureCheckerOutput fco, Map<String, File> logFilesUsed) {
        System.out.println("Looking for Wormholes:");
        for (Entry<String, FeatureInfo> feature : newNameToFeatureMap.entrySet()) {
            feature.getKey();
            if (baseNameToFeatureMap.containsKey(feature.getKey())) {
                FeatureInfo newF = feature.getValue();
                FeatureInfo oldF = baseNameToFeatureMap.get(feature.getKey());
                ManifestDiff md = new ManifestDiff(oldF.baseDir, oldF.featureSourceData, newF.baseDir, newF.featureSourceData);
                MetatypeDiff mtd = new MetatypeDiff(oldF.baseDir, oldF.metatypes, newF.baseDir, newF.metatypes);

                String logFileName = null;
                PrintWriter output = null;

                if (md.hasChanges() | mtd.hasChanges() | fco.hasProblemsForFeature(feature.getKey())) {
                    System.out.println("Feature " + newF.symbolicName + " has changes.");

                    copyOldFile(oldF.featureSourceData);
                    copyNewFile(newF.featureSourceData);

                    logFileName = getFileLog(newF);
                    output = getLog(logFileName, logFilesUsed);

                    output.println("<div class=\"feature\">");
                    output.println("  <div class=\"filename\">" + newF.featureSourceData + "</div>");
                    output.println("  <div class=\"oldfilename\">" + oldF.featureSourceData + "</div>");
                    output.println("  <div class=\"symbolicName\">" + newF.symbolicName + "</div>");
                    output.println("  <div class=\"visibility\">" + newF.visibility + "</div>");
                    output.println("  <div class=\"singleton\">" + newF.singleton + "</div>");
                    output.println("  <div class=\"bndBased\">" + newF.bndBased + "</div>");
                    output.println("  <div class=\"AMD\">MODIFIED</div>");
                }

                //manifest changes...

                if (md.hasChanges()) {

                    output.println("  <div class=\"manifest\">");

                    if (!md.addedHeaders.isEmpty()) {
                        output.println("    <div class=\"addedheaders\">");
                        System.out.println(" - Added headers : ");
                        for (Map.Entry<String, String> header : md.addedHeaders.entrySet()) {
                            output.println("      <div class=\"header\">");

                            System.out.println("  " + header.getKey() + " = " + header.getValue());
                            output.println("        <div class=\"headerKey\">" + header.getKey() + "</div>");
                            output.println("        <div class=\"headerValue\">" + header.getValue() + "</div>");

                            output.println("      </div>");
                        }
                        output.println("    </div>");
                    }
                    if (!md.deletedHeaders.isEmpty()) {
                        output.println("    <div class=\"removedheaders\">");

                        System.out.println(" - Removed headers : ");
                        for (Map.Entry<String, String> header : md.deletedHeaders.entrySet()) {
                            output.println("      <div class=\"header\">");
                            System.out.println("  " + header.getKey() + " = " + header.getValue());
                            output.println("        <div class=\"headerKey\">" + header.getKey() + "</div>");
                            output.println("        <div class=\"headerValue\">" + header.getValue() + "</div>");
                            output.println("      </div>");
                        }

                        output.println("</div>");
                    }
                    if (!md.changedHeaders.isEmpty()) {
                        output.println("    <div class=\"alteredheaders\">");
                        System.out.println(" - Altered headers : ");
                        for (Entry<String, Map<Report.ManifestDiff.Why, Set<Report.ManifestDiff.Change>>> header : md.changedHeaders.entrySet()) {
                            output.println("      <div class=\"header\">");
                            System.out.println("  " + header.getKey());
                            output.println("        <div class=\"headerKey\">" + header.getKey() + "</div>");
                            Set<Report.ManifestDiff.Change> added = header.getValue().get(Report.ManifestDiff.Why.ADDED);
                            Set<Report.ManifestDiff.Change> removed = header.getValue().get(Report.ManifestDiff.Why.REMOVED);
                            Set<Report.ManifestDiff.Change> changed = header.getValue().get(Report.ManifestDiff.Why.CHANGED);
                            if (added != null || removed != null || changed != null) {
                                output.println("          <div class=\"reason\">");
                                if (added != null) {
                                    for (Report.ManifestDiff.Change s : added) {
                                        output.println("            <div class=\"headerValue.ADDED\">" + s.newValue + "</div>");
                                    }
                                }
                                if (removed != null) {
                                    for (Report.ManifestDiff.Change s : removed) {
                                        output.println("            <div class=\"headerValue.REMOVED\">" + s.oldValue + "</div>");
                                    }
                                }
                                if (changed != null) {
                                    for (Report.ManifestDiff.Change s : changed) {
                                        output.println("            <div class=\"headerValue.CHANGED.pair\">");
                                        output.println("              <div class=\"headerValue.CHANGED.old\">" + s.oldValue + "</div>");
                                        output.println("              <div class=\"headerValue.CHANGED.new\">" + s.newValue + "</div>");
                                        output.println("            </div>");
                                    }
                                }
                                output.println("          </div>");
                            }

                            output.println("      </div>");
                        }
                        output.println("    </div>");
                    }

                    output.println("  </div>");
                }

                //metatype changes...

                if (mtd.hasChanges()) {

                    output.println("  <div class=\"metatypechanges\">");

                    if (!mtd.addedMetatypes.isEmpty()) {
                        output.println("    <div class=\"addedmetatypes\">");
                        System.out.println(" - Added metatypes : ");
                        for (String header : mtd.addedMetatypes) {
                            copyNewFile(header);
                            output.println("      <div class=\"metatype\">");
                            System.out.println("  " + header);
                            output.println("        <div class=\"filename\">" + header + "</div>");
                            output.println("      </div>");
                        }
                        output.println("    </div>");
                    }
                    if (!mtd.deletedMetatypes.isEmpty()) {
                        output.println("    <div class=\"deletedmetatypes\">");
                        System.out.println(" - Removed metatypes : ");
                        for (String header : mtd.deletedMetatypes) {
                            copyOldFile(header);
                            output.println("      <div class=\"metatype\">");
                            System.out.println("  " + header);
                            output.println("        <div class=\"filename\">" + header + "</div>");
                            output.println("      </div>");
                        }
                        output.println("    </div>");
                    }
                    if (!mtd.alteredMetatypes.isEmpty()) {
                        output.println("    <div class=\"alteredmetatypes\">");
                        System.out.println(" - Altered metatypes : ");
                        for (Map.Entry<String, Map<MetatypeDiff.DiffType, Object>> header : mtd.alteredMetatypes.entrySet()) {
                            copyOldFile(header.getKey());
                            copyNewFile(header.getKey());
                            output.println("      <div class=\"metatype\">");
                            output.println("        <div class=\"filename\">" + header.getKey() + "</div>");
                            System.out.println("  " + header.getKey() + " has changed. \n" + header.getValue().get(MetatypeDiff.DiffType.WHY_TEXT));
                            //we have to escape..
                            String escaped = ((String) header.getValue().get(MetatypeDiff.DiffType.UNIFIED_TEXT)).replaceAll("<", "&lt;");
                            escaped = escaped.replaceAll(">", "&gt;");
                            output.println("        <div class=\"detail\">\n" + escaped);
                            output.println("        </div>");
                            output.println("      </div>");
                        }
                        output.println("    </div>");
                    }
                    if (!mtd.addedProps.isEmpty()) {
                        output.println("    <div class=\"addedprops\">");
                        System.out.println(" - Added nls : ");
                        for (String header : mtd.addedProps) {
                            copyNewFile(header);
                            output.println("      <div class=\"metatype\">");
                            System.out.println("  " + header);
                            output.println("        <div class=\"filename\">" + header + "</div>");
                            output.println("      </div>");
                        }
                        output.println("    </div>");
                    }
                    if (!mtd.deletedProps.isEmpty()) {
                        output.println("    <div class=\"deletedprops\">");
                        System.out.println(" - Removed nls : ");
                        for (String header : mtd.deletedProps) {
                            copyOldFile(header);
                            output.println("      <div class=\"metatype\">");
                            System.out.println("  " + header);
                            output.println("        <div class=\"filename\">" + header + "</div>");
                            output.println("      </div>");
                        }
                        output.println("    </div>");
                    }
                    if (!mtd.alteredProps.isEmpty()) {
                        output.println("    <div class=\"alteredprops\">");
                        System.out.println(" - Altered nls : ");
                        for (Map.Entry<String, Map<MetatypeDiff.DiffType, Object>> header : mtd.alteredProps.entrySet()) {
                            output.println("      <div class=\"nlsprops\">");
                            output.println("        <div class=\"filename\">" + header.getKey() + "</div>");
                            copyOldFile(header.getKey());
                            copyNewFile(header.getKey());
                            System.out.println("  " + header.getKey() + " has changed. \n" + header.getValue().get(MetatypeDiff.DiffType.WHY_TEXT));
                            //we have to escape..
                            String escaped = ((String) header.getValue().get(MetatypeDiff.DiffType.UNIFIED_TEXT)).replaceAll("<", "&lt;");
                            escaped = escaped.replaceAll(">", "&gt;");
                            output.println("        <div class=\"detail\">\n" + escaped);
                            output.println("        </div>");
                            output.println("      </div>");
                        }
                        output.println("    </div>");
                    }

                    output.println("  </div>");
                }

                // and now.. scan the junit.xmls to see if anything has been reported there..
                if (fco.hasProblemsForFeature(feature.getKey())) {

                    output.println("  <div class=\"checkerdetail\">");

                    System.out.println(" - Feature checker detailed change information : ");
                    for (String s : fco.getNewProblems(feature.getKey())) {
                        output.println("    <div class=\"detailinfo\">");
                        System.out.println(s);
                        //we have to escape s.
                        String escaped = s.replaceAll("<", "&lt;");
                        escaped = escaped.replaceAll(">", "&gt;");
                        output.println(escaped);
                        output.println("    </div>");
                    }

                    output.println("  </div>");
                }

                if (md.hasChanges() | mtd.hasChanges() | fco.hasProblemsForFeature(feature.getKey())) {
                    System.out.println("");
                    output.println("</div>");
                    output.close();
                }
            }
        }
    }

    private void prepareReport(Map<String, FeatureInfo> baseNameToFeatureMap, Map<String, FeatureInfo> newNameToFeatureMap, FeatureCheckerOutput fco) {
        Map<String, File> logFilesUsed = new HashMap<String, File>();

        //tidy up the old html..
        cleanupDir(outDir, ".html");
        //tidy up the old comparison dir..
        File compare = new File(outDir, "compare");
        File oldDir = new File(compare, "old");
        File newDir = new File(compare, "new");
        cleanupDir(oldDir, null);
        cleanupDir(newDir, null);

        reportRemovedFeatures(baseNameToFeatureMap, newNameToFeatureMap, logFilesUsed);
        reportAddedFeatures(baseNameToFeatureMap, newNameToFeatureMap, fco, logFilesUsed);
        reportDifferencesForFeatures(baseNameToFeatureMap, newNameToFeatureMap, fco, logFilesUsed);

        closeLogs(logFilesUsed);
    }

    private void dumpClassPathForLogs() {
        ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
        //Get the URLs
        URL[] urls = ((URLClassLoader) sysClassLoader).getURLs();
        System.out.println("Federation Starship Roster:");
        for (int i = 0; i < urls.length; i++) {
            System.out.println(" USS " + urls[i].getFile());
        }
    }

    public Report(String args[]) throws IOException {
        parseArgs(args);
        dumpClassPathForLogs();

        Map<String, FeatureInfo> baseNameToFeatureMap = new TreeMap<String, FeatureInfo>();
        Map<String, FeatureInfo> baseSourceToFeatureMap = new TreeMap<String, FeatureInfo>();

        buildIndexes(baseDir, baseNameToFeatureMap, baseSourceToFeatureMap);

        Map<String, FeatureInfo> newNameToFeatureMap = new TreeMap<String, FeatureInfo>();
        Map<String, FeatureInfo> newSourceToFeatureMap = new TreeMap<String, FeatureInfo>();

        buildIndexes(newBuildDir, newNameToFeatureMap, newSourceToFeatureMap);

        FeatureCheckerOutput fco = new FeatureCheckerOutput(baseDir, newBuildDir, outDir);

        prepareReport(baseNameToFeatureMap, newNameToFeatureMap, fco);
    }

    public static void main(String[] args) throws IOException {
        new Report(args);
    }

}
