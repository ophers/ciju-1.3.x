/*
 * Copyright (C) 2012 Opher Shachar
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
import java.util.List;
import java.util.Map;
import javax.print.attribute.Attribute;
import org.ciju.ipp.IppRequest;
import org.ciju.client.ipp.IppTransport;
import org.ciju.client.ipp.IppURLConnection;
import org.ciju.ipp.IppObject;


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
/*package*/ class IppURLConnectionImpl extends HttpURLConnection implements IppURLConnection {

    private final Handler handler;
    private final HttpURLConnection huc;
    private boolean gos_called;
    private IppRequest ipp;

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
            throw new IllegalArgumentException("Content-Type may only be 'application/ipp'!");
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
            throw new IllegalArgumentException("Content-Type may only be 'application/ipp'!");
        huc.addRequestProperty(key, value);
    }

    /**
     * {@inheritDoc}
     * <p>NOTE: The request method is set by default to <code>POST</code>
     * and may not be set a different value.
     * @throws ProtocolException if trying to set <code>method</code>
     * to anything other than <code>POST</code>.
     */
    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        if (!method.equalsIgnoreCase("POST"))
            throw new IllegalArgumentException("Request method must be POST!");
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
            throw new IllegalArgumentException("Empty requests are not valid!");
        huc.setDoOutput(dooutput);
    }

    private void sendIppRequest() throws IOException {
        if (ipp == null)
            throw new IllegalStateException("IPP request was not set.");
        OutputStream os = getOutputStream();
        IppTransport.writeRequest(os, ipp);
    }

    public IppURLConnection setIppRequest(IppRequest request) {
        if (gos_called)
            throw new IllegalStateException("Output stream was previously requested.");
        if (connected)
            throw new IllegalStateException("Already connected");
        if (request == null) 
	    throw new NullPointerException("IPP request is null");
        ipp = request;
//        String al = getRequestProperty("Accept-Language");
        Attribute attr = ipp.getAttributesNaturalLanguage();
        setRequestProperty("Accept-Language", attr.toString());
//        if (al != null) addRequestProperty("Accept-Language", al);
        return this;
    }

    public IppRequest getIppRequest() {
        return ipp;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (ipp != null)
            throw new IllegalStateException("IPP request was previously set.");
        gos_called = true;
        return huc.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!gos_called)
            sendIppRequest();
        return huc.getInputStream();
    }

    @Override
    public Object getContent(Class[] classes) throws IOException {
        getInputStream();
        return huc.getContent(classes);
    }

    @Override
    public IppObject getContent() throws IOException {
        getInputStream();
        return (IppObject) huc.getContent();
    }

    public <T extends IppObject> T getContent(T o) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @SuppressWarnings("unchecked")
    public <T extends IppObject> T getContent(Class<T> t) throws IOException {
        return (T) getContent(new Class[] { t });
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
