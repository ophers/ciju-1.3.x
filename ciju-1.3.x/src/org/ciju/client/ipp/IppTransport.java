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

import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.URISyntax;
import org.ciju.ipp.IppEncoding.ValueTag;
import org.ciju.ipp.IppObject;
import org.ciju.ipp.IppRequest;
import org.ciju.ipp.IppResponse;

/**
 *
 * @author Opher Shachar
 */
public class IppTransport {

    /**
     *
     * @param os
     * @param ipp
     */
    public static void writeRequest(OutputStream os, IppRequest ipp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param inputStream
     * @param contentLength
     * @return an {@link IppResponse} object from the response.
     */
    public static IppResponse<IppObject> processResponse(InputStream inputStream, long contentLength) {
        return processResponse(inputStream, contentLength, null);
    }

    /**
     *
     * @param inputStream
     * @param contentLength
     * @param obj
     * @param <T>
     * @return an {@link IppResponse} object encompassing the given obj.
     */
    public static <T extends IppObject> IppResponse<T> processResponse(InputStream inputStream, long contentLength, T obj) {
        final CharsetEncoder ascii = Charset.forName("US-ASCII").newEncoder();
        final CharsetEncoder utf8  = Charset.forName("UTF-8").newEncoder();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void writeIppAttribute(Attribute a, DataOutput out, CharsetEncoder utf8) throws IOException {
        ValueTag vt = deduceValueTag(a);
        out.write(vt.getValue());
        out.writeShort(a.getName().length());
        out.writeBytes(a.getName());                // the standard mandates Name to be US-ASCII
        if (vt.getValue() < 0x20)                   // out-of-band value
            out.writeShort(0);                      // has zero-length
        else if (a instanceof Iterable) {
            Iterator iter = ((Iterable) a).iterator();
            writeIppValue(vt, out, iter.next());    // the standard mandates at least one value
            // Attribute value print loop
            while (iter.hasNext()) {
                Object o = iter.next();             // the standard allows for each value to have
                ValueTag ovt = deduceValueTag(o);   // a diffrent syntax
                out.write(ovt.getValue());
                out.writeShort(0);                  // nameless attribute indicates a multi-value
                writeIppValue(ovt, out, o);
            }
        }
        else
            writeIppValue(vt, out, a);
    }

    private static ValueTag deduceValueTag(Object o) throws IOException {
        if (o instanceof IntegerSyntax) {
            return ValueTag.INTEGER;
        }
        else if (o instanceof URISyntax) {
            return ValueTag.URI;
        }
        throw new IllegalArgumentException("Attribute does not implement a known Syntax.");
    }
    
    private static void writeIppValue(ValueTag vt, DataOutput out, Object o) throws IOException {
        switch (vt) {
            case INTEGER:
                out.writeShort(4);
                out.writeInt(((IntegerSyntax) o).getValue());
                break;
            case ENUM:
                out.writeShort(4);
                out.writeInt(((EnumSyntax) o).getValue());
                break;
            case BOOLEAN:
                out.writeShort(1);
                out.write(((EnumSyntax) o).getValue());
                break;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
