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

package org.ciju.ipp.attribute;

import java.util.Locale;
import static org.ciju.ipp.IppEncoding.*;
import javax.print.attribute.TextSyntax;

/**
 *
 * @author Opher Shachar
 */
public class GenericTextSyntax extends TextSyntax {
    
    private ValueTag tag;

    protected GenericTextSyntax(String value, Locale locale, ValueTag tag) {
        super(value, locale);
        if (tag == ValueTag.NAME)
            if (locale == null || locale.equals(Locale.getDefault()))
                this.tag = ValueTag.NAME_WITHOUT_LANGUAGE;
            else
                this.tag = ValueTag.NAME_WITH_LANGUAGE;
        else if (tag == ValueTag.TEXT)
            if (locale == null || locale.equals(Locale.getDefault()))
                this.tag = ValueTag.TEXT_WITHOUT_LANGUAGE;
            else
                this.tag = ValueTag.TEXT_WITH_LANGUAGE;
        else if (tag == ValueTag.CHARSET || tag == ValueTag.NATURAL_LANGUAGE ||
                 tag == ValueTag.KEYWORD || tag == ValueTag.MIME_MEDIA_TYPE ||
                 tag == ValueTag.URI_SCHEME)
            this.tag = tag;
        else
            throw new IllegalArgumentException("Value tag is not of 'character-string type.");
    }

    protected GenericTextSyntax(String value, Locale locale) {
        this(value, locale, ValueTag.NAME);
    }

    public ValueTag getTag() {
        return tag;
    }
    
}
