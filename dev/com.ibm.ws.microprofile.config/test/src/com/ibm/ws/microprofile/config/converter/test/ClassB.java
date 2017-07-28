/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.config.converter.test;

public class ClassB extends ClassA {

    public static ClassB newClassB(String value) {
        ClassB classB = new ClassB();
        classB.setValue(value);
        return classB;
    }

    @Override
    public String toString() {
        return "ClassB: " + getValue();
    }

}
