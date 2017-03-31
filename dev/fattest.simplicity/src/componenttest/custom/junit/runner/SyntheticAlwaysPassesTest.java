package componenttest.custom.junit.runner;

import org.junit.runners.model.FrameworkMethod;

public class SyntheticAlwaysPassesTest extends FrameworkMethod {

    public SyntheticAlwaysPassesTest() {
        super(AlwaysPassesTest.getAlwaysPassesMethod());
    }

    @Override
    public Object invokeExplosively(Object target, Object... params) throws Throwable {
        // If we cared about actually running the AlwaysPassesTest we would do so here
        return null;
    }
}
