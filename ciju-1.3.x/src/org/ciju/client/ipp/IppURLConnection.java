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

import java.io.IOException;
import org.ciju.ipp.IppRequest;
import org.ciju.ipp.IppObject;


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
 * IppObject response = (IppObject) urlc.setIppRequest(request).getContent();
 * </pre></blockquote>
 * For a server application (ie. running on JavaEE) there is no public API to URLConnection
 * that allows specifying credentials on a per connection basis.
 *
 * @author	Opher
 */
public interface IppURLConnection {

    /**
     * Set the {@link IppRequest} request object to send to the server. This method
     * doesn't cause the request to be sent or this object to be connected.
     * <br>Once set the <i>ipp request</i> cannot be cleared.
     * @param request The <code>IppObject</code> request object.
     * @return this object (allows for builder pattern)
     * @throws IllegalStateException if already connected
     * @throws NullPointerException if request is <code>null</code>
     */
    public abstract IppURLConnection setIppRequest(IppRequest request);

    /**
     * Get the {@link IppRequest} request object set earlier with {@link #setIppRequest}.
     * @return the <code>IppRequest</code> object set with <code>setIppRequest</code>
     */
    public abstract IppRequest getIppRequest();

    /**
     * Retrieves the content for the {@link IppRequest} sent on this connection.
     * @return the {@linkplain IppObject} fetched. The instanceof operator should be used to determine the specific kind of object returned.
     * @throws java.io.IOException if an I/O error occurs while getting the content.
     * @throws java.net.UnknownServiceException if the content type is not <tt>application/ipp</tt>.
     */
    public abstract IppObject getContent() throws IOException;

    /**
     * Retrieves the content for the {@link IppRequest} sent on this connection.
     * @param <T> The class of an {@linkplain IppObject} or a descendant thereof.
     * @param o The object to use as the response.
     * @return the {@linkplain IppObject} fetched (the object passed as input).
     * @throws java.io.IOException if an I/O error occurs while getting the content.
     * @throws java.net.UnknownServiceException if the content type is not <tt>application/ipp</tt>.
     */
    public abstract <T extends IppObject> T getContent(T o) throws IOException;

    /**
     * Retrieves the content for the {@link IppRequest} sent on this connection.
     * @param <T> The class of an {@linkplain IppObject} or a descendant thereof.
     * @param t The class to use as the response.
     * @return the {@linkplain IppObject}, or a descendant thereof, fetched.
     * @throws java.io.IOException if an I/O error occurs while getting the content.
     * @throws java.net.UnknownServiceException if the content type is not <tt>application/ipp</tt>.
     */
    public abstract <T extends IppObject> T getContent(Class<T> t) throws IOException;
    
}
