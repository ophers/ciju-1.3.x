/*
 * Copyright (C) 2012 Opher Shachar, Ladpc Ltd.
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

package org.ciju.client.ipp;

import com.easysw.cups.IPP;
import com.easysw.cups.IPPAttribute;
import com.easysw.cups.IPPDefs;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.security.Permission;
import java.util.List;
import java.util.Map;


/**
 * Implementation of an IPP connection by wrapping the HttpURLConnection class.
 * You get an instance of this class by doing something like:<blockquote><pre>
 *    URL url = new URL("ipp://host/printers/printer1");
 *    URLConnection urlc = url.openConnection();
 *    -or-
 *    IppURLConnection urlc = (IppURLConnection) url.openConnection();
 * </pre></blockquote>
 * This implementation wraps a {@link HttpURLConnection} and proxies all but a handful
 * of methods to the wrapped <pre>HttpURLConnection</pre>. This form of implementation
 * is suggested by <a href="http://tools.ietf.org/html/rfc2910#section-5">section 5</a>
 * of RFC2910.
 *
 * @author	Opher
 */
/*package*/ class IppURLConnectionImpl extends IppURLConnection {

    private Handler handler;
    private HttpURLConnection huc;
    private boolean gos_called;

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
     * @see IppURLConnection#setRequestProperty
     */
    @Override
    public void setRequestProperty(String key, String value) {
        if (key.equalsIgnoreCase("Content-Type") && !value.equalsIgnoreCase("application/ipp"))
            throw new IllegalArgumentException("Content-Type may only be 'application/ipp'!");
        huc.setRequestProperty(key, value);
    }

    /**
     * @see IppURLConnection#addRequestProperty
     */
    @Override
    public void addRequestProperty(String key, String value) {
        if (key.equalsIgnoreCase("Content-Type"))
            throw new IllegalArgumentException("Content-Type may only be 'application/ipp'!");
        huc.addRequestProperty(key, value);
    }

    /**
     * @see IppURLConnection#setRequestMethod
     */
    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        if (!method.equalsIgnoreCase("POST"))
            throw new IllegalArgumentException("Request method must be POST!");
        huc.setRequestMethod(method);
    }

    private void sendIppRequest() throws IOException {
        if (ipp == null)
            throw new IllegalStateException("IPP request was not set.");
        String al = getRequestProperty("Accept-Language");
        IPPAttribute attr = ipp.ippFindAttribute("attributes-natural-language", IPPDefs.TAG_LANGUAGE);
        setRequestProperty("Accept-Language", attr.valuesToString(','));
        addRequestProperty("Accept-Language", al);
        OutputStream os = getOutputStream();
        IppTransport.writeRequest(os, ipp);
    }

    @Override
    public IppURLConnection setIppRequest(IPP request) {
        if (gos_called)
            throw new IllegalStateException("Output stream was previously requested.");
        return super.setIppRequest(request);
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
    public Object getContent() throws IOException {
        getInputStream();
        return huc.getContent();
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
