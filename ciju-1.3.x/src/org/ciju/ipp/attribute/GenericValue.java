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
import java.util.ResourceBundle;
import javax.print.attribute.DateTimeSyntax;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.TextSyntax;
import javax.print.attribute.URISyntax;
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
