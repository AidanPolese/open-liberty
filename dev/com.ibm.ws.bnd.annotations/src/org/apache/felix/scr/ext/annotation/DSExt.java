/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package org.apache.felix.scr.ext.annotation;

//This will be contributed to apache shortly.
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import aQute.bnd.annotation.xml.XMLAttribute;


public interface DSExt {
	
	@XMLAttribute(namespace = "http://felix.apache.org/xmlns/scr/extensions/v1.0.0", prefix = "felix", mapping="value=configurableServiceProperties")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	@interface ConfigurableServiceProperties {
		boolean value() default true;
	}
	
	@XMLAttribute(namespace = "http://felix.apache.org/xmlns/scr/extensions/v1.0.0", prefix = "felix", mapping="value=persistentFactoryComponent")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	@interface PersistentFactoryComponent {
		boolean value() default true;
	}
	
	@XMLAttribute(namespace = "http://felix.apache.org/xmlns/scr/extensions/v1.0.0", prefix = "felix", mapping="value=deleteCallsModify")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	@interface DeleteCallsModify {
		boolean value() default true;
	}
	
	@XMLAttribute(namespace = "http://felix.apache.org/xmlns/scr/extensions/v1.0.0", prefix = "felix", mapping="value=obsoleteFactoryComponentFactory")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	@interface ObsoleteFactoryComponentFactory {
		boolean value() default true;
	}
	
	@XMLAttribute(namespace = "http://felix.apache.org/xmlns/scr/extensions/v1.0.0", prefix = "felix", mapping="value=configureWithInterfaces")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	@interface ConfigureWithInterfaces {
		boolean value() default true;
	}
	
	@XMLAttribute(namespace = "http://felix.apache.org/xmlns/scr/extensions/v1.0.0", prefix = "felix", mapping="value=delayedKeepInstances")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	@interface DelayedKeepInstances {
		boolean value() default true;
	}

}
