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
import java.net.*;


/**
 * Definition of an IPP connection by extending the HttpURLConnection class.
 * Implementation of this class must set properties appropriate for the IPP protocol, like:
 * <ul>
 *      <li>doOutput = true
 *      <li>Request-Method = POST
 *      <li>Content-Type = application/ipp
 * </ul>
 * You get an instance of this class by doing something like:<blockquote><pre>
 * URL url = new URL("ipp://host/printers/printer1");
 * URLConnection urlc = url.openConnection();
 *    -or-
 * IppURLConnection urlc = (IppURLConnection) url.openConnection();
 * </pre></blockquote>
 * The audience for this class would be desktop tool developers.
 * A possible use pattern could be: <blockquote><pre>
 * Authenticator.setDefault(...);
 * URL url = new URL("ipp://...");
 * IPP request = ...;
 * IppURLConnection urlc = (IppURLConnection) url.openConnection();
 * IPP response = (IPP) urlc.setIppRequest(request).getContent();
 * </pre></blockquote>
 * For a server application (ie. running on JavaEE) there is no public API to URLConnection
 * that allows specifying credentials on a per connection basis.
 *
 * @author	Opher
 */
public abstract class IppURLConnection extends HttpURLConnection {
    
    /**
     * The {@link IPP} object that represents the request.
     */
    protected IPP ipp;

    /**
     * Constructor for the IppURLConnection class.
     * @param u the {@linkplain URL} for the connection.
     */
    protected IppURLConnection(URL u) {
        super(u);
    }

    /**
     * This <code>URLConnection</code> has by default its doOutput flag set to
     * <code>true</code>. It may not be set to <code>false</code>.
     * @param dooutput  must be <code>true</code>.
     * @throws IllegalArgumentException if dooutput is passed a value of false.
     */
    @Override
    public void setDoOutput(boolean dooutput) {
        if (!dooutput)
            throw new IllegalArgumentException("Empty requests are not valid!");
        super.setDoOutput(dooutput);
    }

    /**
     * {@inheritDoc}
     * <br>The property <code>Content-Type</code> is set by default to
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
        super.setRequestProperty(key, value);
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
        super.addRequestProperty(key, value);
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
        super.setRequestMethod(method);
    }

    /**
     * Set the {@link IPP} request object to send to the server. This method
     * doesn't cause the request to be sent or this object to be connected.
     * <br>Once set the <i>ipp request</i> cannot be cleared.
     * @param request The <code>IPP</code> request object.
     * @throws IllegalStateException if already connected
     * @throws NullPointerException if request is <code>null</code>
     */
    public IppURLConnection setIppRequest(IPP request) {
        if (connected)
            throw new IllegalStateException("Already connected");
        if (request == null) 
	    throw new NullPointerException("IPP request is null");
        ipp = request;
        return this;
    }

    /**
     * Get the {@link IPP} request object set earlier with {@link #setIppRequest}.
     * @return the <code>IPP</code> object set with <code>setIppRequest</code>
     */
    public IPP getIppRequest() {
        return ipp;
    }
    
}
