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
 * 18925       03-Aug-2011 emijiang@uk Created
 * 25215       07-Oct-2011 emijiang@uk Minor tidy up
 * 26155       18-Oct-2011 emijiang@uk List all binary incompatibility changes
 * 27863       14-Nov-2011 emijiang@uk According to 3rd edition of jvm, generics should be ignored as for as binary compatibility is concerned
 */
package com.ibm.aries.buildtasks.semantic.versioning.model.decls;

import java.lang.reflect.Modifier;

public class MethodDeclaration extends GenericDeclaration
{
    private final String desc;
    private final String[] exceptions;

    public MethodDeclaration(int access, String name, String desc, String signature, String[] exceptions) {
        super(access, name, signature);
        this.desc = desc;
        this.exceptions = exceptions;
    }

    public String toXML() {
        StringBuilder sb = new StringBuilder();
        sb.append("       <methoddecl>\n");
        sb.append("         <name>" + escapeXML(getName()) + "</name>\n");
        sb.append("         <access>" + getRawAccess() + "</access>\n");
        sb.append("         <desc>" + escapeXML(getDesc()) + "</desc>\n");
        if (getSignature() != null) {
            sb.append("         <signature>" + escapeXML(getSignature()) + "</signature>\n");
        }
        if (exceptions != null && exceptions.length > 0) {
            sb.append("         <exceptions>\n");
            for (String e : exceptions) {
                sb.append("            <exception>" + e + "</exception>\n");
            }
            sb.append("         </exceptions>\n");
        }
        sb.append("       </methoddecl>\n");
        return sb.toString();
    }

    public String getDesc()
    {
        return desc;
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getAccess());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        //int result = super.hashCode();
        int result = prime + ((desc == null) ? 0 : desc.hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof MethodDeclaration)) {
            throw new RuntimeException("MethodDeclaration.equals not written to handle non same type");
        }

        //if (getClass() != obj.getClass()) return false;
        MethodDeclaration other = (MethodDeclaration) obj;
        if (desc == null) {
            if (other.desc != null)
                return false;
        } else if (!desc.equals(other.desc))
            return false;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;

        return true;
    }

}
