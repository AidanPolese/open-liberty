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
package test.server.config;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
               ServerConfigTest.class,
               ConfigExtensionsTest.class,
               ConfigValidatorTest.class,
               ChildAliasTest.class,
               ProductExtensionsTest.class,
               BadConfigTests.class,
               MergedConfigTests.class,
               VariableMergeTests.class,
               MetatypeProviderTest.class,
               WSConfigurationHelperTest.class,
               SchemaGeneratorMBeanTest.class,
               FeaturelistGeneratorMBeanTest.class,
               ServerXMLConfigurationMBeanTest.class,
               DropinsTest.class,
               DelayedVariableTests.class

})
public class FATSuite {

}
