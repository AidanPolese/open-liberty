/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package wlp.lib.extract.platform;

/**
 *
 */
public abstract class AbstractPlatformPolicyFactory {

    private Object m_platformPolicy = null;

    protected Object getPlatformPolicy() {
        if (m_platformPolicy == null) {
            if (Platform.isWindows()) {
                m_platformPolicy = createWindowsPolicy();
            } else if (Platform.isLinux()) {
                m_platformPolicy = createLinuxPolicy();
            } else if (Platform.isSolaris()) {
                m_platformPolicy = createSolarisPolicy();
            } else if (Platform.isAIX()) {
                m_platformPolicy = createAIXPolicy();
            } else if (Platform.isHPUX()) {
                m_platformPolicy = createHPUXPolicy();
            } else if (Platform.isZOS()) {
                m_platformPolicy = createZOSPolicy();
            } else if (Platform.isOS400()) {
                m_platformPolicy = createOS400Policy();
            } else if (Platform.isMACOS()) {
                m_platformPolicy = createMACOSPolicy();
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return m_platformPolicy;
    }

    /**
     * Override this method in derived class if you must provide platform
     * specific functionality
     *
     * @return newly created Platform specific object
     */
    protected Object createWindowsPolicy() {
        throw new UnsupportedOperationException();
    }

    /**
     * Override this method in derived class if you must provide platform
     * specific functionality
     *
     * @return newly created Platform specific object
     */
    protected Object createLinuxPolicy() {
        throw new UnsupportedOperationException();
    }

    /**
     * Override this method in derived class if you must provide platform
     * specific functionality
     *
     * @return newly created Platform specific object
     */
    protected Object createSolarisPolicy() {
        return createLinuxPolicy();
    }

    /**
     * Override this method in derived class if you must provide platform
     * specific functionality
     *
     * @return newly created Platform specific object
     */
    protected Object createAIXPolicy() {
        return createLinuxPolicy();
    }

    /**
     * Override this method in derived class if you must provide platform
     * specific functionality
     *
     * @return newly created Platform specific object
     */
    protected Object createHPUXPolicy() {
        return createLinuxPolicy();
    }

    /**
     * Override this method in derived class if you must provide platform
     * specific functionality
     *
     * @return newly created Platform specific object
     */
    protected Object createZOSPolicy() {
        return createLinuxPolicy();
    }

    /**
     * Override this method in derived class if you must provide platform
     * specific functionality
     *
     * @return newly created Platform specific object
     */
    protected Object createOS400Policy() {
        return createLinuxPolicy();
    }

    /**
     * Override this method in derived class if you must provide platform
     * specific functionality
     *
     * @return newly created Platform specific object
     */
    protected Object createMACOSPolicy() {
        return createLinuxPolicy();
    }

}
