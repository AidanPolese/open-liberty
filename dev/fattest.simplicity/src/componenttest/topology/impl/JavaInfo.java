package componenttest.topology.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.ibm.websphere.simplicity.log.Log;

/**
 * A class used for identifying properties of a JDK other
 * than the one that is currently being run.
 */
public class JavaInfo {
    private static Class<?> c = JavaInfo.class;
    private static Map<String, JavaInfo> cache = new HashMap<String, JavaInfo>();

    public static int JAVA_VERSION = JavaInfo.forCurrentVM().majorVersion();

    public static JavaInfo forCurrentVM() {
        String javaHome = System.getProperty("java.home");
        JavaInfo info = cache.get(javaHome);
        if (info == null) {
            info = new JavaInfo();
            cache.put(javaHome, info);
        }
        return info;
    }

    /**
     * Get the JavaInfo for a given JDK path
     *
     * @param jdkPath The jdk path. For example: System.getProperty("java.home")
     */
    public static JavaInfo fromPath(String jdkPath) throws IOException {
        JavaInfo info = cache.get(jdkPath);
        if (info == null) {
            info = runJavaVersion(jdkPath);
            cache.put(jdkPath, info);
        }
        return info;
    }

    /**
     * Get the JavaInfo for the JDK that will be used for a given LibertyServer.
     * The priority is determined in the following way:
     * <ol>
     * <li> ${server.config.dir}/server.env
     * <li> ${wlp.install.dir}/etc/server.env
     * <li> JAVA_HOME returned by LibertyServer.getMachineJavaJDK()
     * </ol>
     */
    public static JavaInfo forServer(LibertyServer server) throws IOException {
        String serverJava = server.getServerEnv().getProperty("JAVA_HOME");
        return fromPath(serverJava);
    }

    /**
     * The java.vendor of the JDK. Note that Sun and Oracle JDKs are considered to be the same.
     */
    public static enum Vendor {
        IBM,
        SUN_ORACLE,
        UNKNOWN
    }

    final String JAVA_HOME;
    final int MAJOR;
    final int MINOR;
    final Vendor VENDOR;
    final int SERVICE_RELEASE;
    final int FIXPACK;

    private JavaInfo(String jdk_home, int major, int minor, Vendor v, int sr, int fp) {
        JAVA_HOME = jdk_home;
        MAJOR = major;
        MINOR = minor;
        VENDOR = v;
        SERVICE_RELEASE = sr;
        FIXPACK = fp;

        Log.info(c, "<init>", this.toString());
    }

    private JavaInfo() {
        JAVA_HOME = System.getProperty("java.home");

        // Parse MAJOR and MINOR versions
        String specVersion = System.getProperty("java.specification.version");
        String[] versions = specVersion.split("[^0-9]"); // split on non-numeric chars
        // Offset for 1.MAJOR.MINOR vs. MAJOR.MINOR version syntax
        int offset = "1".equals(versions[0]) ? 1 : 0;
        if (versions.length <= offset)
            throw new IllegalStateException("Bad Java runtime version string: " + specVersion);
        MAJOR = Integer.parseInt(versions[offset]);
        MINOR = versions.length < (2 + offset) ? 0 : Integer.parseInt(versions[(1 + offset)]);

        // Parse vendor
        String vendorInfo = System.getProperty("java.vendor").toLowerCase();
        if (vendorInfo.contains("ibm")) {
            VENDOR = Vendor.IBM;
        } else if (vendorInfo.contains("oracle") || vendorInfo.contains("sun")) {
            VENDOR = Vendor.SUN_ORACLE;
        } else {
            VENDOR = Vendor.UNKNOWN;
        }

        // Parse service release
        String buildInfo = System.getProperty("java.runtime.version");
        int sr = 0;
        int srloc = buildInfo.toLowerCase().indexOf("sr");
        if (srloc > (-1)) {
            srloc += 2;
            if (srloc < buildInfo.length()) {
                int len = 0;
                while ((srloc + len < buildInfo.length()) && Character.isDigit(buildInfo.charAt(srloc + len))) {
                    len++;
                }
                sr = Integer.parseInt(buildInfo.substring(srloc, srloc + len));
            }
        }
        SERVICE_RELEASE = sr;

        // Parse fixpack
        int fp = 0;
        int fploc = buildInfo.toLowerCase().indexOf("fp");
        if (fploc > (-1)) {
            fploc += 2;
            if (fploc < buildInfo.length()) {
                int len = 0;
                while ((fploc + len < buildInfo.length()) && Character.isDigit(buildInfo.charAt(fploc + len))) {
                    len++;
                }
                fp = Integer.parseInt(buildInfo.substring(fploc, fploc + len));
            }
        }
        FIXPACK = fp;

        Log.info(c, "<init>", this.toString());
    }

    public int majorVersion() {
        return MAJOR;
    }

    public int minorVersion() {
        return MINOR;
    }

    public Vendor vendor() {
        return VENDOR;
    }

    public String javaHome() {
        return JAVA_HOME;
    }

    public int serviceRelease() {
        return SERVICE_RELEASE;
    }

    public int fixpack() {
        return FIXPACK;
    }

    private static JavaInfo runJavaVersion(String javaHome) throws IOException {
        final String m = "runJavaVersion";

        // output for 'java -version' is always as follows:
        // line 1: java version "1.MAJOR.MINOR"
        // line 2: build info
        // line 3: vendor info
        //
        // For example:
        //      java version "1.7.0"
        //      Java(TM) SE Runtime Environment (build pxi3270_27sr3fp50-20160720_02(*SR3fp50*))
        //      IBM J9 VM (build 2.7, JRE 1.7.0 Linux x86-32 20160630_309914 (JIT enabled, AOT enabled)
        //
        // Major version = 7
        // Minor version = 0
        // Service release (IBM JDK specific) = 3
        // Fixpack (IBM JDK specific) = 50
        // Vendor = IBM

        ProcessBuilder pb = new ProcessBuilder(javaHome + "/bin/java", "-version");
        Process p = pb.start();
        try {
            p.waitFor();
        } catch (InterruptedException e) {
        }
        InputStreamReader isr = new InputStreamReader(p.getErrorStream());
        BufferedReader br = new BufferedReader(isr);
        String versionInfo = br.readLine(); // 1st line has version info
        String buildInfo = br.readLine(); // 2nd line has service release and fixpack info
        String vendorInfo = br.readLine().toLowerCase();;

        br.close();
        isr.close();

        Log.info(c, m, versionInfo);
        Log.info(c, m, vendorInfo);

        // Parse vendor
        Vendor v;
        if (vendorInfo.contains("ibm") || vendorInfo.contains("j9")) {
            v = Vendor.IBM;
        } else if (vendorInfo.contains("oracle") || vendorInfo.contains("hotspot")) {
            v = Vendor.SUN_ORACLE;
        } else {
            v = Vendor.UNKNOWN;
        }

        // Parse major/minor versions
        versionInfo = versionInfo.substring(versionInfo.indexOf('"') + 1, versionInfo.lastIndexOf('"'));
        String[] versions = versionInfo.split("[^0-9]"); // split on non-numeric chars

        // Offset for 1.MAJOR.MINOR vs. MAJOR.MINOR version syntax
        int offset = "1".equals(versions[0]) ? 1 : 0;
        if (versions.length <= offset)
            throw new IllegalStateException("Bad Java runtime version string: " + versionInfo);
        int major = Integer.parseInt(versions[offset]);
        int minor = versions.length < (2 + offset) ? 0 : Integer.parseInt(versions[(1 + offset)]);

        // Parse service release
        int sr = 0;
        int srloc = buildInfo.toLowerCase().indexOf("sr");
        if (srloc > (-1)) {
            srloc += 2;
            if (srloc < buildInfo.length()) {
                int len = 0;
                while ((srloc + len < buildInfo.length()) && Character.isDigit(buildInfo.charAt(srloc + len))) {
                    len++;
                }
                sr = Integer.parseInt(buildInfo.substring(srloc, srloc + len));
            }
        }

        // Parse fixpack
        int fp = 0;

        int fploc = buildInfo.toLowerCase().indexOf("fp");
        if (fploc > (-1)) {
            fploc += 2;
            if (fploc < buildInfo.length()) {
                int len = 0;
                while ((fploc + len < buildInfo.length()) && Character.isDigit(buildInfo.charAt(fploc + len))) {
                    len++;
                }
                fp = Integer.parseInt(buildInfo.substring(fploc, fploc + len));
            }
        }

        return new JavaInfo(javaHome, major, minor, v, sr, fp);
    }

    @Override
    public String toString() {
        return "major=" + MAJOR + "  minor=" + MINOR + " service release=" + SERVICE_RELEASE + " fixpack=" + FIXPACK + "  vendor=" + VENDOR + "  javaHome=" + JAVA_HOME;
    }
}
