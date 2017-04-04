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
package com.ibm.ws.jaxws.tools;

import com.sun.tools.ws.wscompile.WsgenTool;

/**
 *
 */
public class WsGen {

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.exit(new WsgenTool(System.out).run(args) ? 0 : 1);
    }

}
