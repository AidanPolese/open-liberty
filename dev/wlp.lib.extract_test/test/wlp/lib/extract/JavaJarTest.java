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
package wlp.lib.extract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Exchanger;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.aries.util.manifest.BundleManifest;
import org.apache.aries.util.manifest.ManifestProcessor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import test.common.SharedOutputManager;
import wlp.lib.extract.platform.Platform;

public class JavaJarTest {
    //private static final File outputUploadDir = new File("c:/build/liberty/build.image/output/upload");

    private static final File outputUploadDir;
    private static boolean disableTestSuite;
    private static boolean runCoreTest;
    private static final File Repo8555 = new File("old_version_jar/8555");
    private static final File Repo8556 = new File("old_version_jar/8556");
    private static final File Repo8557 = new File("old_version_jar/8557");

    // ***IMPORTANT***
    // Mac and Sun builds do NOT produce the wlp-developers-8.5.0.0.zip. Until they do, we need to
    // to disable this testsuite. Since JUnit doesn't have a good 'dynamic' way of ignoring tests,
    // we will simply return early (though it will be counted on a passing test). This will keep the
    // noise in the logs low.

    static {
        runCoreTest = false;
        String uploadDir = System.getProperty("image.output.upload.dir");

        if (uploadDir == null) {
            uploadDir = "../build.image/output/upload/externals/installables";
        }

        outputUploadDir = new File(uploadDir);
        if (!outputUploadDir.exists()) {
            disableTestSuite = true;
        } else {
            disableTestSuite = false;
        }

        // Source License Edition (SLE) also disables this test.
        if (System.getProperty("is.sle") != null) {
            System.out.println("Skipping test because this is running in SLE");
            disableTestSuite = true;
        }
        File[] files = outputUploadDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (file.isFile() && name.startsWith("wlp-core-runtime") && name.endsWith(".jar")) {
                    runCoreTest = true;
                }
            }
        }
    }

    private static final File java = new File(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");

    private static final File buildUnittestDir = new File("build/unittest");
    private static final File tmpDir = new File("build/unittest/tmp");
    private static final File wlpDir = new File(tmpDir, "wlp");
    private static final File readme = new File(wlpDir, "README.TXT");

    private static final File samplesTmpDir = new File("build/unittest/samplestmp");
    private static final File samplesCoreDir = new File("build/unittest/samplescoretmp");
    private static final File samplesBaseDir = new File("build/unittest/samplesbasetmp");
    private static final File samplesWP7UpgradeDir = new File("build/unittest/sampleswp7tmp");
    private static final File samplesILANDowngradeDir = new File("build/unittest/samplesilantmp");
    private static final File samplesBaseUpgradeDir = new File("build/unittest/samplesbaseupgradetmp");
    private static final File samplesLicenseUpgradeDir = new File("build/unittest/sampleslicenseupgradetmp");
    private static final File samplesLicenseDowngradeDir = new File("build/unittest/sampleslicensedowngradetmp");
    private static final File samplesBundleDowngradeDir = new File("build/unittest/samplesbundledowngradetmp");
    private static final File samplesBundleUpgradeDir = new File("build/unittest/samplesbundleupgradetmp");
    private static final File samplesOldVersionDowngradeDir = new File("build/unittest/samplesoldversiondowngradetmp");
    private static final File samplesBaseRollbackDir = new File("build/unittest/samplesbaserollbacktmp");
    private static final File samplesBaseUpgradeWithFeatureDir = new File("build/unittest/samplesbaseupgradewithfeaturetmp");
    private static final File samplesNdDowngradeWithFeatureDir = new File("build/unittest/samplesnddowngradewithfeaturetmp");
    private static final File samplesNdDir = new File("build/unittest/samplesndtmp");
    private static final File samplesNdLicDir = new File("build/unittest/samplesndlictmp");
    private static final File samplesBaseLicDir = new File("build/unittest/samplesbaselictmp");
    private static final File samplesWlpDir = new File(samplesTmpDir, "wlp");
    private static final File samplesMavenDir = new File("build/unittest/samplesmaventmp");

    private static final File dummySampleJar = new File("build/dummySample/output/dummySample.jar");
    private static final File dummySampleFileErrorJar = new File("build/dummySampleFileError/output/dummySampleFileError.jar");
    private static final File dummySampleLicenseJar = new File("build/dummyLicenseSample/output/dummyLicenseSample.jar");
    private static final File dummyInstallerNoExtract = new File("build/dummyInstallerNoExtract/output/dummyInstaller-extractInstallerDisabled.jar");
    private static final File dummyInstallerWithExtract = new File("build/dummyInstallerNoExtract/output/dummyInstaller-extractInstallerEnabled.jar");
    private static final File dummyInstallerExtendedEmpty = new File("build/dummyInstallerExtendedEmpty/output/dummyInstallerExtendedEmpty.jar");
    private static final File dummyInstallerWithExtension = new File("build/dummyInstallerWithExtension/output/dummyInstallerWithExtension.jar");
    private static final File dummyInstallerWithFeature = new File("build/dummyInstallerWithFeature/output/dummyInstallerWithFeature.jar");

    private static final long TIMEOUT_MILLISECONDS = TimeUnit.MILLISECONDS.convert(300, TimeUnit.SECONDS);

    private static final int EXIT_OK = 0;
    private static final int EXIT_BAD_INPUT = 3;
    private static final int EXIT_EXTRACT_ERROR = 4;
    private static final int NOT_APPLICABLE_FEATURE = 5;

    private static final String LA_PREFIX = "wlp/lafiles/LA_";
    private static final String LI_PREFIX = "wlp/lafiles/LI_";

    private static final Locale LOCALE_CZECH = new Locale("cs", "CZ");
    private static final Locale LOCALE_HUNGARIAN = new Locale("hu", "HU");
    private static final Locale LOCALE_POLISH = new Locale("pl", "PL");
    private static final Locale LOCALE_PORTUGUESE_BRAZILIAN = new Locale("pt", "BR");
    private static final Locale LOCALE_ROMANIAN = new Locale("ro", "RO");
    private static final Locale LOCALE_RUSSIAN = new Locale("ru", "RU");
    private static final Locale LOCALE_TURKISH = new Locale("tr", "TR");
    private static final Locale LOCALE_HEBREW = new Locale("iw", "IL");

    private static Translation[] TRANSLATIONS = {
                                                  new Translation(LOCALE_CZECH),
                                                  new Translation(Locale.ENGLISH),
                                                  new Translation(Locale.FRENCH),
                                                  new Translation(Locale.GERMAN),
                                                  new Translation(LOCALE_HUNGARIAN, Locale.ENGLISH),
                                                  new Translation(Locale.ITALIAN),
                                                  new Translation(Locale.JAPANESE),
                                                  new Translation(Locale.KOREAN),
                                                  new Translation(LOCALE_POLISH),
                                                  new Translation(LOCALE_PORTUGUESE_BRAZILIAN),
                                                  new Translation(LOCALE_ROMANIAN, Locale.ENGLISH),
                                                  new Translation(LOCALE_RUSSIAN),
                                                  new Translation(Locale.SIMPLIFIED_CHINESE),
                                                  new Translation(Locale.TRADITIONAL_CHINESE),
                                                  new Translation(LOCALE_TURKISH),
                                                  new Translation(LOCALE_HEBREW, Locale.ENGLISH),
    };

    enum WlpJarType {
        CORE, CORE_LIC, EXTENDED, BASE, BASE_LIC, ND, ND_LIC, CORE_ALL, BASE_ALL, DEVELOPERS_IPLA_ALL, ND_ALL, ILAN, BASE_TRIAL, BETA, BLUEMIX, EXPRESS, ND_TRIAL, CORE_TRIAL,
        CORE_ISV, DEVELOPERS_IPLA, DEVELOPERS
    };

    private static final ResourceBundle selfExtractResourceBundle = ResourceBundle.getBundle(SelfExtract.class.getName() + "Messages");

    private String formatMessage(String key, Object[] args) {
        return MessageFormat.format(selfExtractResourceBundle.getString(key), args);
    }

    @Rule
    public SharedOutputManager outputMgr = SharedOutputManager.getInstance();

    @Rule
    public TestName testName = new TestName();

    private static File getWLPJar(WlpJarType wlpJarType, File repo) {

        String jarFilePrefix = null;
        switch (wlpJarType) {
            case CORE:
                if (runCoreTest)
                    jarFilePrefix = "wlp-core-runtime";
                break;
            case CORE_ALL:
                if (runCoreTest)
                    jarFilePrefix = "wlp-core-all";
                break;
            case CORE_TRIAL:
                if (runCoreTest)
                    jarFilePrefix = "wlp-core-trial-runtime";
                break;
            case CORE_LIC:
                if (runCoreTest)
                    jarFilePrefix = "wlp-core-license";
                break;
            case CORE_ISV:
                if (runCoreTest)
                    jarFilePrefix = "wlp-core-isv-runtime";
                break;
            case EXTENDED:
                jarFilePrefix = "wlp-developers-extended";
                break;
            case BASE:
                jarFilePrefix = "wlp-base-runtime";
                break;
            case BASE_ALL:
                jarFilePrefix = "wlp-base-all";
                break;
            case BASE_TRIAL:
                jarFilePrefix = "wlp-base-trial-runtime";
                break;
            case BASE_LIC:
                jarFilePrefix = "wlp-base-license";
                break;
            case BETA:
                jarFilePrefix = "wlp-beta-runtime";
                break;
            case BLUEMIX:
                jarFilePrefix = "wlp-bluemix-run-runtime";
                break;
            case DEVELOPERS:
                jarFilePrefix = "wlp-developers-runtime";
                break;
            case DEVELOPERS_IPLA:
                jarFilePrefix = "wlp-developers-ipla-runtime";
                break;
            case DEVELOPERS_IPLA_ALL:
                jarFilePrefix = "wlp-developers-ipla-all";
                break;
            case EXPRESS:
                jarFilePrefix = "wlp-express-run-runtime";
                break;
            case ILAN:
                jarFilePrefix = "wlp-runtime";
                break;
            case ND:
                jarFilePrefix = "wlp-nd-runtime";
                break;
            case ND_ALL:
                jarFilePrefix = "wlp-nd-all";
                break;
            case ND_TRIAL:
                jarFilePrefix = "wlp-trial-nd-runtime";
                break;
            case ND_LIC:
                jarFilePrefix = "wlp-nd-license";
                break;
        }
        File[] files;
        if (repo == null) {
            files = outputUploadDir.listFiles();
        } else {
            files = repo.listFiles();
        }

        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (file.isFile() && name.startsWith(jarFilePrefix) && name.endsWith(".jar")) {
                    return file;
                }
            }
        }
        throw new IllegalStateException("Failed to find " + jarFilePrefix + "*.jar in " + outputUploadDir.getAbsolutePath());

    }

    private static class Translation {
        final Locale locale;
        final Locale translation;

        Translation(Locale locale) {
            this(locale, locale);
        }

        Translation(Locale locale, Locale translation) {
            this.locale = locale;
            this.translation = translation;
        }
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        Assert.assertTrue("mkdir -p " + tmpDir.getAbsolutePath(), tmpDir.mkdirs() || tmpDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesTmpDir.getAbsolutePath(), samplesTmpDir.mkdirs() || samplesTmpDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesBaseDir.getAbsolutePath(), samplesBaseDir.mkdirs() || samplesBaseDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesBaseUpgradeDir.getAbsolutePath(), samplesBaseUpgradeDir.mkdirs() || samplesBaseUpgradeDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesBaseRollbackDir.getAbsolutePath(), samplesBaseRollbackDir.mkdirs() || samplesBaseRollbackDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesBaseUpgradeWithFeatureDir.getAbsolutePath(),
                          samplesBaseUpgradeWithFeatureDir.mkdirs() || samplesBaseUpgradeWithFeatureDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesNdDowngradeWithFeatureDir.getAbsolutePath(),
                          samplesNdDowngradeWithFeatureDir.mkdirs() || samplesNdDowngradeWithFeatureDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesNdDir.getAbsolutePath(), samplesNdDir.mkdirs() || samplesNdDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesNdLicDir.getAbsolutePath(), samplesNdLicDir.mkdirs() || samplesNdLicDir.isDirectory());
        Assert.assertTrue("mkdir -p " + samplesBaseLicDir.getAbsolutePath(), samplesBaseLicDir.mkdirs() || samplesBaseLicDir.isDirectory());
        // Get a server unpacked for the samples to extract against
        if (disableTestSuite) {
            return;
        }
        if (runCoreTest) {
            extractEditionJar(WlpJarType.CORE_ALL, samplesCoreDir);
        }
        extractEditionJar(WlpJarType.BASE, samplesTmpDir);
        extractEditionJar(WlpJarType.BASE, samplesBaseDir);
        extractEditionJar(WlpJarType.BASE, samplesBaseLicDir);
        extractEditionJar(WlpJarType.BASE, samplesBaseUpgradeDir);
        extractEditionJar(WlpJarType.BASE, samplesBaseRollbackDir);
        extractEditionJar(WlpJarType.BASE, samplesBaseUpgradeWithFeatureDir);
        extractEditionJar(WlpJarType.ND, samplesNdDir);
        extractEditionJar(WlpJarType.ND, samplesNdLicDir);
        extractEditionJar(WlpJarType.ND, samplesNdDowngradeWithFeatureDir);

    }

    private static void extractEditionJar(WlpJarType type, File dest) throws Exception {
        execute(null, null, null, type, null, new String[0], EXIT_OK,
                input("x", "x", "1", dest.getAbsolutePath()));
    }

    private static void deleteDir(File dir) {
        if (dir.exists()) {
            System.out.println("rm -rf " + dir.getAbsolutePath());
            delete(dir);
        }
    }

    @Before
    public void setUp() {
        System.out.println("Setting up for " + testName.getMethodName() + "...");
        deleteDir(wlpDir);
    }

    @After
    public void tearDown() {
        System.out.println("Tear down for " + testName.getMethodName() + "...");
        deleteDir(wlpDir);
    }

    @AfterClass
    public static void cleanUp() {
        System.out.println("Clean up Samples temp directory...");
        deleteDir(samplesTmpDir);
        deleteDir(samplesCoreDir);
        deleteDir(samplesBaseDir);
        deleteDir(samplesBaseLicDir);
        deleteDir(samplesBaseUpgradeDir);
        deleteDir(samplesBaseRollbackDir);
        deleteDir(samplesNdDir);
        deleteDir(samplesNdLicDir);
        deleteDir(samplesBaseUpgradeWithFeatureDir);
        deleteDir(samplesNdDowngradeWithFeatureDir);
        deleteDir(samplesLicenseUpgradeDir);
        deleteDir(samplesLicenseDowngradeDir);
        deleteDir(samplesBundleDowngradeDir);
        deleteDir(samplesOldVersionDowngradeDir);
        deleteDir(samplesBundleUpgradeDir);
        deleteDir(samplesWP7UpgradeDir);
        deleteDir(samplesILANDowngradeDir);
    }

    private static void delete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File child : files) {
                delete(child);
            }
        }

        System.out.println("Deleting " + file.getAbsolutePath());
        Assert.assertTrue("delete " + file, file.delete());
    }

    private static boolean unzip(File zipFile, File outputDir) throws IOException {
        byte[] buffer = new byte[1024];
        try {
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputDir + File.separator + fileName);

                if (ze.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private static String getLicenseLineFragment(String prefix, Locale locale) throws IOException {
        ZipFile zipFile = new ZipFile(getWLPJar(WlpJarType.BASE, null));
        try {
            String lang = locale.getLanguage();
            String country = locale.getCountry();

            ZipEntry zipEntry = null;
            String[] suffixes = new String[] { lang + '_' + country, lang, "en" };
            for (int i = 0; i < suffixes.length; i++) {
                zipEntry = zipFile.getEntry(prefix + suffixes[i]);
                if (zipEntry != null) {
                    InputStream input = zipFile.getInputStream(zipEntry);
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-16"));
                        for (String line; (line = reader.readLine()) != null;) {
                            line = line.trim();
                            if (!line.isEmpty()) {
                                // The extractor word wraps, so find the first several chars.
                                return line.substring(0, Math.min(line.length(), 40));
                            }
                        }
                    } finally {
                        try {
                            input.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } finally {
            try {
                zipFile.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        throw new AssertionError("failed to find license: " + prefix + ", " + locale);
    }

    private static boolean compareDirectory(String basepath, String deltapath) throws Exception {
        File basedirectory = new File(basepath);
        File deltadirectory = new File(deltapath);
        File[] basefiles = basedirectory.listFiles();
        File[] deltafiles = deltadirectory.listFiles();

        for (int i = 0; i < deltafiles.length; i++) {
            FileInputStream deltafis = new FileInputStream(deltafiles[i]);
            BufferedReader deltabr = new BufferedReader(new InputStreamReader(deltafis));
            StringBuilder deltasb = new StringBuilder();
            String deltaline;
            while ((deltaline = deltabr.readLine()) != null) {
                deltasb.append(deltaline);
            }

            FileInputStream basefis = new FileInputStream(basefiles[i]);
            BufferedReader basebr = new BufferedReader(new InputStreamReader(basefis));
            StringBuilder basesb = new StringBuilder();
            String baseline;
            while ((baseline = basebr.readLine()) != null) {
                basesb.append(baseline);
            }

            deltafis.close();
            basefis.close();
            deltabr.close();
            basebr.close();

            if (!deltasb.toString().equals(basesb.toString())) {
                return false;
            }
        }

        return true;
    }

    private static Collection<String> execute(File workDir, File repo, Locale locale, WlpJarType wlpJarType, Map<String, String> envvars, String[] args, int expectedExit,
                                              Action... actions) throws IOException, InterruptedException, TimeoutException {
        return execute(workDir, locale, envvars, args, expectedExit, getWLPJar(wlpJarType, repo), actions);
    }

    private static Collection<String> execute(File workDir, Locale locale, Map<String, String> envvars, String[] args, int expectedExit, File targetExecutable,
                                              Action... actions) throws IOException, InterruptedException, TimeoutException {
        ProcessBuilder pb = new ProcessBuilder();
        if (envvars != null) {
            pb.environment().putAll(envvars);
        }
        List<String> command = new ArrayList<String>();
        command.add(java.getAbsolutePath());
        if (locale != null) {
            command.add("-Duser.language=" + locale.getLanguage());
            String country = locale.getCountry();
            if (!country.isEmpty()) {
                command.add("-Duser.country=" + country);
            }
        }
        command.add("-Dfile.encoding=UTF-8");
        command.add("-Dconsole.encoding=UTF-8");
        command.add("-jar");
        command.add(targetExecutable.getAbsolutePath());
        command.addAll(Arrays.asList(args));
        pb.command(command);

        if (workDir != null) {
            pb.directory(workDir);
        }

        System.out.println("execute " + command);

        Process p = pb.start();

        BlockingDeque<String> output = new LinkedBlockingDeque<String>();
        Thread stdoutReader = new Thread(new StreamReader(1, p.getInputStream(), output));
        stdoutReader.start();
        Thread stderrReader = new Thread(new StreamReader(2, p.getErrorStream(), output));
        stderrReader.start();

        int inputRemaining = 0;
        for (Action action : actions) {
            if (action instanceof InputAction) {
                inputRemaining++;
            }
        }

        OutputStream stdin = p.getOutputStream();

        Exchanger<String> input = new Exchanger<String>();
        Thread stdinWriter = null;
        if (inputRemaining == 0) {
            stdin.close();
        } else {
            stdinWriter = new Thread(new StreamWriter(0, stdin, input));
            stdinWriter.start();
        }

        for (Action action : actions) {
            action.act(output, input);

            if (action instanceof InputAction) {
                inputRemaining--;
                if (inputRemaining == 0) {
                    stdin.close();
                    input.exchange(null, TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
                }
            }
        }

        stdoutReader.join();
        stderrReader.join();
        if (stdinWriter != null) {
            stdinWriter.join();
        }
        p.waitFor();

        Assert.assertEquals("exit code", expectedExit, p.exitValue());
        return output;
    }

    private static class StreamReader implements Runnable {
        private final int fd;
        private final BufferedReader reader;
        private final BlockingDeque<String> output;

        StreamReader(int fd, InputStream in, BlockingDeque<String> output) throws UnsupportedEncodingException {
            this.fd = fd;
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            this.output = output;
        }

        @Override
        public void run() {
            try {
                try {
                    for (String line; (line = reader.readLine()) != null;) {
                        System.out.println(fd + "<< " + line);
                        output.add(line);
                    }
                } finally {
                    reader.close();
                }
            } catch (IOException ex) {
                throw new Error(ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static class ListBasedExtractProgress implements ExtractProgress {
        private List extractedFiles;
        private List downloadedFiles;
        private List downloadSizeMonitor;
        private boolean canceled;

        @Override
        public void downloadingFile(URL sourceUrl, File targetFile) {
            if (downloadedFiles != null) {
                Map downloadInformation = new HashMap();
                downloadInformation.put(SelfExtractUtils.DOWNLOAD_URL, sourceUrl);
                downloadInformation.put(SelfExtractUtils.DOWNLOAD_TARGET_FILE, targetFile);
                canceled = !!!downloadedFiles.add(downloadInformation);
            }
        }

        @Override
        public void dataDownloaded(int numBytes) {
            if (downloadSizeMonitor != null) {
                downloadSizeMonitor.add(Integer.valueOf(numBytes));
            }
        }

        @Override
        public void extractedFile(String f) {
            if (extractedFiles != null) {
                canceled = !!!extractedFiles.add(f);
            }
        }

        @Override
        public void setFilesToExtract(int count) {}

        @Override
        public void commandRun(List args) {}

        @Override
        public void commandsToRun(int count) {}

        @Override
        public boolean isCanceled() {
            return canceled;
        }

        @Override
        public void skippedFile() {
            if (extractedFiles != null) {
                canceled = !!!extractedFiles.add(1);
            }
        }
    };

    private static class StreamWriter implements Runnable {
        private static final String LINE_SEPARATOR = System.getProperty("line.separator");

        private final int fd;
        private final Writer writer;
        private final Exchanger<String> input;

        StreamWriter(int fd, OutputStream out, Exchanger<String> input) {
            this.fd = fd;
            this.writer = new OutputStreamWriter(out);
            this.input = input;
        }

        @Override
        public void run() {
            try {
                for (String line; (line = input.exchange("writing")) != null;) {
                    System.out.println(fd + ">> " + line);
                    writer.write(line + LINE_SEPARATOR);
                    writer.flush();
                    Assert.assertEquals("ack", input.exchange("wrote"));
                }
            } catch (InterruptedException ex) {
                throw new Error(ex);
            } catch (IOException ex) {
                throw new Error(ex);
            }
        }
    }

    public interface Action {
        void act(BlockingDeque<String> output, Exchanger<String> input) throws InterruptedException, TimeoutException;
    }

    public static class InputAction implements Action {
        private final String[] lines;

        InputAction(String[] lines) {
            this.lines = lines;
        }

        @Override
        public void act(BlockingDeque<String> output, Exchanger<String> input) throws TimeoutException, InterruptedException {
            for (String line : lines) {
                System.out.println("Writing " + line);
                Assert.assertEquals("writing", input.exchange(line, TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS));
                Assert.assertEquals("wrote", input.exchange("ack", TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS));
            }
        }
    }

    public static class OutputFindAction implements Action {
        private final Pattern pattern;
        private final Pattern[] withoutPatterns;

        OutputFindAction(String patternString, String[] withoutPatternStrings) {
            pattern = Pattern.compile(patternString);
            withoutPatterns = new Pattern[withoutPatternStrings.length];
            for (int i = 0; i < withoutPatternStrings.length; i++) {
                withoutPatterns[i] = Pattern.compile(withoutPatternStrings[i]);
            }
        }

        @Override
        public void act(BlockingDeque<String> output, Exchanger<String> input) throws InterruptedException {
            System.out.println("Finding " + pattern);
            String line;
            do {
                line = output.poll(TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS);
                if (line == null) {
                    Assert.fail("expected to find " + pattern);
                }
                System.out.println("x<<" + line);

                for (Pattern withoutPattern : withoutPatterns) {
                    Assert.assertFalse("did not expect to find " + withoutPattern, withoutPattern.matcher(line).find());
                }
            } while (!pattern.matcher(line).find());
        }
    }

    /**
     * Find a pattern in the output.
     */
    private static Action find(String patternString) {
        return new OutputFindAction(patternString, new String[0]);
    }

    /**
     * Find a string in the output - treats the supplied string as a literal, escaping any regex metacharacters.
     */
    private static Action findLiteral(String literalString) {
        return find(Pattern.quote(literalString));
    }

    /**
     * Find a pattern in the output without finding other patterns.
     */
    private static Action findWithout(String patternString, String... withoutPatternStrings) {
        return new OutputFindAction(patternString, withoutPatternStrings);
    }

    /**
     * Write the lines to standard input.
     */
    private static Action input(String... lines) {
        return new InputAction(lines);
    }

    /**
     * Creates an etc/server.env file in wlpDir if not present, then sets all provided properties in server.env, overriding anything
     * already present.
     */
    private void setServerEnvProps(Properties values, File wlpDir) throws IOException {
        File wlpEtcDir = new File(wlpDir, "etc");
        File serverEnvFile = new File(wlpEtcDir, "server.env");
        Properties props = new Properties();

        if (!wlpEtcDir.exists()) {
            wlpEtcDir.mkdirs();
        }
        if (serverEnvFile.exists()) {
            InputStream fis = null;
            try {
                fis = new FileInputStream(serverEnvFile);
                props.load(fis);
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }
        props.putAll(values);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(serverEnvFile);
            props.store(fos, "Dynamically generated by JavaJarTest");
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

    }

    private void setUserDirWithServerEnv(File userDir, File wlpDir) throws IOException {
        final String userDirPath = userDir.getAbsolutePath();

        @SuppressWarnings("serial")
        Properties p = new Properties() {
            {
                put("WLP_USER_DIR", userDirPath);
            }
        };

        setServerEnvProps(p, wlpDir);
    }

    private void deleteServerEnvIfExists(File wlpDir) {
        File serverEnvFile = new File(wlpDir, "etc/server.env");
        if (serverEnvFile.exists()) {
            serverEnvFile.delete();
        }
    }

    private String getWASProperty(String property, File propertyFile) throws IOException {
        Properties props = new Properties();
        InputStream fis = null;
        String wasProperty;

        try {
            fis = new FileInputStream(propertyFile);
            props.load(fis);
            wasProperty = props.getProperty(property);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        return wasProperty;
    }

    private String getXMLProperty(String property, File propertyFile) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(propertyFile);
        String xmlProperty = document.getElementsByTagName(property).item(0).getTextContent();
        return xmlProperty;
    }

    private void setManifestProperty(String property, String value, File manifestFile) throws IOException {
        InputStream fis = null;
        OutputStream fos = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        File newManifest = new File(manifestFile.getAbsolutePath() + "_new");
        boolean propertyExists = false;

        try {
            fis = new FileInputStream(manifestFile);
            fos = new FileOutputStream(newManifest);
            br = new BufferedReader(new InputStreamReader(fis));
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(property)) {
                    bw.write(property + ": " + value);
                    bw.newLine();
                    propertyExists = true;
                } else {
                    bw.write(line);
                    bw.newLine();
                }
            }
            if (!propertyExists) {
                bw.write(property + ": " + value);
                bw.newLine();
            }
        } finally {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
            manifestFile.delete();
            newManifest.renameTo(manifestFile);
        }

        System.out.println("Successfully updated manfiest file " + manifestFile.getCanonicalPath());
    }

    private String getWASTag(String tag, File tagFile) throws IOException {
        InputStream fis = null;
        String wasTag = null;
        Pattern pattern = Pattern.compile(String.format("<%s>(.+?)</%s>", tag, tag));

        try {
            fis = new FileInputStream(tagFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    wasTag = matcher.group(1);
                }
            }
            br.close();

        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        return wasTag;
    }

    private String getWASTagCD(String attr, File tagFile) throws IOException {
        InputStream fis = null;
        String wasTag = null;
        Pattern pattern = Pattern.compile(String.format(".*<.+ %s=\"(.+?)\".+>.*", attr));

        try {
            fis = new FileInputStream(tagFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    wasTag = matcher.group(1);
                }
            }
            br.close();

        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        return wasTag;
    }

    /*
     * Cuts off the fix pack number from the full version. For example, for 17.0.0.1 it will return 17.0.0.
     */
    private String getBaseVersionFromFullVersion(String fullVersion) {
        String baseVersion = "";
        Matcher m = Pattern.compile("^(.*)\\.\\d+$").matcher(fullVersion);
        if (m.find()) {
            baseVersion = m.group(1);
        }
        return baseVersion;
    }

    @Test
    public void testHelp() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(null, null, null, WlpJarType.BASE, null, new String[] { "--help" }, EXIT_OK,
                find("--acceptLicense"),
                find("--verbose"),
                find("--viewLicenseAgreement"),
                find("--viewLicenseInfo"));
    }

    private void testViewLicense(String prefix, String option) throws Exception {
        for (Translation translation : TRANSLATIONS) {
            Locale locale = translation.locale != Locale.ENGLISH ? translation.locale : null;
            execute(null, null, locale, WlpJarType.BASE, null, new String[] { option }, EXIT_OK,
                    find(Pattern.quote(getLicenseLineFragment(prefix, translation.translation))));
        }
    }

    @Test
    public void testViewLicenseAgreement() throws Exception {
        if (disableTestSuite) {
            return;
        }

        testViewLicense(LA_PREFIX, "--viewLicenseAgreement");
    }

    @Test
    public void testViewLicenseInfo() throws Exception {
        if (disableTestSuite) {
            return;
        }

        testViewLicense(LI_PREFIX, "--viewLicenseInfo");
    }

    @Test
    public void testAcceptLicense() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(null, null, null, WlpJarType.BASE, null, new String[] { "--acceptLicense" }, EXIT_BAD_INPUT,
                find("--acceptLicense"));
    }

    @Test
    public void testDisplayLicenseAgreement() throws Exception {
        if (disableTestSuite) {
            return;
        }

        for (Translation translation : TRANSLATIONS) {
            Locale locale = translation.locale != Locale.ENGLISH ? translation.locale : null;
            execute(null, null, locale, WlpJarType.BASE, null, new String[0], EXIT_BAD_INPUT,
                    find("--viewLicenseAgreement"),
                    input(""),
                    find(Pattern.quote(getLicenseLineFragment(LA_PREFIX, translation.translation))));
        }
    }

    @Test
    public void testDisplayLicenseInfo() throws Exception {
        if (disableTestSuite) {
            return;
        }

        for (Translation translation : TRANSLATIONS) {
            Locale locale = translation.locale != Locale.ENGLISH ? translation.locale : null;
            execute(null, null, locale, WlpJarType.BASE, null, new String[0], EXIT_BAD_INPUT,
                    find("--viewLicenseAgreement"),
                    input("x"),
                    find("--viewLicenseInfo"),
                    input(""),
                    find(Pattern.quote(getLicenseLineFragment(LI_PREFIX, translation.translation))));
        }
    }

    @Test
    public void testExtract() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(tmpDir, null, null, WlpJarType.DEVELOPERS, null, new String[0], EXIT_OK,
                input("x", "x", "1", ""));
        File installerSelfExtractor = new File(wlpDir, "lib/extract/SelfExtractor.class");
        File installerManifest = new File(wlpDir, "lib/extract/META-INF/MANIFEST.MF");
        Assert.assertTrue(readme.getAbsolutePath(), readme.exists());
        Assert.assertTrue("Core install should have created a SelfExtractor.class in " + installerSelfExtractor.getAbsolutePath(), installerSelfExtractor.exists());
        Assert.assertTrue("Core install should have created a MANIFEST.MF in " + installerManifest.getAbsolutePath(), installerManifest.exists());
    }

    @Test
    public void testExtractDummyWithoutInstaller() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(tmpDir, null, null, new String[0], EXIT_OK, dummyInstallerNoExtract,
                input(""));

        File installerSelfExtractor = new File(wlpDir, "lib/extract/SelfExtractor.class");
        File installerManifest = new File(wlpDir, "lib/extract/META-INF/MANIFEST.MF");
        File markerFile = new File(wlpDir, "MARKER");
        Assert.assertTrue("The installer should have created a marker file in the wlp dir - " + markerFile.getAbsolutePath(), markerFile.exists());
        Assert.assertFalse("The installer with Extract-Installer: false should not have created a SelfExtractor.class in " + installerSelfExtractor.getAbsolutePath(),
                           installerSelfExtractor.exists());
        Assert.assertFalse("The installer with Extract-Installer: false should not have created a MANIFEST.MF in " + installerManifest.getAbsolutePath(),
                           installerManifest.exists());
    }

    @Test
    public void testExtractDummyWithInstaller() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(tmpDir, null, null, new String[0], EXIT_OK, dummyInstallerWithExtract,
                input(""));

        File installerSelfExtractor = new File(wlpDir, "lib/extract/SelfExtractor.class");
        File installerManifest = new File(wlpDir, "lib/extract/META-INF/MANIFEST.MF");
        File markerFile = new File(wlpDir, "MARKER");
        Assert.assertTrue("The installer should have created a marker file in the wlp dir - " + markerFile.getAbsolutePath(), markerFile.exists());
        Assert.assertTrue("The installer with Extract-Installer: true should have created a SelfExtractor.class in " + installerSelfExtractor.getAbsolutePath(),
                          installerSelfExtractor.exists());
        Assert.assertTrue("The installer with Extract-Installer: true should have created a MANIFEST.MF in " + installerManifest.getAbsolutePath(),
                          installerManifest.exists());
    }

    @Test
    public void testExtractDummyWithInstallerAndFeature() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(tmpDir, null, null, new String[0], EXIT_OK, dummyInstallerWithFeature,
                input(""));

        File installerSelfExtractor = new File(wlpDir, "lib/extract/SelfExtractor.class");
        File installerManifest = new File(wlpDir, "lib/extract/META-INF/MANIFEST.MF");
        File markerFile = new File(wlpDir, "MARKER");
        File scriptFile1 = new File(wlpDir, "script/file1");
        File scriptFile2 = new File(wlpDir, "script/file2exe");
        Assert.assertTrue("The installer should have created a marker file in the wlp dir - " + markerFile.getAbsolutePath(), markerFile.exists());
        Assert.assertTrue("The installer with Extract-Installer: true should have created a SelfExtractor.class in " + installerSelfExtractor.getAbsolutePath(),
                          installerSelfExtractor.exists());
        Assert.assertTrue("The installer with Extract-Installer: true should have created a MANIFEST.MF in " + installerManifest.getAbsolutePath(),
                          installerManifest.exists());

        Assert.assertTrue("The installer should have created script file - " + scriptFile1.getAbsolutePath(), scriptFile1.exists());
        Assert.assertTrue("The installer should have created script file2 - " + scriptFile2.getAbsolutePath(), scriptFile2.exists());

        if (!Platform.isWindows()) {
            Assert.assertTrue("The script file2 should be executable.", scriptFile2.canExecute());
        }
    }

    @Test
    public void testExtractDummyWithExtension() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(tmpDir, null, null, new String[0], EXIT_OK, dummyInstallerWithExtension,
                input(""));

        File installerSelfExtractor = new File(wlpDir, "lib/extract/SelfExtractor.class");
        File installerManifest = new File(wlpDir, "lib/extract/META-INF/MANIFEST.MF");
        File markerFile = new File(wlpDir, "MARKER");
        File extMarkerFile = new File(tmpDir, "PFS/bpmFederatedProcessServer/wlp-ext/EXTMARKER");

        Assert.assertTrue("The installer should have created a marker file in the wlp dir - " + markerFile.getAbsolutePath(), markerFile.exists());
        Assert.assertTrue("The installer should have created a marker file in the extension's dir - " + extMarkerFile.getAbsolutePath(), extMarkerFile.exists());
        Assert.assertTrue("The installer with Extract-Installer: true should have created a SelfExtractor.class in " + installerSelfExtractor.getAbsolutePath(),
                          installerSelfExtractor.exists());
        Assert.assertTrue("The installer with Extract-Installer: true should have created a MANIFEST.MF in " + installerManifest.getAbsolutePath(),
                          installerManifest.exists());
    }

    @Test
    public void testInvalidLicenseAgreementInput() throws Exception {
        if (disableTestSuite) {
            return;
        }

        String[] badInputs = new String[] { "1", "2", "zz" };
        execute(null, null, null, WlpJarType.BASE, null, new String[0], EXIT_BAD_INPUT,
                find("--viewLicenseAgreement"),
                input(badInputs),
                findWithout("( 'x' .*){" + badInputs.length + "}",
                            "--viewLicenseInfo"));
    }

    @Test
    public void testInvalidLicenseInfoInput() throws Exception {
        if (disableTestSuite) {
            return;
        }

        String[] badInputs = new String[] { "1", "2", "zz" };
        execute(null, null, null, WlpJarType.BASE, null, new String[0], EXIT_BAD_INPUT,
                find("--viewLicenseAgreement"),
                input("x"),
                find("--viewLicenseInfo"),
                input("1", "2", "zz"),
                findWithout("( 'x' .*){" + badInputs.length + "}",
                            "[1]"));
    }

    @Test
    public void testInvalidAcceptLicenseAgreementInput() throws Exception {
        if (disableTestSuite) {
            return;
        }

        String[] badInputs = new String[] { "", "y", "Y", "n", "N", "11", "22" };
        execute(null, null, null, WlpJarType.BASE, null, new String[0], EXIT_BAD_INPUT,
                find("--viewLicenseAgreement"),
                input("x"),
                find("--viewLicenseInfo"),
                input("x"),
                input(badInputs),
                findWithout("(\\[1].*){" + badInputs.length + "}",
                            "[\\\\/]"));
    }

    @Test
    public void testDisagreeLicense() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(null, null, null, WlpJarType.BASE, null, new String[0], EXIT_OK,
                input("x", "x", "2"));
        Assert.assertFalse(readme.getAbsolutePath(), readme.exists());
    }

    @Test
    public void testRelativeInstallDir() throws Exception {
        if (disableTestSuite) {
            return;
        }

        Assert.assertTrue(wlpDir.getAbsolutePath(), wlpDir.mkdirs());
        String testFile = "test.txt";
        String testData = "Some dummy data";
        PrintStream out = new PrintStream(new File(wlpDir, testFile));
        try {
            out.println(testData);
        } finally {
            out.close();
            // An explicit close is required to ensure the file is unlocked
            // in time to be deleted during cleanup.
        }
        execute(null, null, null, WlpJarType.BASE, null, new String[0], EXIT_EXTRACT_ERROR,
                input("x", "x", "1", tmpDir.getPath()));
        Assert.assertFalse(readme.getAbsolutePath(), readme.exists());
    }

    @Test
    public void testAbsoluteInstallDir() throws Exception {
        if (disableTestSuite) {
            return;
        }

        Assert.assertTrue(wlpDir.getAbsolutePath(), wlpDir.mkdirs());
        String testFile = "test.txt";
        String testData = "Some dummy data";
        PrintStream out = new PrintStream(new File(wlpDir, testFile));
        try {
            out.println(testData);
        } finally {
            out.close();
            // An explicit close is required to ensure the file is unlocked
            // in time to be deleted during cleanup.
        }
        execute(null, null, null, WlpJarType.BASE, null, new String[0], EXIT_EXTRACT_ERROR,
                input("x", "x", "1", tmpDir.getAbsolutePath()));
        Assert.assertFalse(readme.getAbsolutePath(), readme.exists());
    }

    /*----------------------------------------------
     * Next block of tests are for Extended installs
     *---------------------------------------------*/
    private boolean extendedJarPresent() {
        File f = null;
        try {
            f = getWLPJar(WlpJarType.EXTENDED, null);
        } catch (IllegalStateException x) {
            return false;
        }
        return f != null && f.exists();
    }

    @Test
    public void testExtractExtendFailsWithoutCore() throws Exception {
        if (disableTestSuite) {
            return;
        }
        if (!extendedJarPresent()) { // Extended jar not built: cannot proceed
            return;
        }
        execute(tmpDir, null, null, WlpJarType.EXTENDED, null, new String[] {}, EXIT_EXTRACT_ERROR, input("x", "x", "1", "wlp"));
    }

    @Test
    public void testExtractExtendSucceedsOnCore() throws Exception {
        if (disableTestSuite) {
            return;
        }
        if (!extendedJarPresent()) { // Extended jar not built: cannot proceed
            return;
        }
        testExtract();

        // How do we know we've installed Extended? I've taken lib/features/jaxb-2.2.mf
        // as an example file that is in Extended and is not in Core.
        File anExtendedFile = new File(wlpDir, "lib/features/com.ibm.websphere.appserver.jaxb-2.2.mf");
        Assert.assertFalse("The extended file to be installed lib/features/com.ibm.websphere.appserver.jaxb-2.2.mf already exists", anExtendedFile.exists());

        execute(tmpDir, null, null, WlpJarType.EXTENDED, null, new String[0], EXIT_OK, input("x", "x", "1", "wlp"));
        Assert.assertTrue("The extended file lib/features/com.ibm.websphere.appserver.jaxb-2.2.mf failed to be installed", anExtendedFile.exists());
    }

    @Test
    public void testIFixDetectionInExtendedInstaller() throws Exception {
        if (disableTestSuite) {
            return;
        }
        if (!extendedJarPresent()) { // Extended jar not built: cannot proceed
            return;
        }
        testExtract();

        writeIFixFiles(wlpDir);

        Collection<String> output = execute(tmpDir, null, null, WlpJarType.EXTENDED, null, new String[0], EXIT_OK,
                                            input("x", "x", "1", "wlp"),
                                            find("The following fixes must be reapplied: \\[8.5.0.1-WS-WASProd_WLPArchive-IFPM77826\\]"));
    }

    private File[] getBundles(File wlpDir, String bundleSymName) {

        final String bundleSymbolicName = bundleSymName;

        // This should list just the real bundle that we need to get the version for.
        File[] toReturn = new File(wlpDir, "lib").listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                boolean result = false;
                if (pathname.getName().endsWith(".jar") && pathname.getName().startsWith(bundleSymbolicName + "_")) {
                    result = true;
                }
                return result;
            }
        });

        return toReturn;
    }

    private void writeIFixFiles(File wlpDir) throws Exception {

        // We only have one real bundle in the ifix xml and so we need to make sure we get the correct bundle name and version.
        // So we read the jar looking for the kernel.service jar and make sure we put the correct version in.
        InputStream zis = null;
        ZipFile zipFile = null;

        Version bundleVersion = new Version("1.0.0");

        //try using kernel service bundle first - low chance this has been ifixed
        String[] bsnames = { "com.ibm.ws.kernel.service", "com.ibm.ws.kernel.feature", "com.ibm.ws.kernel.fileinstall", "com.ibm.ws.kernel.filemonitor" };
        File[] bundles = {};
        String bundleSymbolicName = "";
        for (String b : bsnames) {
            bundleSymbolicName = b;
            bundles = getBundles(wlpDir, bundleSymbolicName);
            if (bundles != null) {
                if (bundles.length != 1) {
                    System.out.println("Bundle " + bundleSymbolicName + " has been ifixed - trying next");
                } else {
                    System.out.println("Using " + bundleSymbolicName);
                    break;
                }
            }
        }

        String bundleName = bundleSymbolicName + "_" + bundleVersion + ".jar";

        if (bundles == null || bundles.length != 1) {
            Assert.assertFalse("The test needs altering as specified bundles have been ifixed and we need one that hasn't" +
                               "for this test to work.", true);
        } else {
            try {
                zipFile = new ZipFile(bundles[0]);
                ZipEntry manifestEntry = zipFile.getEntry(JarFile.MANIFEST_NAME);
                if (manifestEntry != null) {
                    zis = zipFile.getInputStream(manifestEntry);
                    Manifest manifest = ManifestProcessor.parseManifest(zis);
                    BundleManifest bundleManifest = new BundleManifest(manifest);
                    String currBundleSymbolicName = bundleManifest.getSymbolicName();
                    if (currBundleSymbolicName != null && currBundleSymbolicName.startsWith(bundleSymbolicName)) {
                        bundleVersion = bundleManifest.getVersion();
                        bundleName = bundles[0].getName();
                        System.out.println("Found bundle: " + bundleName);
                    }
                }
            } finally {
                if (zis != null)
                    zis.close();
                if (zipFile != null)
                    zipFile.close();
            }
        }

        System.out.println("Using bundleName: " + bundleName);

        String iFixFile = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
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
                          + "<file date=\"2012-11-28 18:22\" hash=\"30bca3fa4ae9fa62b7ffda0e6125e389\" size=\"13149\" id=\"lib/com.ibm.ws.artifact.api.bundle_1.0.1.20121128-1822.jar\"/>"
                          + "<file date=\"2012-11-28 18:22\" hash=\"a4032fe03645f75d335b6e922ac2fcf7\" size=\"22775\" id=\"lib/com.ibm.ws.artifact.api.file_1.0.1.20121128-1822.jar\"/>"
                          + "<file date=\"2013-11-28 18:22\" hash=\"401136452a53470c6fe369a4e32b1cec\" size=\"142023\" id=\"lib/"
                          + bundleName
                          + "\"/>"
                          + "<file date=\"2013-13-03 16:04\" hash=\"i_have_noideabut_itdoesnotmatter\" size=\"99999\" id=\"lib/com.ibm.ws.ejbcontainer.mdb_1.0.TimeWePatchedThis.jar\"/>"
                          + "</updates>"
                          + "</fix>";

        String lpmfFile = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                          + "<libertyFixMetadata>"
                          + "<bundles>"
                          + "<bundle date=\"2012-11-28 18:22\" hash=\"7afb211071a3c08f103692e39bd97b51\" size=\"41903\" id=\"lib/com.ibm.ws.artifact.api.overlay_1.0.1.20121128-1822.jar\" symbolicName=\"com.ibm.ws.artifact.api.overlay\" version=\"1.0.1.20121128-1822\" isBaseBundle=\"false\"/>"
                          + "<bundle date=\"2012-11-28 18:22\" hash=\"39db8e9f1800965c36119651b3a32695\" size=\"197373\" id=\"lib/com.ibm.ws.classloading_1.0.1.20121128-1822.jar\" symbolicName=\"com.ibm.ws.classloading\" version=\"1.0.1.20121128-1822\" isBaseBundle=\"false\"/>"
                          + "<bundle date=\"2012-10-22 18:08\" hash=\"1fbd375da50a0fbca3a24f70c0bb7992\" size=\"121748\" id=\"lib/com.ibm.ws.jndi_1.0.1.20121022-1808.jar\" symbolicName=\"com.ibm.ws.jndi\" version=\"1.0.1.20121022-1808\" isBaseBundle=\"false\"/>"
                          + "<bundle date=\"2012-11-28 18:22\" hash=\"6e82f7692cac816f655877865b9e23c6\" size=\"62879\" id=\"lib/com.ibm.ws.artifact.api.zip_1.0.1.20121128-1822.jar\" symbolicName=\"com.ibm.ws.artifact.api.zip\" version=\"1.0.1.20121128-1822\" isBaseBundle=\"false\"/>"
                          + "<bundle date=\"2012-11-28 18:22\" hash=\"1905727b0b04137c7b92736ff65a614d\" size=\"77473\" id=\"lib/com.ibm.ws.artifact.api.loose_1.0.1.20121128-1822.jar\" symbolicName=\"com.ibm.ws.artifact.api.loose\" version=\"1.0.1.20121128-1822\" isBaseBundle=\"false\"/>"
                          + "<bundle date=\"2012-11-28 18:22\" hash=\"30bca3fa4ae9fa62b7ffda0e6125e389\" size=\"13149\" id=\"lib/com.ibm.ws.artifact.api.bundle_1.0.1.20121128-1822.jar\" symbolicName=\"com.ibm.ws.artifact.api.bundle\" version=\"1.0.1.20121128-1822\" isBaseBundle=\"false\"/>"
                          + "<bundle date=\"2012-11-28 18:22\" hash=\"a4032fe03645f75d335b6e922ac2fcf7\" size=\"22775\" id=\"lib/com.ibm.ws.artifact.api.file_1.0.1.20121128-1822.jar\" symbolicName=\"com.ibm.ws.artifact.api.file\" version=\"1.0.1.20121128-1822\" isBaseBundle=\"false\"/>"
                          + "<bundle date=\"2013-11-28 18:22\" hash=\"401136452a53470c6fe369a4e32b1cec\" size=\"142023\" id=\"lib/com.ibm.ws.kernel.service_1.0.1.jar\" symbolicName=\""
                          + bundleSymbolicName
                          + "\" version=\""
                          + bundleVersion
                          + "\" isBaseBundle=\"false\"/>"
                          + "<bundle date=\"2013-13-03 16:04\" hash=\"i_have_noideabut_itdoesnotmatter\" size=\"99999\" id=\"lib/com.ibm.ws.ejbcontainer.mdb_1.0.TimeWePatchedThis.jar\" symbolicName=\"com.ibm.ws.ejbcontainer.mdb\" version=\"1.0.0.20000101\" isBaseBundle=\"false\"/>"
                          + "</bundles>"
                          + "</libertyFixMetadata>";

        File libFixes = new File(wlpDir, "lib/fixes");
        libFixes.mkdirs();
        File iFix = new File(libFixes, "anIFix.xml");
        PrintWriter pw = new PrintWriter(iFix);
        pw.print(iFixFile);
        pw.close();

        // Write out lpmf file.
        File lpmf = new File(libFixes, "anIFix.lpmf");
        PrintWriter pw2 = new PrintWriter(lpmf);
        pw2.print(lpmfFile);
        pw2.close();

    }

    /**
     * This test ensures that if the extended zip is unzipped into a runtime that does have the required core features,
     * that is required by the extended zip, that we fail and issue the expected error message.
     *
     * @throws Exception
     */
    @Test
    public void testArchiveInstallIntoRuntimeWithMissingCoreFeatures() throws Exception {
        if (disableTestSuite) {
            return;
        }

        // Create the cut down extended jar which just has a manifest, which lists the required features.
        wlpDir.mkdirs();
        File extendedJar = new File(wlpDir, "extended.jar");
        createExtendedFile(extendedJar, "com.ibm.websphere.appserver.a-1.0,com.ibm.websphere.appserver.b-1.0,com.ibm.websphere.appserver.c-1.0");
        JarFile extendedJarFile = new JarFile(extendedJar);

        // Write the WebSphereApplicationServer.properties file in the lib/versions dir.
        String productID = "com.ibm.websphere.appserver";
        String productVersion = "8.5.next.beta";
        String productInstallType = "Archive";
        String productEdition = "DEVELOPERS";

        StringBuffer versions = new StringBuffer();
        versions.append("com.ibm.websphere.productId=" + productID + "\n");
        versions.append("com.ibm.websphere.productOwner=IBM\n");
        versions.append("com.ibm.websphere.productVersion=" + productVersion + "\n");
        versions.append("com.ibm.websphere.productName=WebSphere Application Server\n");
        versions.append("com.ibm.websphere.productInstallType=" + productInstallType + "\n");
        versions.append("com.ibm.websphere.productEdition=" + productEdition + "\n");

        File serverProps = new File(wlpDir, "lib/versions/WebSphereApplicationServer.properties");
        serverProps.getParentFile().mkdirs();
        FileOutputStream versionPropsOS = null;
        try {
            versionPropsOS = new FileOutputStream(serverProps);
            versionPropsOS.write(versions.toString().getBytes());
        } finally {
            if (versionPropsOS != null)
                versionPropsOS.close();
        }

        // Create some but not all of the required core features
        createFeature(new File(wlpDir, "lib/features/a-1.0.mf"), "com.ibm.websphere.appserver.a-1.0", "a-1.0", true);
        createFeature(new File(wlpDir, "lib/features/c-1.0.mf"), "com.ibm.websphere.appserver.c-1.0", null, false);

        // Create some Manifest attributes that we'll plug into the creation of the SelfExtractor.
        Attributes attrs = new Attributes();
        attrs.putValue("Applies-To", productID + "; productVersion=" + productVersion + "; productInstallType=" + productInstallType +
                                     "; productEdition=\"BASE,EXPRESS,ND," + productEdition + "\"");
        attrs.putValue("Archive-Root", "wlp/");
        attrs.putValue("Require-Feature", "com.ibm.websphere.appserver.a-1.0,com.ibm.websphere.appserver.b-1.0,com.ibm.websphere.appserver.c-1.0");

        //Create an instance of the SelfExtractor using just the necessary args to get the validate method to run the core feature checks.
        Constructor<SelfExtractor> constructor = SelfExtractor.class.getDeclaredConstructor(JarFile.class, LicenseProvider.class, Attributes.class);
        constructor.setAccessible(true);

        SelfExtractor extractor = constructor.newInstance(extendedJarFile, null, attrs);
        ReturnCode returnCode = extractor.validate(wlpDir);

        // Close the jar File.
        extendedJarFile.close();
        // Check that the error message we receive is as expected.
        Assert.assertTrue("Expected missing core feature com.ibm.websphere.appserver.b-1.0 not found in failure message: " + returnCode.getErrorMessage(),
                          returnCode.getErrorMessage().contains("is missing required features: [com.ibm.websphere.appserver.b-1.0]"));

    }

    /**
     * This method creates a mocked up extended jar which just contains a manifest with the Core Feature header.
     *
     * @param file - The file object for the manifest.
     * @param coreFeatures - The String value for the Require-Feature header.
     * @throws IOException
     */
    public static void createExtendedFile(File file, String coreFeatures) {

        JarOutputStream jos = null;
        FileOutputStream fos = null;

        try {
            // Create the file, if it doesn't already exist.
            if (!file.exists())
                file.createNewFile();

            fos = new FileOutputStream(file);
            jos = new JarOutputStream(fos);

            String manifestFileName = "META-INF/MANIFEST.MF";
            //Generate the manifest using a StringBuffer.
            StringBuffer buffer = new StringBuffer();
            buffer.append("Require-Feature: " + coreFeatures + "\n");

            // write out the jarEntry for the manifest.
            JarEntry jarEntry = new JarEntry(manifestFileName);
            jarEntry.setTime(System.currentTimeMillis());
            jos.putNextEntry(jarEntry);
            jos.write(buffer.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jos != null) {
                try {
                    jos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * This method writes out a feature manifest.
     *
     * @param feature - A file object representing the feature manifest to write to.
     * @param featureSymbolicName - The feature symbolic name
     * @param featureShortName - The feature short name.
     * @param directive - Whether we should have a directive on the symbolic Name.
     */
    private void createFeature(File feature, String featureSymbolicName, String featureShortName, boolean directive) {
        StringBuffer subsystemManifest = new StringBuffer();

        // Build the feature using a String buffer.
        subsystemManifest.append("Subsystem-ManifestVersion: 1\n");
        if (featureShortName != null)
            subsystemManifest.append("IBM-ShortName: " + featureShortName + "\n");
        subsystemManifest.append("Subsystem-SymbolicName: " + featureSymbolicName);
        if (directive)
            subsystemManifest.append("; visibility:=public");
        subsystemManifest.append("\n");
        subsystemManifest.append("Subsystem-Version: 1.0.0\n");

        // Create all parent dirs.
        feature.getParentFile().mkdirs();
        FileOutputStream featureOS = null;
        // Write out the StringBuffer to the file.
        try {
            featureOS = new FileOutputStream(feature);
            featureOS.write(subsystemManifest.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (featureOS != null) {
                try {
                    featureOS.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Verify additional --downloadDependencies flag is displayed when running SelfExtractor on a sample.
     */
    @Test
    public void testSampleHelp() throws Exception {
        if (disableTestSuite) {
            return;
        }

        execute(null, null, null, new String[] { "--help" }, EXIT_OK, dummySampleJar,
                find("--acceptLicense"),
                find("--verbose"),
                find("--viewLicenseAgreement"),
                find("--viewLicenseInfo"),
                find("--downloadDependencies"));
    }

    /**
     * Enter wlp dir at prompt, set user dir in server env, and accept the prompt for dependencies.
     */
    @Test
    public void testSampleExtractionWLPPromptServerEnvAcceptDeps() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirOne = new File(samplesTmpDir, "usrDirOne");

        setUserDirWithServerEnv(userDirOne, samplesWlpDir);
        execute(null, null, null, new String[0], EXIT_OK, dummySampleJar,
                findLiteral(formatMessage("extractInstruction", new Object[0])),
                //Point at what we just unpacked
                input(samplesWlpDir.getAbsolutePath()),
                //Accept Deps
                findLiteral("dummySample/libs/dummy-dep-1.0.jar"),
                //Do install external deps
                input("1"));

        File sampleReadmeLocation = new File(userDirOne, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirOne, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertTrue(sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertTrue(sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
    }

    /**
     * Enter wlp dir at prompt, set user dir in server env, and reject the prompt for dependencies
     */
    @Test
    public void testSampleExtractionRejectDeps() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirTwo = new File(samplesTmpDir, "usrDirTwo");

        setUserDirWithServerEnv(userDirTwo, samplesWlpDir);
        execute(null, null, null, new String[0], EXIT_OK, dummySampleJar,
                findLiteral(formatMessage("extractInstruction", new Object[0])),
                //Point at what we just unpacked
                input(samplesWlpDir.getAbsolutePath()),
                findLiteral("dummySample/libs/dummy-dep-1.0.jar"),
                //Reject deps
                input("2"));

        File sampleReadmeLocation = new File(userDirTwo, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirTwo, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertTrue(sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertFalse(sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
    }

    /**
     * Enter wlp dir at prompt, set user dir in server env, and set accept dependencies flag
     */
    @Test
    public void testSampleExtractionDepsFlagWLPPromptServerEnv() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirThree = new File(samplesTmpDir, "usrDirThree");

        setUserDirWithServerEnv(userDirThree, samplesWlpDir);
        execute(null, null, null, new String[] { "--downloadDependencies" }, EXIT_OK, dummySampleJar,
                findLiteral(formatMessage("extractInstruction", new Object[0])),
                //Point at what we just unpacked
                input(samplesWlpDir.getAbsolutePath()));

        File sampleReadmeLocation = new File(userDirThree, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirThree, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertTrue(sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertTrue(sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
    }

    /**
     * Enter wlp dir at prompt, take default user dir, and set accept dependencies flag.
     */
    @Test
    public void testSampleExtractionDepsFlagWLPPromptDefault() throws Exception {
        if (disableTestSuite) {
            return;
        }

        @SuppressWarnings("serial")
        Map<String, String> envvars = new HashMap<String, String>() {
            {
                put("WLP_USER_DIR", "");
            }
        };

        deleteServerEnvIfExists(samplesWlpDir);
        execute(null, null, envvars, new String[] { "--downloadDependencies" }, EXIT_OK, dummySampleJar,
                findLiteral(formatMessage("extractInstruction", new Object[0])),
                //Point at what we just unpacked
                input(samplesWlpDir.getAbsolutePath()));

        File sampleReadmeLocation = new File(samplesWlpDir, "usr/servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(samplesWlpDir, "usr/shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertTrue(sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertTrue(sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
    }

    /**
     * Set wlp dir flag, accept dependencies flag, and set user dir in server env.
     */
    @Test
    public void testSampleExtractionWLPDepsFlagServerEnv() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirFive = new File(samplesTmpDir, "usrDirFive");

        setUserDirWithServerEnv(userDirFive, samplesWlpDir);
        execute(null, null, null, new String[] { "--downloadDependencies", samplesWlpDir.getAbsolutePath() }, EXIT_OK, dummySampleJar,
                new JavaJarTest.Action[0]);

        File sampleReadmeLocation = new File(userDirFive, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirFive, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertTrue(sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertTrue(sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
    }

    /**
     * Set wlp dir flag, accept dependencies flag, and set user dir in environment variable.
     */
    @SuppressWarnings("serial")
    @Test
    public void testSampleExtractionEnvVar() throws Exception {
        if (disableTestSuite) {
            return;
        }

        final File userDirSix = new File(samplesTmpDir, "usrDirSix");
        deleteServerEnvIfExists(samplesWlpDir);

        Map<String, String> envvars = new HashMap<String, String>() {
            {
                put("WLP_USER_DIR", userDirSix.getAbsolutePath());
            }
        };

        execute(null, null, envvars, new String[] { "--downloadDependencies", samplesWlpDir.getAbsolutePath() }, EXIT_OK,
                dummySampleJar,
                new JavaJarTest.Action[0]);

        File sampleReadmeLocation = new File(userDirSix, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirSix, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertTrue(sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertTrue(sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
    }

    /**
     * Set wlp dir flag, accept dependencies flag, and set user dir in server env. Accept license at prompt.
     */
    @Test
    public void testSampleExtractionWithLicense() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirSeven = new File(samplesTmpDir, "usrDirSeven");
        setUserDirWithServerEnv(userDirSeven, samplesWlpDir);
        execute(null, null, null, new String[] { "--downloadDependencies", samplesWlpDir.getAbsolutePath() }, EXIT_OK, dummySampleLicenseJar,
                input("x", "x", "1"));

        File sampleReadmeLocation = new File(userDirSeven, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirSeven, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        File sampleLicensesLocation = new File(userDirSeven, "servers/dummySampleServer/lafiles/LA_en");
        Assert.assertTrue(sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertTrue(sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
        Assert.assertTrue(sampleLicensesLocation.getAbsolutePath(), sampleLicensesLocation.exists());
    }

    /**
     * Dependencies XML file contains a bad URL that will give connection refused, test we roll back.
     */
    @Test
    public void testSampleExtractionBadDepRollback() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirEight = new File(samplesTmpDir, "usrDirEight");

        setUserDirWithServerEnv(userDirEight, samplesWlpDir);
        execute(null, null, null, new String[] { "--downloadDependencies" }, EXIT_EXTRACT_ERROR, dummySampleFileErrorJar,
                findLiteral(formatMessage("extractInstruction", new Object[0])),
                //Point at what we just unpacked
                input(samplesWlpDir.getAbsolutePath()));

        File sampleReadmeLocation = new File(userDirEight, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirEight, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertFalse("README.html should not exist - we rolled back: " + sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertFalse("dummy-dep-1.0.jar should not exist - we rolled back: " + sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
    }

    @Test
    public void testAddonInstallerDefaultsToValidCWD() throws Exception {
        if (disableTestSuite) {
            return;
        }
        //CWD is samplesWlpDir, which is our clean WLP unpack
        execute(samplesWlpDir, null, null, new String[0], EXIT_OK, dummyInstallerExtendedEmpty,
                input(""),
                find(formatMessage("extractDirectory", new String[] { Pattern.quote(samplesWlpDir.getAbsolutePath()) })));
    }

    @Test
    public void testAddonInstallerDefaultsToValidWlpSubDir() throws Exception {
        if (disableTestSuite) {
            return;
        }
        //CWD is samplesTmpDir, which is one level above our clean WLP unpack called 'wlp'
        execute(samplesTmpDir, null, null, new String[0], EXIT_OK, dummyInstallerExtendedEmpty,
                input(""),
                find(formatMessage("extractDirectory", new String[] { Pattern.quote(samplesWlpDir.getAbsolutePath()) })));
    }

    @Test
    public void testAddonInstallerAcceptsValidDirectory() throws Exception {
        if (disableTestSuite) {
            return;
        }
        //Pass in samplesWlpDir on command line, which is our clean WLP unpack
        execute(null, null, null, new String[] { samplesWlpDir.getAbsolutePath() }, EXIT_OK, dummyInstallerExtendedEmpty,
                find(formatMessage("extractDirectory", new String[] { Pattern.quote(samplesWlpDir.getAbsolutePath()) })));
    }

    @Test
    public void testAddonInstallerAcceptsDirectoryWithValidWlpSubdir() throws Exception {
        if (disableTestSuite) {
            return;
        }
        //Pass in samplesTmpDir on command line, which is one level above our clean WLP unpack called 'wlp'
        execute(null, null, null, new String[] { samplesTmpDir.getAbsolutePath() }, EXIT_OK, dummyInstallerExtendedEmpty,
                find(formatMessage("extractDirectory", new String[] { Pattern.quote(samplesWlpDir.getAbsolutePath()) })));
    }

    @Test
    public void testAddonInstallerAcceptsRelativeDirectoryWithValidWlpSubdir() throws Exception {
        if (disableTestSuite) {
            return;
        }
        //Pass in samplesTmpDir on command line, which is one level above our clean WLP unpack called 'wlp'
        execute(buildUnittestDir, null, null, new String[] { "samplestmp" }, EXIT_OK, dummyInstallerExtendedEmpty,
                find(formatMessage("extractDirectory", new String[] { Pattern.quote(samplesWlpDir.getAbsolutePath()) })));
    }

    @Test
    public void testAddonInstallerAcceptsDirectoryWithValidWlpSubdirAtPrompt() throws Exception {
        if (disableTestSuite) {
            return;
        }
        //Pass in samplesTmpDir interactively, which is one level above our clean WLP unpack called 'wlp'
        execute(null, null, null, new String[0], EXIT_OK, dummyInstallerExtendedEmpty,
                findLiteral(formatMessage("extractInstruction", new Object[0])),
                input(samplesTmpDir.getAbsolutePath()),
                find(formatMessage("extractDirectory", new String[] { Pattern.quote(samplesWlpDir.getAbsolutePath()) })));
    }

    @Test
    public void testAddonInstallerAcceptsRelativeDirectoryWithValidWlpSubdirAtPrompt() throws Exception {
        if (disableTestSuite) {
            return;
        }
        //Pass in samplesTmpDir interactively, which is one level above our clean WLP unpack called 'wlp'
        execute(buildUnittestDir, null, null, new String[0], EXIT_OK, dummyInstallerExtendedEmpty,
                findLiteral(formatMessage("extractInstruction", new Object[0])),
                input("samplestmp"),
                find(formatMessage("extractDirectory", new String[] { Pattern.quote(samplesWlpDir.getAbsolutePath()) })));
    }

    @Test
    public void testAddonInstallerAcceptsValidDirectoryAtPrompt() throws Exception {
        if (disableTestSuite) {
            return;
        }
        //Pass in samplesWlpDir interactively, which is our clean WLP unpack
        execute(null, null, null, new String[0], EXIT_OK, dummyInstallerExtendedEmpty,
                findLiteral(formatMessage("extractInstruction", new Object[0])),
                input(samplesWlpDir.getAbsolutePath()),
                find(formatMessage("extractDirectory", new String[] { Pattern.quote(samplesWlpDir.getAbsolutePath()) })));
    }

    /**
     * Test that if an IOException is thrown in the extract phase, we roll back any files already extracted, any directories created,
     * and in the case of a sample, do the same for any external dependencies that were downloaded.
     */
    @Test
    public void testExtractRollbackIntoNonexistent() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirNine = new File(samplesTmpDir, "usrDirNine");

        setUserDirWithServerEnv(userDirNine, samplesWlpDir);

        // Write the WebSphereApplicationServer.properties file in the lib/versions dir.
        String productID = "com.ibm.websphere.appserver";

        // Create some Manifest attributes that we'll plug into the creation of the SelfExtractor.
        Attributes attrs = new Attributes();
        attrs.putValue("Applies-To", productID);
        attrs.putValue("Archive-Root", "wlp/usr/");
        attrs.putValue("Main-Class", "wlp.lib.extract.SelfExtract");
        attrs.putValue("Archive-Content-Type", "sample");

        JarFile sampleJarFile = new ErroringJarfile(dummySampleJar);

        Constructor<SelfExtractor> constructor = SelfExtractor.class.getDeclaredConstructor(JarFile.class, LicenseProvider.class, Attributes.class);
        constructor.setAccessible(true);

        SelfExtractor extractor = constructor.newInstance(sampleJarFile, null, attrs);
        ReturnCode returnCode = extractor.extract(samplesWlpDir, null);

        File sampleReadmeLocation = new File(userDirNine, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirNine, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertFalse("Sample readme file (extracted) should have been rolled back: " + sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertFalse("Sample deps file (downloaded) should have been rolled back: " + sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
        Assert.assertFalse("A new user dir was created, and should have been rolled back: " + userDirNine.getAbsolutePath(), userDirNine.exists());
        Assert.assertEquals("Expected return code should indicate bad output and rollback", ReturnCode.BAD_OUTPUT, returnCode.getCode());
    }

    /**
     * Test that if an IOException is thrown in the extract phase, we roll back any files already extracted, any directories created,
     * and in the case of a sample, do the same for any external dependencies that were downloaded. For this test, the user directory
     * already exists, as does the shared/resources subfolder, so we should NOT delete them, unlike the test above.
     */
    @Test
    public void testExtractRollbackLeavesExistingParentDirectory() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirTen = new File(samplesTmpDir, "usrDirTen");
        setUserDirWithServerEnv(userDirTen, samplesWlpDir);
        File sharedResourcesFolder = new File(userDirTen, "usr/shared/resources/");
        sharedResourcesFolder.mkdirs();
        File dummyLibsFolder = new File(sharedResourcesFolder, "dummylibs");

        // Write the WebSphereApplicationServer.properties file in the lib/versions dir.
        String productID = "com.ibm.websphere.appserver";

        // Create some Manifest attributes that we'll plug into the creation of the SelfExtractor.
        Attributes attrs = new Attributes();
        attrs.putValue("Applies-To", productID);
        attrs.putValue("Archive-Root", "wlp/usr/");
        attrs.putValue("Main-Class", "wlp.lib.extract.SelfExtract");
        attrs.putValue("Archive-Content-Type", "sample");

        JarFile sampleJarFile = new ErroringJarfile(dummySampleJar);

        Constructor<SelfExtractor> constructor = SelfExtractor.class.getDeclaredConstructor(JarFile.class, LicenseProvider.class, Attributes.class);
        constructor.setAccessible(true);

        SelfExtractor extractor = constructor.newInstance(sampleJarFile, null, attrs);
        ReturnCode returnCode = extractor.extract(samplesWlpDir, null);

        File sampleReadmeLocation = new File(userDirTen, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirTen, "shared/resources/dummylibs/dummy-dep-1.0.jar");
        Assert.assertFalse("Sample readme file (extacted) should have been rolled back: " + sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertFalse("Sample deps file (downloaded) should have been rolled back: " + sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());
        Assert.assertTrue("The usr/shared dir already existed, so should NOT have been deleted during rollback: " + sharedResourcesFolder.getAbsolutePath(),
                          sharedResourcesFolder.exists());
        Assert.assertFalse("The usr/shared/dummylibs folder was created, so SHOULD have been deleted during rollback: " + dummyLibsFolder.getAbsolutePath(),
                           dummyLibsFolder.exists());
        Assert.assertEquals("Expected return code should indicate bad output and rollback", ReturnCode.BAD_OUTPUT, returnCode.getCode());
    }

    /**
     * Test that if an IOException is thrown in the extract phase, we roll back any files already extracted, any directories created,
     * and in the case of a sample, do the same for any external dependencies that were downloaded.
     *
     * In this case, the entire installation has already been run, so the rollback should NOT remove any files, as they all already existed.
     */
    @Test
    public void testExtractRollbackWhenEverythingExists() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File userDirEleven = new File(samplesTmpDir, "usrDirEleven");
        setUserDirWithServerEnv(userDirEleven, samplesWlpDir);
        File sampleReadmeLocation = new File(userDirEleven, "servers/dummySampleServer/README.html");
        File sampleDummyLibLocation = new File(userDirEleven, "shared/resources/dummylibs/dummy-dep-1.0.jar");

        //First, run the standard install without a rollback
        execute(null, null, null, new String[] { "--downloadDependencies", samplesWlpDir.getAbsolutePath() }, EXIT_OK, dummySampleJar,
                new JavaJarTest.Action[0]);

        Assert.assertTrue("First pass install should place readme onto disk: " + sampleReadmeLocation.getAbsolutePath(), sampleReadmeLocation.exists());
        Assert.assertTrue("First pass install should pull dummylib dependency onto disk: " + sampleDummyLibLocation.getAbsolutePath(), sampleDummyLibLocation.exists());

        //Now, run the installer containing the same files that causes a rollback during the download phase
        //- make sure we don't wipe out what was already there.

        execute(null, null, null, new String[] { "--downloadDependencies", samplesWlpDir.getAbsolutePath() }, EXIT_EXTRACT_ERROR, dummySampleFileErrorJar,
                new JavaJarTest.Action[0]);

        Assert.assertTrue("User dir already existed, and should NOT have been rolled back: " + userDirEleven.getAbsolutePath(), userDirEleven.exists());
        Assert.assertTrue("Sample readme file (extracted) was already on disk, and should NOT have been rolled back: " + sampleReadmeLocation.getAbsolutePath(),
                          sampleReadmeLocation.exists());
        Assert.assertTrue("Sample deps file (downloaded) was already on disk, and should NOT have been rolled back: " + sampleDummyLibLocation.getAbsolutePath(),
                          sampleDummyLibLocation.exists());
    }

    private File[] getTagFileNames(File workDir, WlpJarType jarType, String version) {

        String editionSuffix = "";
        if (jarType.equals(WlpJarType.CORE) || jarType.equals(WlpJarType.CORE_ALL) || jarType.equals(WlpJarType.CORE_LIC)) {
            editionSuffix = "_Core";
        } else if (jarType.equals(WlpJarType.DEVELOPERS_IPLA_ALL)) {
            editionSuffix = "_for_Developers";
        } else if (jarType.equals(WlpJarType.ND) || jarType.equals(WlpJarType.ND_ALL) || jarType.equals(WlpJarType.ND_LIC)) {
            editionSuffix = "_Network_Deployment";
        } else if (jarType.equals(WlpJarType.BASE) || jarType.equals(WlpJarType.BASE_ALL) || jarType.equals(WlpJarType.BASE_LIC)) {
            editionSuffix = "";
        } else {
            return null;
        }

        File[] tagFiles = new File[2];
        String baseversion = getBaseVersionFromFullVersion(version);
        String tagFiles0 = "wlp/lib/versions/tags/ibm.com_WebSphere_Application_Server_Liberty" + editionSuffix + "-" + baseversion + ".swidtag";
        String tagFiles1 = "wlp/lib/versions/tags/ibm.com_WebSphere_Application_Server_Liberty" + editionSuffix + "-" + version + ".swidtag";
        tagFiles[0] = new File(workDir, tagFiles0);
        tagFiles[1] = new File(workDir, tagFiles1);

        Assert.assertTrue(tagFiles[0].getAbsolutePath() + "doesn't exist", tagFiles[0].exists());
        return tagFiles;
    }

    private String getLicenseFileFolders(WlpJarType jarType) {
        if (jarType.equals(WlpJarType.CORE) || jarType.equals(WlpJarType.CORE_ALL) || jarType.equals(WlpJarType.CORE_LIC)) {
            return samplesCoreDir + "/wlp/lafiles";
        } else if (jarType.equals(WlpJarType.DEVELOPERS_IPLA_ALL)) {
            return samplesNdLicDir + "/wlp/lafiles";
        } else if (jarType.equals(WlpJarType.ND) || jarType.equals(WlpJarType.ND_ALL) || jarType.equals(WlpJarType.ND_LIC)) {
            return samplesNdLicDir + "/wlp/lafiles";
        } else if (jarType.equals(WlpJarType.BASE) || jarType.equals(WlpJarType.BASE_ALL) || jarType.equals(WlpJarType.BASE_LIC)) {
            return samplesBaseDir + "/wlp/lafiles";
        } else
            return samplesTmpDir + "/wlp/lafiles";
    }

    private void testLegalUpgradeEditionHelper(WlpJarType installationJar, WlpJarType licenseJar, String originalEdition, String targetEdition) throws Exception {

        if (disableTestSuite) {
            return;
        }
        if (samplesLicenseUpgradeDir.exists() && samplesLicenseUpgradeDir.listFiles().length > 0)
            deleteDir(samplesLicenseUpgradeDir);
        Assert.assertTrue("mkdir -p " + samplesLicenseUpgradeDir.getAbsolutePath(), samplesLicenseUpgradeDir.mkdirs() || samplesLicenseUpgradeDir.isDirectory());
        extractEditionJar(installationJar, samplesLicenseUpgradeDir);
        //Check if Properties file is BASE edition
        File installedProperties = new File(samplesLicenseUpgradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(installedProperties.getAbsolutePath(), installedProperties.exists());
        String edition_before = getWASProperty("com.ibm.websphere.productEdition", installedProperties);
        Assert.assertEquals("Expected edition should be " + originalEdition, originalEdition, edition_before);
        File featuresDir = new File(samplesLicenseUpgradeDir, "wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behaviour across all platforms
        String version = getWASProperty("com.ibm.websphere.productVersion", installedProperties);

        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        File[] tagFiles = getTagFileNames(samplesLicenseUpgradeDir, installationJar, version);
        if (tagFiles != null) {
            // Check if fixpack tags are the base version
            File originalSwTag = tagFiles[0];
            File originalFxTag = tagFiles[1];

            String fixVersionBefore = getWASTagCD("version", originalFxTag);
            String revisionBefore = getWASTagCD("revision", originalFxTag);
            Assert.assertEquals("revision and version should report the same version number", revisionBefore, fixVersionBefore);
            // Check if license files are the base version
            Assert.assertTrue(compareDirectory(samplesLicenseUpgradeDir + "/wlp/lafiles", getLicenseFileFolders(installationJar)));
            // Attempt to perform the license upgrade
            execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                    input("x", "x", "1", samplesLicenseUpgradeDir.getAbsolutePath() + "/wlp"));
            // Check if fixpack tags have been updated, including changing the fixpack number
            File[] tagFilesNew = getTagFileNames(samplesLicenseUpgradeDir, licenseJar, version);
            File newSwtag = tagFilesNew[0];
            File newfxTag = tagFilesNew[1];
            Assert.assertTrue(newSwtag.getAbsolutePath(), newSwtag.exists());
            Assert.assertTrue(newfxTag.getAbsolutePath(), newfxTag.exists());
            String fixVersionAfter = getWASTagCD("version", newfxTag);
            String revisionAfter = getWASTagCD("revision", newfxTag);
            System.out.println("Fix Verson After:" + fixVersionAfter);
            System.out.println("Revision After:" + revisionAfter);
            Assert.assertEquals("The fixpack number is different from the original installation", fixVersionAfter, fixVersionBefore);
            Assert.assertEquals("revision and FixVersion should report the same version number after", revisionAfter, fixVersionAfter);
            Assert.assertFalse("Original Edition fixpack tag still exists", originalFxTag.exists());
            Assert.assertFalse("Original Edition software tag still exists", originalSwTag.exists());
        } else {
            // Check if license files are the base version
            Assert.assertTrue(compareDirectory(samplesLicenseUpgradeDir + "/wlp/lafiles", getLicenseFileFolders(installationJar)));
            // Attempt to perform the license upgrade
            execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                    input("x", "x", "1", samplesLicenseUpgradeDir.getAbsolutePath() + "/wlp"));
            // Check if fixpack tags have been updated, including changing the fixpack number
            File[] tagFilesNew = getTagFileNames(samplesLicenseUpgradeDir, licenseJar, version);
            File newSwtag = tagFilesNew[0];
            File newfxTag = tagFilesNew[1];
            Assert.assertTrue(newSwtag.getAbsolutePath(), newSwtag.exists());
            Assert.assertTrue(newfxTag.getAbsolutePath(), newfxTag.exists());
        }

        /*
         * Keep this piece of code for future debugging purpose.
         * Attributes attrs = new Attributes();
         * attrs.putValue("Applies-To", "com.ibm.websphere.appserver; productVersion=16.0.0; productInstallType=Archive;");
         * attrs.putValue("Archive-Root", "wlp/");
         * attrs.putValue("Main-Class", "wlp.lib.extract.SelfExtract");
         * attrs.putValue("Archive-Content-Type", "license");
         * attrs.putValue("License-Agreement", "wlp/lafiles/LA");
         * attrs.putValue("License-Information", "wlp/lafiles/LI");
         * attrs.putValue("Main-Class", "wlp.lib.extract.SelfExtract");
         *
         * JarFile ndLicenseJar = new JarFile(getWLPJar(WlpJarType.ND_LIC));
         * Constructor<LicenseSelfExtractor> constructor = LicenseSelfExtractor.class.getDeclaredConstructor(JarFile.class, LicenseProvider.class, Attributes.class);
         * LicenseSelfExtractor extractor = constructor.newInstance(ndLicenseJar, null, attrs);
         * ListBasedExtractProgress monitor = new ListBasedExtractProgress();
         * ReturnCode returnCode = extractor.extract(new File(samplesBaseUpgradeDir.getAbsolutePath() + "/wlp"), monitor);
         */

        // Check if Properties files is upgraded to ND edition
        // Point to new file
        File upgradedProperties = new File(samplesLicenseUpgradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(upgradedProperties.getAbsolutePath(), upgradedProperties.exists());
        String edition_after = getWASProperty("com.ibm.websphere.productEdition", upgradedProperties);
        Assert.assertEquals("Expected edition should be ND", targetEdition, edition_after);
        // Check if license files have been replaced
        Assert.assertTrue(compareDirectory(samplesLicenseUpgradeDir + "/wlp/lafiles", getLicenseFileFolders(licenseJar)));
        deleteDir(samplesLicenseUpgradeDir);
    }

    @Test
    public void testLegalUpgradeEdition() throws Exception {
        testLegalUpgradeEditionHelper(WlpJarType.BASE, WlpJarType.ND_LIC, "BASE", "ND");
        testLegalUpgradeEditionHelper(WlpJarType.BASE_ALL, WlpJarType.ND_LIC, "BASE", "ND");
        if (runCoreTest) {
            testLegalUpgradeEditionHelper(WlpJarType.CORE, WlpJarType.ND_LIC, "LIBERTY_CORE", "ND");
            testLegalUpgradeEditionHelper(WlpJarType.CORE, WlpJarType.BASE_LIC, "LIBERTY_CORE", "BASE");
            testLegalUpgradeEditionHelper(WlpJarType.CORE_ALL, WlpJarType.ND_LIC, "LIBERTY_CORE", "ND");
            testLegalUpgradeEditionHelper(WlpJarType.CORE_ALL, WlpJarType.BASE_LIC, "LIBERTY_CORE", "BASE");
        }
    }

    @Test
    public void testIllegalUpgradeEditionWithFeatures() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File featuresDir = new File(samplesBaseUpgradeWithFeatureDir + "/wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behaviour across all platforms
        File manifestToUpdate = manifestFiles[0];

        File propertiesFile = new File(samplesNdLicDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        String version = getWASProperty("com.ibm.websphere.productVersion", propertiesFile);
        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }

        // Modify the IBM-AppliesTo property of a feature so that it applies to a different edition than the upgrade
        setManifestProperty("IBM-AppliesTo",
                            "com.ibm.websphere.appserver; productVersion=" + version + "; productInstallType=Archive; productEdition=\"LIBERTY_CORE,BASE,BASE_ILAN\"",
                            manifestToUpdate);

        // Test that the upgrade is blocked
        execute(null, null, null, WlpJarType.ND_LIC, null, new String[0], EXIT_EXTRACT_ERROR,
                input("x", "x", "1", samplesBaseUpgradeWithFeatureDir.getAbsolutePath() + "/wlp"));

        // Modify the IBM-AppliesTo property of a feature so that it applies to a different version than the upgrade
        setManifestProperty("IBM-AppliesTo", "com.ibm.websphere.appserver; productVersion=8.1.1.1; productInstallType=Archive; productEdition=\"LIBERTY_CORE,BASE,BASE_ILAN,ND\"",
                            manifestToUpdate);

        // Test that the upgrade should not be blocked since the upgrade jar is now version insensitive.
        execute(null, null, null, WlpJarType.ND_LIC, null, new String[0], EXIT_OK,
                input("x", "x", "1", samplesBaseUpgradeWithFeatureDir.getAbsolutePath() + "/wlp"));
    }

    @Test
    public void testWebProfile7LegalUpgradeEdition() throws Exception {
        if (disableTestSuite) {
            return;
        }

        String v = System.getProperty("version", "");
        File f = new File("../build.image/wlp/lib/versions/WebSphereApplicationServer.properties");
        if (f.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileReader(f));
                v = props.getProperty("com.ibm.websphere.productVersion");
            } catch (IOException e) {
            }
        }

        // Extract webProfile7 zip and create wlp/lafiles and wlp/lib/versions/tags directories
        File zipfile = null;
        File[] files = outputUploadDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (file.isFile() && name.startsWith("wlp-webProfile7-" + v) && name.endsWith(".zip")) {
                    zipfile = file;
                }
            }
        }

        Assert.assertTrue("Failed to extract webProfile 7 zip", unzip(zipfile, samplesWP7UpgradeDir));
        File lafilesDir = new File(samplesWP7UpgradeDir + "/wlp/lafiles");
        File tagsDir = new File(samplesWP7UpgradeDir + "/wlp/lib/versions/tags");

        if (!lafilesDir.exists()) {
            lafilesDir.mkdir();
        }
        if (!tagsDir.exists()) {
            tagsDir.mkdir();
        }

        //Check if Properties file is webProfile7 edition
        File propertiesWP7Edition = new File(samplesWP7UpgradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(propertiesWP7Edition.getAbsolutePath(), propertiesWP7Edition.exists());

        String edition_before = getWASProperty("com.ibm.websphere.productEdition", propertiesWP7Edition);
        Assert.assertEquals("Expected edition should be BASE_ILAN", "BASE_ILAN", edition_before);

        File featuresDir = new File(samplesWP7UpgradeDir, "wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behaviour across all platforms

        String version = getWASProperty("com.ibm.websphere.productVersion", propertiesWP7Edition);
        String baseVersion = getBaseVersionFromFullVersion(version);
        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }

        // Attempt to perform the license upgrade
        execute(null, null, null, WlpJarType.BASE_LIC, null, new String[0], EXIT_OK,
                input("x", "x", "1", samplesWP7UpgradeDir.getAbsolutePath() + "/wlp"));

        // Check if Properties files is changed to base edition
        // Point to new file
        File propertiesBaseEdition = new File(samplesWP7UpgradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(propertiesBaseEdition.getAbsolutePath(), propertiesBaseEdition.exists());

        String edition_after = getWASProperty("com.ibm.websphere.productEdition", propertiesBaseEdition);
        Assert.assertEquals("Expected edition should be BASE", "BASE", edition_after);

        // Check if fixpack tags have been extracted
        File swtagBaseEdition = new File(samplesWP7UpgradeDir, String.format("wlp/lib/versions/tags/ibm.com_WebSphere_Application_Server_Liberty-%s.swidtag", baseVersion));
        File fxtagBaseEdition = new File(samplesWP7UpgradeDir, String.format("wlp/lib/versions/tags/ibm.com_WebSphere_Application_Server_Liberty-%s.swidtag", version));

        Assert.assertTrue(swtagBaseEdition.getAbsolutePath(), swtagBaseEdition.exists());
        Assert.assertTrue(fxtagBaseEdition.getAbsolutePath(), fxtagBaseEdition.exists());

        // Check if license files have been extracted
        Assert.assertTrue(compareDirectory(samplesBaseDir + "/wlp/lafiles", samplesWP7UpgradeDir + "/wlp/lafiles"));

    }

    @Test
    public void testLegalUpgradeEditionWithFeatures() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File featuresDir = new File(samplesBaseUpgradeWithFeatureDir + "/wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behaviour across all platforms
        File manifestToUpdate = manifestFiles[0];

        File propertiesFile = new File(samplesNdLicDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        String version = getWASProperty("com.ibm.websphere.productVersion", propertiesFile);
        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }

        System.out.println("Loaded version from wlp/lib/versions/WebSphereApplicationServer.properties: " + version);

        // Modify the IBM-AppliesTo property of a feature so that it applies to the version/edition we are trying to upgrade to
        setManifestProperty("IBM-AppliesTo", "com.ibm.websphere.appserver; productVersion=" + version
                                             + "; productInstallType=Archive; productEdition=\"LIBERTY_CORE,BASE,BASE_ILAN,ND\"",
                            manifestToUpdate);

        // Test that the upgrade goes through
        execute(null, null, null, WlpJarType.ND_LIC, null, new String[0], EXIT_OK,
                input("x", "x", "1", samplesBaseUpgradeWithFeatureDir.getAbsolutePath() + "/wlp"));
    }

    private void testLegalDowngradeEditionHelper(WlpJarType installationJar, WlpJarType licenseJar, String originalEdition, String targetEdition,
                                                 int expectedExit) throws Exception {

        if (disableTestSuite) {
            return;
        }
        if (samplesLicenseDowngradeDir.exists() && samplesLicenseDowngradeDir.listFiles().length > 0)
            deleteDir(samplesLicenseDowngradeDir);
        Assert.assertTrue("mkdir -p " + samplesLicenseDowngradeDir.getAbsolutePath(), samplesLicenseDowngradeDir.mkdirs() || samplesLicenseDowngradeDir.isDirectory());
        extractEditionJar(installationJar, samplesLicenseDowngradeDir);
        //Check if Properties file is original edition
        File installedProperties = new File(samplesLicenseDowngradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(installedProperties.getAbsolutePath(), installedProperties.exists());
        String edition_before = getWASProperty("com.ibm.websphere.productEdition", installedProperties);
        Assert.assertEquals("Expected edition should be " + originalEdition, originalEdition, edition_before);
        File featuresDir = new File(samplesLicenseDowngradeDir, "wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behaviour across all platforms
        String version = getWASProperty("com.ibm.websphere.productVersion", installedProperties);
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }
        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        File[] tagFiles = getTagFileNames(samplesLicenseDowngradeDir, installationJar, version);

        // Check if fixpack tags are the base version
        File originalSwTag = tagFiles[0];
        File originalFxTag = tagFiles[1];

        String fixVersionBefore = getWASTagCD("version", originalFxTag);
        String revisionBefore = getWASTagCD("revision", originalFxTag);
        Assert.assertEquals("revision and version should report the same version number", revisionBefore, fixVersionBefore);
        // Check if license files are the base version
        Assert.assertTrue(compareDirectory(samplesLicenseDowngradeDir + "/wlp/lafiles", getLicenseFileFolders(installationJar)));
        // Attempt to perform the license upgrade
        if (expectedExit == EXIT_OK) {
            execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                    input("x", "x", "1", samplesLicenseDowngradeDir.getAbsolutePath() + "/wlp"));
            File[] tagFilesNew = getTagFileNames(samplesLicenseDowngradeDir, licenseJar, version);
            File newSwtag = tagFilesNew[0];
            File newfxTag = tagFilesNew[1];
            Assert.assertTrue(newSwtag.getAbsolutePath(), newSwtag.exists());
            Assert.assertTrue(newfxTag.getAbsolutePath(), newfxTag.exists());
            String fixVersionAfter = getWASTagCD("version", newfxTag);
            String revisionAfter = getWASTagCD("revision", newfxTag);
            System.out.println("Fix Verson After:" + fixVersionAfter);
            System.out.println("Revision After:" + revisionAfter);
            Assert.assertEquals("The fixpack number is different from the original installation", fixVersionAfter, fixVersionBefore);
            Assert.assertEquals("revision and FixVersion should report the same version number after", revisionAfter, fixVersionAfter);
            Assert.assertFalse("Original Edition fixpack tag still exists", originalFxTag.exists());
            Assert.assertFalse("Original Edition software tag still exists", originalSwTag.exists());
            return;
        } else if (expectedExit == NOT_APPLICABLE_FEATURE) {
            Collection<String> output = execute(null, null, null, licenseJar, null, new String[0], NOT_APPLICABLE_FEATURE,
                                                input("x", "x", "1", samplesLicenseDowngradeDir.getAbsolutePath() + "/wlp"),
                                                find("Use the following command to uninstall these features before applying the license"));
            File toolsEdition = new File(samplesLicenseDowngradeDir, "wlp/bin/tools");
            File toolsinstallUtility = new File(samplesLicenseDowngradeDir, "wlp/bin/tools/installUtility.jar");
            String out = output.toString();
            int startpoint = out.lastIndexOf("uninstall");
            int endpoint = out.lastIndexOf("\"");
            String command = out.substring(startpoint, endpoint);
            execute(toolsEdition, null, null,
                    new String[] { command },
                    EXIT_OK, toolsinstallUtility, input(""));
            execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                    input("x", "x", "1", samplesLicenseDowngradeDir.getAbsolutePath() + "/wlp"));
            return;
        }
    }

    private void testLegalDowngradeZipHelper(String zip, WlpJarType licenseJar, String originalEdition, String targetEdition, int expectedExit) throws Exception {
        // Extract ILAN zip
        if (samplesILANDowngradeDir.exists() && samplesILANDowngradeDir.listFiles().length > 0)
            deleteDir(samplesILANDowngradeDir);
        Assert.assertTrue("mkdir -p " + samplesILANDowngradeDir.getAbsolutePath(), samplesILANDowngradeDir.mkdirs() || samplesILANDowngradeDir.isDirectory());

        File zipfile = null;
        File[] files = outputUploadDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (file.isFile() && name.startsWith(zip) && name.endsWith(".zip")) {
                    zipfile = file;
                }
            }
        }
        Assert.assertTrue("Failed to extract ILAN zip", unzip(zipfile, samplesILANDowngradeDir));

        //Check if Properties file is webProfile7 edition
        File propertiesEdition = new File(samplesILANDowngradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(propertiesEdition.getAbsolutePath(), propertiesEdition.exists());

        String edition_before = getWASProperty("com.ibm.websphere.productEdition", propertiesEdition);
        Assert.assertEquals("Expected edition should be" + originalEdition, originalEdition, edition_before);

        File featuresDir = new File(samplesILANDowngradeDir, "wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behavior across all platforms

        String version = getWASProperty("com.ibm.websphere.productVersion", propertiesEdition);

        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }
        if (expectedExit == EXIT_OK) {
            execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                    input("x", "x", "1", samplesILANDowngradeDir.getAbsolutePath() + "/wlp"));
        } else if (expectedExit == NOT_APPLICABLE_FEATURE) {
            Collection<String> output = execute(null, null, null, licenseJar, null, new String[0], NOT_APPLICABLE_FEATURE,
                                                input("x", "x", "1", samplesILANDowngradeDir.getAbsolutePath() + "/wlp"),
                                                find("Use the following command to uninstall these features before applying the license"));
            File toolsEdition = new File(samplesILANDowngradeDir, "wlp/bin/tools");
            File toolsinstallUtility = new File(samplesILANDowngradeDir, "wlp/bin/tools/installUtility.jar");
            String out = output.toString();
            int startpoint = out.lastIndexOf("uninstall");
            int endpoint = out.lastIndexOf("\"");
            String command = out.substring(startpoint, endpoint);
            execute(toolsEdition, null, null,
                    new String[] { command },
                    EXIT_OK, toolsinstallUtility, input(""));
            execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                    input("x", "x", "1", samplesILANDowngradeDir.getAbsolutePath() + "/wlp"));
        }
        deleteDir(samplesILANDowngradeDir);
        return;
    }

    @Test
    public void testDowngradeEdition() throws Exception {
        testLegalDowngradeEditionHelper(WlpJarType.ND, WlpJarType.BASE_LIC, "ND", "BASE", NOT_APPLICABLE_FEATURE);
        if (runCoreTest) {
            //in this case, testLegalUpgradeEditionHelper can be used here
            testLegalDowngradeEditionHelper(WlpJarType.ND, WlpJarType.CORE_LIC, "ND", "LIBERTY_CORE", NOT_APPLICABLE_FEATURE);
            testLegalDowngradeEditionHelper(WlpJarType.BASE, WlpJarType.CORE_LIC, "BASE", "LIBERTY_CORE", EXIT_OK);
            testLegalDowngradeZipHelper("wlp-javaee7-", WlpJarType.CORE_LIC, "BASE_ILAN", "LIBERTY_CORE", NOT_APPLICABLE_FEATURE);
            testLegalDowngradeZipHelper("wlp-webProfile7-", WlpJarType.CORE_LIC, "BASE_ILAN", "LIBERTY_CORE", EXIT_OK);
        }

    }

    @Test
    public void testIllegalDowngrdeEditionWithFeatures() throws Exception {
        if (disableTestSuite) {
            return;
        }

        File featuresDir = new File(samplesNdDowngradeWithFeatureDir + "/wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behaviour across all platforms
        File manifestToUpdate = manifestFiles[0];

        File propertiesFile = new File(samplesBaseLicDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        String version = getWASProperty("com.ibm.websphere.productVersion", propertiesFile);
        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }

        // Modify the IBM-AppliesTo property of a feature so that it applies to a different edition than the upgrade
        setManifestProperty("IBM-AppliesTo",
                            "com.ibm.websphere.appserver; productVersion=" + version + "; productInstallType=Archive; productEdition=\"LIBERTY_CORE,BASE_ILAN,ND\"",
                            manifestToUpdate);

        // Test that the downgrade is blocked
        execute(null, null, null, WlpJarType.BASE_LIC, null, new String[0], EXIT_EXTRACT_ERROR,
                input("x", "x", "1", samplesNdDowngradeWithFeatureDir.getAbsolutePath() + "/wlp"), find("Use the following command to uninstall these features"));

        // Modify the IBM-AppliesTo property of a feature so that it applies to a different version than the upgrade
        setManifestProperty("IBM-AppliesTo", "com.ibm.websphere.appserver; productVersion=8.1.1.1; productInstallType=Archive; productEdition=\"LIBERTY_CORE,BASE,BASE_ILAN,ND\"",
                            manifestToUpdate);

        // Test that the downgrade should not be blocked since the upgrade jar is now version insensitive.
        execute(null, null, null, WlpJarType.BASE_LIC, null, new String[0], EXIT_OK,
                input("x", "x", "1", samplesNdDowngradeWithFeatureDir.getAbsolutePath() + "/wlp"));
    }

    private void testDowngradeWithBundleHelper(WlpJarType installationJar, WlpJarType licenseJar, String originalEdition, String targetEdition, int expectedExit) throws Exception {
        if (samplesBundleDowngradeDir.exists() && samplesBundleDowngradeDir.listFiles().length > 0)
            deleteDir(samplesBundleDowngradeDir);
        Assert.assertTrue("mkdir -p " + samplesBundleDowngradeDir.getAbsolutePath(), samplesBundleDowngradeDir.mkdirs() || samplesBundleDowngradeDir.isDirectory());
        extractEditionJar(installationJar, samplesBundleDowngradeDir);
        //Check if Properties file is BASE edition
        File installedProperties = new File(samplesBundleDowngradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(installedProperties.getAbsolutePath(), installedProperties.exists());
        String edition_before = getWASProperty("com.ibm.websphere.productEdition", installedProperties);
        Assert.assertEquals("Expected edition should be " + originalEdition, originalEdition, edition_before);

        File featuresDir = new File(samplesBundleDowngradeDir, "wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behavior across all platforms

        String version = getWASProperty("com.ibm.websphere.productVersion", installedProperties);

        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }

        //install the bundle to the installation jar
        File toolsBundleEdition = new File(samplesBundleDowngradeDir, "wlp/bin/tools");
        File toolsinstallUtility = new File(samplesBundleDowngradeDir, "wlp/bin/tools/installUtility.jar");
        String bundlename = originalEdition.toLowerCase() + "Bundle";
        if (originalEdition == "ND") {
            bundlename = "ndControllerBundle";
        } else if (originalEdition == "LIBERTY_CORE") {
            bundlename = "libertyCoreBundle";
        }
        execute(toolsBundleEdition, null, null,
                new String[] { "install " + bundlename + " --from=" + outputUploadDir + "/wlp-featureRepo-" + version + ".zip  --acceptLicense" },
                EXIT_OK, toolsinstallUtility, find(""));
        // Attempt to perform the license downgrade
        if (expectedExit == EXIT_OK) {
            execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                    input("x", "x", "1", samplesBundleDowngradeDir.getAbsolutePath() + "/wlp"));
        } else if (expectedExit == NOT_APPLICABLE_FEATURE) {
            String bundle_name = "";
            if (originalEdition == "ND") {
                bundle_name = "ndMemberBundle";
            } else if (originalEdition == "BASE") {
                bundle_name = "baseBundle";
            }
            Collection<String> output = execute(null, null, null, licenseJar, null, new String[0], NOT_APPLICABLE_FEATURE,
                                                input("x", "x", "1", samplesBundleDowngradeDir.getAbsolutePath() + "/wlp"), find(bundle_name),
                                                find("Use the following command to uninstall these features before applying the license"));
            File toolsEdition = new File(samplesBundleDowngradeDir, "wlp/bin/tools");
            String out = output.toString();
            int startpoint = out.lastIndexOf("uninstall");
            int endpoint = out.lastIndexOf("\"");
            String command = out.substring(startpoint, endpoint);
            execute(toolsEdition, null, null,
                    new String[] { command },
                    EXIT_OK, toolsinstallUtility, input("\n"));
            execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                    input("x", "x", "1", samplesBundleDowngradeDir.getAbsolutePath() + "/wlp"));
        }

        // Check if Properties files is changed to base edition
        // Point to new file
        File propertiesBaseEdition = new File(samplesBundleDowngradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(propertiesBaseEdition.getAbsolutePath(), propertiesBaseEdition.exists());

        String edition_after = getWASProperty("com.ibm.websphere.productEdition", propertiesBaseEdition);
        Assert.assertEquals("Expected edition should be" + originalEdition, originalEdition, edition_after);

        deleteDir(samplesBundleDowngradeDir);
    }

    @Test
    public void testDowngrdeEditionWithBundle() throws Exception {
        if (!runCoreTest || disableTestSuite) {
            return;
        }
        testDowngradeWithBundleHelper(WlpJarType.ND, WlpJarType.BASE_LIC, "ND", "BASE", NOT_APPLICABLE_FEATURE);
        testDowngradeWithBundleHelper(WlpJarType.ND, WlpJarType.CORE_LIC, "ND", "LIBERTY_CORE", NOT_APPLICABLE_FEATURE);
        testDowngradeWithBundleHelper(WlpJarType.BASE, WlpJarType.CORE_LIC, "BASE", "LIBERTY_CORE", NOT_APPLICABLE_FEATURE);
    }

    private void testOldVersionDowngradeHelper(WlpJarType installationJar, WlpJarType licenseJar, String originalEdition, String targetEdition, File repo) throws Exception {
        if (samplesOldVersionDowngradeDir.exists() && samplesOldVersionDowngradeDir.listFiles().length > 0)
            deleteDir(samplesOldVersionDowngradeDir);
        Assert.assertTrue("mkdir -p " + samplesOldVersionDowngradeDir.getAbsolutePath(), samplesOldVersionDowngradeDir.mkdirs() || samplesOldVersionDowngradeDir.isDirectory());
        //extractEditionJar(installationJar, samplesOldVersionDowngradeDir);
        execute(null, repo, null, installationJar, null, new String[0], EXIT_OK,
                input("x", "x", "1", samplesOldVersionDowngradeDir.getAbsolutePath()));
        //Check if Properties file is original edition
        File installedProperties = new File(samplesOldVersionDowngradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(installedProperties.getAbsolutePath(), installedProperties.exists());
        String edition_before = getWASProperty("com.ibm.websphere.productEdition", installedProperties);
        Assert.assertEquals("Expected edition should be " + originalEdition, originalEdition, edition_before);

        File featuresDir = new File(samplesOldVersionDowngradeDir, "wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behavior across all platforms

        String version = getWASProperty("com.ibm.websphere.productVersion", installedProperties);

        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }

        // Attempt to perform the license downgrade with latest license jar
        execute(null, null, null, licenseJar, null, new String[0], EXIT_EXTRACT_ERROR,
                input("x", "x", "1", samplesOldVersionDowngradeDir.getAbsolutePath() + "/wlp"),
                find("This license can only be applied to installation at 8.5.5.8 or above."));

        // Check if Properties files is changed to original edition
        // Point to new file
        File propertiesBaseEdition = new File(samplesOldVersionDowngradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(propertiesBaseEdition.getAbsolutePath(), propertiesBaseEdition.exists());

        String edition_after = getWASProperty("com.ibm.websphere.productEdition", propertiesBaseEdition);
        Assert.assertEquals("Expected edition should be" + originalEdition, originalEdition, edition_after);

        deleteDir(samplesOldVersionDowngradeDir);

    }

    @Test
    public void testOldVersionDowngrade() throws Exception {
        if (disableTestSuite) {
            return;
        }
        testOldVersionDowngradeHelper(WlpJarType.ND, WlpJarType.BASE_LIC, "ND", "BASE", Repo8555);
        if (runCoreTest) {
            testOldVersionDowngradeHelper(WlpJarType.BASE, WlpJarType.CORE_LIC, "BASE", "LIBERTY_CORE", Repo8556);
            testOldVersionDowngradeHelper(WlpJarType.ND, WlpJarType.CORE_LIC, "ND", "LIBERTY_CORE", Repo8557);
        }
    }

    private void testUpgradeWithBundleHelper(WlpJarType installationJar, WlpJarType licenseJar, String originalEdition, String targetEdition) throws Exception {
        if (samplesBundleUpgradeDir.exists() && samplesBundleUpgradeDir.listFiles().length > 0)
            deleteDir(samplesBundleUpgradeDir);
        Assert.assertTrue("mkdir -p " + samplesBundleUpgradeDir.getAbsolutePath(), samplesBundleUpgradeDir.mkdirs() || samplesBundleUpgradeDir.isDirectory());
        extractEditionJar(installationJar, samplesBundleUpgradeDir);
        //Check if Properties file is BASE edition
        File installedProperties = new File(samplesBundleUpgradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(installedProperties.getAbsolutePath(), installedProperties.exists());
        String edition_before = getWASProperty("com.ibm.websphere.productEdition", installedProperties);
        String bundlename = originalEdition.toLowerCase() + "Bundle";
        if (originalEdition == "ND") {
            bundlename = "ndControllerBundle";
        } else if (originalEdition == "LIBERTY_CORE") {
            bundlename = "libertyCoreBundle";
        }
        Assert.assertEquals("Expected edition should be " + originalEdition, originalEdition, edition_before);

        File featuresDir = new File(samplesBundleUpgradeDir, "wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behavior across all platforms

        String version = getWASProperty("com.ibm.websphere.productVersion", installedProperties);

        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }

        //install the bundle to the installation jar
        File toolsBundleEdition = new File(samplesBundleUpgradeDir, "wlp/bin/tools");
        File toolsinstallUtility = new File(samplesBundleUpgradeDir, "wlp/bin/tools/installUtility.jar");
        execute(toolsBundleEdition, null, null,
                new String[] { "install " + bundlename + " --from=" + outputUploadDir + "/wlp-featureRepo-" + version + ".zip  --acceptLicense" },
                EXIT_OK, toolsinstallUtility, input(""));

        // Attempt to perform the license upgrade
        execute(null, null, null, licenseJar, null, new String[0], EXIT_OK,
                input("x", "x", "1", samplesBundleUpgradeDir.getAbsolutePath() + "/wlp"));

        // Check if Properties files is changed to base edition
        // Point to new file
        File propertiesBaseEdition = new File(samplesBundleUpgradeDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(propertiesBaseEdition.getAbsolutePath(), propertiesBaseEdition.exists());

        String edition_after = getWASProperty("com.ibm.websphere.productEdition", propertiesBaseEdition);
        Assert.assertEquals("Expected edition should be" + targetEdition, targetEdition, edition_after);

        deleteDir(samplesBundleUpgradeDir);

    }

    @Test
    public void testUpgrdeEditionWithBundle() throws Exception {
        if (!runCoreTest || disableTestSuite) {
            return;
        }
        testUpgradeWithBundleHelper(WlpJarType.CORE, WlpJarType.BASE_LIC, "LIBERTY_CORE", "BASE");
        testUpgradeWithBundleHelper(WlpJarType.CORE, WlpJarType.ND_LIC, "LIBERTY_CORE", "ND");
        testUpgradeWithBundleHelper(WlpJarType.BASE, WlpJarType.ND_LIC, "BASE", "ND");
        testUpgradeWithBundleHelper(WlpJarType.ND, WlpJarType.ND_LIC, "ND", "ND");
    }

    private void executeAllInstaller(WlpJarType type, File tmpDir) throws IOException, InterruptedException, TimeoutException {
        File allJar = null;
        try {
            allJar = getWLPJar(type, null);
        } catch (Exception e) {
        }
        if (allJar != null && allJar.exists()) {
            long predictSize = (long) (allJar.length() * 1.17);
            long freeSpace = tmpDir.getParentFile().getParentFile().getFreeSpace();
            if (freeSpace < predictSize) {
                System.out.println("Skipped to test" + type + " because not enough disk space. (free: " + freeSpace + ", predict: " + predictSize + ")");
            } else {
                Collection<String> output = execute(null, null, null, new String[0], EXIT_OK, allJar, input("x", "x", "1", tmpDir.getAbsolutePath()));
                for (String o : output) {
                    Assert.assertFalse("Duplicate IBM-ProductID WARNING occured. Need to find out which feature .mf file contain IBM-ProductID. IBM-ProductID should be added automatically.",
                                       o.contains("WARNING: Duplicate name in Manifest: IBM-ProductID."));
                }
                deleteDir(new File(tmpDir, "wlp"));
            }
        } else {
            System.out.println("Skipped to test " + type + " because the all installer could not be located.");
        }
    }

    @Test
    public void testAllInstallers() throws Exception {
        if (disableTestSuite) {
            return;
        }
        File tmpDir = new File("build/unittest/allInstallerTmp");
        try {
            if (runCoreTest) {
                executeAllInstaller(WlpJarType.CORE_ALL, tmpDir);
            }
            executeAllInstaller(WlpJarType.BASE_ALL, tmpDir);
            executeAllInstaller(WlpJarType.DEVELOPERS_IPLA_ALL, tmpDir);
            executeAllInstaller(WlpJarType.ND_ALL, tmpDir);
        } finally {
            deleteDir(tmpDir);
        }
    }

    @Test
    public void testUpgradeEditionRollback() throws Exception {
        if (disableTestSuite) {
            return;
        }
        // Check that properties file, tag files, and license files are all base edition
        File propertiesBaseEdition = new File(samplesBaseRollbackDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(propertiesBaseEdition.getAbsolutePath(), propertiesBaseEdition.exists());

        String edition_before = getWASProperty("com.ibm.websphere.productEdition", propertiesBaseEdition);
        Assert.assertEquals("Expected edition should be BASE", "BASE", edition_before);

        File featuresDir = new File(samplesBaseRollbackDir, "wlp/lib/features");
        File[] manifestFiles = featuresDir.listFiles(createManifestFilter());
        Arrays.sort(manifestFiles); // Need to sort in order to have reliable behaviour across all platforms

        String version = getWASProperty("com.ibm.websphere.productVersion", propertiesBaseEdition);
        String baseVersion = getBaseVersionFromFullVersion(version);
        // Continue the test if the manifest versions are consistent with the version in WebSphereApplicationServer.properties.
        if (!checkManifestIBMAppliesToVersion(manifestFiles, version)) {
            return;
        }

        // Check if fixpack tags are the base version
        File swtagBaseEdition = new File(samplesBaseRollbackDir, String.format("wlp/lib/versions/tags/ibm.com_WebSphere_Application_Server_Liberty-%s.swidtag", baseVersion));
        File fxtagBaseEdition = new File(samplesBaseRollbackDir, String.format("wlp/lib/versions/tags/ibm.com_WebSphere_Application_Server_Liberty-%s.swidtag", version));
        boolean isCdOffering = swtagBaseEdition.exists();

        Assert.assertTrue(swtagBaseEdition.getAbsolutePath(), swtagBaseEdition.exists());
        Assert.assertTrue(fxtagBaseEdition.getAbsolutePath(), fxtagBaseEdition.exists());

        String fixVersionBefore = isCdOffering ? getWASTagCD("version", fxtagBaseEdition) : getWASTag("version", fxtagBaseEdition);
        String revisionBefore = isCdOffering ? getWASTagCD("revision", fxtagBaseEdition) : getWASTag("revision", fxtagBaseEdition);
        Assert.assertEquals("Revision and FixVersion should report the same version number", revisionBefore, fixVersionBefore);

        Assert.assertTrue(compareDirectory(samplesBaseRollbackDir + "/wlp/lafiles", samplesBaseDir + "/wlp/lafiles"));

        // Lock one of the license files so that an exception is thrown when attempting to delete it
        // java.nio.channels.FileLock is platform dependent and will not work on UNIX. Just set the file to be unreadable in this case
        File lockedfile = new File(samplesBaseRollbackDir, "wlp/lafiles/lockedfile");
        Assert.assertFalse("The locked file should not already exist", lockedfile.exists());

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            FileOutputStream fos = new FileOutputStream(lockedfile);
            java.nio.channels.FileLock lock = fos.getChannel().lock();
            try {
                execute(null, null, null, WlpJarType.ND_LIC, null, new String[0], EXIT_EXTRACT_ERROR,
                        input("x", "x", "1", samplesBaseRollbackDir.getAbsolutePath() + "/wlp"));
            } finally {
                lock.release();
                fos.close();
            }
        } else {
            Assert.assertTrue(lockedfile.createNewFile());
            lockedfile.setReadable(false);

            if ("root".equals(System.getProperty("user.name"))) {
                // The super-user can't set a file not readable to itself, so just return
                return;
            }

            execute(null, null, null, WlpJarType.ND_LIC, null, new String[0], EXIT_EXTRACT_ERROR,
                    input("x", "x", "1", samplesBaseRollbackDir.getAbsolutePath() + "/wlp"));
        }

        // Need to remove the locked file for the directory compare to work
        Assert.assertTrue("The locked file was not deleted", lockedfile.delete());

        // Check that properties file, tag files, and license files have been rolled back

        // Disable the following until Defect 168476 is fixed
        // String edition_after = getWASProperty("com.ibm.websphere.productEdition", propertiesBaseEdition);
        // Assert.assertEquals("Expected edition should be BASE", "BASE", edition_after);
        // Assert.assertTrue(compareDirectory(samplesBaseRollbackDir + "/wlp/lafiles", samplesBaseDir + "/wlp/lafiles"));

        Assert.assertTrue(swtagBaseEdition.getAbsolutePath(), swtagBaseEdition.exists());

        Assert.assertTrue(fxtagBaseEdition.getAbsolutePath(), fxtagBaseEdition.exists());

        String fixVersionAfter = isCdOffering ? getWASTagCD("version", fxtagBaseEdition) : getWASTag("version", fxtagBaseEdition);
        String revisionAfter = isCdOffering ? getWASTagCD("revision", fxtagBaseEdition) : getWASTag("revision", fxtagBaseEdition);
        Assert.assertEquals("Revision and FixVersion should report the same version number", revisionAfter, fixVersionAfter);
    }

    protected static FilenameFilter createManifestFilter() {
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                boolean result = false;
                if (name.endsWith(".mf"))
                    result = true;

                return result;
            }
        };
    }

    protected static boolean checkManifestIBMAppliesToVersion(File[] manifestFiles, String expectedVersion) {
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
                // Check that the version in the IBM-AppliesTo: is consistent with the expected version.
                if (appliesTo.contains("productVersion") && !appliesTo.contains(expectedVersion)) {
                    return false;
                }
            }
            // There was a problem reading the .mf file. Just skip it.
            catch (IOException e) {
            }
        }
        return true;
    }

    public void maventestHelper(WlpJarType installationJar, String nameprefix) throws Exception {

        if (disableTestSuite) {
            return;
        }
        if (samplesMavenDir.exists() && samplesMavenDir.listFiles().length > 0)
            deleteDir(samplesMavenDir);
        Assert.assertTrue("mkdir -p " + samplesMavenDir.getAbsolutePath(), samplesMavenDir.mkdirs() || samplesMavenDir.isDirectory());

        File zipfile = null;
        File[] files = outputUploadDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (file.isFile() && name.startsWith(nameprefix) && name.endsWith(".jar")) {
                    zipfile = file;
                }
            }
        }
        Assert.assertTrue("Failed to extract the jar license file", unzip(zipfile, samplesMavenDir));

        // Check the existence of the files in the META-INF directory
        File propertyFile = new File(samplesMavenDir, "META-INF/maven/com.ibm.websphere.appserver.license/" + nameprefix + "/pom.properties");
        Assert.assertTrue("The " + propertyFile.getAbsolutePath() + " file does not exist", propertyFile.exists());
        File xmlFile = new File(samplesMavenDir, "META-INF/maven/com.ibm.websphere.appserver.license/" + nameprefix + "/pom.xml");
        Assert.assertTrue("The " + xmlFile.getAbsolutePath() + " file does not exist", xmlFile.exists());

        //check the content of the pom.properties file
        File wlpproperties = new File(samplesMavenDir, "wlp/lib/versions/WebSphereApplicationServer.properties");
        Assert.assertTrue(wlpproperties.getAbsolutePath() + "doesn't exist", wlpproperties.exists());

        String version_wlp = getWASProperty("com.ibm.websphere.productVersion", wlpproperties);
        String version_meta = getWASProperty("version", propertyFile);

        Assert.assertEquals("The version in wlp is:" + version_wlp + " , in META is:" + version_meta + " , doesn't match", version_wlp, version_meta);
        Assert.assertEquals("groupID is not correct", getWASProperty("groupId", propertyFile), "com.ibm.websphere.appserver.license");
        Assert.assertEquals("artifactID is not correct", getWASProperty("artifactId", propertyFile), nameprefix);

        //check the content of the pom.xml file
        Assert.assertEquals("groupID is not correct in xml file", getXMLProperty("groupId", xmlFile),
                            "com.ibm.websphere.appserver.license");
        Assert.assertEquals("artifactID is not correct in xml file", getXMLProperty("artifactId", xmlFile), nameprefix);
        Assert.assertEquals("version is not correct in xml file", getXMLProperty("version", xmlFile), version_wlp);

        if (nameprefix == "wlp-base-license") {
            Assert.assertEquals("description is not correct in xml file", getXMLProperty("description", xmlFile),
                                "WebSphere Application Server Liberty " + version_wlp + " License");
        } else if (nameprefix == "wlp-nd-license") {
            Assert.assertEquals("description is not correct in xml file", getXMLProperty("description", xmlFile),
                                "WebSphere Application Server Liberty Network Deployment " + version_wlp + " License");
        } else if (nameprefix == "wlp-core-license") {
            Assert.assertEquals("description is not correct in xml file", getXMLProperty("description", xmlFile),
                                "WebSphere Application Server Liberty Core " + version_wlp + " License");
        }
        deleteDir(samplesMavenDir);
        return;
    }

    @Test
    public void maventest() throws Exception {
        maventestHelper(WlpJarType.BASE_LIC, "wlp-base-license");
        maventestHelper(WlpJarType.ND_LIC, "wlp-nd-license");
        if (runCoreTest) {
            maventestHelper(WlpJarType.CORE_LIC, "wlp-core-license");
        }
    }

    /**
     * This is a replica of JarFile that throws an IOException when getInputStream is called on anything called ThrowIOException.test.
     * entries() is overridden to ensure the special ThrowIOException.test file sorts last in the Enumeration. On the rollback tests, we want
     * the normal files to drop onto disk first, so we can ensure they get cleared up correctly.
     */
    class ErroringJarfile extends JarFile {
        public ErroringJarfile(File file) throws IOException {
            super(file);
        }

        @Override
        public Enumeration<JarEntry> entries() {
            List<JarEntry> list = Collections.list(super.entries());
            Collections.sort(list, ALPHABETICAL_EXCEPTION_FILE_LAST);
            return Collections.enumeration(list);
        }

        @Override
        public synchronized InputStream getInputStream(ZipEntry ze) throws IOException {
            if (ze.getName().contains("ThrowIOException.test")) {
                throw new IOException();
            } else {
                return super.getInputStream(ze);
            }
        }

        final Comparator<JarEntry> ALPHABETICAL_EXCEPTION_FILE_LAST = new Comparator<JarEntry>() {
            @Override
            public int compare(JarEntry j1, JarEntry j2) {
                //Make the special file always sort last.
                if (j2.getName().contains("ThrowIOException.test") && j1.getName().contains("ThrowIOException.test")) {
                    return 0;
                } else if (j2.getName().contains("ThrowIOException.test")) {
                    return 1;
                } else if (j1.getName().contains("ThrowIOException.test")) {
                    return -1;
                } else {
                    return j2.getName().compareTo(j1.getName());
                }
            }
        };
    }
}
