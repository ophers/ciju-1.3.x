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

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.ProtocolException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.attribute.Attribute;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.Severity;
import org.ciju.ipp.IppEncoding.GroupTag;
import org.ciju.ipp.IppEncoding.ValueTag;
import org.ciju.ipp.IppObject.Conformity;
import org.ciju.ipp.attribute.AttributeGroup;
import org.ciju.ipp.attribute.GenericAttribute;
import org.ciju.ipp.attribute.GenericValue;
import static org.ciju.ipp.attribute.GenericValue.deduceValueTag;
import static org.ciju.ipp.attribute.GenericValue.getNaturalLanguage;

/**
 *
 * @author Opher Shachar
 */
public abstract class IppTransport {

    // Logging facilities
    /* package */ static final ResourceBundle resourceStrings = ResourceBundle.getBundle("org/ciju/ResourceStrings");
    /* package */ static final Logger logger;
    static {
        String name = IppTransport.class.getName();
        String packageName = name.substring(0, name.lastIndexOf('.'));
        logger = Logger.getLogger(packageName, "org/ciju/ResourceStrings");
    }

    /**
     * Writes an IPP request to the given {@link OutputStream}.
     * 
     * @param os the <tt>OutputStream</tt> to write the request to.
     * @param ipp the IPP request
     * @throws IOException
     */
    public static void writeRequest(OutputStream os, IppRequest ipp) throws IOException {
        IppTransportEncoder t = new IppTransportEncoder(os, ipp);
        // this would be substituted by Java7 try-with-resources
        IOException ioex = null;
        try {
            t.writeRequest();
        } catch (IOException ex) {
            ioex = ex;
        } finally {
            for (Closeable io : t.ios) {
                try {
                    if (io != null)
                        io.close();
                } catch (IOException ex) {
                    if (ioex == null)
                        ioex = ex;
                    else
                        // otherwise supress exception
                        logger.logp(Level.FINE, io.getClass().getName(), "close()", "SUPRESSING EXCEPTION", ex);
                }
            }
            if (ioex != null)
                throw ioex;
        }
    }

    /**
     *
     * @param inputStream
     * @param contentLength
     * @return an {@link IppResponse} object from the response.
     * @throws IOException
     */
    public static IppResponse<IppObject> processResponse(InputStream inputStream, long contentLength)
            throws IOException {
        return processResponse(inputStream, contentLength, null);
    }

    /**
     *
     * @param is
     * @param contentLength
     * @param obj
     * @param <T>
     * @return an {@link IppResponse} object encompassing the given obj.
     * @throws IOException
     */
    public static <T extends IppObject> IppResponse<T> processResponse(InputStream is, long contentLength, T obj)
            throws IOException {
        IppTransportDecoder<T> t = new IppTransportDecoder<T>(is, contentLength);
        // this would be substituted by Java7 try-with-resources
        IOException ioex = null;
        try {
            return t.processResponse(obj);
        } catch (IOException ex) {
            ioex = ex;
        } finally {
            for (Closeable io : t.ios) {
                try {
                    if (io != null)
                        io.close();
                } catch (IOException ex) {
                    if (ioex == null)
                        ioex = ex;
                    else
                        // otherwise supress exception
                        logger.logp(Level.FINE, io.getClass().getName(), "close()", "SUPRESSING EXCEPTION", ex);
                }
            }
            if (ioex != null)
                throw ioex;
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Instance common members
    final Closeable[] ios = new Closeable[2];


    /** IppTransportEncoder - Encodes / sends an IPP request */
    private static class IppTransportEncoder extends IppTransport {
        private final DataOutputStream out;
        private final CharsetEncoder utf8enc;
        private final IppRequest request;
        private       ByteBuffer bb = ByteBuffer.allocate(ValueTag.TEXT_WITHOUT_LANGUAGE.MAX);

        private IppTransportEncoder(OutputStream out, IppRequest request) {
            ios[0] = this.out = new DataOutputStream(out);
            this.request = request;
            utf8enc = Charset.forName("UTF-8").newEncoder();
            if (request.conformity != Conformity.STRICT)
                utf8enc.onMalformedInput(CodingErrorAction.REPLACE)
                       .onUnmappableCharacter(CodingErrorAction.REPLACE);
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
            // write operation attributes
            writeOperationHead();
            Iterator<AttributeGroup> it = request.getAttributeGroups().iterator();
            AttributeGroup ag = it.next();      // first is operation attributes
            for (Attribute attr : ag)
                writeIppAttribute(attr);
            // write all other attributes
            while (it.hasNext()) {
                ag = it.next();
                out.write(ag.groupTag().getValue());
                for (Attribute attr : ag)
                    writeIppAttribute(attr);
            }
            // write end attributes group tag
            out.write(GroupTag.END.getValue());
            // write request data if provided
            int n;
            switch (request.getDocDataFlavor()) {
                case STREAM:
                    InputStream in = request.getDoc().getStreamForBytes();
                    ios[1] = in;
                    while ((n = in.read(bb.array())) > 0)
                        out.write(bb.array(), 0, n);
                    break;
                case READER:
                    Reader rdr = request.getDoc().getReaderForText();
                    ios[1] = rdr;
                    char[] ca = new char[1024];
                    String cstr = request.getDoc().getDocFlavor().getParameter("charset");
                    if (cstr == null)
                        cstr = "utf-16";
                    OutputStreamWriter osw = new OutputStreamWriter(out, cstr);
                    ios[0] = osw;
                    while ((n = rdr.read(ca)) > 0)
                        osw.write(ca, 0, n);
                    break;
            }
        }

        private void writeOperationHead() throws IOException {
            // write operational attributes group tag
            out.write(GroupTag.OPERATION.getValue());
            // write charset attribute. Always utf-8.
            out.write(ValueTag.CHARSET.getValue());
            out.writeShort(18);
            out.writeBytes("attributes-charset");
            out.writeShort(5);
            out.writeBytes("utf-8");
            // write natural language attribute
            String anl = getNaturalLanguage(request.getLocale());
            out.write(ValueTag.NATURAL_LANGUAGE.getValue());
            out.writeShort(27);
            out.writeBytes("attributes-natural-language");
            out.writeShort(anl.length());
            out.writeBytes(anl);
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
            validateConformity(a);

            // get the length-limit for the attribute's value(s)
            Integer ll = IppEncoding.LengthLimits.get(a.getClass());

            Iterator iter = null;
            Object o = a;
            // determine if the attribute is multi-valued
            if (a instanceof Iterable) {
                iter = ((Iterable) a).iterator();
                o = iter.next();                        // the standard mandates at least one value
            }

            // get the value-tag
            ValueTag vt = deduceValueTag(o, request.getLocale());
            // write the attribute
            out.write(vt.getValue());
            out.writeShort(a.getName().length());
            out.writeBytes(a.getName());                // the standard mandates Name to be US-ASCII
            if (iter != null)
                writeIppMultiValue(vt, o, ll, iter);
            else if (a instanceof PrinterStateReasons)
                writeIppMultiValue(vt, (PrinterStateReasons) a);
            else if (a instanceof SetOfIntegerSyntax)
                writeIppMultiValue(vt, (SetOfIntegerSyntax) a);
            else
                writeIppValue(vt, a, ll);
        }

        private void validateConformity(Attribute a) throws ProtocolException {
            // Validate attribute name
            if (a.getName().length() > Short.MAX_VALUE) {
                throw new ProtocolException(MessageFormat.format(resourceStrings.getString("ATTRIBUTE NAME IS LONGER THAN {0}."), Short.MAX_VALUE, a.getName()));
            }
            else if (a.getName().length() > ValueTag.KEYWORD.MAX) {
                if (request.conformity != IppObject.Conformity.NONE)
                    throw new ProtocolException(MessageFormat.format(resourceStrings.getString("ATTRIBUTE NAME IS LONGER THAN {0}."), ValueTag.KEYWORD.MAX, a.getName()));
            }
        }

        private void writeIppMultiValue(ValueTag vt, Object o, Integer ll, Iterator iter)
                throws IOException {
            while (true) {
                if (o instanceof SetOfIntegerSyntax)
                    writeIppMultiValue(vt, (SetOfIntegerSyntax) o);
                else
                    writeIppValue(vt, o, ll);
                // Attribute value print loop
                if (iter.hasNext()) {
                    o = iter.next();                             // the standard allows for each
                    vt = deduceValueTag(o, request.getLocale()); // value to have a diffrent syntax
                    out.write(vt.getValue());
                    out.writeShort(0);                  // nameless attribute indicates a multi-value
                }
                else break;
            }
        }

        private void writeIppMultiValue(ValueTag vt, PrinterStateReasons psr) throws IOException {
            Iterator<Map.Entry<PrinterStateReason, Severity>> iter = psr.entrySet().iterator();
            Map.Entry<PrinterStateReason, Severity> o;
            do {
                o = iter.next();            
                writeIppValue(vt, o.getKey().toString() + "-" + o.getValue().toString(), null);
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
                writeIppValue(vt, iter.next(), null);
                // Attribute value print loop
                if (iter.hasNext()) {
                    out.write(vt.getValue());
                    out.writeShort(0);                  // nameless attribute indicates a multi-value
                }
            } while (iter.hasNext());
        }

        /**
         * The picture of the encoding of a value is:
         * ---------------------
         * |   value-length    |   2 bytes
         * ---------------------
         * |          value    |   v bytes
         * ---------------------
         */
        private void writeIppValue(ValueTag vt, Object o, Integer ll)
                throws IOException {
            int n = -1;                                 // used for TEXT/NAME_WITH*_LANGUAGE
            int i;                                      // used for ENUM
            Date date;
            Calendar cal;
            // the following would not throw ClassCastException as GenericValue allows
            // just those types for those value-tag.
            o = validateAndTransform(vt, o, ll);
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
                    if (o instanceof IntegerSyntax)
                        i = ((IntegerSyntax) o).getValue();
                    else
                        i = (Integer) o;
                    out.writeInt(i);
                    break;
                case BOOLEAN:
                    out.writeShort(1);
                    if (o instanceof EnumSyntax)
                        i = ((EnumSyntax) o).getValue();
                    else
                        i = (Boolean) o ? 1 : 0;
                    out.write(i);
                    break;
                case ENUM:
                    out.writeShort(4);
                    if (o instanceof EnumSyntax)
                        i = ((EnumSyntax) o).getValue();
                    else
                        i = (Integer) o;
                    out.writeInt(i);
                    break;
                case OCTET_STRING:
                    // there is no standard attribute using this syntax ...
                    out.writeShort(((byte[]) o).length);
                    out.write((byte[]) o);
                    break;
                case DATE_TIME:
                    // DateAndTime 11 octets syntax in RFC1903
                    if (o instanceof Calendar)
                        cal = (Calendar) o;
                    else {
                        if (o instanceof DateTimeSyntax)
                            date = ((DateTimeSyntax) o).getValue();
                        else
                            date = (Date) o;
                        // setup calendar object
                        cal = new GregorianCalendar();
                        cal.setTime(date);
                    }
                    // write local time
                    out.writeShort(11);
                    out.writeShort(cal.get(Calendar.YEAR));
                    out.write(cal.get(Calendar.MONTH));
                    out.write(cal.get(Calendar.DAY_OF_MONTH));
                    out.write(cal.get(Calendar.HOUR_OF_DAY));
                    out.write(cal.get(Calendar.MINUTE));
                    out.write(cal.get(Calendar.SECOND));
                    out.write(cal.get(Calendar.MILLISECOND) / 100);
                    // ... and timezone offset
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
                    n = bb.limit();
                    out.writeShort(4 + nl.length() + n);
                    out.writeShort(nl.length());
                    out.writeBytes(nl);                 // natural-language is always US-ASCII
                    // fall through to the string cases
                case TEXT_WITHOUT_LANGUAGE:
                case NAME_WITHOUT_LANGUAGE:
                    if (n < 0)
                        n = bb.limit();
                    out.writeShort(n);
                    out.write(bb.array(), 0, n);
                    break;
                case KEYWORD:
                case URI:
                case URI_SCHEME:
                case CHARSET:
                case NATURAL_LANGUAGE:
                case MIME_MEDIA_TYPE:
                case MEMBER_ATTR_NAME:
                    String str = o.toString();
                    out.writeShort(str.length());
                    out.writeBytes(str);                // these syntaxes are always US-ASCII
                    break;
                default:
                    logger.logp(Level.SEVERE, this.getClass().getName(), "writeIppValue",
                            "PLEASE REPORT TO THE DEVELOPER: THIS VALUETAG {0} HAS BEEN OVERLOOKED!", o);
                    assert false : "This ValueTag " + vt + " has been overlooked!";
            }
        }

        private Object validateAndTransform(ValueTag vt, Object o, Integer ll)
                throws ProtocolException {
            // decide limit for string length
            int limit;
            if (request.conformity == IppObject.Conformity.NONE)
                limit = Short.MAX_VALUE;
            else
                limit = ll != null ? ll : vt.MAX;

            switch (vt) {
                case OCTET_STRING:
                    if (((byte[]) o).length > limit)
                        throw new IllegalArgumentException(MessageFormat.format(resourceStrings.getString("ATTRIBUTE STRING IS LONGER THAN {0}."), limit));
                    break;

                case TEXT_WITH_LANGUAGE:
                case NAME_WITH_LANGUAGE:
                    if (request.conformity == IppObject.Conformity.NONE)
                        limit -= 9;                     // 2x2 length bytes + 5 natural-language length
                case TEXT_WITHOUT_LANGUAGE:
                case NAME_WITHOUT_LANGUAGE:
                    try {
                        encodeStringUTF8(o.toString(), limit);
                    } catch (BufferOverflowException ex) {
                        if (request.conformity == IppObject.Conformity.STRICT)
                            throw new IllegalArgumentException(
                                    MessageFormat.format(resourceStrings.getString("ATTRIBUTE STRING IS LARGER THAN {0} BYTES ENCODED AS UTF-8."), limit),
                                    ex);
                        else {
                            bb.flip();
                            logger.log(Level.INFO, "ATTRIBUTE STRING IS LARGER THAN {0} BYTES ENCODED AS UTF-8. TRUNCATED!", limit);
                        }
                    } catch (CharacterCodingException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                    break;

                case KEYWORD:
                case URI:
                case URI_SCHEME:
                case CHARSET:
                case NATURAL_LANGUAGE:
                case MIME_MEDIA_TYPE:
                case MEMBER_ATTR_NAME:
                    String str;
                    o = str = o.toString();
                    if (str.length() > limit) {
                        if (request.conformity == IppObject.Conformity.STRICT)
                            throw new IllegalArgumentException(MessageFormat.format(resourceStrings.getString("ATTRIBUTE STRING IS LONGER THAN {0}."), limit));
                        o = str.substring(0, limit);
                        logger.log(Level.INFO, "ATTRIBUTE STRING IS LONGER THAN {0}. TRUNCATED!", limit);
                    }
                    break;
            }
            return o;
        }

        /** Encodes string into this.bb */
        private ByteBuffer encodeStringUTF8(String str, int max)
                throws CharacterCodingException {
            // setup in, out and encoder
            CharBuffer in = CharBuffer.wrap(str);
            bb.clear();
            if (max < bb.capacity())
                bb.limit(max);
            utf8enc.reset();

            // do encoding loop
            while (true) {
                CoderResult cr = in.hasRemaining() ?
                        utf8enc.encode(in, bb, true) : CoderResult.UNDERFLOW;
                if (cr.isUnderflow())
                    cr = utf8enc.flush(bb);
                if (cr.isUnderflow())
                    // we're done
                    break;
                if (!cr.isOverflow() || bb.capacity() >= max)
                    // either malformed or unmappable or maxed-out and overflowed
                    cr.throwException();

                // Allocate a new buffer and copy over last encoding run
                ByteBuffer old = bb;
                int n = old.capacity() + (int)((in.remaining()+1) * utf8enc.averageBytesPerChar());
                if (n > max)
                    n = max;
                bb = ByteBuffer.allocate(n);
                System.arraycopy(old.array(), old.arrayOffset(), bb.array(), 0, old.position());
                bb.position(old.position());
            }
            bb.flip();
            return bb;
        }
    }


    /** IppTransportDecoder - Decodes / reads an IPP response */
    private static class IppTransportDecoder<T extends IppObject> extends IppTransport {
        private final DataInputStream in;
        private final long contentLength;
        private final CharsetDecoder usa;
        private CharsetDecoder csd;
        private final ByteBuffer bb = ByteBuffer.allocate(ValueTag.TEXT_WITHOUT_LANGUAGE.MAX);
        private CharBuffer cb = CharBuffer.allocate(ValueTag.TEXT_WITHOUT_LANGUAGE.MAX);
        private IppResponse<T> response;

        private enum ParsingState {
            /** Expect group-tag, value-tag, end-tag */ NEXT,
            /** Expect NEXT, multi-value-tag */ MULTI
        }
        private ParsingState state = ParsingState.NEXT;
        private GroupTag lastTag;
        private GenericAttribute curr;

        public IppTransportDecoder(InputStream in, long contentLength) throws IOException {
            ios[0] = this.in = new DataInputStream(in);
            this.contentLength = contentLength;
            usa = Charset.forName("US-ASCII").newDecoder();
            if (response.conformity != Conformity.STRICT)
                usa.onMalformedInput(CodingErrorAction.REPLACE)
                   .onUnmappableCharacter(CodingErrorAction.REPLACE);
        }
        
        private IppResponse<T> processResponse(T obj) throws IOException {
            // read response header
            response = new IppResponse<T>(in.readShort() /* version */,
                                          in.readShort() /* status */, 
                                          in.readInt() /* request id */, obj);
            // read operational attribures
            readOperationHead();
            
            // parse the remainder of the response
            int b,
                len;
            String str;
            Object value;
            curr = null;
            lastTag = GroupTag.OPERATION;
            while (lastTag != GroupTag.END) {
                // On each iteration read either a group-tag or an attribute (name+value)
                b = in.read();
                if (b < ValueTag.UNSUPPORTED.getValue()) {
                    // seen a group tag
                    if (curr != null) {
                        // add current attribute
                        response.addAttribute(curr);
                        curr = null;
                    }
                    // record new group-tag
                    lastTag = validateGroupTag(b);
                    response.newAttributeGroup(lastTag);
                }
                else { // attribute up ahead
                    len = in.readShort();
                    if (len > 0) {
                        // new attibute ahead
                        if (curr != null)
                            // so add current
                            response.addAttribute(curr);
                        // read new attribute's name and create it
                        validateConformity(len);
                        str = readString(len, usa);
                        curr = new GenericAttribute(str);
                    }
                    else if (len < 0)
                        throw new ProtocolException(MessageFormat.format(resourceStrings.getString("PRINT SERVER BROKEN: NEW ATTRIBUTE HAS NEGATIVE-LENGTH ({0}) NAME!"), len));
                    else if (curr == null)
                        throw new ProtocolException(resourceStrings.getString("PRINT SERVER BROKEN: NEW ATTRIBUTE HAS ZERO-LENGTH NAME!"));
                    
                    // read attribute's value length
                    len = in.readShort();
                    ValueTag vt = validateConformity(b, len);
                    value = readIppValue(vt, len);
                    curr.add(new GenericValue(vt, value));
                }                
            }
            
            return response;
        }

        private void readOperationHead() throws IOException, ProtocolException {
            int b, len;
            String str;
            
            // Read operational attributes group tag
            b = in.read();
            if (GroupTag.valueOf(b) != GroupTag.OPERATION)
                throw new ProtocolException(resourceStrings.getString("PRINT SERVER BROKEN: RESPONSE DOES NOT BEGIN WITH OPERATION GROUP TAG!"));
            
            // Read charset attribute
            b = in.read();  //CHARSET
            len = in.readShort();   //18
            if (ValueTag.valueOf(b) != ValueTag.CHARSET || len != 18 ||
                    !readString(len, usa).equals("attributes-charset"))
                throw new ProtocolException(resourceStrings.getString("PRINT SERVER BROKEN: FIRST ATTRIBUTE IS NOT CHARSET!"));
            len = in.readShort();
            str = readString(len, usa);
            response.addOperationAttribute(new GenericAttribute("attributes-charset", str, ValueTag.CHARSET));
            // Initialize the charset decoder
            csd = Charset.forName(str).newDecoder();
            if (response.conformity != Conformity.STRICT)
                csd.onMalformedInput(CodingErrorAction.REPLACE)
                   .onUnmappableCharacter(CodingErrorAction.REPLACE);
            
            // Read natural language attribute
            b = in.read();  //NATURAL_LANGUAGE
            len = in.readShort();   //27
            if (ValueTag.valueOf(b) != ValueTag.NATURAL_LANGUAGE || len != 27 ||
                    !readString(len, usa).equals("attributes-natural-language"))
                throw new ProtocolException(resourceStrings.getString("PRINT SERVER BROKEN: SECOND ATTRIBUTE IS NOT LANGUAGE!"));
            len = in.readShort();
            str = readString(len, usa);
            response.addOperationAttribute(new GenericAttribute("attributes-natural-language", str, ValueTag.NATURAL_LANGUAGE));
            // response.locale is set on first call to getLocale()
        }
        
        // Read string from a DataInputStream using readFully()
        private String readString (int len, CharsetDecoder csd) throws IOException {
            csd.reset();
            cb.clear();
            bb.limit(0);    // + next loop calls compact() := clear()
            
            // do read input and decode loop
            while (len > 0) {
                bb.compact();
                if (len < bb.remaining()) {
                    bb.limit(bb.position() + len);
                    in.readFully(bb.array(), bb.position(), len);
                    len = 0;
                }
                else {
                    in.readFully(bb.array(), bb.position(), bb.remaining());
                    len -= bb.remaining();
                }
                bb.rewind();    // not flip() as readFully doesn't advance position
                estimateCapacity(0, csd);
                CoderResult cr = csd.decode(bb, cb, len == 0);
                if (cr.isError())
                    cr.throwException();
            }
            
            // do final part decoding loop
            if (bb.hasRemaining())
                estimateCapacity(0, csd);
            while (true) {
                CoderResult cr = bb.hasRemaining() ?
                        csd.decode(bb, cb, true) : CoderResult.UNDERFLOW;
                if (cr.isUnderflow())
                    cr = csd.flush(cb);
                if (cr.isUnderflow())
                    // we're done
                    break;
                if (cr.isError())
                    cr.throwException();
                estimateCapacity(1, csd);
            }
            cb.flip();
            return cb.toString();
        }

        /** Estimate cb's capacity to be large enough to hold the input string */
        private void estimateCapacity(int min, CharsetDecoder csd) {
            int n = (int)(bb.remaining() * csd.averageCharsPerByte() + 1) - cb.remaining();
            n = Math.max(n, min);   // ensure minimum growth
            if (n > 0) {
                // Allocate a new buffer and copy over last decoding run
                CharBuffer old = cb;
                cb = CharBuffer.allocate(old.limit() + n);
                System.arraycopy(old.array(), old.arrayOffset(), cb.array(), 0, old.position());
                cb.position(old.position());
            }
        }
        
        private GroupTag validateGroupTag(int b) throws ProtocolException {
            try {
                return GroupTag.valueOf(b);
            }
            catch (IllegalArgumentException e) {
                if (response.conformity == Conformity.NONE)
                    return GroupTag.RESERVED;
            }
            throw new ProtocolException(resourceStrings.getString("MALFORMED RESPONSE: GROUP TAG IS RESERVED."));
        }

        /** check the attribute name length against the Conformity level */
        private void validateConformity(int len) throws ProtocolException {
            assert len >= 0 && len <= Short.MAX_VALUE; // len is short
            if (response.conformity == Conformity.STRICT &&
                    len > ValueTag.KEYWORD.MAX)
                throw new ProtocolException(MessageFormat.format(resourceStrings.getString("MALFORMED RESPONSE: NEW ATTRIBUTE NAME LENGTH IS {0}!"), len));
        }

        /** check the value-tag and length against the Conformity level */
        private ValueTag validateConformity(int b, int len) throws ProtocolException {
            assert len >= Short.MIN_VALUE && len <= Short.MAX_VALUE; // len is short
            final ValueTag vt;
            if (len < 0)
                throw new ProtocolException(MessageFormat.format(resourceStrings.getString("PRINT SERVER BROKEN: ATTRIBUTE HAS NEGATIVE-LENGTH ({0}) VALUE!"), len));
            try {
                vt = ValueTag.valueOf(b);
                validateConformity(vt, len);
            }
            catch (IllegalArgumentException ignore) {
                if (response.conformity == Conformity.NONE)
                    return ValueTag.RESERVED;
                throw new ProtocolException(resourceStrings.getString("MALFORMED RESPONSE: VALUE TAG IS RESERVED."));
            }
            return vt;
        }

        private void validateConformity(ValueTag vt, int len) throws ProtocolException {
            switch (vt) {
                case UNSUPPORTED:
                case UNKNOWN:
                case NO_VALUE:
                case NOT_SETTABLE:
                case DELETE_ATTRIBUTE:
                case ADMIN_DEFINE:                      // the above out-of-band values
                case BEGIN_COLLECTION:
                case END_COLLECTION:                    // and begin/end-collection
                    if (len != 0)                       // have zero-length
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST HAVE NO VALUE!"), vt));
                    break;
                case INTEGER:
                case ENUM:
                    if (len != 4)
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST BE {1} BYTES LONG NOT {2}!"),
                                vt, 4, len));
                    break;
                case BOOLEAN:
                    if (len != 1)
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST BE {1} BYTES LONG NOT {2}!"),
                                vt, 1, len));
                    break;
                case OCTET_STRING:
                case TEXT_WITHOUT_LANGUAGE:
                case NAME_WITHOUT_LANGUAGE:
                    if (response.conformity == Conformity.STRICT &&
                            len > vt.MAX)
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("MALFORMED RESPONSE: VALUE-TYPE, {0}, MUST BE AT MOST {1} BYTES LONG (GOT {2})."),
                                vt, vt.MAX, len));
                    break;
                case DATE_TIME:
                    if (len != 11)
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST BE {1} BYTES LONG NOT {2}!"),
                                vt, 11, len));
                    break;
                case RESOLUTION:
                    if (len != 9)
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST BE {1} BYTES LONG NOT {2}!"),
                                vt, 9, len));
                    break;
                case RANGE_OF_INTEGER:
                    if (len != 8)
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST BE {1} BYTES LONG NOT {2}!"),
                                vt, 8, len));
                    break;
                case TEXT_WITH_LANGUAGE:
                case NAME_WITH_LANGUAGE:
                    if (response.conformity == Conformity.STRICT &&
                            len > vt.MAX + 9) // 9 = 2x length fields + 5 char locale
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("MALFORMED RESPONSE: VALUE-TYPE, {0}, MUST BE AT MOST {1} BYTES LONG (GOT {2})."),
                                vt, vt.MAX + 9, len));
                    break;
                default:
                    throw new AssertionError();
            }
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * The picture of the encoding of a value is:
         * ---------------------
         * |   value-length    |   2 bytes
         * ---------------------
         * |          value    |   v bytes
         * ---------------------
         */
        private Object readIppValue(ValueTag vt, int len) throws ProtocolException, IOException {
            switch (vt) {
                case UNSUPPORTED:
                case UNKNOWN:
                case NO_VALUE:
                case NOT_SETTABLE:
                case DELETE_ATTRIBUTE:
                case ADMIN_DEFINE:                      // the above out-of-band values
                case BEGIN_COLLECTION:
                case END_COLLECTION:                    // and begin/end-collection
                    if (len != 0)                       // have zero-length
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST HAVE NO VALUE!"), vt));
                    break;
                case INTEGER:
                case ENUM:
                    if (len != 4)
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST BE {1} BYTES LONG NOT {2}!"),
                                vt, 4, len));
                    else
                        return in.readInt();
                case BOOLEAN:
                    if (len != 4)
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST BE {1} BYTES LONG NOT {2}!"),
                                vt, 4, len));
                    else
                        return in.readBoolean();
                case OCTET_STRING:
                    if (len < 0 || 
                        (response.conformity == Conformity.STRICT && len > ValueTag.OCTET_STRING.MAX))
                        throw new ProtocolException(MessageFormat.format(
                                resourceStrings.getString("PRINT SERVER BROKEN: VALUE-TYPE, {0}, MUST BE {1} BYTES LONG NOT {2}!"),
                                vt, 4, len));
                    // there is no standard attribute using this syntax ...
                    out.writeShort(((byte[]) o).length);
                    out.write((byte[]) o);
                    break;
                default:
                    throw new AssertionError();
            }
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
