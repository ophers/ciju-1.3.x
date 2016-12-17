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

package org.ciju.client.impl.apache;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.util.List;
import org.ciju.ipp.IppObjectFactory;
import org.ciju.client.ipp.IppConnection;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppRequest;
import org.ciju.ipp.IppResponse;


public class ApacheConnection implements IppConnection {

    public ApacheConnection(URI uri, Proxy proxy) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public IppConnection setIppRequest(IppRequest request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public IppRequest getIppRequest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public <T extends IppObject> IppResponse<T> getContent(T obj) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public <T extends IppObject> List<T> getContent(IppObjectFactory<T> fact) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public IppConnection setPasswordAuthentication(PasswordAuthentication authn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
