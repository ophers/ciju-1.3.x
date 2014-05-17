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
import java.net.ProtocolException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.URISyntax;
import org.ciju.ipp.IppEncoding;
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
    
    private final DataOutput out;
    private final IppRequest request;
    private final CharsetEncoder utf8enc;
    private final ByteBuffer bb;

    private IppTransport(DataOutput out, IppRequest request) {
        this.out = out;
        this.request = request;
        this.utf8enc = Charset.forName("UTF-8").newEncoder();
        this.bb = ByteBuffer.allocate(1023);
    }
    
    /**
     * The picture of the encoding of an attribute is:
     * -----------------------------------------
     * |                value-tag              |   1 byte
     * -----------------------------------------
     * |            name-length  (value is u)  |   2 bytes
     * -----------------------------------------
     * |                  name                 |   u bytes
     * -----------------------------------------
     * |           value-length  (value is v)  |   2 bytes
     * -----------------------------------------
     * |                  value                |   v bytes
     * -----------------------------------------
     * 
     * The picture for next values in multi-valued attribute is:
     * -----------------------------------------
     * |               value-tag               |   1 byte
     * -----------------------------------------
     * |       name-length  (value is 0x0000)  |   2 bytes
     * -----------------------------------------
     * |         value-length (value is w)     |   2 bytes
     * -----------------------------------------
     * |                 value                 |   w bytes
     * -----------------------------------------
     */
    private void writeIppAttribute(Attribute a) throws IOException {
        // determine if the attribute is multi-valued
        Iterator iter;
        if (a instanceof Iterable) 
            iter = ((Iterable) a).iterator();
        else if (a instanceof SetOfIntegerSyntax)
            iter = Arrays.asList(((SetOfIntegerSyntax) a).getMembers()).iterator();
        else
            iter = null;
        
        // get the (first) value and value-tag
        Object o;
        if (iter == null)
            o = a;
        else
            o = iter.next();                        // the standard mandates at least one value
        ValueTag vt = deduceValueTag(o);
        
        // write the attribute
        out.write(vt.getValue());
        out.writeShort(a.getName().length());
        out.writeBytes(a.getName());                // the standard mandates Name to be US-ASCII
        writeIppValue(vt, o);
        if (iter != null) {
            // Attribute value print loop
            while (iter.hasNext()) {
                o = iter.next();                    // the standard allows for each value to have
                vt = deduceValueTag(o);             // a diffrent syntax
                out.write(vt.getValue());
                out.writeShort(0);                  // nameless attribute indicates a multi-value
                writeIppValue(vt, o);
            }
        }
    }

    private ValueTag deduceValueTag(Object o) throws IOException {
        if (o instanceof IntegerSyntax) {
            return ValueTag.INTEGER;
        }
        else if (o instanceof URISyntax) {
            return ValueTag.URI;
        }
        throw new IllegalArgumentException("Attribute does not implement a known Syntax.");
    }

    /**
     * The picture of the encoding of a value is:
     * ---------------------
     * |   value-length    |   2 bytes
     * ---------------------
     * |          value    |   v bytes
     * ---------------------
     */
    private void writeIppValue(ValueTag vt, Object o) throws IOException {
        int n = -1;                                 // used for TEXT/NAME_WITH*_LANGUAGE
        switch (vt) {
            case UNSUPPORTED:
            case UNKNOWN:
            case NO_VALUE:
            case NOT_SETTABLE:
            case DELETE_ATTRIBUTE:
            case ADMIN_DEFINE:                      // the above out-of-band values
            case BEGIN_COLLECTION:
            case END_COLLECTION:                    // and begin/end-collection
                out.writeShort(0);                  // have zero-length
                break;
            case INTEGER:
                out.writeShort(4);
                out.writeInt(((IntegerSyntax) o).getValue());
                break;
            case BOOLEAN:
                out.writeShort(1);
                out.write(((EnumSyntax) o).getValue());
                break;
            case ENUM:
                out.writeShort(4);
                out.writeInt(((EnumSyntax) o).getValue());
                break;
            case OCTET_STRING:
                // there is no standard attribute using this syntax ...
                out.writeShort(((byte[]) o).length);
                out.write((byte[]) o);
                break;
            case DATE_TIME:
                // setup calendar object
                Date date = ((DateTimeSyntax) o).getValue();
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);
                // write value
                out.writeShort(11);
                out.writeShort(cal.get(Calendar.YEAR));
                out.write(cal.get(Calendar.MONTH));
                out.write(cal.get(Calendar.DAY_OF_MONTH));
                out.write(cal.get(Calendar.HOUR_OF_DAY));
                out.write(cal.get(Calendar.MINUTE));
                out.write(cal.get(Calendar.SECOND));
                out.write(cal.get(Calendar.MILLISECOND) / 100);
                // timezone offset
                int minoff = cal.get(Calendar.ZONE_OFFSET) / 60000;
                if (minoff < 0) {
                    out.write('-');
                    minoff = -minoff;
                } else
                    out.write('+');
                out.write(minoff / 60);
                out.write(minoff % 60);
                break;
            case RESOLUTION:
                out.writeShort(9);
                out.writeInt(((ResolutionSyntax) o).getCrossFeedResolution(ResolutionSyntax.DPI));
                out.writeInt(((ResolutionSyntax) o).getFeedResolution(ResolutionSyntax.DPI));
                out.write(3);                       // 3 == dpi
                break;
            case RANGE_OF_INTEGER:
                out.writeShort(8);
                out.writeInt(((int[]) o)[0]);
                out.writeInt(((int[]) o)[1]);
                break;
            case TEXT_WITH_LANGUAGE:
            case NAME_WITH_LANGUAGE:
                TextSyntax t = (TextSyntax) o;
                String nl = getNaturalLanguage(t.getLocale());
                n = encodeStringUTF8(o.toString(), bb, deduceValueLimit(o.getClass(), vt.MAX));
                out.writeShort(4 + nl.length() + n);
                out.writeShort(nl.length());
                out.writeBytes(nl);                 // locale-country is always US-ASCII
                // fall through to the string cases
            case TEXT_WITHOUT_LANGUAGE:
            case NAME_WITHOUT_LANGUAGE:
            case KEYWORD:
            case URI:
            case URI_SCHEME:
            case CHARSET:
            case NATURAL_LANGUAGE:
            case MIME_MEDIA_TYPE:
                if (n < 0)
                    n = encodeStringUTF8(o.toString(), bb, deduceValueLimit(o.getClass(), vt.MAX));
                out.writeShort(n);
                out.write(bb.array(), 0, n);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private int encodeStringUTF8(String str, ByteBuffer bb, int limit)
            throws ProtocolException, CharacterCodingException {
        bb.clear();
        bb.limit(limit);
        utf8enc.reset();
        CoderResult cr = utf8enc.encode(CharBuffer.wrap(str), bb, true);
        if (cr.isUnderflow())
            cr = utf8enc.flush(bb);
        if (cr.isOverflow())
            throw new ProtocolException("Attribute string is larger than " + bb.limit() + " bytes encoded as utf-8.");
        else if (!cr.isUnderflow())
            cr.throwException();
        return bb.position();
    }

    private int deduceValueLimit(Class<?> o, int MAX) {
        return IppEncoding.LengthLimits.containsKey(o) ?
                IppEncoding.LengthLimits.get(o) :
                MAX;
    }

    private String getNaturalLanguage(Locale locale) {
        String lang = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if (lang.length() == 0)
            throw new IllegalArgumentException("Locale cannot have an empty language field.");
        if (lang.equals("iw"))
            lang = "he";                            // new code for Hebrew
        // TODO: code all changed 2-letter language codes
        return country.length() == 0 ? lang : lang + "-" + country;
    }
}
