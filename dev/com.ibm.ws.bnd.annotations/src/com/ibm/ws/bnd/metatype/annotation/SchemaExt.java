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
package com.ibm.ws.bnd.metatype.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import aQute.bnd.annotation.xml.XMLAttribute;

public interface SchemaExt {

	/**
	 * Something to do with schema generation
	 */
	@XMLAttribute(namespace = "http://www.ibm.com/xmlns/appservers/osgi/metatype/v1.0.0", prefix = "ibm", mapping="value=action")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	public @interface Action {
		String value();
	}

	/**
	 * Something to do with schema generation
	 */
	@XMLAttribute(namespace = "http://www.ibm.com/xmlns/appservers/osgi/metatype/v1.0.0", prefix = "ibm", mapping="value=any")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	public @interface Any {
		int value();
	}

	/**
	 * Comma separated list of child first pids to exclude from schema generation
	 */
	@XMLAttribute(namespace = "http://www.ibm.com/xmlns/appservers/osgi/metatype/v1.0.0", prefix = "ibm", mapping="value=excludeChildren")
	@Retention(RetentionPolicy.CLASS)
	@Target(ElementType.TYPE)
	public @interface ExcludeChildren {
		String value();
	}

}
