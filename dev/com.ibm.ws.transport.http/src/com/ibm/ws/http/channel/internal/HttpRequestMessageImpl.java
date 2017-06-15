/*******************************************************************************
 * Copyright (c) 2004, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.http.channel.internal;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.genericbnf.internal.GenericConstants;
import com.ibm.ws.genericbnf.internal.GenericUtils;
import com.ibm.ws.http.channel.internal.outbound.HttpOutboundServiceContextImpl;
import com.ibm.ws.http.internal.GenericEnumWrap;
import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.genericbnf.BNFHeaders;
import com.ibm.wsspi.genericbnf.HeaderStorage;
import com.ibm.wsspi.genericbnf.exception.MalformedMessageException;
import com.ibm.wsspi.genericbnf.exception.MessageSentException;
import com.ibm.wsspi.genericbnf.exception.UnsupportedMethodException;
import com.ibm.wsspi.genericbnf.exception.UnsupportedProtocolVersionException;
import com.ibm.wsspi.genericbnf.exception.UnsupportedSchemeException;
import com.ibm.wsspi.http.HttpCookie;
import com.ibm.wsspi.http.channel.HttpChannelUtils;
import com.ibm.wsspi.http.channel.HttpRequestMessage;
import com.ibm.wsspi.http.channel.HttpServiceContext;
import com.ibm.wsspi.http.channel.inbound.HttpInboundServiceContext;
import com.ibm.wsspi.http.channel.outbound.HttpOutboundServiceContext;
import com.ibm.wsspi.http.channel.values.HttpHeaderKeys;
import com.ibm.wsspi.http.channel.values.MethodValues;
import com.ibm.wsspi.http.channel.values.SchemeValues;
import com.ibm.wsspi.http.channel.values.VersionValues;

/**
 * Class representing an HTTP request message. This contains the Request-Line
 * of "Method Resource Version", plus any headers set on the message.
 *
 */
public class HttpRequestMessageImpl extends HttpBaseMessageImpl implements HttpRequestMessage {
    /** RAS trace variable */
    private static final TraceComponent tc = Tr.register(HttpRequestMessageImpl.class, HttpMessages.HTTP_TRACE_NAME, HttpMessages.HTTP_BUNDLE);

    /** Serialization ID field */
    private static final long serialVersionUID = 5759292362534888246L;

    /** Static representation of a left bracket */
    private static final byte LEFT_BRACKET = '[';
    /** Static representation of a right bracket */
    private static final byte RIGHT_BRACKET = ']';
    /** Default URI is just a slash */
    private static final byte[] SLASH = { '/' };
    /** Default byte[] for a star */
    private static final byte[] STAR = { '*' };
    /** Value used when a search target is not present */
    private static final int NOT_PRESENT = -1;
    /** Value used before a search target has been tested */
    private static final int NOT_TESTED = -2;

    /** Request method for the message */
    private transient MethodValues myMethod = MethodValues.UNDEF;
    /** Scheme (protocol) for the message */
    private transient SchemeValues myScheme = null;
    /** Request-Resource as a byte[] */
    private byte[] myURIBytes = SLASH;
    /** URI as a string */
    private transient String myURIString = null;
    /** Query string as a byte[] */
    private byte[] myQueryBytes = null;
    /** Host string in the request URL (if present) */
    private transient String sUrlHost = null;
    /** Host string parsed from Host header */
    private transient String sHdrHost = null;
    /** Port value in the request URL (if present) */
    private transient int iUrlPort = HeaderStorage.NOTSET;
    /** Port value parsed from Host header */
    private transient int iHdrPort = NOT_TESTED;
    /** Map of the query name/value pieces */
    private transient Map<String, String[]> queryParams = null;

    /**
     * Default constructor with no service context.
     * <P>
     * Warning: this object will be prone to NPEs if used prior to the init with a real service context.
     *
     */
    public HttpRequestMessageImpl() {
        super();
        // for requests, we don't care about the validation
        setHeaderValidation(false);
        setOwner(null);
        setBinaryParseState(HttpInternalConstants.PARSING_BINARY_VERSION);
    }

    /**
     * Constructor for an inbound request.
     *
     * @param isc
     */
    public HttpRequestMessageImpl(HttpInboundServiceContext isc) {
        super();
        init(isc);
    }

    /**
     * Constructor for an outbound request.
     *
     * @param osc
     */
    public HttpRequestMessageImpl(HttpOutboundServiceContext osc) {
        super();
        // for requests, we don't care about the validation
        setHeaderValidation(false);
        setOwner(osc);
        setBinaryParseState(HttpInternalConstants.PARSING_BINARY_VERSION);
    }

    /**
     * Initialize this incoming HTTP request message.
     *
     * @param sc
     */
    public void init(HttpInboundServiceContext sc) {
        // for requests, we don't care about the validation
        setHeaderValidation(false);
        setOwner(sc);
        setBinaryParseState(HttpInternalConstants.PARSING_BINARY_VERSION);
    }

    /**
     * Initialize this outgoing HTTP request message.
     *
     * @param sc
     */
    public void init(HttpOutboundServiceContext sc) {
        // for requests, we don't care about the validation
        setHeaderValidation(false);
        setOwner(sc);
        setBinaryParseState(HttpInternalConstants.PARSING_BINARY_VERSION);
        setVersion(getServiceContext().getHttpConfig().getOutgoingVersion());
    }

    /**
     * Initialize this incoming HTTP request message with specific headers,
     * ie. ones stored in a cache perhaps.
     *
     * @param sc
     * @param hdrs
     */
    public void init(HttpInboundServiceContext sc, BNFHeaders hdrs) {
        // for requests, we don't care about the validation
        setHeaderValidation(false);
        setOwner(sc);
        setBinaryParseState(HttpInternalConstants.PARSING_BINARY_VERSION);
        if (null != hdrs) {
            hdrs.duplicate(this);
        }
    }

    /**
     * Initialize this outgoing HTTP request message with specific headers,
     * ie. ones stored in a cache perhaps.
     *
     * @param sc
     * @param hdrs
     */
    public void init(HttpOutboundServiceContext sc, BNFHeaders hdrs) {
        // for requests, we don't care about the validation
        setHeaderValidation(false);
        setOwner(sc);
        setBinaryParseState(HttpInternalConstants.PARSING_BINARY_VERSION);
        if (null != hdrs) {
            hdrs.duplicate(this);
        }
        setVersion(getServiceContext().getHttpConfig().getOutgoingVersion());
    }

    /**
     * Set the "ownership" of this message to either a new servicecontext, i.e.
     * a message is being transfered from an inbound SC to and outbound SC, or
     * a channel wants to cache this message and would pass in null to this
     * method.
     *
     * @param hsc
     */
    public void setOwner(HttpServiceContext hsc) {
        // if we have an SC owner, set it's flag to false
        if (null != getServiceContext()) {
            getServiceContext().setRequestOwner(false);
        }
        // if the owner is a new HSC, then init the flags
        if (null != hsc) {
            super.init(hsc);
            getServiceContext().setRequestOwner(true);
            setIncoming(getServiceContext().isInboundConnection());
        }
    }

    /*
     * @see com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#isBodyExpected()
     */
    @Override
    public boolean isBodyExpected() {

        // check basic validation first
        if (super.isBodyExpected()) {
            // return whatever is default for this method
            return this.myMethod.isBodyAllowed();
        }

        // no body here
        return false;
    }

    /*
     * @see com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#isBodyAllowed()
     */
    @Override
    public boolean isBodyAllowed() {

        // requests must be delimited by something (not socket closure) so
        // the behavior here is the same as isBodyExpected
        return isBodyExpected();
    }

    /*
     * @see com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#clear()
     */
    @Override
    public void clear() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Clearing this request: " + this);
        }
        super.clear();
        this.myMethod = MethodValues.UNDEF;
        this.myScheme = null;
        this.myURIBytes = SLASH;
        this.myURIString = null;
        this.myQueryBytes = null;
        this.queryParams = null;
        this.sUrlHost = null;
        this.sHdrHost = null;
        this.iUrlPort = HeaderStorage.NOTSET;
        this.iHdrPort = NOT_TESTED;
    }

    /*
     * @see com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#destroy()
     */
    @Override
    public void destroy() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Destroying this request: " + this);
        }
        HttpObjectFactory tempFactory = getObjectFactory();
        super.destroy();
        if (null != tempFactory) {
            tempFactory.releaseRequest(this);
        }
    }

    /*
     * @see
     * com.ibm.ws.genericbnf.internal.GenericMessageImpl#setParsedFirstToken(byte
     * [])
     */
    @Override
    protected void setParsedFirstToken(byte[] token) throws Exception {
        setMethod(MethodValues.find(token));
    }

    /*
     * @see
     * com.ibm.ws.genericbnf.internal.GenericMessageImpl#setParsedSecondToken(
     * byte[])
     */
    @Override
    protected void setParsedSecondToken(byte[] token) throws Exception {
        setRequestURL(token);
    }

    /*
     * @see
     * com.ibm.ws.genericbnf.internal.GenericMessageImpl#setParsedThirdToken(byte
     * [])
     */
    @Override
    protected void setParsedThirdToken(byte[] token) throws Exception {
        setVersion(token);
    }

    /*
     * @see com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#parsingComplete()
     */
    @Override
    protected void parsingComplete() throws MalformedMessageException {

        // HTTP/0.9 requests might omit the version and only have 2 tokens
        int numTokens = getNumberFirstLineTokens(); // save a local copy
        if (2 == numTokens) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Received a request without a version ID");
            }
            throw new UnsupportedProtocolVersionException("Missing version");

            // otherwise it better have 3 to be valid
        } else if (3 != numTokens) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "numFirstLineTokensRead is " + numTokens);
            }
            throw new MalformedMessageException("Received " + numTokens + " first line tokens");
        }
    }

    /*
     * @see
     * com.ibm.ws.genericbnf.internal.GenericMessageImpl#getMarshalledFirstToken()
     */
    @Override
    protected byte[] getMarshalledFirstToken() {
        return this.myMethod.getByteArray();
    }

    /*
     * @see
     * com.ibm.ws.genericbnf.internal.GenericMessageImpl#getMarshalledSecondToken
     * ()
     */
    @Override
    protected byte[] getMarshalledSecondToken() {
        return getResource();
    }

    /*
     * @see
     * com.ibm.ws.genericbnf.internal.GenericMessageImpl#getMarshalledThirdToken()
     */
    @Override
    protected byte[] getMarshalledThirdToken() {
        return getVersionValue().getByteArray();
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#parseBinaryFirstLine
     * (com.ibm.wsspi.bytebuffer.WsByteBuffer)
     */
    @Override
    public boolean parseBinaryFirstLine(WsByteBuffer buff) throws MalformedMessageException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "parseBinaryFirstLine for " + this);
            Tr.debug(tc, "Buffer: " + buff);
        }
        if (getBinaryParseState() == HttpInternalConstants.PARSING_BINARY_VERSION) {
            if (!buff.hasRemaining()) {
                return false;
            }
            byte version = buff.get();
            if (version != HttpInternalConstants.BINARY_TRANSPORT_V1) {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Unsupported binary version in message: " + version);
                }
                throw new MalformedMessageException("Invalid binary message");
            }
            setBinaryParseState(HttpInternalConstants.PARSING_METHOD_ID_OR_LEN);
            resetCacheToken(4);
        }

        boolean complete = false;
        int value;
        while (!complete) {
            // at this point, the parsed token array is always set up, so we
            // can try to fill it now
            if (!fillCacheToken(buff)) {
                return false;
            }
            switch (getBinaryParseState()) {
                case HttpInternalConstants.PARSING_METHOD_ID_OR_LEN:
                    value = GenericUtils.asInt(getParsedToken());
                    if (0 == (value & GenericConstants.KNOWN_MASK)) {
                        setMethod(MethodValues.getByOrdinal(value));
                        setBinaryParseState(HttpInternalConstants.PARSING_URI_LEN);
                        resetCacheToken(4);
                    } else {
                        setBinaryParseState(HttpInternalConstants.PARSING_UNKNOWN_METHOD);
                        resetCacheToken(value & GenericConstants.UNKNOWN_MASK);
                    }
                    break;
                case HttpInternalConstants.PARSING_UNKNOWN_METHOD:
                    setMethod(MethodValues.find(getParsedToken()));
                    setBinaryParseState(HttpInternalConstants.PARSING_URI_LEN);
                    createCacheToken(4);
                    break;

                case HttpInternalConstants.PARSING_URI_LEN:
                    setBinaryParseState(HttpInternalConstants.PARSING_URI);
                    resetCacheToken(GenericUtils.asInt(getParsedToken()));
                    break;

                case HttpInternalConstants.PARSING_URI:
                    setRequestURL(getParsedToken());
                    setBinaryParseState(HttpInternalConstants.PARSING_VERSION_ID_OR_LEN);
                    createCacheToken(4);
                    break;

                case HttpInternalConstants.PARSING_VERSION_ID_OR_LEN:
                    value = GenericUtils.asInt(getParsedToken());
                    if (0 == (value & GenericConstants.KNOWN_MASK)) {
                        setVersion(VersionValues.getByOrdinal(value));
                        setBinaryParseState(GenericConstants.PARSING_HDR_FLAG);
                        resetCacheToken(4);
                        complete = true;
                    } else {
                        setBinaryParseState(HttpInternalConstants.PARSING_UNKNOWN_VERSION);
                        resetCacheToken(value & GenericConstants.UNKNOWN_MASK);
                    }
                    break;

                case HttpInternalConstants.PARSING_UNKNOWN_VERSION:
                    setVersion(VersionValues.find(getParsedToken()));
                    setBinaryParseState(GenericConstants.PARSING_HDR_FLAG);
                    createCacheToken(4);
                    complete = true;
                    break;

                default:
                    throw new MalformedMessageException("Invalid state in first line: " + getBinaryParseState());
            } // end of switch block

        } // end of while loop

        setFirstLineComplete(true);
        return true;
    }

    /*
     * @see com.ibm.ws.genericbnf.internal.GenericMessageImpl#marshallLine()
     */
    @Override
    public WsByteBuffer[] marshallLine() {
        WsByteBuffer[] firstLine = new WsByteBuffer[1];
        firstLine[0] = allocateBuffer(getOutgoingBufferSize());

        firstLine = putBytes(this.myMethod.getByteArray(), firstLine);
        firstLine = putByte(SPACE, firstLine);
        firstLine = putBytes(getMarshalledSecondToken(), firstLine);
        firstLine = putByte(SPACE, firstLine);
        firstLine = putBytes(getVersionValue().getByteArray(), firstLine);
        firstLine = putBytes(BNFHeaders.EOL, firstLine);
        // don't flip the last buffer as headers get tacked on the end
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            String url = GenericUtils.nullOutPasswords(getMarshalledSecondToken(), (byte) '&');
            Tr.event(tc, "Marshalling first line: " + getMethod() + " " + url + " " + getVersion());
        }
        return firstLine;
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#marshallBinaryFirstLine
     * ()
     */
    @Override
    public WsByteBuffer[] marshallBinaryFirstLine() {
        WsByteBuffer[] firstLine = new WsByteBuffer[1];
        firstLine[0] = allocateBuffer(getOutgoingBufferSize());
        byte[] data = null;

        // marshall the binary protocol version first
        firstLine = putByte(HttpInternalConstants.BINARY_TRANSPORT_V1, firstLine);

        if (getMethodValue().isUndefined()) {
            // unknown method....marshall (masked(len)+Str)
            data = this.myMethod.getByteArray();
            firstLine = putInt(data.length | GenericConstants.KNOWN_MASK, firstLine);
            firstLine = putBytes(data, firstLine);
        } else {
            // known method...marshall methodID
            firstLine = putInt(this.myMethod.getOrdinal(), firstLine);
        }

        // URI(len + str)
        byte[] uri = getResource();
        firstLine = putInt(uri.length, firstLine);
        firstLine = putBytes(uri, firstLine);

        if (getVersionValue().isUndefined()) {
            // unknown version....marshall (masked(len)+Str)
            data = getVersionValue().getByteArray();
            firstLine = putInt(data.length | GenericConstants.KNOWN_MASK, firstLine);
            firstLine = putBytes(data, firstLine);
        } else {
            // known version...marshall versionID
            firstLine = putInt(getVersionValue().getOrdinal(), firstLine);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            String url = GenericUtils.nullOutPasswords(getMarshalledSecondToken(), (byte) '&');
            Tr.event(tc, "Marshalling binary first line: " + getMethod() + " " + url + " " + getVersion());
        }
        // don't flip the last buffer as headers get tacked on the end
        return firstLine;
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#headerComplianceCheck
     * ()
     */
    @Override
    protected void headerComplianceCheck() throws MessageSentException {
        final boolean bTrace = TraceComponent.isAnyTracingEnabled();
        if (bTrace && tc.isEntryEnabled()) {
            Tr.entry(tc, "headerComplianceCheck");
        }

        super.headerComplianceCheck();

        // 339972 - requests should include Date header only if a body is part
        // of the message
        if (getServiceContext().isOutgoingBodyValid() && !containsHeader(HttpHeaderKeys.HDR_DATE)) {
            if (bTrace && tc.isDebugEnabled()) {
                Tr.debug(tc, "Creating a missing Date header");
            }
            setCurrentDate();
        }

        // HTTP/1.1 Requests must have Host header
        if (getVersionValue().equals(VersionValues.V11)) {
            if (!containsHeader(HttpHeaderKeys.HDR_HOST)) {
                // 335003 - use the interface to allow localhttp to call here
                HttpOutboundServiceContext osc = (HttpOutboundServiceContext) getServiceContext();
                String data = osc.getTargetAddress().getHostname();
                // if we're targeting a non-standard port, then append that
                if (80 != osc.getRemotePort()) {
                    data += ":" + osc.getRemotePort();
                }
                if (bTrace && tc.isDebugEnabled()) {
                    Tr.debug(tc, "Creating a missing Host header: " + data);
                }
                setSpecialHeader(HttpHeaderKeys.HDR_HOST, data);
            }
        }

        if (bTrace && tc.isEntryEnabled()) {
            Tr.exit(tc, "headerComplianceCheck");
        }
    }

    /**
     * Query the value of the method.
     *
     * @return String
     */
    @Override
    public String getMethod() {
        if (null != this.myMethod) {
            return this.myMethod.getName();
        }
        return null;
    }

    /**
     * Query the value of the method.
     *
     * @return MethodValues
     */
    @Override
    final public MethodValues getMethodValue() {
        return this.myMethod;
    }

    /**
     * Set the method of this request to the given String if it is valid.
     *
     * @param method
     * @throws UnsupportedMethodException
     */
    @Override
    public void setMethod(String method) throws UnsupportedMethodException {
        MethodValues val = MethodValues.match(method, 0, method.length());
        if (null == val) {
            throw new UnsupportedMethodException("Illegal method " + method);
        }
        setMethod(val);
    }

    /**
     * Set the method to the given byte[] input if it is valid.
     *
     * @param method
     * @throws UnsupportedMethodException
     */
    @Override
    public void setMethod(byte[] method) throws UnsupportedMethodException {
        MethodValues val = MethodValues.match(method, 0, method.length);
        if (null == val) {
            throw new UnsupportedMethodException("Illegal method " + GenericUtils.getEnglishString(method));
        }
        setMethod(val);
    }

    /**
     * Set the request method to the given value.
     *
     * @param method
     */
    @Override
    public void setMethod(MethodValues method) {
        this.myMethod = method;
        super.setFirstLineChanged();
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "setMethod(v): " + (null != method ? method.getName() : null));
        }
    }

    /**
     * Query the value of the request URI.
     *
     * @return String
     */
    @Override
    final public String getRequestURI() {
        if (null == this.myURIString) {
            this.myURIString = GenericUtils.getEnglishString(this.myURIBytes);
        }
        return this.myURIString;
    }

    /**
     * Query the value of the request URI.
     *
     * @return byte[]
     */
    @Override
    final public byte[] getRequestURIAsByteArray() {
        return this.myURIBytes;
    }

    /**
     * Find the target host of the request. This checks the VirtualHost data but
     * falls back on the socket layer target if need be.
     *
     * @return String
     */
    private String getTargetHost() {
        String host = getVirtualHost();
        if (null == host) {
            host = (isIncoming()) ? getServiceContext().getLocalAddr().getCanonicalHostName() : getServiceContext().getRemoteAddr().getCanonicalHostName();
        }
        return host;
    }

    /**
     * Find the target port of the request. This checks the VirtualPort data and
     * falls back on the socket port information if need be.
     *
     * @return int
     */
    private int getTargetPort() {
        int port = getVirtualPort();
        if (NOTSET == port) {
            port = (isIncoming()) ? getServiceContext().getLocalPort() : getServiceContext().getRemotePort();
        }
        return port;
    }

    /**
     * Return the requested URL. This will be of the form
     * scheme://host:port/URI.
     *
     * @return StringBuffer
     */
    @Override
    public StringBuffer getRequestURL() {

        StringBuffer sb = new StringBuffer(getScheme());
        sb.append("://");
        // Note: following code required if IPv6 IP host does not contain brackets
        // String host = getTargetHost();
        // if (-1 != host.indexOf(":")) {
        // // wrap brackets around the IPv6 IP address
        // host = "[" + host + "]";
        // }
        // sb.append(host);
        sb.append(getTargetHost());
        sb.append(':');
        sb.append(getTargetPort());
        sb.append(getRequestURI());
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "getRequestURL() returning " + sb.toString());
        }
        return sb;
    }

    /**
     * Return the requested URL. This will be of the form
     * scheme://host:port/URI.
     *
     * @return String
     */
    @Override
    final public String getRequestURLAsString() {
        return getRequestURL().toString();
    }

    /**
     * Return the requested URL. This will be of the form
     * scheme://host:port/URI
     *
     * @return byte[]
     */
    @Override
    final public byte[] getRequestURLAsByteArray() {
        return GenericUtils.getBytes(getRequestURL());
    }

    /**
     * Query the target host string in the request URL. If there was no host in
     * the URL, then this will return null.
     *
     * @return String
     */
    @Override
    final public String getURLHost() {
        return this.sUrlHost;
    }

    /**
     * Query the target port in the request URL. If it was not present, then
     * this will return NOTSET (-1).
     *
     * @return int
     */
    @Override
    final public int getURLPort() {
        return this.iUrlPort;
    }

    /**
     * Query the virtual host target of this request. This will check the URL
     * first for any host value (name or IP), if it is not present, then it
     * will check the Host header. If that is not present, then it will return
     * null.
     *
     * @return String
     */
    @Override
    public String getVirtualHost() {
        if (null != this.sUrlHost) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "getVirtualHost: url host-> " + this.sUrlHost);
            }
            return this.sUrlHost;
        }
        // check for preparsed Host header value
        if (null != this.sHdrHost) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "getVirtualHost: hdr host-> " + this.sHdrHost);
            }
            return this.sHdrHost;
        }
        // otherwise need to review the Host header value
        String host = getHeader(HttpHeaderKeys.HDR_HOST).asString();
        // PK09940 handle empty string too
        if (null == host || 0 >= host.length()) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "getVirtualHost: No host header: [" + host + "]");
            }
            return null;
        }
        int index = -1;
        if (LEFT_BRACKET == host.charAt(0)) {
            // IPv6 IP
            index = host.indexOf(RIGHT_BRACKET);
            if (-1 == index) {
                // invalid IP
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "getVirtualHost: Invalid IPv6 IP, missing right bracket");
                }
                return null;
            }
            index++; // keep the right bracket
        } else {
            index = host.indexOf(COLON);
        }
        if (-1 != index) {
            host = host.substring(0, index);
        }
        // PK14634 - cache the parsed host
        this.sHdrHost = host;
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "getVirtualHost: " + host);
        }
        return host;
    }

    /**
     * Query the target port of this request. It will first check for a port
     * in the URL string and if not found, it will check the Host header. This
     * will return -1 if it is not found in either spot.
     *
     * @return int
     */
    @Override
    public int getVirtualPort() {
        if (HeaderStorage.NOTSET != this.iUrlPort) {
            // use the port from the parsed URL
            return this.iUrlPort;
        }
        if (NOT_PRESENT <= this.iHdrPort) {
            // already searched the header value and either found it or not,
            // either way, return what we saved
            return this.iHdrPort;
        }

        // need to extract the value from the header
        byte[] host = getHeader(HttpHeaderKeys.HDR_HOST).asBytes();
        if (null == host || host.length == 0) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "getVirtualPort: No/empty host header");
            }
            return -1;
        }
        // default to not_present now
        this.iHdrPort = NOT_PRESENT;
        int start = -1;
        if (LEFT_BRACKET == host[0]) {
            // IPV6 IP address
            start = GenericUtils.byteIndexOf(host, RIGHT_BRACKET, 0);
            if (-1 == start) {
                // invalid IP
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "getVirtualPort: Invalid IPV6 ip in host header");
                }
                return -1;
            }
            start++; // skip past the bracket
        } else {
            // everything but an IPV6 IP
            start = GenericUtils.byteIndexOf(host, BNFHeaders.COLON, 0);
        }
        if (-1 == start || host.length <= start || BNFHeaders.COLON != host[start]) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "getVirtualPort: No port in host header");
            }
            return -1;
        }
        start++; // skip past the colon
        int length = host.length - start;
        if (0 >= length) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "getVirtualPort: No port after colon");
            }
            return -1;
        }
        try {
            // PK14634 - cache the parsed port
            this.iHdrPort = GenericUtils.asIntValue(host, start, length);
        } catch (NumberFormatException nfe) {
            // no FFDC required
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "getVirtualPort: Invalid port value: " + HttpChannelUtils.getEnglishString(host, start, length));
            }
            return -1;
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "getVirtualPort: returning " + this.iHdrPort);
        }
        return this.iHdrPort;
    }

    /**
     * Return the resource information that will be marshalled with the outgoing
     * request message. This will use the URL bytes if they exist (caller would
     * provide a full URL through the setRequestURL if they wish to target a
     * forward proxy), or just use the URI. If a query string exists, then it
     * will be appended.
     *
     * @return byte[]
     */
    protected byte[] getResource() {
        byte[] resource = this.myURIBytes;
        // 355398 - use SC impl
        HttpOutboundServiceContextImpl sc = (HttpOutboundServiceContextImpl) super.getServiceContext();
        if (sc.getLink().getTargetAddress().isForwardProxy()) {
            resource = getRequestURLAsByteArray();
        }

        if (null != this.myQueryBytes) {
            // append the query string
            byte[] temp = resource;
            resource = new byte[temp.length + 1 + this.myQueryBytes.length];
            System.arraycopy(temp, 0, resource, 0, temp.length);
            resource[temp.length] = QUESTIONMARK;
            System.arraycopy(this.myQueryBytes, 0, resource, temp.length + 1, this.myQueryBytes.length);
        }
        return resource;
    }

    /**
     * Parse out the host and possible port from the input bytes. The bytes in
     * the range specified by the input could look like this:
     * > hostname
     * > hostname:port
     * > IPv4Address
     * > IPv4Address:port
     * > [IPv6Address]
     * > [IPv6Address]:port
     * <p>
     * Anything else will cause an IllegalArgumentException to be thrown.
     *
     * @param url
     * @param start
     * @param end
     * @throws IllegalArgumentException
     */
    private void parseURLHost(byte[] url, int start, int end) {
        // save the host:port now, could be hostname, hostname:port, IP,
        // IP:port, or [IPv6]:port
        int length = end - start;
        if (0 >= length) {
            throw new IllegalArgumentException("Missing host/port");
        }
        int name_start = start;
        int name_end = end;
        int port_start = -1;
        int port_end = -1;
        if (LEFT_BRACKET != url[name_start]) {
            // hostname plus optional port if colon is found
            int colon_index = GenericUtils.byteIndexOf(url, BNFHeaders.COLON, name_start, length);
            if (-1 != colon_index) {
                name_end = colon_index;
                port_start = colon_index + 1;
                port_end = end;
            }
        } else {
            // IPV6 IP and port
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "IPV6 host in the URL");
            }
            // find the right bracket marking the end of the IPV6 IP
            // name_start++; // skip past the bracket
            int index = GenericUtils.byteIndexOf(url, RIGHT_BRACKET, name_start, length);
            if (-1 != index) {
                // save the ip, then check for port
                // Note: reverse these 2 lines if we want to strip []s off
                index++;
                name_end = index;
                if (index < end && BNFHeaders.COLON == url[index]) {
                    port_start = index + 1;
                    port_end = end;
                }
            } else {
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "No end to the IPV6 IP");
                }
                throw new IllegalArgumentException("Invalid IPV6 IP");
            }
        }
        // save the hostname information
        length = name_end - name_start;
        if (0 >= length) {
            throw new IllegalArgumentException("Hostname not present");
        }
        this.sUrlHost = GenericUtils.getEnglishString(url, name_start, name_end);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Found URL host: " + this.sUrlHost);
        }
        // save the port information
        if (-1 != port_start && port_end > port_start) {
            length = port_end - port_start;
            this.iUrlPort = GenericUtils.asIntValue(url, port_start, length);
        } else {
            // PK06407
            // if the port was not in the URL but the host was, then default the
            // virtual host to match the scheme
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Defaulting URL port to match scheme: " + getScheme());
            }
            if (SchemeValues.HTTPS.equals(getSchemeValue())) {
                this.iUrlPort = 443;
            } else {
                this.iUrlPort = 80;
            }
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Found URL port of " + this.iUrlPort);
        }
    }

    /**
     * Parse the URI information out of the input data, along with any query
     * information found afterwards. If format errors are found, then an
     * exception is thrown.
     *
     * @param data
     * @param start
     * @throws IllegalArgumentException
     */
    private void parseURI(byte[] data, int start) {
        // at this point, we're parsing /URI [?querystring]
        if (start >= data.length) {
            // PK22096 - default to "/" if not found, should have caught empty
            // string inputs previously (http://host:port is valid)
            this.myURIBytes = SLASH;
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Defaulting to slash since no URI data found");
            }
            return;
        }
        int uri_end = data.length;
        for (int i = start; i < data.length; i++) {
            // look for the query string marker
            if ('?' == data[i]) {
                uri_end = i;
                break;
            }
        }
        // save off the URI
        if (start == uri_end) {
            // no uri found
            throw new IllegalArgumentException("Missing URI: " + GenericUtils.getEnglishString(data));
        }
        if (0 == start && uri_end == data.length) {
            this.myURIBytes = data;
        } else {
            this.myURIBytes = new byte[uri_end - start];
            System.arraycopy(data, start, this.myURIBytes, 0, this.myURIBytes.length);
            uri_end++; // jump past the '?'
            if (uri_end < data.length) {
                // save off the query string data
                byte[] query = new byte[data.length - uri_end];
                System.arraycopy(data, uri_end, query, 0, query.length);
                setQueryString(query);
            }
        }
    }

    /**
     * Parse the authority information out that followed a "//" marker. Once
     * that data is saved, then it starts the parse for the remaining info in
     * the URL. If any format errors are encountered, then an exception is thrown.
     *
     * @param data
     * @param start
     * @throws IllegalArgumentException
     */
    private void parseAuthority(byte[] data, int start) {
        // authority is [userinfo@] host [:port] "/URI"
        if (start >= data.length) {
            // nothing after the "//" which is invalid
            throw new IllegalArgumentException("Invalid authority: " + GenericUtils.getEnglishString(data));
        }
        int i = start;
        int host_start = start;
        int slash_start = data.length;
        for (; i < data.length; i++) {
            // find either a "@" or "/"
            if ('@' == data[i]) {
                // Note: we're just cutting off the userinfo section for now
                host_start = i + 1;
            } else if ('/' == data[i]) {
                slash_start = i;
                break;
            }
        }
        parseURLHost(data, host_start, slash_start);
        parseURI(data, slash_start);
    }

    /**
     * Parse the scheme marker out of the input data and then start the parse
     * for the next section. If any errors are encountered, then an exception
     * is thrown.
     *
     * @param data
     * @throws IllegalArgumentException
     */
    private void parseScheme(byte[] data) {
        // we know the first character is correct, find the colon
        for (int i = 1; i < data.length; i++) {
            if (':' == data[i]) {
                SchemeValues val = SchemeValues.match(data, 0, i);
                if (null == val) {
                    throw new IllegalArgumentException("Invalid scheme inside URL: " + GenericUtils.getEnglishString(data));
                }
                setScheme(val);
                // scheme should be followed by "://"
                if ((i + 2) >= data.length || ('/' != data[i + 1] || '/' != data[i + 2])) {
                    throw new IllegalArgumentException("Invalid net_path: " + GenericUtils.getEnglishString(data));
                }
                parseAuthority(data, i + 3);
                return;
            }
        }
        // no colon found
        throw new IllegalArgumentException("Invalid scheme in URL: " + GenericUtils.getEnglishString(data));
    }

    /**
     * Check whether the input byte is an alphabetic character.
     *
     * @param b
     * @return boolean
     */
    private boolean isAlpha(byte b) {
        if ('a' <= b && 'z' >= b) {
            return true;
        }
        return ('A' <= b && 'Z' >= b);
    }

    /**
     * Set the requested URL to the given string. This string will be
     * parsed into whatever distinct pieces are present (scheme, URI,
     * host, etc). Input can be :
     * [scheme://host[:port]]/URI[?querystring]
     * <p>
     * Only /URI is required.
     *
     * @param url
     */
    @Override
    public void setRequestURL(String url) {
        setRequestURL(GenericUtils.getEnglishBytes(url));
    }

    /**
     * Set the requested URL to the given byte[]. This byte[] will be
     * parsed into whatever distinct pieces are present (scheme, URI,
     * host, etc). Input can be :
     * [scheme://[user:password@]host[:port]]/URI[?querystring]
     * [scheme:/]/URI[?querystring]
     * <p>
     * Only /URI is required.
     *
     * @param url
     */
    @Override
    public void setRequestURL(byte[] url) {

        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "setRequestURL input [" + GenericUtils.nullOutPasswords(url, (byte) '&') + "]");
        }
        if (null == url || 0 == url.length) {
            throw new IllegalArgumentException("setRequestURL: null URL");
        }

        // setup certain default values now
        super.setFirstLineChanged();
        initScheme();
        this.myURIString = null;
        this.sUrlHost = null;
        this.iUrlPort = HeaderStorage.NOTSET;
        this.myQueryBytes = null;
        this.queryParams = null;
        // either starts with "/", "//", "*", or alpha chars
        if ('*' == url[0]) {
            if (1 == url.length) {
                // only a *
                this.myURIBytes = STAR;
            } else if ('?' == url[1]) {
                // a * following by query data
                parseURI(url, 0);
            } else {
                // invalid uri
                throw new IllegalArgumentException("Invalid leading * : " + GenericUtils.getEnglishString(url));
            }
        } else if ('/' == url[0]) {
            if (1 == url.length) {
                // just a /
                this.myURIBytes = SLASH;
            } else if ('/' == url[1] && getServiceContext().getHttpConfig().isStrictURLFormat()) {
                // starts with "//". Only parse the authority if we are in a
                // strict compliance setting, otherwise assume anything with a
                // leading slash is just the URI
                parseAuthority(url, 2);
            } else {
                // starts with just "/xx"
                parseURI(url, 0);
            }
        }
        // otherwise we should be looking at alpha chars and the scheme
        else if (isAlpha(url[0])) {
            parseScheme(url);
        } else if (url[0] == '?') { //PI42523 Start
            // Inject Slash to the beginning of the URL if the URL is
            // composed of only the query
            byte[] injectRootURL = new byte[url.length + 1];
            injectRootURL[0] = '/';
            for (int i = 1; i < injectRootURL.length; i++) {
                injectRootURL[i] = url[i - 1];
            }
            parseURI(injectRootURL, 0);// PI42523 End
        } else {
            throw new IllegalArgumentException("setRequestURL: invalid URL: " + GenericUtils.getEnglishString(url));
        }

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "setRequestURL: set URI to " + getRequestURI());
        }
    }

    /**
     * Allow the user to set the URI in the Request prior to sending it.
     *
     * @param uri
     */
    @Override
    public void setRequestURI(String uri) {
        setRequestURI(GenericUtils.getEnglishBytes(uri));
    }

    /**
     * Allow the user to set the URI in the HttpRequest to the given
     * byte array, prior to sending the request.
     *
     * @param uri
     */
    @Override
    public void setRequestURI(byte[] uri) {

        if (null == uri || 0 == uri.length) {
            throw new IllegalArgumentException("setRequestURI: null input");
        }
        super.setFirstLineChanged();

        if ('*' == uri[0]) {
            // URI of "*" can only be one character long to be valid
            if (1 != uri.length && '?' != uri[1]) {
                String value = GenericUtils.getEnglishString(uri);
                if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                    Tr.debug(tc, "setRequestURI: invalid uri [" + value + "]");
                }
                throw new IllegalArgumentException("Invalid uri: " + value);
            }
        } else if ('/' != uri[0]) {
            String value = GenericUtils.getEnglishString(uri);
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "setRequestURI: invalid uri [" + value + "]");
            }
            throw new IllegalArgumentException("Invalid uri: " + value);
        }

        initScheme();
        this.myURIString = null;
        this.sUrlHost = null;
        this.iUrlPort = HeaderStorage.NOTSET;
        this.myQueryBytes = null;
        this.queryParams = null;
        parseURI(uri, 0);

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "setRequestURI: set URI to " + getRequestURI());
        }
    }

    /**
     * Query the value of the scheme.
     *
     * @return SchemeValues
     */
    @Override
    final public SchemeValues getSchemeValue() {
        // if it hasn't been set yet, then check whether the SC is secure
        // or not and set the value accordingly
        if (null == this.myScheme) {
            initScheme();
        }
        return this.myScheme;
    }

    /**
     * Initialize the scheme information based on the socket being secure or not.
     *
     */
    public void initScheme() {
        // set the scheme based on whether the socket is secure or not
        if (null == getServiceContext() || null == getServiceContext().getTSC()) {
            // discrimination path, not ready for this yet
            return;
        }
        if (getServiceContext().isSecure()) {
            this.myScheme = SchemeValues.HTTPS;
        } else {
            this.myScheme = SchemeValues.HTTP;
        }
    }

    /**
     * Query the value of the scheme.
     *
     * @return String
     */
    @Override
    public String getScheme() {
        // if it hasn't been set yet, then check whether the SC is secure
        // or not and set the value accordingly
        if (null == this.myScheme) {
            initScheme();
        }
        if (null == this.myScheme) { // if the request has been already destroyed then it will be still null
            return null;
        }
        return this.myScheme.getName();
    }

    /**
     * Set the value of the scheme in the Request by using the
     * int identifiers.
     *
     * @param scheme
     */
    @Override
    public void setScheme(SchemeValues scheme) {
        this.myScheme = scheme;
        super.setFirstLineChanged();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "setScheme(v): " + (null != scheme ? scheme.getName() : null));
        }
    }

    /**
     * Set the scheme value to the given input.
     *
     * @param scheme
     * @throws UnsupportedSchemeException
     */
    @Override
    public void setScheme(String scheme) throws UnsupportedSchemeException {
        SchemeValues val = SchemeValues.match(scheme, 0, scheme.length());
        if (null == val) {
            throw new UnsupportedSchemeException("Illegal scheme " + scheme);
        }
        setScheme(val);
    }

    /**
     * Set the scheme value to the given input.
     *
     * @param scheme
     * @throws UnsupportedSchemeException
     */
    @Override
    public void setScheme(byte[] scheme) throws UnsupportedSchemeException {
        SchemeValues val = SchemeValues.find(scheme, 0, scheme.length);
        if (null == val) {
            throw new UnsupportedSchemeException("Illegal scheme " + GenericUtils.getEnglishString(scheme));
        }
        setScheme(val);
    }

    /**
     * @see com.ibm.wsspi.http.channel.cookies.CookieHandler#getCookie(java.lang.String)
     */
    @Override
    public HttpCookie getCookie(String name) {
        if (null == name) {
            return null;
        }
        HttpCookie cookie = getCookie(name, HttpHeaderKeys.HDR_COOKIE);
        if (null == cookie) {
            cookie = getCookie(name, HttpHeaderKeys.HDR_COOKIE2);
        }
        // Note: return a clone to avoid corruption by the caller
        return (null == cookie) ? null : cookie.clone();
    }

    /**
     * @see com.ibm.wsspi.http.channel.cookies.CookieHandler#getCookieValue(java.lang.String)
     */
    @Override
    public byte[] getCookieValue(String name) {
        if (null == name) {
            return null;
        }
        byte[] val = getCookieValue(name, HttpHeaderKeys.HDR_COOKIE);
        if (null == val) {
            val = getCookieValue(name, HttpHeaderKeys.HDR_COOKIE2);
        }
        return val;
    }

    /**
     * @see com.ibm.wsspi.http.channel.cookies.CookieHandler#getAllCookieValues(java.lang.String)
     */
    @Override
    public List<String> getAllCookieValues(String name) {
        List<String> list = new LinkedList<String>();
        if (null != name) {
            getAllCookieValues(name, HttpHeaderKeys.HDR_COOKIE, list);
            getAllCookieValues(name, HttpHeaderKeys.HDR_COOKIE2, list);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "getAllCookieValues: Found " + list.size() + " instances of " + name);
        }
        return list;
    }

    /**
     * @see com.ibm.wsspi.http.channel.cookies.CookieHandler#getAllCookies(java.lang.String)
     */
    @Override
    public List<HttpCookie> getAllCookies(String name) {
        List<HttpCookie> list = new LinkedList<HttpCookie>();
        if (null != name) {
            getAllCookies(name, HttpHeaderKeys.HDR_COOKIE, list);
            getAllCookies(name, HttpHeaderKeys.HDR_COOKIE2, list);
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "getAllCookies: Found " + list.size() + " instances of " + name);
        }
        return list;
    }

    /**
     * @see com.ibm.wsspi.http.channel.cookies.CookieHandler#getAllCookies()
     */
    @Override
    public List<HttpCookie> getAllCookies() {
        List<HttpCookie> list = new LinkedList<HttpCookie>();
        getAllCookies(HttpHeaderKeys.HDR_COOKIE, list);
        getAllCookies(HttpHeaderKeys.HDR_COOKIE2, list);
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "getAllCookies: Found " + list.size() + " instances");
        }
        return list;
    }

    /*
     * @see
     * com.ibm.wsspi.http.channel.cookies.CookieHandler#setCookie(com.ibm.websphere
     * .http.HttpCookie, com.ibm.wsspi.http.channel.values.HttpHeaderKeys)
     */
    @Override
    public boolean setCookie(HttpCookie cookie, HttpHeaderKeys header) {
        if (null == cookie || null == header) {
            return false;
        }

        // only cookie versions 0 and 1 are supported
        if (1 < cookie.getVersion()) {
            throw new IllegalArgumentException("Invalid cookie version: " + cookie.getVersion());
        }

        if (header.equals(HttpHeaderKeys.HDR_COOKIE) || header.equals(HttpHeaderKeys.HDR_COOKIE2)) {
            return addCookie(cookie, header);
        }
        return false;
    }

    /*
     * @see
     * com.ibm.wsspi.http.channel.cookies.CookieHandler#setCookie(java.lang.String
     * , java.lang.String, com.ibm.wsspi.http.channel.values.HttpHeaderKeys)
     */
    @Override
    public boolean setCookie(String name, String value, HttpHeaderKeys header) {
        // value can be null, a null name is caught by the Cookie constructor
        if (null == header) {
            return false;
        }
        return setCookie(new HttpCookie(name, value), header);
    }

    /**
     * @see com.ibm.wsspi.http.channel.cookies.CookieHandler#removeCookie(java.lang.String, com.ibm.wsspi.http.channel.values.HttpHeaderKeys)
     */
    @Override
    public boolean removeCookie(String name, HttpHeaderKeys header) {
        if (null == name || null == header) {
            return false;
        }

        if (header.equals(HttpHeaderKeys.HDR_COOKIE) || header.equals(HttpHeaderKeys.HDR_COOKIE2)) {
            return deleteCookie(name, header);
        }
        return false;
    }

    /*
     * @see com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#debug()
     */
    @Override
    public void debug() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Request Message: " + this);
            Tr.debug(tc, "Method: " + getMethod());
            Tr.debug(tc, "URL: " + getRequestURLAsString());
            Tr.debug(tc, "Query: " + getQueryString());
            Tr.debug(tc, "UrlHost: " + getURLHost());
            Tr.debug(tc, "UrlPort: " + getURLPort());
            Tr.debug(tc, "Host: " + getHeader(HttpHeaderKeys.HDR_HOST).asString());
            super.debug();
        }
    }

    /**
     * Duplicate this entire message.
     *
     * @return HttpRequestMessage
     */
    @Override
    public HttpRequestMessage duplicate() {
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "Duplicating the request message: " + this);
        }

        HttpRequestMessageImpl msg = null;
        HttpObjectFactory factory = getObjectFactory();
        if (null == factory) {
            msg = new HttpRequestMessageImpl();
        } else {
            msg = factory.getRequest();
        }
        msg.setIncoming(isIncoming());
        // set the request specifics
        msg.setMethod(this.myMethod);
        msg.setRequestURI(this.myURIBytes);
        msg.setScheme(this.myScheme);
        msg.setQueryString(this.myQueryBytes);
        msg.iUrlPort = this.iUrlPort;
        msg.sUrlHost = this.sUrlHost;
        msg.iHdrPort = this.iHdrPort;
        msg.sHdrHost = this.sHdrHost;

        // set the common object values
        super.duplicate(msg);
        return msg;
    }

    /**
     * Deserialize the method information from the input stream.
     *
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void deserializeMethod(ObjectInput stream) throws IOException, ClassNotFoundException {
        MethodValues method = null;
        if (SERIALIZATION_V2 == getDeserializationVersion()) {
            method = MethodValues.find(readByteArray(stream));
        } else {
            method = MethodValues.find((String) stream.readObject());
        }
        if (null == method) {
            throw new IOException("Missing method");
        }
        setMethod(method);
    }

    /**
     * Deserialize the scheme information from the input stream.
     *
     * @param stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void deserializeScheme(ObjectInput stream) throws IOException, ClassNotFoundException {
        SchemeValues scheme = null;
        if (SERIALIZATION_V2 == getDeserializationVersion()) {
            byte[] value = readByteArray(stream);
            if (null == value) {
                throw new IOException("Missing scheme");
            }
            scheme = SchemeValues.find(value);
        } else {
            String value = (String) stream.readObject();
            scheme = SchemeValues.find(value);
        }
        setScheme(scheme);
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#readExternal(java.
     * io.ObjectInput)
     */
    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {

        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "De-serializing into: " + this);
        }
        super.readExternal(input);
        deserializeMethod(input);
        deserializeScheme(input);
        this.myURIBytes = readByteArray(input);
        setQueryString(readByteArray(input));
    }

    /*
     * @see
     * com.ibm.ws.http.channel.internal.HttpBaseMessageImpl#writeExternal(java
     * .io.ObjectOutput)
     */
    @Override
    public void writeExternal(ObjectOutput output) throws IOException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEventEnabled()) {
            Tr.event(tc, "Serializing: " + this);
        }
        super.writeExternal(output);
        // write out the byte[] to avoid hardcoding impl information (ordinals)
        writeByteArray(output, getMethodValue().getByteArray());
        writeByteArray(output, getSchemeValue().getByteArray());

        writeByteArray(output, getRequestURIAsByteArray());
        writeByteArray(output, getQueryStringAsByteArray());
    }

    // ----------------------------------------------------------------------
    // Query string specific methods
    // ----------------------------------------------------------------------

    /**
     * @see com.ibm.wsspi.http.channel.HttpRequestMessage#setQueryString(String)
     */
    @Override
    public void setQueryString(String query) {
        this.myQueryBytes = GenericUtils.getEnglishBytes(query);
        this.queryParams = null;
        super.setFirstLineChanged();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled() && null != this.myQueryBytes) {
            Tr.debug(tc, "setQueryString(String): set query to [" + GenericUtils.nullOutPasswords(this.myQueryBytes, (byte) '&') + "]");
        }
    }

    /**
     * @see com.ibm.wsspi.http.channel.HttpRequestMessage#setQueryString(byte[])
     */
    @Override
    public void setQueryString(byte[] query) {
        this.myQueryBytes = query;
        this.queryParams = null;
        super.setFirstLineChanged();
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled() && null != this.myQueryBytes) {
            Tr.debug(tc, "setQueryString(byte[]): set query to [" + GenericUtils.nullOutPasswords(this.myQueryBytes, (byte) '&') + "]");
        }
    }

    /**
     * @see com.ibm.wsspi.http.channel.HttpRequestMessage#getQueryString()
     */
    @Override
    public String getQueryString() {
        return GenericUtils.getEnglishString(this.myQueryBytes);
    }

    /**
     * @see com.ibm.wsspi.http.channel.HttpRequestMessage#getQueryStringAsByteArray()
     */
    @Override
    final public byte[] getQueryStringAsByteArray() {
        return this.myQueryBytes;
    }

    /**
     * @see com.ibm.wsspi.http.channel.HttpRequestMessage#getParameter(String)
     */
    @Override
    public String getParameter(String name) {
        String[] list = getParameterMap().get(name);
        return (null == list || 0 == list.length) ? null : list[0];
    }

    /**
     * @see com.ibm.wsspi.http.channel.HttpRequestMessage#getParameterMap()
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        if (null == this.queryParams) {
            parseParameters();
        }
        return this.queryParams;
    }

    /**
     * @see com.ibm.wsspi.http.channel.HttpRequestMessage#getParameterNames()
     */
    @Override
    public Enumeration<String> getParameterNames() {
        return new GenericEnumWrap<String>(getParameterMap().keySet().iterator());
    }

    /**
     * @see com.ibm.wsspi.http.channel.HttpRequestMessage#getParameterValues(String)
     */
    @Override
    public String[] getParameterValues(String name) {
        return getParameterMap().get(name);
    }

    /**
     * Parse the query parameters out of this message. This will check just the
     * URL query data of the request.
     */
    private synchronized void parseParameters() {
        if (null != this.queryParams) {
            // already parsed
            return;
        }
        if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
            Tr.debug(tc, "parseParameters for " + this);
        }

        String encoding = getCharset().name();
        // PQ99481... non-English environments are too complex at the moment due to
        // the fact that current clients are not enforcing the proper Content-Type
        // header usage therefore figuring out what encoding to use involves system
        // properties, WAS config files, etc. So query parameters will only be
        // pulled
        // from the URI and not POST formdata

        // now merge in any possible URL params
        String queryString = getQueryString();
        if (null != queryString) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Parsing URL query data");
            }
            this.queryParams = HttpChannelUtils.parseQueryString(queryString, encoding);
        } else {
            // if we didn't have any data then just create an empty table
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                // PQ99481 Tr.debug(tc, "No parameter data found in body or URL");
                Tr.debug(tc, "No query data found in URL");
            }
            this.queryParams = new Hashtable<String, String[]>();
        }
    }

    /**
     * @return request start time with nanosecond precision (relative to the JVM instance as opposed to time since epoch)
     */
    public long getStartNanoTime() {
        return getServiceContext().getStartNanoTime();
    }
}
