//------------------------------------------------------------------------------
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// COMPONENT_NAME: WAS.sca.ras
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
//
// Defect/Feature  Date      CMVC ID   Description
// --------------  --------  --------- -----------------------------------------
// 410408          20070919  sykesm    Initial implementation
//------------------------------------------------------------------------------
package com.ibm.ws.ras.instrument.internal.bci;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Interface that must be implemented by method adapters that wish to plug into
 * the trace instrumentation framework.
 */
public abstract class RasMethodAdapter extends MethodVisitor {

    public RasMethodAdapter(MethodVisitor visitor) {
        super(Opcodes.ASM4, visitor);
    }

    /**
     * Generate the code required to initialize the trace object field. The
     * generated code will exist in a static method used during class static
     * initialization and may not access access any field other than the
     * trace object field.
     */
    public abstract void initializeTraceObjectField();

    /**
     * Generate the code required to trace method entry. This method
     * is called for all methods including:
     * <ul>
     * <li><strike>static initializers (<code>&lt;clinit&gt;</code>)</strike></li>
     * <li>constructors (<code>&lt;init&gt;</code>)</li>
     * <li>static methods</li>
     * <li>declared instance methods</li>
     * </ul>
     * The generated code must not declare new local variables and should
     * use the stack to handle method arguments.
     *
     * @return true if this adapter modified the method byte code
     */
    public abstract boolean onMethodEntry();

    /**
     * Generate the code required to trace method exit.
     * <ul>
     * <li><strike>static initializers (<code>&lt;clinit&gt;</code>)</strike></li>
     * <li>constructors (<code>&lt;init&gt;</code>)</li>
     * <li>static methods</li>
     * <li>declared instance methods</li>
     * </ul>
     * The generated code must declare new local variables. The result of
     * the method (if any) is on the top of the stack and must preserved by
     * the generated code.
     *
     * @return true if this adapter modified the method byte code
     */
    public abstract boolean onMethodReturn();

    /**
     * Generate the code required to trace entry to an exception handler. The <code>java.lang.Throwable</code> that
     * was caught is either on the top of the stack if <code>var</code> is -1
     * (and the value must be preserved by the generated code), or otherwise, it
     * is stored in the local variable <code>var</code>.
     * </ul>
     * stack and must be preserved by the generated code.
     * <p>
     * This method is not called for <code>finally</code> blocks.
     * </p>
     *
     * @param exception
     *            the <code>Type</code> of exceptions caught by this
     *            exception handler
     * @return true if this adapter modified the method byte code
     */
    public abstract boolean onExceptionHandlerEntry(Type exception, int var);

    /**
     * Generate the code required to trace the explicit throw of an Exception.
     * The <code>java.lang.Throwable</code> that is being thrown is on the top of
     * the stack and must be preserved by the generated code.
     *
     * @return true if this adapter modified the method byte code
     */
    public abstract boolean onThrowInstruction();

}
