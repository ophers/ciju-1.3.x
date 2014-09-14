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

package org.ciju.ipp.attribute;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.print.attribute.Attribute;
import javax.print.attribute.CijuAttributeUtils;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.URISyntax;
import javax.print.attribute.standard.PrinterStateReasons;
import org.ciju.ipp.IppEncoding.ValueTag;

/**
 *
 * @author Opher Shachar
 */
public class GenericValue {
    private static final ResourceBundle resourceStrings = ResourceBundle.getBundle("org/ciju/ResourceStrings");
    private static final String BADVALUE = resourceStrings.getString("INCOMPATIBLE VALUE FOR THE GIVEN VALUE-TAG.");
    
    private final ValueTag vt;
    private final Object   value;

    public GenericValue(ValueTag vt, Object value) {
        validateSyntax(vt, value);
        this.vt = vt;
        this.value = value;
    }

    public ValueTag getValueTag() {
        return vt;
    }

    public Object getValue() {
        return value;
    }

    /**
     * @throws NullPointerException if vt is <tt>null</tt>.
     * @throws IllegalArgumentException if a value is provided for a value-tag that 
     *          does not accept a value or is incompatible with.
     */
    private static void validateSyntax(ValueTag vt, Object o) {
        if (vt == null)
            throw new NullPointerException("value-tag");
        
        switch (vt) {
            case UNSUPPORTED:
            case UNKNOWN:
            case NO_VALUE:
            case NOT_SETTABLE:
            case DELETE_ATTRIBUTE:
            case ADMIN_DEFINE:                      // the above out-of-band values
            case BEGIN_COLLECTION:                  // and begin/end-collection
            case END_COLLECTION:                    // have no value
                if (o != null)
                    throw new IllegalArgumentException(resourceStrings.getString("THIS VALUE-TAG MUST HAVE NO VALUE."));
                return;
            case INTEGER:
                if (!(o instanceof IntegerSyntax || o instanceof Integer))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            case ENUM:
                if (!(o instanceof EnumSyntax || o instanceof Integer))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            case BOOLEAN:
                if (!(o instanceof EnumSyntax || o instanceof Boolean))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            case OCTET_STRING:
                if (!(o instanceof byte[]))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            case DATE_TIME:
                if (!(o instanceof DateTimeSyntax || o instanceof Date))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            case RESOLUTION:
                if (!(o instanceof ResolutionSyntax))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            case RANGE_OF_INTEGER:
                if (!(o instanceof SetOfIntegerSyntax || o instanceof int[]))
                    throw new IllegalArgumentException(BADVALUE);
                if (o instanceof int[] && ((int[]) o).length != 2)
                    throw new IllegalArgumentException(resourceStrings.getString("ARRAY MUST HOLD EXACTLY TWO ELEMENTS."));
                break;
            case TEXT_WITHOUT_LANGUAGE:
            case NAME_WITHOUT_LANGUAGE:
                if (o instanceof String)
                    break;
            case TEXT_WITH_LANGUAGE:
            case NAME_WITH_LANGUAGE:
                if (!(o instanceof TextSyntax))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            case KEYWORD:
                if (o instanceof EnumSyntax)
                    break;
            case URI_SCHEME:
            case CHARSET:
            case NATURAL_LANGUAGE:
            case MIME_MEDIA_TYPE:
            case MEMBER_ATTR_NAME:
                if (!(o instanceof String))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            case URI:
                if (!(o instanceof URISyntax || o instanceof URI))
                    throw new IllegalArgumentException(BADVALUE);
                break;
            default:
                assert false : "This ValueTag " + vt + " is unknown!";
                throw new IllegalArgumentException(MessageFormat.format(resourceStrings.getString("THIS VALUETAG {0} IS UNKNOWN!"), vt));
        }
    }

    /**
     * The JPS Object Model has its quirks (mostly documented).
     * So, for example, PrinterStateReasons needs a special consideration.
     * @param o
     * @param loc
     * @return 
     */
    public static ValueTag deduceValueTag(Object o, Locale loc) {
        // First are generic objects used by CIJU
        if (o instanceof GenericValue)
            return ((GenericValue) o).getValueTag();
        else if (o instanceof String)
            return ValueTag.NAME_WITHOUT_LANGUAGE;
        // Below are the JPS standard syntaxes
        else if (o instanceof PrinterStateReasons)
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
            return deduceTextIPPSyntax((TextSyntax) o, loc);
        else if (o instanceof URISyntax)
            return ValueTag.URI;
        else if (o instanceof int[])
            return ValueTag.RANGE_OF_INTEGER;

        throw new IllegalArgumentException(resourceStrings.getString("ATTRIBUTE DOES NOT IMPLEMENT A KNOWN SYNTAX."));
    }

    private static ValueTag deduceTextIPPSyntax(TextSyntax o, Locale loc) {
        boolean wol = o.getLocale().equals(loc);
        Attribute a = (Attribute) o;
        if (a.getName().endsWith("-name") ||
            a.getName().equals("output-device-assigned"))
            return wol ? ValueTag.NAME_WITHOUT_LANGUAGE :
                         ValueTag.NAME_WITH_LANGUAGE;
        return wol ? ValueTag.TEXT_WITHOUT_LANGUAGE :
                     ValueTag.TEXT_WITH_LANGUAGE;
    }
    
    public static String getNaturalLanguage(Locale locale) {
        String lang = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if (lang.length() == 0)
            throw new IllegalArgumentException(resourceStrings.getString("LOCALE CANNOT HAVE AN EMPTY LANGUAGE FIELD."));
        if (lang.equals("iw"))
            lang = "he";                            // new code for Hebrew
        else if (lang.equals("ji"))
            lang = "yi";                            // new code for Yiddish
        else if (lang.equals("in"))
            lang = "id";                            // new code for Indonesian
        return country.length() == 0 ? lang : lang + "-" + country;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + vt.hashCode();
        hash = 97 * hash + (value != null ? value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        final GenericValue other = (GenericValue) obj;
        return this.vt == other.vt &&
               (this.value == other.value || (this.value != null && this.value.equals(other.value)));
    }
}