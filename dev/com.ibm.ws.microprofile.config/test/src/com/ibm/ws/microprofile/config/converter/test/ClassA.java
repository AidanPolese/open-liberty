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

public class ClassA {

    private String value;

    public static ClassA newClassA(String value) {
        ClassA classA = new ClassA();
        classA.value = value;
        return classA;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ClassA: " + getValue();
    }

}
