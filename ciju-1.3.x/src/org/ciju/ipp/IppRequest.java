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

import java.util.Locale;
import javax.print.attribute.Attribute;
import static org.ciju.ipp.IppTransport.resourceStrings;

/**
 *
 * @author Opher Shachar
 */
public class IppRequest extends IppObject {
    
    private final IppHeader header;
    private final Locale    locale;

    public IppRequest() {
        this.header = new IppHeader();
        this.locale = Locale.getDefault();
    }

    public IppRequest(IppEncoding.OpCode opCode) {
        this.header = new IppHeader(opCode);
        this.locale = Locale.getDefault();
    }

    public IppRequest(IppEncoding.OpCode opCode, int requestId) {
        this.header = new IppHeader(opCode, requestId);
        this.locale = Locale.getDefault();
    }

    public IppRequest(Locale locale) {
        this.header = new IppHeader();
        this.locale = validate(locale);
    }

    public IppRequest(IppEncoding.OpCode opCode, Locale locale) {
        this.header = new IppHeader(opCode);
        this.locale = validate(locale);
    }

    public IppRequest(IppEncoding.OpCode opCode, int requestId, Locale locale) {
        this.header = new IppHeader(opCode, requestId);
        this.locale = validate(locale);
    }

    private Locale validate(Locale locale) throws NullPointerException, IllegalArgumentException {
        if (locale == null) {
            throw new NullPointerException("locale");
        } else if (locale.getLanguage().length() == 0) {
            throw new IllegalArgumentException(resourceStrings.getString("LOCALE CANNOT HAVE AN EMPTY LANGUAGE FIELD."));
        }
        return locale;
    }

    public short getVersion() {
        return header.getVersion();
    }

    public IppEncoding.OpCode getOpCode() {
        return IppEncoding.OpCode.valueOf(Integer.valueOf(header.getCode()));
    }

    public void setOpCode(IppEncoding.OpCode opCode) {
        header.setCode((short) opCode.getValue());
    }

    public int getRequestId() {
        return header.getRequestId();
    }

    public void setRequestId(int requestId) {
        header.setRequestId(requestId);
    }

    public Locale getLocale() {
        return locale;
    }
    
    public Iterable<Attribute> getOperAttrs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 }
