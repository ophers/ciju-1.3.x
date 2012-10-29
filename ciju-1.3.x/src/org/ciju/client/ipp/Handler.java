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

import com.easysw.cups.IPPDefs;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 *
 * @author Opher
 */
public class Handler extends URLStreamHandler {
    
    /**
     * Instantiate a {@link IppURLConnection} using a IPP/S <b>URI</b> and Proxy.
     * <br>In restricted (secured) environments it may be that we couldn't register
     * our URL Stream Handler so this would fail: new URL("ipp://...")
     * <br>Consequently we allow for a IPP/S URI via this constructor.
     * @param u a IPP/S {@linkplain URI} for an IPP print service.
     * @param p the {@linkplain Proxy} through which to connect, {@linkplain Proxy#NO_PROXY}
     *      for direct connection or <code>null</code> to use the system's default
     *      {@linkplain ProxySelector} settings.
     * @throws MalformedURLException if some error occurred while constructing the URL
     *      from the URI <code>u</code>.
     * @throws IOException if failed to create a connection.
     * @throws SecurityException if a security manager is present and the caller 
     *      doesn't have permission to connect to the proxy.
     * @throws IllegalArgumentException will be thrown if proxy has the wrong type,
     *      or the URI <code>u</code> does not specify one of the schemes:
     *      <strong>ipp ipps</strong>.
     */
    public IppURLConnection openConnection(URI u, Proxy p) throws IOException {
        String scheme = u.getScheme();
        if (scheme.equalsIgnoreCase("ipp"))
            scheme = "http";
        else if (scheme.equalsIgnoreCase("ipps"))
            scheme = "https";
        else
            throw new IllegalArgumentException("The scheme may only be ipp or ipps.");
        int port = u.getPort() == -1 ? this.getDefaultPort() : u.getPort();

        URL url;
        try {
            url = new URI(scheme, u.getUserInfo(), u.getHost(), port, u.getPath(),
                    u.getQuery(), u.getFragment()).toURL();
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
        
        return new IppURLConnectionImpl(url, p, this);
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        return new IppURLConnectionImpl(u, p, this);
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return openConnection(u, null);
    }

    @Override
    protected int getDefaultPort() {
        return IPPDefs.PORT;
    }

}
