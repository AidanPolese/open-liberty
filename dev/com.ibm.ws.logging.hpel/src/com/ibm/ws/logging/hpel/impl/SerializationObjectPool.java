// %Z% %I% %W% %G% %U% [%H% %T%]
/*
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2009
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change History:
 *
 * Reason           Version        Date       User id     Description
 * ----------------------------------------------------------------------------
 * F001340-15950.1    8.0        09/04/2009   belyi       Initial HPEL code
 */
package com.ibm.ws.logging.hpel.impl;

import java.util.ArrayList;

import com.ibm.ws.logging.hpel.LogRecordSerializer;
import com.ibm.ws.logging.hpel.SerializationObject;

/**
 * Pool of reusable {@link SerializationObject} instances.
 */
public abstract class SerializationObjectPool {
	private static final int INITIAL_NUM_OF_OBJECTS = 20;
	private static final int MAXIMUM_NUM_OF_OBJECTS = 25;

	private ArrayList<SerializationObject> ivListOfObjects = new ArrayList<SerializationObject>(INITIAL_NUM_OF_OBJECTS);

	/**
	 * create <code>SerializationObjectPool</code> using the specified formatter.
	 * @see LogRecordSerializer
	 */
	public SerializationObjectPool() {
		// the intialization will be done here
		for (int i = 0; i < INITIAL_NUM_OF_OBJECTS; ++i) {
			ivListOfObjects.add(createNewObject());
		}
	}

	/**
	 * Gets the next avaialable serialization object.
	 * 
	 * @return ISerializationObject instance to do the conversation with.
	 */
	public SerializationObject getSerializationObject() {
		SerializationObject object;
		synchronized (ivListOfObjects) {
			if (ivListOfObjects.isEmpty()) {
				object = createNewObject();
			} else {
				object = ivListOfObjects.remove(ivListOfObjects.size() - 1);
			}
		}
		return object;
	}

	/**
	 * Returns a serialization object back into the pool.
	 * 
	 * @param object an instance previously allocated with {@link #getSerializationObject()}.
	 */
	public void returnSerializationObject(SerializationObject object) {
		synchronized (ivListOfObjects) {
			if (ivListOfObjects.size() < MAXIMUM_NUM_OF_OBJECTS) {
				ivListOfObjects.add(object);
			}
		}
	}

	/**
	 * Creates new object for the pool. It is called whenever pool runs
	 * out of the objects.
	 * 
	 * @return the instance of the SerializationObject.
	 */
	public abstract SerializationObject createNewObject();
}
