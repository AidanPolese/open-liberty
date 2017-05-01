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
package com.ibm.ws.jmx.connector.server.rest.helpers;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.wsspi.kernel.service.location.WsLocationConstants;

@Trivial
public enum ServerPath {
    INSTALL_DIR {
        @Override
        public String getSymbol() {
            return WsLocationConstants.SYMBOL_INSTALL_DIR;
        }

        @Override
        public String getName() {
            return WsLocationConstants.LOC_INSTALL_DIR;
        }

        @Override
        public String getDefault(String userDir, String serverName) {
            int index = userDir.lastIndexOf("usr/");
            return userDir.substring(0, index);
        }
    },
    USER_DIR {
        @Override
        public String getSymbol() {
            return WsLocationConstants.SYMBOL_USER_DIR;
        }

        @Override
        public String getName() {
            return WsLocationConstants.LOC_USER_DIR;
        }

        @Override
        public String getDefault(String userDir, String serverName) {
            return userDir;
        }
    },
    OUTPUT_DIR {
        @Override
        public String getSymbol() {
            return WsLocationConstants.SYMBOL_SERVER_OUTPUT_DIR;
        }

        @Override
        public String getName() {
            return WsLocationConstants.LOC_SERVER_OUTPUT_DIR;
        }

        @Override
        public String getDefault(String userDir, String serverName) {
            return userDir + "servers/" + serverName + "/";
        }
    },
    CONFIG_DIR {
        @Override
        public String getSymbol() {
            return WsLocationConstants.SYMBOL_SERVER_CONFIG_DIR;
        }

        @Override
        public String getName() {
            return WsLocationConstants.LOC_SERVER_CONFIG_DIR;
        }

        @Override
        public String getDefault(String userDir, String serverName) {
            return userDir + "servers/" + serverName + "/";
        }
    },
    SHARED_APPS_DIR {
        @Override
        public String getSymbol() {
            return WsLocationConstants.SYMBOL_SHARED_APPS_DIR;
        }

        @Override
        public String getName() {
            return WsLocationConstants.LOC_SHARED_APPS_DIR;
        }

        @Override
        public String getDefault(String userDir, String serverName) {
            return userDir + "shared/apps/";
        }
    },
    SHARED_CONFIG_DIR {
        @Override
        public String getSymbol() {
            return WsLocationConstants.SYMBOL_SHARED_CONFIG_DIR;
        }

        @Override
        public String getName() {
            return WsLocationConstants.LOC_SHARED_CONFIG_DIR;
        }

        @Override
        public String getDefault(String userDir, String serverName) {
            return userDir + "shared/config/";
        }
    },
    SHARED_RESC_DIR {
        @Override
        public String getSymbol() {
            return WsLocationConstants.SYMBOL_SHARED_RESC_DIR;
        }

        @Override
        public String getName() {
            return WsLocationConstants.LOC_SHARED_RESC_DIR;
        }

        @Override
        public String getDefault(String userDir, String serverName) {
            return userDir + "shared/resources/";
        }
    };

    public abstract String getSymbol();

    public abstract String getName();

    public abstract String getDefault(String userDir, String serverName);
}
