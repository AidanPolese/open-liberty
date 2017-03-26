package com.ibm.websphere.simplicity.config.dsprops.testrules;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.ibm.websphere.simplicity.config.DataSource;
import com.ibm.websphere.simplicity.config.DataSourceProperties;
import com.ibm.websphere.simplicity.log.Log;
import componenttest.topology.utils.FATServletClient;

/**
 * This class can be used as a @Rule to process a @OnlyIfDataSourceProperties annotation
 * specified on one or more @Test.
 * <p>
 * <B>Usage:</B> The simplest way to use this rule, is to extend the {@link FATServletClient} class.<br>
 * If it is not possible to extend FATServletClient, it may be used manually -- see below.
 * <p>
 * Before this rule is used, the <code>setDataSource()</code>
 * method must be called on this class to indicate which <code>DataSource</code>
 * should be examined for a nested type of <code>DataSourceProperties</code>.
 * If the <code>DataSource</code> is not set on this rule, then a null pointer exception will
 * occur when the first test that is annotated with @OnlyIfDataSourceProperties is executed.
 * <p>
 * The @Test will be run only if the <code>DataSourceProperties</code> type nested under the
 * <code>DataSource</code> matches one of the <code>DataSourceProperties</code> specified
 * in the @OnlyIfDataSourceProperties annotation.
 * <p>
 * For example this code would specify that the test should only be run with "DB2 with JCC" and
 * "Derby embedded".
 * <pre>
 * import static com.ibm.websphere.simplicity.config.DataSourceProperties.*;
 * public class MyTestClass extends FATServletClient {
 * 
 * {@literal @Test} {@literal @OnlyIfDataSourceProperties}({ DB2_JCC, DERBY_EMBEDDED })
 * public void onlyTestIf_DB2JCC_DerbyEmbedded() throws Exception {
 * // test code
 * }
 * }
 * </pre>
 * <p>
 * A full listing of supported database types can be found in:<br> {@link com.ibm.websphere.simplicity.config.DataSourceProperties}
 */
public class DataSourcePropertiesOnlyRule implements TestRule {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public Statement apply(final Statement statement, final Description description) {
        OnlyIfDataSourceProperties annotation = description.getAnnotation(OnlyIfDataSourceProperties.class);
        if (annotation == null) {
            return statement;
        }

        List<String> allowedDataSets = Arrays.asList(annotation.value());
        Set<DataSourceProperties> dsp = null;
        try {
            dsp = dataSource.getDataSourceProperties();
        } catch (NullPointerException e) {
            NullPointerException eMessage =
                            new NullPointerException("Must set the DataSourcePropertiesOnlyRule DataSource, using .setDataSource()");
            eMessage.setStackTrace(e.getStackTrace());
            throw eMessage;
        }
        for (DataSourceProperties p : dsp) {
            if (allowedDataSets.contains(p.getElementName())) {
                return statement;
            }
        }

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Log.info(description.getTestClass(), description.getMethodName(), "Test method is skipped due to DataSourcePropertiesOnlyRule");
            }
        };
    }
}