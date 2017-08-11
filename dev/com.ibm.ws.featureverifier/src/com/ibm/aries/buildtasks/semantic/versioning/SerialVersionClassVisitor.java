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
 */
package com.ibm.aries.buildtasks.semantic.versioning;

import java.io.IOException;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.SerialVersionUIDAdder;

public class SerialVersionClassVisitor extends SerialVersionUIDAdder {

    public SerialVersionClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);

    }

    public long getComputeSerialVersionUID() {

        try {
            return computeSVUID();
        } catch (IOException ioe) {
            // not a issue						
        }
        //If you start seeing these.. you're probably invoking getComputeSerialVersionUID on an interface (I was).

        //catch (NullPointerException e){
//			[err] java.lang.NullPointerException
//			[err] 	at org.objectweb.asm.commons.SerialVersionUIDAdder.computeSVUID(Unknown Source)
//			[err] 	at com.ibm.aries.buildtasks.semantic.versioning.SerialVersionClassVisitor.getComputeSerialVersionUID(SerialVersionClassVisitor.java:36)
//			[err] 	at com.ibm.aries.buildtasks.semantic.versioning.decls.LiveClassDeclaration.getSerialVersionUID(LiveClassDeclaration.java:176)
//			[err] 	at com.ibm.aries.buildtasks.semantic.versioning.decls.ClassDeclaration.toXML(ClassDeclaration.java:59)
        //}

        return 0;
    }
}
