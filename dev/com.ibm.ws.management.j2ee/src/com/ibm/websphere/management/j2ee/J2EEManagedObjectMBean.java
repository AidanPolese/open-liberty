/**
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

package com.ibm.websphere.management.j2ee;

/**
 * The J2EEManagedObject model is the base model of all managed objects in the
 * J2EE Management Model. All managed objects in the J2EE platform must include
 * the attributes of the J2EEManagedObject model. All managed objects must have a
 * unique name.
 */
public interface J2EEManagedObjectMBean {

    /**
     * The object name of the managed object. The objectName attribute is of the
     * type OBJECT_NAME which is a string that complies with the syntax specified
     * for a J2EEManagedObject name below. The objectName attribute must not be
     * null. The value of objectName must be unique within the management domain.
     * Management applications use this value to identify managed objects, for example
     * identifying the source of events.
     * The J2EEManagedObject objectName consists of two parts:
     * <ul>
     * <li>A domain name</li>
     * <li>An unordered set of key properties, which must include the j2eeType, name
     * and <parent-j2eeType> key properties.</li>
     * </ul>
     * The J2EEManagedObject name has the following syntax:
     * [domainName]:j2eeType=value,name=value,<parent-j2eeType>[,property=value]*
     */
    String getobjectName();

    /**
     * If true, indicates that this managed object implements the StateManageable
     * model and is state manageable by the specification of Chapter JSR77.5, State
     * Management. If false, the managed object does not support state management.
     */
    boolean isstateManageable();

    /**
     * If true, indicates that the managed object supports the generation of
     * performance statistics and implements the StatisticsProvider model.
     * If false, the J2EEManagedObject does not support performance statistics.
     */
    boolean isstatisticsProvider();

    /**
     * If true, indicates that the managed object implements the EventProvider
     * model and provides notification about events that occur on that object.
     */
    boolean iseventProvider();

}
