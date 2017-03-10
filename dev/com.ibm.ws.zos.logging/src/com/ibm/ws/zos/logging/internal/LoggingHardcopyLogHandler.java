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
package com.ibm.ws.zos.logging.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ws.kernel.zos.NativeMethodManager;
import com.ibm.ws.logging.RoutedMessage;
import com.ibm.ws.logging.WsLogHandler;
import com.ibm.ws.zos.jni.NativeMethodUtils;

/**
 *
 * An implementation of a LogHandler that routes a message to the hardcopy log.
 *
 * Note: this has been broken ever since the LogHandler package was changed.
 */
@Component(configurationPolicy = ConfigurationPolicy.IGNORE,
           property = { "service.vendor=IBM",
                        "id=HARDCOPY" })
public class LoggingHardcopyLogHandler implements WsLogHandler {

    /**
     * vector of errors
     */
    private final LoggingHandlerDiagnosticsVector savedDiagnostics = new LoggingHandlerDiagnosticsVector();

    /**
     * For translating to english.
     */
    private final LocaleHelper localeHelper = new LocaleHelper();

    /**
     * Method to set the native method manager.
     */
    @Reference
    protected void setNativeMethodManager(NativeMethodManager nativeMethodManager) {
        nativeMethodManager.registerNatives(LoggingHardcopyLogHandler.class);
    }

    /** {@inheritDoc} */
    @Override
    public void publish(RoutedMessage routedMessage) {
        String englishMsg = localeHelper.translateToEnglish(routedMessage.getFormattedMsg(),
                                                            routedMessage.getLogRecord());

        if (englishMsg != null) {
            int writeReturnCode = ntv_WriteToOperatorProgrammerAndHardcopy(NativeMethodUtils.convertToEBCDIC(englishMsg, false));
            // if there was an error save it
            if (writeReturnCode != 0) {
                savedDiagnostics.insertElementAtBegining(englishMsg, writeReturnCode);
            }
        }
    }

    /**
     * Call to native code to write the message to the programmer and hardcopy.
     *
     */
    protected native int ntv_WriteToOperatorProgrammerAndHardcopy(byte[] msg);
}
