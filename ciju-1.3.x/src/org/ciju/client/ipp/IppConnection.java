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

package org.ciju.client.ipp;

import java.io.IOException;
import java.util.List;
import org.ciju.ipp.IppMultiObject;
import org.ciju.ipp.IppObjectFactory;
import org.ciju.ipp.IppRequest;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppResponse;


/**
 * Definition of the interface for an IPP connection consumed by 
 * <a href="{@docRoot}/org/ciju/client/package-summary.html">org.ciju.client</a>.* classes.
 * We provide two implementations of this interface. One extends {@link java.net.HttpURLConnection} 
 * and the other wraps an {@link org.apache.http.client.HttpClient Apache Client} class.
 *
 * @author	Opher
 */
public interface IppConnection {

    /**
     * Set the {@link IppRequest} request object to send to the server. This method
     * doesn't cause the request to be sent or this object to be connected.
     * <br>Once set the <i>ipp request</i> cannot be cleared.
     * @param request The <code>IppRequest</code> request object.
     * @return this object (allows for builder pattern)
     * @throws IllegalStateException if already connected
     * @throws NullPointerException if request is <code>null</code>
     */
    IppConnection setIppRequest(IppRequest request);

    /**
     * Get the {@link IppRequest} request object set earlier with {@link #setIppRequest}.
     * @return the <code>IppRequest</code> object set with <code>setIppRequest</code>
     */
    IppRequest getIppRequest();

    /**
     * Retrieves the content for the {@link IppRequest} sent on this connection.
     * The given <code>obj</code>, if not null, will be populated with from response data.
     * @param <T> The class of an {@linkplain IppObject} or a descendant thereof.
     * @param obj The object (a subclass of <code>IppObject</code>) to use as the response.
     *      Can be <code>null</code>.
     * @return the {@linkplain IppResponse} fetched, holding the given object.
     * @throws java.io.IOException if an I/O error occurs while getting the content.
     * @throws java.net.UnknownServiceException if the content type is not <tt>application/ipp</tt>.
     * @throws IllegalStateException if the response contains an array of objects and <tt>obj</tt> 
     *      is not of type {@linkplain IppMultiObject}.
     */
    <T extends IppObject> IppResponse<T> getContent(T obj) throws IOException;

    /**
     * Retrieves the content for the {@link IppRequest} sent on this connection.
     * The given <code>fact</code> will be used to create instances to populated from response data.
     * @param <T> The class of an {@linkplain IppObject} or a descendant thereof.
     * @param fact A factory class capable of {@link IppObjectFactory#create(org.ciju.ipp.IppEncoding.GroupTag) 
     *      creating} instances to populate from the response.
     * @return a {@linkplain List} of <code>IppObject</code>S, or a descendant thereof, fetched.
     * @throws java.io.IOException if an I/O error occurs while getting the content.
     * @throws java.net.UnknownServiceException if the content type is not <tt>application/ipp</tt>.
     * @throws NullPointerException if <tt>fact</tt> is null.
     */
    <T extends IppObject> List<T> getContent(IppObjectFactory<T> fact) throws IOException;
    
}
