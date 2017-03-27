package componenttest.custom.junit.runner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;

import com.ibm.websphere.simplicity.log.Log;

import componenttest.annotation.TestServlet;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

public class SyntheticServletTest extends FrameworkMethod {

    private final Field server;
    private final String queryPath;
    private final String testName;

    public SyntheticServletTest(Field server, TestServlet anno, Method method) {
        super(method);
        this.server = server;
        this.queryPath = anno.path();
        this.testName = method.getName();
    }

    @Override
    public Object invokeExplosively(Object target, Object... params) throws Throwable {
        Log.info(SyntheticServletTest.class, "invokeExplosively", "Running test: " + testName);
        LibertyServer s = (LibertyServer) server.get(null);
        FATServletClient.runTest(s, queryPath, testName);
        return null;
    }
}
