/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * 
 * 
 * Change activity:
 *
 * Issue       Date        Name     Description
 * ----------- ----------- -------- ------------------------------------
 */

package com.ibm.wsspi.request.probe.bci;

/**
 * 
 * class ContextInfoHelper : is used by the TransformDescriptior implementation to
 * encapsulate the current object of the class for which the instrumentation
 * will be performed. It also consists of the detail of the current method
 * argument list.
 */
public abstract class ContextInfoHelper {
	private Object instanceOfThisClass;
	private Object methodArgs;

	public ContextInfoHelper(Object instanceOfThisClass, Object methodArgs) {
		this.instanceOfThisClass = instanceOfThisClass;
		this.methodArgs = methodArgs;
	}

	/**
	 * Allowing the decedent object who will implement this class to have its
	 * own implementation of toString method.
	 */
	public abstract String toString();

	/**
	 * This method will return the current method and its list of argument for
	 * which transform descriptor will implement the instrumentation.
	 * 
	 * @return the list of the method argument for which the instrumentation
	 *         will be performed.
	 */
	public Object getMethodArgs() {
		return methodArgs;
	}

	/**
	 * This method will return the current instance of the implemented object
	 * for which transform descriptor will implement the instrumentation.
	 * 
	 * @return the current object of type Object for which the instrumentation
	 *         will be performed.
	 */
	public Object getInstanceOfThisClass() {
		return instanceOfThisClass;
	}

}
