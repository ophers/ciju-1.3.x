/*
 * Copyright (C) 2013 Opher Shachar
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

package org.ciju.client.impl.apache;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.ciju.client.ipp.IppObject;
import org.ciju.client.ipp.IppTransport;


/**
 * This class is part of the Apache HttpClient supporting classes.
 * @author Opher
 */
public class IppResponseHandler implements ResponseHandler<IppObject> {

    public IppObject handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        final HttpEntity entity = response.getEntity();
        return IppTransport.processResponse(entity.getContent(), entity.getContentLength());
    }

}
