/*
 * Copyright (C) 2012-2014 Opher Shachar
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
 */
/* package */ class IppHeader {
    
    private final short version;
    private short code;
    private int requestId;

    public short getVersion() {
        return version;
    }

    public short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
    
    public IppHeader() {
        this.version = IppEncoding.DEF_VERSION;
    }

    public IppHeader(short version, IppEncoding.OpCode code, int requestId) {
        this.version = version;
        this.code = (short) code.getValue();
        this.requestId = requestId;
    }

    public IppHeader(IppEncoding.OpCode code, int requestId) {
        this();
        this.code = (short) code.getValue();
        this.requestId = requestId;
    }

    public IppHeader(IppEncoding.OpCode code) {
        this();
        this.code = (short) code.getValue();
        this.requestId = 1;
    }

    public IppHeader(short version, short responseCode, int requestId) {
        this.version = version;
        this.code = responseCode;
        this.requestId = requestId;
    }

}
