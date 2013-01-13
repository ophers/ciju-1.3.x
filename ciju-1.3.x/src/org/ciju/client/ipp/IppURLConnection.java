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

package org.ciju.client.ipp;

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
 * IppRequest request = ...;
 * IppURLConnection urlc = (IppURLConnection) url.openConnection();
 * IppResponse response = (IppResponse) urlc.setIppRequest(request).getContent();
 * </pre></blockquote>
 * For a server application (ie. running on JavaEE) there is no public API to URLConnection
 * that allows specifying credentials on a per connection basis.
 *
 * @author	Opher
 */
public abstract class IppURLConnection extends HttpURLConnection {
    
    /**
     * The {@link IppRequest} object that represents the request.
     */
    protected IppRequest ipp;

    /**
     * Constructor for the IppURLConnection class.
     * @param u the {@linkplain URL} for the connection.
     */
    protected IppURLConnection(URL u) {
        super(u);
    }

    /**
     * Set the {@link IppRequest} request object to send to the server. This method
     * doesn't cause the request to be sent or this object to be connected.
     * <br>Once set the <i>ipp request</i> cannot be cleared.
     * @param request The <code>IppObject</code> request object.
     * @throws IllegalStateException if already connected
     * @throws NullPointerException if request is <code>null</code>
     */
    public IppURLConnection setIppRequest(IppRequest request) {
        if (connected)
            throw new IllegalStateException("Already connected");
        if (request == null) 
	    throw new NullPointerException("IPP request is null");
        ipp = request;
        return this;
    }

    /**
     * Get the {@link IppRequest} request object set earlier with {@link #setIppRequest}.
     * @return the <code>IppObject</code> object set with <code>setIppRequest</code>
     */
    public IppRequest getIppRequest() {
        return ipp;
    }
    
}
