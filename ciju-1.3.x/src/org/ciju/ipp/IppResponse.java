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

package org.ciju.ipp;

/**
 *
 * @author Opher Shachar
 * @param <T>
 */
public class IppResponse<T extends IppObject> extends IppObject {
    
    private final T obj;
    private final IppHeader header;

    public IppResponse(short version) {
        this(version, null);
    }
    
    @SuppressWarnings("unchecked")
    public IppResponse(short version, T obj) {
        if (obj != null)
            this.obj = obj;
        else
            this.obj = (T) this;
            
        this.header = new IppHeader(version);
    }

    public short getVersion() {
        return header.getVersion();
    }

    public short getResponseCode() {
        return header.getCode();
    }

    public void setResponseCode(short responseCode) {
        header.setCode(responseCode);
    }

    public int getRequestId() {
        return header.getRequestId();
    }

    public void setRequestId(int requestId) {
        header.setRequestId(requestId);
    }

    public T getObject() {
        if (obj != this)
            return obj;
        else
            return null;
    }
    
    // TODO: Delegate relevant methods to 'obj'
}
