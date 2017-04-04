/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2016
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jmx.connector.server.rest.handlers;

import java.io.IOException;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.jmx.connector.server.rest.APIConstants;
import com.ibm.ws.jmx.connector.server.rest.helpers.ErrorHelper;
import com.ibm.ws.jmx.connector.server.rest.helpers.FileTransferHelper;
import com.ibm.ws.jmx.connector.server.rest.helpers.RESTHelper;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.rest.handler.RESTHandler;
import com.ibm.wsspi.rest.handler.RESTRequest;
import com.ibm.wsspi.rest.handler.RESTResponse;
import com.ibm.wsspi.rest.handler.helper.RESTHandlerMethodNotAllowedError;

/**
 * Legacy code for V2 clients ONLY.
 * 
 * DO NOT add new functionality or change behaviour.
 */
@Component(service = { RESTHandler.class },
           configurationPolicy = ConfigurationPolicy.IGNORE,
           immediate = true,
           property = { "service.vendor=IBM",
                       RESTHandler.PROPERTY_REST_HANDLER_CONTEXT_ROOT + "=" + APIConstants.JMX_CONNECTOR_API_ROOT_PATH,
                       RESTHandler.PROPERTY_REST_HANDLER_ROOT + "=" + APIConstants.PATH_FILETRANSFER_FILEPATH })
public class FileTransferHandler implements RESTHandler {
    public static final TraceComponent tc = Tr.register(FileTransferHandler.class);

    //OSGi service
    private final String KEY_FILE_TRANSFER_HELPER = "fileTransferHelper";
    private transient FileTransferHelper fileTransferHelper;
    private final AtomicServiceReference<FileTransferHelper> fileTransferHelperRef = new AtomicServiceReference<FileTransferHelper>(KEY_FILE_TRANSFER_HELPER);

    @Activate
    protected void activate(ComponentContext context) {
        fileTransferHelperRef.activate(context);
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        fileTransferHelperRef.deactivate(context);
    }

    // FileTransferHelper
    @Reference(name = KEY_FILE_TRANSFER_HELPER, service = FileTransferHelper.class)
    protected void setFileTransferHelperRef(ServiceReference<FileTransferHelper> ref) {
        fileTransferHelperRef.setReference(ref);
    }

    protected void unsetFileTransferHelperRef(ServiceReference<FileTransferHelper> ref) {
        fileTransferHelperRef.unsetReference(ref);
    }

    @Override
    public void handleRequest(RESTRequest request, RESTResponse response) {
        String method = request.getMethod();
        if (RESTHelper.isGetMethod(method)) {
            downloadLegacy(request, response);
        } else if (RESTHelper.isPostMethod(method)) {
            uploadLegacy(request, response);
        } else if (RESTHelper.isDeleteMethod(method)) {
            deleteLegacy(request, response);
        } else {
            throw new RESTHandlerMethodNotAllowedError("GET,POST,DELETE");
        }
    }

    /**
     * @Legacy
     */
    private void downloadLegacy(RESTRequest request, RESTResponse response) {
        String filePath = RESTHelper.getRequiredParam(request, APIConstants.PARAM_FILEPATH);

        //Get the helper
        final FileTransferHelper helper = getFileTransferHelper();
        helper.downloadInternal(request, response, filePath, true);

        response.setStatus(APIConstants.STATUS_NO_CONTENT);
    }

    /**
     * @Legacy
     */
    private void uploadLegacy(RESTRequest request, RESTResponse response) {
        String filePath = RESTHelper.getRequiredParam(request, APIConstants.PARAM_FILEPATH);
        String expand = RESTHelper.getQueryParam(request, APIConstants.PARAM_EXPAND_ON_COMPLETION);
        final boolean expansion = expand != null && "true".compareToIgnoreCase(expand) == 0;

        //Get the helper
        final FileTransferHelper helper = getFileTransferHelper();
        helper.uploadInternal(request, filePath, expansion, true);

        response.setStatus(APIConstants.STATUS_NO_CONTENT);
    }

    /**
     * @Legacy
     */
    private void deleteLegacy(RESTRequest request, RESTResponse response) {
        String filePath = RESTHelper.getRequiredParam(request, APIConstants.PARAM_FILEPATH);

        //Get the helper
        final FileTransferHelper helper = getFileTransferHelper();
        helper.deleteInternal(filePath, false);

        response.setStatus(APIConstants.STATUS_NO_CONTENT);
    }

    private synchronized FileTransferHelper getFileTransferHelper() {
        if (fileTransferHelper == null) {
            fileTransferHelper = fileTransferHelperRef != null ? fileTransferHelperRef.getService() : null;

            if (fileTransferHelper == null) {
                IOException ioe = new IOException(TraceNLS.getFormattedMessage(this.getClass(),
                                                                               APIConstants.TRACE_BUNDLE_FILE_TRANSFER,
                                                                               "OSGI_SERVICE_ERROR",
                                                                               new Object[] { "FileTransferHelper" },
                                                                               "CWWKX0122E: OSGi service is not available."));
                throw ErrorHelper.createRESTHandlerJsonException(ioe, null, APIConstants.STATUS_INTERNAL_SERVER_ERROR);
            }
        }

        return fileTransferHelper;
    }
}
