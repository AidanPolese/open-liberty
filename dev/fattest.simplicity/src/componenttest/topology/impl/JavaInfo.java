package componenttest.topology.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
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
     * <li> JAVA_HOME of the system
     * </ol>
     */
    public static JavaInfo forServer(LibertyServer server) throws IOException {
        String serverJava = server.getServerEnv().getProperty("JAVA_HOME");
        return fromPath(serverJava);
    }

    public static enum Vendor {
        IBM,
        ORACLE
    }

    final String JAVA_HOME;
    final int MAJOR;
    final int MINOR;
    final Vendor VENDOR;

    private JavaInfo(String jdk_home, int major, int minor, Vendor v) {
        JAVA_HOME = jdk_home;
        MAJOR = major;
        MINOR = minor;
        VENDOR = v;
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

    private static JavaInfo runJavaVersion(String javaHome) throws IOException {
        final String m = "runJavaVersion";
        // output for 'java -version' is always as follows:
        // line 1: java version "1.MAJOR.MINOR"
        // line 2: build info
        // line 3: vendor info
        ProcessBuilder pb = new ProcessBuilder(javaHome + "/bin/java", "-version");
        Process p = pb.start();
        try {
            p.waitFor();
        } catch (InterruptedException e) {
        }
        InputStreamReader isr = new InputStreamReader(p.getErrorStream());
        BufferedReader br = new BufferedReader(isr);
        String versionInfo = br.readLine(); // 1st line has version info
        br.readLine(); // ignore 2nd line
        String vendorInfo = br.readLine();
        br.close();
        isr.close();

        Log.info(c, m, versionInfo);
        Log.info(c, m, vendorInfo);

        // Parse vendor
        Vendor v = vendorInfo.toLowerCase().contains("ibm") ? Vendor.IBM : Vendor.ORACLE;

        // Parse major/minor versions
        versionInfo = versionInfo.substring(versionInfo.indexOf('"') + 1, versionInfo.lastIndexOf('"'));
        String[] versions = versionInfo.split("[^0-9]"); // split on non-numeric chars
        System.out.println(Arrays.toString(versions));

        // Offset for 1.MAJOR.MINOR vs. MAJOR.MINOR version syntax
        int offset = "1".equals(versions[0]) ? 1 : 0;
        if (versions.length <= offset)
            throw new IllegalStateException("Bad Java runtime version string: " + versionInfo);
        int major = Integer.parseInt(versions[offset]);
        int minor = versions.length < (2 + offset) ? 0 : Integer.parseInt(versions[(1 + offset)]);
        return new JavaInfo(javaHome, major, minor, v);
    }

    @Override
    public String toString() {
        return "major=" + MAJOR + "  minor=" + MINOR + "  vendor=" + VENDOR + "  javaHome=" + JAVA_HOME;
    }
}
