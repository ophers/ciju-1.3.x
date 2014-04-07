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

import javax.print.attribute.Attribute;
import static org.ciju.ipp.IppEncoding.GroupTag;
import static org.ciju.ipp.IppEncoding.ValueTag;

/**
 *
 * @author Opher Shachar
 */
public class IppRequest extends IppObject {
    
    private final IppHeader header;

    public IppRequest() {
        header = new IppHeader();
    }

    public IppRequest(IppEncoding.OpCode opCode) {
        header = new IppHeader(opCode);
    }

    public IppRequest(IppEncoding.OpCode opCode, int requestId) {
        header = new IppHeader(opCode, requestId);
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
    
    //public IppEncoding.OpCode get
    
    public Attribute getAttributesNaturalLanguage() {
        return getAttribute("attributes-natural-language", GroupTag.OPERATION, ValueTag.NATURAL_LANGUAGE);
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
