//------------------------------------------------------------------------------
//%Z% %I% %W% %G% %U% [%H% %T%]

//COMPONENT_NAME: WAS.sca.ras

//IBM Confidential OCO Source Material
//5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.

//Change History:

//Defect/Feature  Date      CMVC ID   Description
//--------------  --------  --------- -----------------------------------------
//410408          20070919  sykesm    Initial implementation
//------------------------------------------------------------------------------
package com.ibm.ws.ras.instrument.internal.bci;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Interface to be implemented by class adpaters that wish to plug into
 * the trace instrumentation framework.
 */
public interface RasClassAdapter {

    /**
     * Create a method adapter to inject trace code into the methods of a class.
     * 
     * @param delegate
     *            the <code>MethodVisitor</code> that the new method
     *            adapter must forward ASM calls to
     * @param access
     *            the method access flags
     * @param methodName
     *            the name of the method we're processing
     * @param descriptor
     *            the method descriptor containing the parameter and return types
     * @param signature
     *            the method's signature (may be null if generic types are not used)
     * @param exceptions
     *            the internal names of the exception types declared to be thrown
     * 
     * @return the method adapter
     */
    public RasMethodAdapter createRasMethodAdapter(MethodVisitor delegate, int access, String name, String descriptor, String signature, String[] exceptions);

    /**
     * Determine whether or not the field that holds the trace object must
     * be declared.
     */
    public boolean isTraceObjectFieldDefinitionRequired();

    /**
     * Determine whether or not the field that holds the trace object must
     * be initialized out of the static initializer.
     */
    public boolean isTraceObjectFieldInitializationRequired();

    /**
     * Get the name of the field that holds the trace object.
     * 
     * @return the name of the field that holds the trace object
     */
    public String getTraceObjectFieldName();

    /**
     * Get the <code>Type</code> of the field that holds the trace object.
     * 
     * @return the declared <code>Type</code> of the field that holds the
     *         trace object.
     */
    public Type getTraceObjectFieldType();
}
