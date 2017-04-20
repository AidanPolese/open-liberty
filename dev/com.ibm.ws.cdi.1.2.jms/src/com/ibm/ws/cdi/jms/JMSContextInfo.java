/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.jms;

/*
 * Class used to compare two different JMSContext configurations,  we override the equals and hashcode
 */
class JMSContextInfo {

    private final String connectionFactoryString;
    private final String userName;
    private final String password;
    private final int acknowledgeMode;

    JMSContextInfo(String connectionFactoryString, String userName, String password, int ackMode) {
        this.connectionFactoryString = connectionFactoryString;
        this.userName = userName;
        this.password = password;
        this.acknowledgeMode = ackMode;
    }

    public String getConnectionFactoryString() {
        return connectionFactoryString;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getAcknowledgeMode() {
        return acknowledgeMode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + acknowledgeMode;
        result = prime * result + ((connectionFactoryString == null) ? 0 : connectionFactoryString.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JMSContextInfo other = (JMSContextInfo) obj;
        if (acknowledgeMode != other.acknowledgeMode) {
            return false;
        }
        if (connectionFactoryString == null) {
            if (other.connectionFactoryString != null) {
                return false;
            }
        } else if (!connectionFactoryString.equals(other.connectionFactoryString)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (userName == null) {
            if (other.userName != null) {
                return false;
            }
        } else if (!userName.equals(other.userName)) {
            return false;
        }
        return true;
    }

}