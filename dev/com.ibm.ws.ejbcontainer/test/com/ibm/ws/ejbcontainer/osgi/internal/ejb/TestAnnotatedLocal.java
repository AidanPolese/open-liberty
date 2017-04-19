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
package com.ibm.ws.ejbcontainer.osgi.internal.ejb;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import javax.ejb.Stateless;
import javax.ejb.TimedObject;
import javax.ejb.Timer;

@Stateless
public class TestAnnotatedLocal implements Serializable, Externalizable, TimedObject, LocalAnnotatedIntf {
    @Override
    public void writeExternal(ObjectOutput out) {}

    @Override
    public void readExternal(ObjectInput in) {}

    @Override
    public void ejbTimeout(Timer timer) {}
}
