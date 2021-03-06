/*
 * Copyright (C) 2012-2016 Opher Shachar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ciju.client.impl.ipp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.ciju.client.impl.Base64Coder;
import static org.ciju.client.impl.ipp.Handler.resourceStrings;
import org.ciju.client.ipp.IppConnection;
import org.ciju.ipp.IppEncoding.StatusCode;
import org.ciju.ipp.IppException;
import org.ciju.ipp.IppMultiObject;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppObjectFactory;
import org.ciju.ipp.IppRequest;
import org.ciju.ipp.IppResponse;
import org.ciju.ipp.IppTransport;
import org.ciju.ipp.attribute.GenericValue;


/**
 * Implementation of an IPP connection by wrapping the HttpURLConnection class.
 * You get an instance of this class by doing something like:<blockquote><pre>
 *    URL url = new URL("ipp://host/printers/printer1");
 *    URLConnection urlc = url.openConnection();
 *    -or-
 *    IppURLConnection urlc = (IppURLConnection) url.openConnection();
 * </pre></blockquote>
 * This implementation wraps a {@link HttpURLConnection} and proxies all but a handful
 * of methods to the wrapped <tt>HttpURLConnection</tt>. This form of implementation
 * is suggested by <a href="http://tools.ietf.org/html/rfc2910#section-5">section 5</a>
 * of RFC2910.
 *
 * @author	Opher
 */
/*package*/ class IppURLConnectionImpl extends HttpURLConnection implements IppConnection {

    private final Handler handler;
    private final HttpURLConnection huc;
    private IppRequest ipp;
    private boolean sent;

    /**
     * Constructor for the IppURLConnectionImpl class. To make a direct connection
     * without a proxy pass {@linkplain Proxy#NO_PROXY} to <code>p</code>. To use
     * the system's default ProxySelector settings pass <code>null</code> to <code>p</code>.
     * <p>This constructor sets properties appropriate for the IPP protocol, like:
     * <ul>
     *      <li>doOutput = true
     *      <li>Request-Method = POST
     *      <li>Content-Type = application/ipp
     * </ul>
     * @param u the {@linkplain URL} for the connection.
     * @param p the {@linkplain Proxy} through which to connect, {@linkplain Proxy#NO_PROXY}
     *      for direct connection or <code>null</code> to use the system's default
     *      {@linkplain ProxySelector} settings.
     * @param h the {@linkplain Handler} for 'ipp' URLs
     * @throws MalformedURLException if the URL <code>u</code> does not specify one of the
     *      protocols: <strong>ipp ipps http https</strong> or cannot be converted
     *      to an HTTP(S) URL.
     * @throws SecurityException if a security manager is present and the caller 
     *      doesn't have permission to connect to the proxy.
     * @throws IllegalArgumentException will be thrown if proxy has the wrong type.
     * @throws IOException if failed to create a connection.
     */
    protected IppURLConnectionImpl(URL u, Proxy p, Handler h) throws MalformedURLException, IOException {
        super(u);
        handler = h;
        
        final URL hu;
        String protocol = u.getProtocol();
        if (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https"))
            hu = u;
        else {
            if (protocol.equalsIgnoreCase("ipp"))
                protocol = "http";
            else if (protocol.equalsIgnoreCase("ipps"))
                protocol = "https";
            // protocol can have no other value since the constructor is protected
            int port = u.getPort() == -1 ? handler.getDefaultPort() : u.getPort();
            hu = new URL(protocol, u.getHost(), port, u.getFile());
        }
        
        if (p == null)
            huc = (HttpURLConnection)hu.openConnection();
        else
            huc = (HttpURLConnection)hu.openConnection(p);
        huc.setDoOutput(true);
        huc.setRequestMethod("POST");
        huc.setRequestProperty("Content-Type", "application/ipp");
    }

    public void connect() throws IOException {
        huc.connect();
        connected = true;
    }

    public void disconnect() {
        huc.disconnect();
        connected = false;
    }

    /**
     * {@inheritDoc}
     * <p>NOTE: The property <code>Content-Type</code> is set by default to
     * <code>application/ipp</code> and may not be set a different value.
     * @throws IllegalStateException if already connected
     * @throws NullPointerException if key is <code>null</code>
     * @throws IllegalArgumentException if trying to set <code>Content-Type</code>
     * to anything other than <code>application/ipp</code>.
     */
    @Override
    public void setRequestProperty(String key, String value) {
        if (key.equalsIgnoreCase("Content-Type") && !value.equalsIgnoreCase("application/ipp"))
            throw new IllegalArgumentException(resourceStrings.getString("CONTENT-TYPE MAY ONLY BE 'APPLICATION/IPP'!"));
        huc.setRequestProperty(key, value);
    }

    /**
     * {@inheritDoc}
     * <p>NOTE: The property <code>Content-Type</code> is set by default to
     * <code>application/ipp</code> and may not be set a different value.
     * @throws IllegalStateException if already connected
     * @throws NullPointerException if key is <CODE>null</CODE>
     * @throws IllegalArgumentException if trying to change <code>Content-Type</code>.
     */
    @Override
    public void addRequestProperty(String key, String value) {
        if (key.equalsIgnoreCase("Content-Type"))
            throw new IllegalArgumentException(resourceStrings.getString("CONTENT-TYPE MAY ONLY BE 'APPLICATION/IPP'!"));
        huc.addRequestProperty(key, value);
    }

    /**
     * {@inheritDoc}
     * <p>NOTE: The request method is set by default to <code>POST</code>
     * and may not be set a different value.
     * @throws IllegalArgumentException if trying to set <code>method</code>
     * to anything other than <code>POST</code>.
     */
    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        if (!method.equalsIgnoreCase("POST"))
            throw new IllegalArgumentException(resourceStrings.getString("REQUEST METHOD MUST BE POST!"));
        huc.setRequestMethod(method);
    }

    /**
     * {@inheritDoc}
     * <p>NOTE: This <code>URLConnection</code> has by default its doOutput
     * flag set to <code>true</code>. It may not be set to <code>false</code>.
     * @param dooutput  must be <code>true</code>.
     * @throws IllegalArgumentException if dooutput is passed a value of false.
     */
    @Override
    public void setDoOutput(boolean dooutput) {
        if (!dooutput)
            throw new IllegalArgumentException(resourceStrings.getString("EMPTY REQUESTS ARE NOT VALID!"));
        huc.setDoOutput(dooutput);
    }

    private void sendIppRequest() throws IOException {
        if (ipp == null)
            throw new IllegalStateException(resourceStrings.getString("IPP REQUEST WAS NOT SET."));
        if (!sent) { // IPP Resquest not sent
            OutputStream os = huc.getOutputStream();
            IppTransport.writeRequest(os, ipp);
            sent = true;
        }
    }

    public IppConnection setIppRequest(IppRequest request) {
        if (connected)
            throw new IllegalStateException(resourceStrings.getString("ALREADY CONNECTED"));
        if (request == null) 
	    throw new NullPointerException();
        ipp = request;
        String lang = GenericValue.getNaturalLanguage(ipp.getLocale());
        setRequestProperty("Accept-Language", lang);
        return this;
    }

    public IppRequest getIppRequest() {
        return ipp;
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException if <tt>authn</tt> is <code>null</code>.
     */
    public IppConnection setPasswordAuthentication(PasswordAuthentication authn) {
        if (connected)
            throw new IllegalStateException(resourceStrings.getString("ALREADY CONNECTED"));
        StringBuilder auths = new StringBuilder(authn.getUserName())
                .append(':').append(authn.getPassword());
        setRequestProperty("Authorization", "Basic " + Base64Coder.encodeString(auths.toString()));
        return this;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException(resourceStrings.getString("USE SETIPPREQUEST(...) TO SEND IPP REQUEST."));
    }

    @Override
    public InputStream getInputStream() throws IOException {
        sendIppRequest();
        return huc.getInputStream();
    }

    // Methods getContent() and getContent(Class[] classes) left for URLConnection.

    /**
     * @see IppConnection#getContent(IppObject)
     */
    public <T extends IppObject> IppResponse<T> getContent(T obj) throws IOException, IppException {
        IppResponse<T> resp = IppTransport.processResponse(getInputStream(), getContentLength(), obj);
        return checkResponse(resp);
    }

    /**
     * @see IppConnection#getContent(IppObjectFactory)
     */
    public <T extends IppObject> List<T> getContent(IppObjectFactory<T> fact) throws IOException, IppException {
        final List<T> list = new ArrayList<T>();
        final IppMultiObject<T> imo = new IppMultiObject<T>(list, fact);
        IppResponse<IppMultiObject<T>> resp = IppTransport.processResponse(getInputStream(), getContentLength(), imo);
        checkResponse(resp);
        /* ignore the returned IppResponse */
        return list;
    }

    /**
     * Check the response for it's status code and if it's an error throw {@link IppException}.
     * @param resp the {@linkplain IppResponse} argument.
     * @return the <tt>resp</tt> parameter (if an exception was not thrown).
     */
    private <T extends IppObject> IppResponse<T> checkResponse(IppResponse<T> resp) throws IppException {
        if (resp.getResponseCode() >= StatusCode.INFORMATIONAL.getValue())
            throw new IppException(resp, ipp);
        return resp;
    }

// <editor-fold defaultstate="collapsed" desc="delegated methods">
    @Override
    public void setUseCaches(boolean usecaches) {
        huc.setUseCaches(usecaches);
    }

    @Override
    public void setReadTimeout(int timeout) {
        huc.setReadTimeout(timeout);
    }

    @Override
    public void setIfModifiedSince(long ifmodifiedsince) {
        huc.setIfModifiedSince(ifmodifiedsince);
    }

    @Override
    public void setDoInput(boolean doinput) {
        huc.setDoInput(doinput);
    }

    @Override
    public void setConnectTimeout(int timeout) {
        huc.setConnectTimeout(timeout);
    }

    @Override
    public void setAllowUserInteraction(boolean allowuserinteraction) {
        huc.setAllowUserInteraction(allowuserinteraction);
    }

    @Override
    public boolean getUseCaches() {
        return huc.getUseCaches();
    }

    @Override
    public String getRequestProperty(String key) {
        return huc.getRequestProperty(key);
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        return huc.getRequestProperties();
    }

    @Override
    public int getReadTimeout() {
        return huc.getReadTimeout();
    }

    @Override
    public long getLastModified() {
        return huc.getLastModified();
    }

    @Override
    public long getIfModifiedSince() {
        return huc.getIfModifiedSince();
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        return huc.getHeaderFields();
    }

    @Override
    public int getHeaderFieldInt(String name, int Default) {
        return huc.getHeaderFieldInt(name, Default);
    }

    @Override
    public String getHeaderField(String name) {
        return huc.getHeaderField(name);
    }

    @Override
    public long getExpiration() {
        return huc.getExpiration();
    }

    @Override
    public boolean getDoOutput() {
        return huc.getDoOutput();
    }

    @Override
    public boolean getDoInput() {
        return huc.getDoInput();
    }

    @Override
    public long getDate() {
        return huc.getDate();
    }

    @Override
    public String getContentType() {
        return huc.getContentType();
    }

    @Override
    public int getContentLength() {
        return huc.getContentLength();
    }

    @Override
    public String getContentEncoding() {
        return huc.getContentEncoding();
    }

    @Override
    public int getConnectTimeout() {
        return huc.getConnectTimeout();
    }

    @Override
    public boolean getAllowUserInteraction() {
        return huc.getAllowUserInteraction();
    }

    public boolean usingProxy() {
        return huc.usingProxy();
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects) {
        huc.setInstanceFollowRedirects(followRedirects);
    }

    @Override
    public void setFixedLengthStreamingMode(int contentLength) {
        huc.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public void setChunkedStreamingMode(int chunklen) {
        huc.setChunkedStreamingMode(chunklen);
    }

    @Override
    public String getResponseMessage() throws IOException {
        return huc.getResponseMessage();
    }

    @Override
    public int getResponseCode() throws IOException {
        return huc.getResponseCode();
    }

    @Override
    public String getRequestMethod() {
        return huc.getRequestMethod();
    }

    @Override
    public Permission getPermission() throws IOException {
        return huc.getPermission();
    }

    @Override
    public boolean getInstanceFollowRedirects() {
        return huc.getInstanceFollowRedirects();
    }

    @Override
    public String getHeaderFieldKey(int n) {
        return huc.getHeaderFieldKey(n);
    }

    @Override
    public long getHeaderFieldDate(String name, long Default) {
        return huc.getHeaderFieldDate(name, Default);
    }

    @Override
    public String getHeaderField(int n) {
        return huc.getHeaderField(n);
    }

    @Override
    public InputStream getErrorStream() {
        return huc.getErrorStream();
    }
    // </editor-fold>
}
