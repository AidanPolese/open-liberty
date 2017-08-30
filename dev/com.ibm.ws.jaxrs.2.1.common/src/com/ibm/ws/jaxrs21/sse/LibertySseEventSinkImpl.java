/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs21.sse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseEventSink;

import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

/**
 * This class implements the <code>SseEventSink</code> that is injected into
 * resource fields/methods and represent a client's handle to SSE events.
 */
public class LibertySseEventSinkImpl implements SseEventSink {
    private final static TraceComponent tc = Tr.register(LibertySseEventSinkImpl.class);

    private final MessageBodyWriter<OutboundSseEvent> writer;
    private final Message message;
    private final HttpServletResponse response;

    public LibertySseEventSinkImpl(MessageBodyWriter<OutboundSseEvent> writer, Message message) {
        this.writer = writer;
        this.message = message;
        this.response = message.get(HttpServletResponse.class);

        message.getExchange().put(JAXRSUtils.IGNORE_MESSAGE_WRITERS, "true");
    }

    private volatile boolean closed;
    /* (non-Javadoc)
     * @see javax.ws.rs.sse.SseEventSink#close()
     */
    @Override
    public void close() {
        if (!closed) {
            closed = true;
            try {
                response.getOutputStream().close();
            } catch (IOException ex) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Failed to close response stream", ex);
                }
            } finally {
                ServerProviderFactory.releaseRequestState(message);
            }
            
            //TODO: remove from all broadcasters
        }

    }

    /* (non-Javadoc)
     * @see javax.ws.rs.sse.SseEventSink#isClosed()
     */
    @Override
    public boolean isClosed() {
        return closed;
    }

    /* (non-Javadoc)
     * @see javax.ws.rs.sse.SseEventSink#send(javax.ws.rs.sse.OutboundSseEvent)
     */
    @Override
    public CompletionStage<?> send(OutboundSseEvent event) {
        final CompletableFuture<?> future = new CompletableFuture<>();

        if (!closed && writer != null) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                writer.writeTo(event, event.getClass(), null, new Annotation [] {}, event.getMediaType(), null, os);

                String eventContents = os.toString();
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "send - sending " + eventContents);
                }
                
                if (!response.isCommitted()) {
                    response.setHeader("Content-Type", MediaType.SERVER_SENT_EVENTS);
                    response.flushBuffer();
                }
                
                //TODO: this seems like a bug, but most SSE clients seem to expect a named event
                //      so for now, we will provide one if one is not provided by the user
                if (event.getName() == null) {
                    response.getOutputStream().print("    UnnamedEvent\n");
                }
                response.getOutputStream().println(eventContents);
                response.getOutputStream().flush();
                
                return CompletableFuture.completedFuture(eventContents);
            } catch (WebApplicationException | IOException ex) {
                //TODO: convert to warning?
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "send - failed sending event " + event);
                    future.completeExceptionally(ex);
                }
            }
        } else {
            future.complete(null);
        }

        return future;
    }
}
