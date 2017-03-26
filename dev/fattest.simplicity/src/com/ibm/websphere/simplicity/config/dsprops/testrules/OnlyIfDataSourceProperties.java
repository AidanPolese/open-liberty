package com.ibm.websphere.simplicity.config.dsprops.testrules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to indicate that a test should only be executed
 * if one of the <code>DataSourceProperties</code> in the array
 * was a nested properties for the <code>DataSource</code> that was set on
 * the <code>DataSourcePropertiesOnlyRule</code>.
 * 
 * @param value one or classes of type <code>DataSourceProperties</code>.
 * 
 * @see {@link DataSourcePropertiesOnlyRule} for an example of how to use this annotation.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnlyIfDataSourceProperties {
    String[] value();
}