/*
 * @start_prolog@
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 * 
 * Change activity:
 * 
 * Issue       Date        Name        Description
 * ----------- ----------- --------    ------------------------------------
 * 26539       07-Nov-2011 emijiang@uk Created.
 */
package com.ibm.aries.buildtasks.semantic.versioning;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class EmptyClassVisitor extends ClassVisitor {

    public EmptyClassVisitor() {
        super(Opcodes.ASM5);
    }
}
