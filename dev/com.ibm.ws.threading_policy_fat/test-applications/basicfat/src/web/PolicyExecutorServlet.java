package web;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;

import org.junit.Test;

import componenttest.app.FATServlet;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/PolicyExecutorServlet")
public class PolicyExecutorServlet extends FATServlet {

    @Resource(lookup = "test/TestPolicyExecutorProvider")
    private Object provider;

    //Ensure that a policy executor can be obtained from the injected provider
    @Test
    public void testGetPolicyExecutor() throws Exception {
        Object exec = provider.getClass().getMethod("create", String.class).invoke(provider, "testGetPolicyExecutor");
        exec.getClass().getMethod("maxConcurrency", int.class).invoke(exec, 2);
    }
}
