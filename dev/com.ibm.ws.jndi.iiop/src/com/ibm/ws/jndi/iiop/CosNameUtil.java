package com.ibm.ws.jndi.iiop;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;

import org.omg.CosNaming.NameComponent;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.jndi.WSName;

public enum CosNameUtil {
    ;
    static NameComponent[] cosify(WSName name) throws InvalidNameException {
        NameComponent[] cosName = new NameComponent[name.size()];
        for (int i = 0; i < cosName.length; i++)
            cosName[i] = new NameComponent(name.get(i), "");
        return cosName;
    }

    @FFDCIgnore(InvalidNameException.class)
    static <T extends NamingException> T detailed(T toThrow, Exception initCause, NameComponent...rest_of_name) {
        toThrow.initCause(initCause);
        if (rest_of_name != null && rest_of_name.length > 0) {
            try {
                toThrow.setRemainingName(compose(rest_of_name));
            } catch (InvalidNameException e) {
                toThrow.addSuppressed(e);
            }
        }
        return toThrow;
    }

    private static CompositeName compose(NameComponent... rest_of_name) throws InvalidNameException {
        WSName wsName = new WSName();
        for (int i = 0; i < rest_of_name.length; i++) {
            wsName = wsName.plus(rest_of_name[i].id);
        }
        CompositeName cName = new CompositeName();
        cName.add(wsName.toString());
        return cName;
    }
}
