/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.aries.buildtasks.semantic.versioning.model.decls;

import java.lang.reflect.Modifier;

import org.objectweb.asm.Opcodes;

public abstract class GenericDeclaration {

    private final int access;
    private final String name;
    private final String signature;

    protected String escapeXML(String raw) {

        if (raw == null)
            return null;

        //if(raw.matches(".*[<>&\"'\\\\].*")){
        //we're about to insert ampersands, so we need to make the original ampersands special enough that we can find them later ;p
        //if the below string pattern already occurs.. then we need to find a new one ;p
        String magic = "~@!#&#!@~";
        while (raw.indexOf(magic) != -1) {
            magic += "@";
        }
        raw = raw.replaceAll("&", magic);
        raw = raw.replaceAll("<", "&lt;");
        raw = raw.replaceAll(">", "&gt;");
        raw = raw.replaceAll("\"", "&quot;");
        raw = raw.replaceAll("'", "&apos;");
        raw = raw.replaceAll("\\\\", "&#039;");
        raw = raw.replace(magic, "&amp;");
        //}

        return raw;
    }

    public GenericDeclaration(int access, String name, String signature) {
        this.access = access;
        this.name = name;
        this.signature = signature;
    }

    public int getRawAccess() {
        return access;
    }

    public int getAccess() {
        int updatedAccess = access;
        // ignore the native or synchronized modifier as they do not affect binary compatibility
        if (Modifier.isNative(access)) {
            updatedAccess = updatedAccess - Opcodes.ACC_NATIVE;
        }
        if (Modifier.isSynchronized(access)) {
            updatedAccess = updatedAccess - Opcodes.ACC_SYNCHRONIZED;
        }
        return updatedAccess;
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public boolean isFinal() {
        return Modifier.isFinal(access);
    }

    public boolean isStatic() {
        return Modifier.isStatic(access);
    }

    public boolean isPublic() {
        return Modifier.isPublic(access);
    }

    public boolean isProtected() {
        return Modifier.isProtected(access);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(access);
    }

    public boolean isInterface() {
        return Modifier.isInterface(access);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + access;
        result = prime * result + ((name == null) ? 0 : name.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GenericDeclaration other = (GenericDeclaration) obj;
        if (access != other.access)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;

        return true;
    }

}