/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal.impl;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import com.ibm.websphere.logging.WsLevel;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.logging.internal.impl.LoggingConstants.FFDCSummaryPolicy;
import com.ibm.ws.logging.internal.impl.LoggingConstants.TraceFormat;
import com.ibm.wsspi.logging.TextFileOutputStreamFactory;
import com.ibm.wsspi.logprovider.FFDCFilterService;
import com.ibm.wsspi.logprovider.LogProviderConfig;
import com.ibm.wsspi.logprovider.TrService;

/**
 *
 */
public class LogProviderConfigImpl implements LogProviderConfig {

    private final static TraceComponent tc = Tr.register(LogProviderConfigImpl.class);

    /** TrService delegate */
    protected final TrService trDelegate;

    /** FFDC service delegate */
    protected final FFDCFilterService ffdcDelegate;

    /** TextFileOutputStreamFactory to use when creating FileOutputStreams */
    protected final TextFileOutputStreamFactory fileStreamFactory;

    /** If true, JSR47 trace will be caught and logged over/via a TrService. */
    protected final boolean loggerUsesTr;

    /** FFDC summary policy -- how often FFDC summary files are written/updated */
    protected final FFDCSummaryPolicy ffdcSummaryPolicy;

    /** Final log location */
    protected volatile File logDirectory;

    /** Configured trace format: default is enhanced */
    protected volatile TraceFormat traceFormat = TraceFormat.ENHANCED;

    /** Format the date and time in ISO-8601 format */
    protected volatile boolean isoDateFormat = false;

    /**
     * Max file size in MB: 0 for no limit
     * (the default will come in via server.xml, enforce no limit until then unless specified
     * in bootstrap.properties).
     */
    protected volatile int maxFileSize = 0;

    /** Max file size in bytes: calculated from maxFileSize */
    protected volatile long maxFileSizeBytes = 0;

    /**
     * Max files: 0 for no limit
     * (the default will come in via server.xml, enforce no limit until then unless specified
     * in bootstrap.properties).
     */
    protected volatile int maxFiles = 0;

    /** Configured trace file name */
    protected volatile String traceFileName = LoggingConstants.DEFAULT_TRACE_FILE;

    /** Configured console file name */
    protected volatile String messageFileName = LoggingConstants.DEFAULT_MSG_FILE;

    /** Configured message Ids to be suppressed in console/message.log */
    protected volatile Collection<String> hideMessageIds = new ArrayList<String>();

    /** Configured console file name */
    protected volatile Level consoleLogLevel = WsLevel.AUDIT;

    /** Copy System.out and System.err invocations to the original system streams */
    protected volatile boolean copySystemStreams = true;

    /** The current/active trace specification */
    protected volatile String traceSpec = "*=info";

    /** The header written at the beginning of all log files. */
    private final String logHeader;

    /** True if java.lang.instrument is available for trace. */
    private final boolean javaLangInstrument;

    /**
     * Initial configuration of BaseTraceService from TrServiceConfig.
     *
     * @param config
     */
    public LogProviderConfigImpl(Map<String, String> config, File logLocation, TextFileOutputStreamFactory factory) {
        if (config == null)
            config = Collections.emptyMap();

        logDirectory = logLocation;

        fileStreamFactory = factory;

        ffdcSummaryPolicy = LoggingConfigUtils.getFFDCSummaryPolicy(config.get(LoggingConstants.PROP_FFDC_SUMMARY_POLICY),
                                                                    FFDCSummaryPolicy.DEFAULT);
        doCommonInit(config, true);

        // If the trace file name is 'java.util.logging', then Logger won't write output via Tr,
        // Tr will write output via Logger.
        loggerUsesTr = !traceFileName.equals("java.util.logging");

        final String defaultDelegate;
        if (loggerUsesTr) {
            defaultDelegate = LoggingConstants.DEFAULT_TRACE_IMPLEMENTATION;
        } else {
            defaultDelegate = LoggingConstants.JSR47_TRACE_IMPLEMENTATION;
        }

        trDelegate = LoggingConfigUtils.getDelegate(TrService.class,
                                                    config.get(LoggingConstants.PROP_TRACE_DELEGATE),
                                                    defaultDelegate);

        ffdcDelegate = LoggingConfigUtils.getDelegate(FFDCFilterService.class,
                                                      config.get(LoggingConstants.PROP_FFDC_DELEGATE),
                                                      LoggingConstants.DEFAULT_FFDC_IMPLEMENTATION);

        logHeader = getLogHeader(config);

        javaLangInstrument = Boolean.parseBoolean(config.get("java.lang.instrument"));

    }

    /**
     * Update RAS/Tr configuration based on properties read/parsed/processed at runtime.
     * See metatype.xml
     */
    @Override
    public synchronized void update(Map<String, Object> config) {
        doCommonInit(config, false);
    }

    @SuppressWarnings("rawtypes")
    protected void doCommonInit(Map config, boolean isInit) {

        // Brazen cheating. Map<String, String> and Map<String, Object> both come here.
        // We can't use ? in the generic value, as we need to be able to modify (add an element)
        // to the map. So we're cheating.
        @SuppressWarnings("unchecked")
        Map<String, Object> c = config;
        maxFiles = InitConfgAttribute.MAX_NUM_FILES.getIntValue(c, maxFiles, isInit);
        maxFileSize = InitConfgAttribute.FILE_MAX_SIZE.getIntValue(c, maxFileSize, isInit);
        maxFileSizeBytes = maxFileSize * 1024L * 1024L;

        traceSpec = InitConfgAttribute.TRACE_SPEC.getStringValue(c, traceSpec, isInit);
        traceFormat = InitConfgAttribute.TRACE_FORMAT.getTraceFormatValue(c, traceFormat, isInit);

        isoDateFormat = InitConfgAttribute.ISO_DATE_FORMAT.getBooleanValue(c, isoDateFormat, isInit);

        consoleLogLevel = InitConfgAttribute.CONSOLE_LOG_LEVEL.getLogLevelValue(c, consoleLogLevel, isInit);
        copySystemStreams = InitConfgAttribute.COPY_SYSTEM_STREAMS.getBooleanValue(c, copySystemStreams, isInit);

        traceFileName = InitConfgAttribute.TRACE_FILE_NAME.getStringValue(c, traceFileName, isInit);
        messageFileName = InitConfgAttribute.MSG_FILE_NAME.getStringValue(c, messageFileName, isInit);
        logDirectory = InitConfgAttribute.LOG_LOCATION.getLogDirectory(c, logDirectory, isInit);

        hideMessageIds = InitConfgAttribute.HIDE_MESSAGES.getStringCollectionValue(c, hideMessageIds, isInit);

    }

    /**
     * Returns the header to be written at the beginning of all log files.
     */
    static String getLogHeader(Map<String, String> config) {
        StringBuilder builder = new StringBuilder(512);

        String productInfo = config.get("websphere.product.info");
        if (productInfo != null) {
            builder.append("product = ").append(productInfo).append(LoggingConstants.nl);
        }

        String installDir = config.get("wlp.install.dir");
        if (installDir != null) {
            builder.append("wlp.install.dir = ").append(installDir).append(LoggingConstants.nl);
        }

        String serverConfigDir = config.get("server.config.dir");
        if (serverConfigDir != null && !"true".equals(config.get("wlp.user.dir.isDefault"))) {
            builder.append("server.config.dir = ").append(serverConfigDir).append(LoggingConstants.nl);
        }

        String serverOutputDir = config.get("server.output.dir");
        if (serverOutputDir != null && !serverOutputDir.equals(serverConfigDir)) {
            builder.append("server.output.dir = ").append(serverOutputDir).append(LoggingConstants.nl);
        }

        builder.append("java.home = ").append(System.getProperty("java.home")).append(LoggingConstants.nl);
        builder.append("java.version = ").append(System.getProperty("java.version")).append(LoggingConstants.nl);
        builder.append("java.runtime = ").append(System.getProperty("java.runtime.name")).append(" (").append(System.getProperty("java.runtime.version")).append(')').append(LoggingConstants.nl);

        builder.append("os = ").append(System.getProperty("os.name")).append(" (").append(System.getProperty("os.version")).append("; ").append(System.getProperty("os.arch")).append(") (").append(Locale.getDefault()).append(")").append(LoggingConstants.nl);

        builder.append("process = ").append(ManagementFactory.getRuntimeMXBean().getName()).append(LoggingConstants.nl);
        return builder.toString();
    }

    public String getLogHeader() {
        return logHeader;
    }

    public boolean hasJavaLangInstrument() {
        return javaLangInstrument;
    }

    @Override
    public File getLogDirectory() {
        return this.logDirectory;
    }

    public TraceFormat getTraceFormat() {
        return traceFormat;
    }

    public boolean getIsoDateFormat() {
        return isoDateFormat;
    }

    /**
     * @return the maxTraceFileSize
     */
    public long getMaxFileBytes() {
        return maxFileSizeBytes;
    }

    /**
     * @return the maxTraceFiles
     */
    @Override
    public int getMaxFiles() {
        return maxFiles;
    }

    @Override
    public TextFileOutputStreamFactory getTextFileOutputStreamFactory() {
        return fileStreamFactory;
    }

    /**
     * @return the traceFileName
     */
    public String getTraceFileName() {
        return traceFileName;
    }

    /**
     * @return the configured TraceSpecification
     */
    @Override
    public String getTraceString() {
        return traceSpec;
    }

    /**
     * @return the consoleOutput
     */
    public String getMessageFileName() {
        return messageFileName;
    }

    public Collection<String> getMessagesToHide() {
        return hideMessageIds;
    }

    public Level getConsoleLogLevel() {
        return consoleLogLevel;
    }

    public boolean copySystemStreams() {
        return copySystemStreams;
    }

    @Override
    public TrService getTrDelegate() {
        return trDelegate;
    }

    @Override
    public FFDCFilterService getFfdcDelegate() {
        return ffdcDelegate;
    }

    public FFDCSummaryPolicy getFfdcSummaryPolicy() {
        return ffdcSummaryPolicy;
    }

    /**
     * @return true if we should use the logger -> tr handler
     */
    public boolean loggerUsesTr() {
        return loggerUsesTr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append("[maxFileSize=").append(maxFileSize);
        sb.append(",maxFiles=").append(maxFiles);
        sb.append(",logDirectory=").append(logDirectory);
        sb.append(",consoleLogLevel=").append(consoleLogLevel.getName());
        sb.append(",copySystemStreams=").append(copySystemStreams);
        sb.append(",messageFileName=").append(messageFileName);
        sb.append(",traceFormat=").append(traceFormat);
        sb.append(",isoDateFormat=").append(isoDateFormat);
        sb.append(",traceFileName=").append(traceFileName);
        sb.append("]");

        return sb.toString();
    }

    private static enum InitConfgAttribute {

        FILE_MAX_SIZE("maxFileSize", "com.ibm.ws.logging.max.file.size"),
        MAX_NUM_FILES("maxFiles", "com.ibm.ws.logging.max.files"),
        CONSOLE_LOG_LEVEL("consoleLogLevel", "com.ibm.ws.logging.console.log.level"),
        COPY_SYSTEM_STREAMS("copySystemStreams", "com.ibm.ws.logging.copy.system.streams"),
        LOG_LOCATION("logDirectory", "com.ibm.ws.logging.log.directory"),
        MSG_FILE_NAME("messageFileName", "com.ibm.ws.logging.message.file.name"),
        TRACE_FILE_NAME("traceFileName", "com.ibm.ws.logging.trace.file.name"),
        TRACE_SPEC("traceSpecification", "com.ibm.ws.logging.trace.specification"),
        TRACE_FORMAT("traceFormat", "com.ibm.ws.logging.trace.format"),
        ISO_DATE_FORMAT("isoDateFormat", "com.ibm.ws.logging.isoDateFormat"),
        HIDE_MESSAGES("hideMessage", "com.ibm.ws.logging.hideMessage");

        final String configKey;
        final String propertyKey;

        InitConfgAttribute(String cfgKey, String propKey) {
            configKey = cfgKey;
            propertyKey = propKey;
        }

        boolean getBooleanValue(Map<String, Object> config, boolean defaultValue, boolean isInit) {
            Object value = config.get(isInit ? propertyKey : configKey);
            return LoggingConfigUtils.getBooleanValue(value, defaultValue);
        }

        int getIntValue(Map<String, Object> config, int defaultValue, boolean isInit) {
            Object value = config.get(isInit ? propertyKey : configKey);
            return LoggingConfigUtils.getIntValue(value, defaultValue);
        }

        String getStringValue(Map<String, Object> config, String defaultValue, boolean isInit) {
            Object value = config.get(isInit ? propertyKey : configKey);
            return LoggingConfigUtils.getStringValue(value, defaultValue);
        }

        TraceFormat getTraceFormatValue(Map<String, Object> config, TraceFormat defaultValue, boolean isInit) {
            Object value = config.get(isInit ? propertyKey : configKey);
            TraceFormat newValue = LoggingConfigUtils.getFormatValue(value, defaultValue);
            if (isInit && newValue != defaultValue) {
                config.put(propertyKey, newValue.name());
            }
            return newValue;
        }

        Level getLogLevelValue(Map<String, Object> config, Level defaultValue, boolean isInit) {
            Object value = config.get(isInit ? propertyKey : configKey);
            return LoggingConfigUtils.getLogLevel(value, defaultValue);
        }

        File getLogDirectory(Map<String, Object> config, File defaultValue, boolean isInit) {
            Object value = config.get(isInit ? propertyKey : configKey);
            File newValue = LoggingConfigUtils.getLogDirectory(value, defaultValue);
            if (isInit && value == null) {
                // For initial parameters, we're provided the log directory coming in.
                // That directory should be reflected in properties to make sure that later
                // use of ibm:variable will find this initial directory.
                config.put(propertyKey, newValue.getAbsolutePath());
            }
            return newValue;
        }

        Collection<String> getStringCollectionValue(Map<String, Object> config, Collection<String> defaultValue, boolean isInit) {
            Object value = config.get(isInit ? propertyKey : configKey);
            Collection<String> hideMessageIds = LoggingConfigUtils.parseStringCollection("hideMessage", value, defaultValue);
            return hideMessageIds;
        }

    }
}
