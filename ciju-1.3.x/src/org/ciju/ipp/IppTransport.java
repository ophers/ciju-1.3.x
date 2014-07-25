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

import java.io.DataOutput;
import java.io.DataOutputStream;
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
import static java.util.Map.Entry;
import java.util.TimeZone;
import javax.print.attribute.Attribute;
import javax.print.attribute.CijuAttributeUtils;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.Size2DSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.URISyntax;
import javax.print.attribute.standard.JobHoldUntil;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.Severity;
import static org.ciju.ipp.IppEncoding.GroupTag;
import static org.ciju.ipp.IppEncoding.ValueTag;

/**
 *
 * @author Opher Shachar
 */
public class IppTransport {

    /**
     * 
     * @param os
     * @param ipp
     * @throws java.io.IOException
     */
    public static void writeRequest(OutputStream os, IppRequest ipp) throws IOException {
        IppTransport t = new IppTransport(new DataOutputStream(os), ipp);
        t.writeRequest();
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
     * The picture of the encoding for a request or response is:<pre>
     * -----------------------------------------------
     * |                  version-number             |   2 bytes
     * -----------------------------------------------
     * |               operation-id (request)        |
     * |                      or                     |   2 bytes
     * |               status-code (response)        |
     * -----------------------------------------------
     * |                   request-id                |   4 bytes
     * -----------------------------------------------
     * |                 attribute-group             |   n bytes - 0 or more
     * -----------------------------------------------
     * |              end-of-attributes-tag          |   1 byte
     * -----------------------------------------------
     * |                     data                    |   q bytes - optional
     * -----------------------------------------------
     * </pre>
     */
    private void writeRequest() throws IOException {
        // write request header
        out.writeShort(request.getVersion());
        out.writeShort(request.getOpCode().getValue());
        out.writeShort(request.getRequestId());
        writeOperAttrs(getNaturalLanguage(request.getLocale()));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void writeOperAttrs(String anl) throws IOException {
        // write operational attributes group tag
        out.write(GroupTag.OPERATION.getValue());
        // write charset attribute. Always utf-8.
        out.write(ValueTag.CHARSET.getValue());
        out.writeShort(18);
        out.writeBytes("attributes-charset");
        out.writeShort(5);
        out.writeBytes("utf-8");
        // write natural language attribute
        out.write(ValueTag.NATURAL_LANGUAGE.getValue());
        out.writeShort(27);
        out.writeBytes("attributes-natural-language");
        out.writeShort(anl.length());
        out.writeBytes(anl);
        // write request operational attributes
        for (Attribute attr : request.getOperAttrs())
            writeIppAttribute(attr);
    }

    /**
     * The picture of the encoding of an attribute is:
     * <pre>
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
     * </pre>
     * The picture for next values in multi-valued attribute is:<pre>
     * -----------------------------------------
     * |               value-tag               |   1 byte
     * -----------------------------------------
     * |       name-length  (value is 0x0000)  |   2 bytes
     * -----------------------------------------
     * |         value-length (value is w)     |   2 bytes
     * -----------------------------------------
     * |                 value                 |   w bytes
     * -----------------------------------------
     * </pre>
     */
    private void writeIppAttribute(Attribute a) throws IOException {
        Iterator iter = null;
        Object o = a;
        // determine if the attribute is multi-valued
        if (a instanceof Iterable) {
            iter = ((Iterable) a).iterator();
            o = iter.next();                        // the standard mandates at least one value
        }

        // get the value-tag
        ValueTag vt = deduceValueTag(o);
        // write the attribute
        out.write(vt.getValue());
        out.writeShort(a.getName().length());
        out.writeBytes(a.getName());                // the standard mandates Name to be US-ASCII
        if (iter != null)
            writeIppMultiValue(vt, o, iter);
        else if (a instanceof PrinterStateReasons)
            writeIppMultiValue(vt, (PrinterStateReasons) a);
        else if (a instanceof SetOfIntegerSyntax)
            writeIppMultiValue(vt, (SetOfIntegerSyntax) a);
        else
            writeIppValue(vt, a);
    }

    private void writeIppMultiValue(ValueTag vt, Object o, Iterator iter) throws IOException {
        while (true) {
            if (o instanceof SetOfIntegerSyntax)
                writeIppMultiValue(vt, (SetOfIntegerSyntax) o);
            else
                writeIppValue(vt, o);
            // Attribute value print loop
            if (iter.hasNext()) {
                o = iter.next();                    // the standard allows for each value to have
                vt = deduceValueTag(o);             // a diffrent syntax
                out.write(vt.getValue());
                out.writeShort(0);                  // nameless attribute indicates a multi-value
            }
            else break;
        }
    }

    private void writeIppMultiValue(ValueTag vt, PrinterStateReasons psr) throws IOException {
        Iterator<Entry<PrinterStateReason, Severity>> iter = psr.entrySet().iterator();
        Entry<PrinterStateReason, Severity> o;
        do {
            o = iter.next();            
            writeIppValue(vt, o.getKey().toString() + "-" + o.getValue().toString());
            // Attribute value print loop
            if (iter.hasNext()) {
                out.write(vt.getValue());
                out.writeShort(0);                  // nameless attribute indicates a multi-value
            }
        } while (iter.hasNext());
    }

    private void writeIppMultiValue(ValueTag vt, SetOfIntegerSyntax sois) throws IOException {
        Iterator<int[]> iter = Arrays.asList(sois.getMembers()).iterator();
        do {
            writeIppValue(vt, iter.next());
            // Attribute value print loop
            if (iter.hasNext()) {
                out.write(vt.getValue());
                out.writeShort(0);                  // nameless attribute indicates a multi-value
            }
        } while (iter.hasNext());
    }

    /**
     * The JPS Object Model has its quirks (mostly documented).
     * So, for example, JobHoldUntil needs a special consideration.
     */
    private ValueTag deduceValueTag(Object o) throws IOException {
        if (o instanceof PrinterStateReasons)
            return ValueTag.KEYWORD;
        else if (o instanceof DateTimeSyntax)
            return ValueTag.DATE_TIME;
        else if (o instanceof EnumSyntax)
            return CijuAttributeUtils.deduceEnumIPPSyntax((EnumSyntax) o);
        else if (o instanceof IntegerSyntax)
            return ValueTag.INTEGER;
        else if (o instanceof ResolutionSyntax)
            return ValueTag.RESOLUTION;
        else if (o instanceof SetOfIntegerSyntax)
            return ValueTag.RANGE_OF_INTEGER;
        else if (o instanceof TextSyntax)
            return deduceTextIPPSyntax((TextSyntax) o);
        else if (o instanceof URISyntax)
            return ValueTag.URI;
        else if (o instanceof int[])
            return ValueTag.RANGE_OF_INTEGER;

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
        Date date;
        Calendar cal;
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
                date = ((DateTimeSyntax) o).getValue();
                cal = new GregorianCalendar();
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
                String nl = getNaturalLanguage(((TextSyntax) o).getLocale());
                n = encodeStringUTF8(o.toString(), bb, deduceValueLimit(o.getClass(), vt.MAX));
                out.writeShort(4 + nl.length() + n);
                out.writeShort(nl.length());
                out.writeBytes(nl);                 // locale-country is always US-ASCII
                // fall through to the string cases
            case TEXT_WITHOUT_LANGUAGE:
            case NAME_WITHOUT_LANGUAGE:
            case URI:
                if (n < 0)
                    n = encodeStringUTF8(o.toString(), bb, deduceValueLimit(o.getClass(), vt.MAX));
                out.writeShort(n);
                out.write(bb.array(), 0, n);
                break;
            case KEYWORD:
            case URI_SCHEME:
            case CHARSET:
            case NATURAL_LANGUAGE:
            case MIME_MEDIA_TYPE:
            case MEMBER_ATTR_NAME:
                String str = o.toString();
                if (str.length() > vt.MAX)
                    throw new ProtocolException("Attribute string is longer than " + vt.MAX + ".");
                out.writeShort(str.length());
                out.writeBytes(str);                // these syntaxes are always US-ASCII
                break;
            default:
                assert false : "This ValueTag " + vt + " is unknown!";
        }
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
        else if (lang.equals("ji"))
            lang = "yi";                            // new code for Yiddish
        else if (lang.equals("in"))
            lang = "id";                            // new code for Indonesian
        return country.length() == 0 ? lang : lang + "-" + country;
    }

    private ValueTag deduceTextIPPSyntax(TextSyntax o) {
        boolean wol = o.getLocale().equals(request.getLocale());
        Attribute a = (Attribute) o;
        if (a.getName().endsWith("-name") ||
            a.getName().equals("output-device-assigned"))
            return wol ? ValueTag.NAME_WITHOUT_LANGUAGE :
                         ValueTag.NAME_WITH_LANGUAGE;
        return wol ? ValueTag.TEXT_WITHOUT_LANGUAGE :
                     ValueTag.TEXT_WITH_LANGUAGE;
    }
}
